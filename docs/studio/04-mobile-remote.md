# Phase 4: the mobile remote

Goal: an Android and iOS app that pairs with the desktop studio by QR code and controls a presentation: next/previous, reveal steps, a slide list to jump to, speaker notes, the timer, and a preview of the current and next slide.

Done when (milestone M4): with the desktop presenting and a phone on the same Wi-Fi, scanning the QR pairs in under 5 seconds; slide changes round-trip in under 100 ms; notes and timer match the presenter window; and the phone reconnects on its own after the screen locks.

## What exists today

- `DeckNavigator` (`core/domain`) holds `currentIndex`, `currentStep` and `direction` as Compose state, with `next()`, `previous()`, `goTo(index, step)`.
- `DeckSyncState(slideIndex, direction, step)` is already `@Serializable`. It is what the web build sends between tabs through `localStorage` (`webApp/.../WebMain.kt`).
- `TimerController` exposes `state: StateFlow<TimerState>` and takes intents (`Start`, `Pause`, `Snooze`, `Deduct`…).
- `DeckMode.Local` (the presenter panel: notes, timer, TOC) is close to what the phone should show.

The remote is a networked version of what the two desktop windows already share in-process.

## Modules

- **`:remote:protocol`** (all targets, no Compose, package `ke.don.remote.protocol`): message types and versioning.
- **`:remote:server`** (JVM, package `ke.don.remote.server`): Ktor server embedded in the studio.
- **`:remote:client`** (Android + iOS + JVM for tests, package `ke.don.remote.client`): Ktor client, pairing and reconnection.
- **`:mobileRemote`** (Compose Multiplatform, Android + iOS, package `ke.don.mobileremote`): the remote UI.
- **`:androidApp`** and **`iosApp/`** in `ski-studio` are the remote app's entry points for `:mobileRemote`. `ski`'s own `androidApp`/`iosApp` stay as code-first demo apps and aren't touched.

## Task 4.1: protocol

```kotlin
const val PROTOCOL_VERSION = 1

@Serializable sealed interface ClientMessage {
    @Serializable @SerialName("hello")  data class Hello(val protocolVersion: Int, val deviceId: String, val deviceName: String, val token: String) : ClientMessage
    @Serializable @SerialName("next")   data object Next : ClientMessage
    @Serializable @SerialName("prev")   data object Previous : ClientMessage
    @Serializable @SerialName("goto")   data class GoTo(val slideIndex: Int, val step: Int = 0) : ClientMessage
    @Serializable @SerialName("timer")  data class Timer(val intent: TimerIntent) : ClientMessage   // mirror of TimerIntentHandler, serializable
    @Serializable @SerialName("asset")  data class RequestAsset(val assetId: String) : ClientMessage
    @Serializable @SerialName("ping")   data object Ping : ClientMessage
}

@Serializable sealed interface ServerMessage {
    @Serializable @SerialName("welcome") data class Welcome(val protocolVersion: Int, val sessionId: String, val entitled: Boolean) : ServerMessage
    @Serializable @SerialName("deck")    data class Deck(val document: DeckDocument) : ServerMessage            // sent once on connect and whenever the deck changes
    @Serializable @SerialName("state")   data class State(val sync: DeckSyncState, val presenting: Boolean) : ServerMessage
    @Serializable @SerialName("timer")   data class Timer(val state: TimerStateDto) : ServerMessage
    @Serializable @SerialName("asset")   data class Asset(val assetId: String, val bytesBase64: String) : ServerMessage
    @Serializable @SerialName("error")   data class Error(val code: String, val message: String) : ServerMessage
    @Serializable @SerialName("pong")    data object Pong : ServerMessage
}
```

- JSON over WebSocket text frames. Easy to debug; size is fine at this scale.
- The server is the source of truth. The phone sends **intents** (`Next`), never state; the server applies them to the presentation's `DeckNavigator` and broadcasts `State`. Two phones, or phone + keyboard, never disagree.
- The phone gets the whole `DeckDocument` and draws slides with `:engine:renderer`. That's why the engine had to be multiplatform: the remote shows real previews without streaming images.
- **Slides with components from packs** (02, Task 2.9): the phone can't run pack code, so the desktop renders those slides to images instead. Use Compose Desktop's offscreen rendering (`ImageComposeScene`) at the phone's preview size, cache by `(slideId, step, documentRevision, packRevision)`, and serve them from `GET /render/{slideId}/{step}`. The phone shows the image wherever it would have drawn the slide. Built-in components are still drawn natively.
- Assets are fetched lazily by ID and cached on the phone by `sha256`. Images and GIFs are sent as-is. Videos are **not** sent: the phone shows the `posterAssetId` frame with a video badge, since the video plays on the audience window anyway. Large images (over 2 MB) should be downscaled by the server to phone size before sending. Send binary assets over a separate `GET /asset/{id}` HTTP endpoint (same token) instead of base64 inside the WebSocket, so a big GIF doesn't block navigation messages. The `ServerMessage.Asset` message above can then be dropped.
- `TimerState` contains `Duration`s; send a DTO with milliseconds.

Tests: round-trip every message; a version mismatch produces `Error("version_mismatch")` with a helpful message ("Update the Ski app on your phone").

## Task 4.2: server (`:remote:server`)

- Ktor server (CIO engine) started by the studio when a presentation starts, or always on if the user enables "Allow remote while editing" (useful for rehearsal).
- Binds to `0.0.0.0` on a configurable port (default e.g. 47822). Endpoints: `GET /pair` (returns server info, used to check reachability) and `WS /remote`.
- Auth: the first message must be `Hello` with a valid token. Close any connection that sends anything else first, or nothing within 5 s.
- Bridges to the studio through an interface the studio implements, so the server has no Compose dependency:

```kotlin
interface PresentationSession {
    val document: StateFlow<DeckDocument>
    val sync: StateFlow<DeckSyncState>
    val timer: StateFlow<TimerState>
    fun handle(intent: ClientMessage)
}
```

- In the studio, `handle` calls `DeckNavigator.next()` etc. **on the main thread** (`withContext(Dispatchers.Main)`), because the navigator's state is Compose snapshot state.
- Advertise over mDNS (`_ski._tcp`) with JmDNS so the phone can find the desktop again without scanning. The QR code (4.3) stays the primary path.
- macOS shows a firewall prompt the first time a server listens. Mention it in the pairing screen before it appears.

## Task 4.3: pairing

1. Desktop Settings → Remote (or the Present toolbar) shows a QR code encoding:
   `ski://pair?h=192.168.1.20,10.0.0.5&p=47822&t=<token>&n=<desktop name>&v=1`
   List every non-loopback IPv4 address; the phone tries each.
2. The token is 128 random bits, generated per pairing and stored hashed on desktop with the device ID once paired, so the phone can reconnect without scanning again. Settings lists paired devices with "Forget".
3. The phone scans (CameraX + ML Kit barcode on Android, `AVCaptureMetadataOutput` on iOS via expect/actual), connects, sends `Hello`, gets `Welcome`.
4. Desktop shows "Pixel 9 connected" as a toast and in the presenter window.

Also show a 6-digit code + IP under the QR for when the camera won't cooperate.

## Task 4.4: client (`:remote:client`)

- Ktor client WebSockets (OkHttp engine on Android, Darwin on iOS).
- `RemoteConnection` exposes `StateFlow<ConnectionState>` (`Searching`, `Connecting`, `Connected(session)`, `Reconnecting(attempt)`, `Failed(reason)`) and `StateFlow<RemoteDeckState>` (document, sync, timer).
- Reconnect with backoff (0.5 s, 1 s, 2 s, max 5 s) when the app returns to the foreground or the network changes. Try the last-known IP first, then mDNS.
- Heartbeat: `Ping` every 5 s; treat 3 missed `Pong`s as disconnected.
- Next/Previous are sent immediately and shown optimistically; the next `State` from the server corrects it if needed.

## Task 4.5: phone UI (`:mobileRemote`)

Screens:
1. **Pair**: scan QR, manual code entry, list of recently paired desktops.
2. **Remote** (main screen, portrait):
   - Top: current slide preview (rendered from the document at the current reveal step) and a smaller next-slide preview.
   - Middle: speaker notes for the current slide, scrollable, large text, with a text size control.
   - Bottom: big Previous / Next buttons (the whole lower half should be tappable; presenters don't look at the phone). Timer with pause/resume and ±1 min (reusing `TimerComponent` from `:shared:components` if it fits).
   - Swipe left/right also navigates. Volume keys as next/previous on Android (`onKeyDown` in `MainActivity`); on iOS, volume key capture isn't allowed in a public API, so skip it there.
   - Haptic tap on every navigation.
   - Keep the screen on while connected.
3. **Slides**: grid of thumbnails to jump to (`GoTo`).
4. **Paywall**: shown when `Welcome.entitled == false` (see 05).

Platform setup:
- **iOS:** `NSLocalNetworkUsageDescription` and `NSBonjourServices` (`_ski._tcp`) in `Info.plist`, or iOS blocks LAN access silently. `NSCameraUsageDescription` for scanning.
- **Android:** `INTERNET`, `CAMERA`, `ACCESS_WIFI_STATE`, and `CHANGE_WIFI_MULTICAST_STATE` (for mDNS via `NsdManager`). Cleartext traffic to LAN IPs needs a `network_security_config` allowing it, since the server is plain `ws://` on the LAN (see Risks).

## Task 4.6: replace web sync (optional, later)

`ski`'s web build (and the web player published from the studio, 02 Task 2.8) syncs through `localStorage`. If that keeps causing bugs, move a small sync protocol into `ski` (with a `BroadcastChannel` transport) and have `:remote:protocol` reuse its message types. Not needed for v1. A later version could let the phone remote control a deck published to the web through the relay (Risks).

## Risks

- **Venue Wi-Fi blocks device-to-device traffic** (client isolation is common at conferences). The pairing screen should detect "phone can't reach desktop" after a few seconds and suggest: (a) turn on the phone's hotspot and connect the laptop to it, or (b) the relay (below).
- **Relay fallback (later, paid tier only):** a small WebSocket relay (Ktor on Fly.io or Cloudflare Durable Objects) where desktop and phone both connect outbound and are matched by a session ID from the QR. Same protocol, different transport. Costs money to run, which is another reason it fits the paid tier.
- **Plain `ws://` on the LAN.** Acceptable for v1 because the token is needed and the network is local, but anyone on the same Wi-Fi can see traffic. Later: TLS with a self-signed cert whose fingerprint is in the QR code, pinned by the phone.
- **Clock and timer drift.** The desktop owns the timer; the phone only displays it. Don't run a second timer on the phone.
