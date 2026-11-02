# Ski Studio: plan overview

Ski Studio turns Ski from a code-first framework into a desktop app. People build decks by chatting with an AI they choose, present from the app, and drive the talk from their phone. RevenueCat handles payments.

This folder holds the plans. Read this file first, then the plan for the area you are working on.

| File | Area | Phase |
|---|---|---|
| [01-engine.md](01-engine.md) | Slides as data: `DeckDocument`, renderer, edit operations, Kotlin export | 1 |
| [02-studio-shell.md](02-studio-shell.md) | Desktop app shell: files, preview, present | 2 |
| [03-ai-chat.md](03-ai-chat.md) | Chat-only editing with the user's own AI, plus an MCP server | 3 |
| [04-mobile-remote.md](04-mobile-remote.md) | Phone remote over LAN, QR pairing | 4 |
| [05-monetisation.md](05-monetisation.md) | RevenueCat entitlements, accounts, paywalls | 5 |
| [06-packaging.md](06-packaging.md) | Signing, notarization, auto-update, release CI | 6 |

## The product in one paragraph

A desktop app (macOS first, then Windows and Linux) with a chat on the left, a live deck preview in the middle, and an Assets side panel on the right where users upload images, GIFs and videos. There is no manual slide editor: the Assets panel manages files, and the chat puts them on slides. Every change comes from the AI, which calls typed tools such as `add_slide` or `update_element` against a structured deck document. Presenting opens the existing two-window setup (audience window and presenter panel). A phone app pairs with the desktop by scanning a QR code and acts as a remote with notes, timer and next-slide preview. Editing and previewing are free. Presenting full-screen and using the phone remote need a paid entitlement.

## Decisions already made

These were settled when planning started. Reopen them only with a written reason.

1. **Slides are data, not compiled Kotlin.** A deck is a serializable `DeckDocument`, drawn at runtime by a renderer built on `:shared:components`. We do not ship the Kotlin compiler. Reasons: install size, compile speed, safety of running AI-written code, and the phone needs to draw the same slides. See [01-engine.md](01-engine.md).
2. **Every change goes through one set of edit operations** (`:engine:ops`). The AI tools, the MCP server, undo/redo and any future manual editing all use them. Nothing else mutates a document.
3. **Users bring their own AI.** We do not proxy model calls or pay for tokens. Keys live in the OS keychain.
4. **The desktop app is the server for the remote.** It runs Ktor over WebSockets on the LAN. No cloud is needed to present.
5. **Direct desktop distribution** (signed DMG/MSI) with RevenueCat Web Billing, not the Mac App Store, at least for v1.
6. **The code-first framework stays.** `:shared:components` keeps publishing as `io.github.donald-okara:ski`. The studio is a consumer of it, and "Export to Kotlin" gives users a path back to the code-first workflow.

## Target module layout

New modules are marked **new**. Existing ones keep their package names (see `CLAUDE.md`).

```
:core:domain            existing  SlideConfig, DeckNavigator, DeckMode, timer, locals
:shared:components      existing  published library, unchanged API
:shared:design          existing
:shared:resources       existing

:engine:model           new  DeckDocument schema (pure Kotlin + kotlinx.serialization, no Compose)
:engine:ops             new  edit operations, validation, undo/redo history (no Compose)
:engine:renderer        new  DeckDocument -> List<SlideConfig>, element composables
:engine:export          new  DeckDocument -> Kotlin source (JVM only)

:remote:protocol        new  messages shared by desktop and phone (no Compose)
:remote:server          new  Ktor WebSocket server (JVM only)
:remote:client          new  Ktor client + pairing (Android, iOS)

:ai:agent               new  provider setup, tool definitions, chat loop (JVM only)
:ai:mcp                 new  MCP server exposing the same tools (JVM only)

:billing                new  entitlement interface; RevenueCat on mobile, REST on desktop

:studio                 new  desktop studio UI (chat, preview, settings, paywall)
:mobileRemote           new  phone remote UI (shared Compose for Android + iOS)

:composeApp             existing  keeps hosting the code-first demo deck and gallery
:desktopApp             existing  becomes the studio entry point (or a new :studioApp, see 02)
:androidApp, iosApp     existing  become remote entry points (see 04)
:webApp                 existing  unchanged; gallery + demo deck on Vercel
```

Rules for the graph:
- `:engine:model`, `:engine:ops` and `:remote:protocol` must not depend on Compose. The model and protocol must be easy to test on the JVM and to share with the phone.
- Only `:engine:renderer` and the UI modules (`:studio`, `:mobileRemote`) depend on `:shared:components`.
- JVM-only modules use a JVM-only convention. Add a `ke.don.ski.jvmLibrary` convention plugin in `build-logic/convention` (Phase 1, first task) instead of calling `kotlin("jvm")` by hand in each module.
- The current `ConfigureKotlinMultiplatform.kt` turns on every target (android, jvm, js, wasmJs, ios). Engine and protocol modules can use it as is. If a dependency (Ktor server, keychain) does not support a target, that module goes JVM-only rather than adding target switches to the shared convention.

## Phases and order

```
Phase 1  Engine            ──┐
Phase 2  Studio shell      ──┤ (2 starts when 1's model + renderer can draw one deck)
Phase 3  AI chat           ──┼── 3 and 4 can run in parallel once 1 and 2 are stable
Phase 4  Mobile remote     ──┘
Phase 5  Monetisation          needs 2 (present button) and 4 (remote app)
Phase 6  Packaging             can start any time after 2; must finish before a public release
```

Milestones worth demoing:
- **M1 (end of Phase 1):** the droidcon/demo deck is rewritten as a `.ski.json` file and renders the same as the Kotlin version, including reveal steps.
- **M2 (end of Phase 2):** open a `.ski` file in the desktop app, preview it, press Present and get both windows.
- **M3 (end of Phase 3):** "Make me a 5-slide talk about Compose state" produces a deck you can present.
- **M4 (end of Phase 4):** a phone on the same Wi-Fi pairs by QR and moves slides with notes on screen.
- **M5 (end of Phase 5):** a sandbox purchase on the phone unlocks Present on the desktop.

## Where to start (day one)

1. Read [01-engine.md](01-engine.md), section "Task 1.1".
2. Add the `ke.don.ski.jvmLibrary` convention plugin and the empty `:engine:model` module.
3. Write `DeckDocument` and its first five element types, with round-trip JSON tests.

The repo has no tests today (see `CLAUDE.md`). The engine is where that changes: `:engine:model`, `:engine:ops` and `:remote:protocol` should be developed test-first with `kotlin-test`, run via `./gradlew :engine:model:jvmTest`.

## Cross-cutting concerns

- **Versioning the file format.** `DeckDocument.schemaVersion` starts at 1. Every breaking change adds a migration in `:engine:model` (see 01). The phone and desktop exchange the version during pairing.
- **Analytics and crash reporting.** Out of scope for v1 plans. Pick one (Sentry or PostHog are already connected to this workspace) before the first public beta.
- **Privacy.** Deck content and API keys never leave the machine except to the user's chosen AI provider. Say this plainly in onboarding and in the privacy policy.
- **`context.md` rules.** They become the AI system prompt (see 03). Rules about Kotlin specifics (named composables, `:segments:demos`) apply only to the code-first path and to Kotlin export.

## Open questions

Track these here and move answers into the relevant plan.

- Brand: keep "Ski" for the app, or "Ski Studio"?
- Pricing: subscription, one-off "Pro" unlock, or both? (see 05)
- Do we want a web-hosted viewer for sharing decks later? It would change how assets are stored (see 01, "Assets").
- Is Windows/Linux in v1, or macOS only? Affects keychain, packaging and firewall work.
