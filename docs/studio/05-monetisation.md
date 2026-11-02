# Phase 5: monetisation with RevenueCat

Goal: editing and previewing stay free. Presenting full-screen from the desktop and using the phone remote need a paid entitlement, bought on any platform and honoured on all of them.

Done when (milestone M5): a sandbox purchase on the phone unlocks Present on the desktop within a few seconds, a Web Billing purchase on the desktop unlocks the phone remote, and both keep working offline for the grace period.

## What we sell

RevenueCat entitlements (configure in the RevenueCat dashboard):

| Entitlement | Unlocks |
|---|---|
| `present` | The Present button in the studio (audience + presenter windows, Task 2.4) |
| `remote` | The phone remote app beyond the pairing screen (Task 4.5) |

Products (start with one bundle, it is easier to explain):
- **Ski Pro (monthly / yearly)**: grants `present` + `remote`.
- Optional later: a lifetime unlock, or `present`-only at a lower price.

Pricing is an open question in the README. Set prices in the stores and the RevenueCat dashboard, not in code; the app reads the current **Offering** and shows whatever is configured there, so prices can change without a release.

Free tier must still be useful: unlimited decks, chat editing, preview with arrow keys inside the editor, Export to Kotlin. Preview inside the editor window is not gated, only the full-screen two-window presentation.

## Module: `:billing`

Package `ke.don.billing`. One interface, two implementations.

```kotlin
interface Billing {
    val entitlements: StateFlow<EntitlementState>   // Unknown, Loaded(active: Set<Entitlement>, expiresAt, fetchedAt)
    suspend fun refresh()
    suspend fun offerings(): Offerings
    suspend fun purchase(packageId: String): PurchaseResult
    suspend fun restore(): EntitlementState
    suspend fun logIn(appUserId: String)
    suspend fun logOut()
}
enum class Entitlement { Present, Remote }
```

- **Mobile (`androidMain`, `iosMain`):** RevenueCat's KMP SDK (`com.revenuecat.purchases:purchases-kmp-core`, plus `purchases-kmp-ui` for ready-made paywalls). Check the latest version and its Kotlin/Compose requirements against ours before adding.
- **Desktop (`jvmMain`):** there is no RevenueCat SDK for the JVM. Use:
  - **RevenueCat Web Billing** for purchases: open the hosted checkout (Web Purchase Link) in the system browser with the user's app user ID.
  - **RevenueCat REST API** to read entitlements: `GET /v1/subscribers/{app_user_id}` with the public app API key. Verify against the current RevenueCat docs which key type and endpoint version to use from a client app.
  - After checkout, poll `refresh()` for up to ~60 s while showing "Finishing your purchase…", and refresh again when the window regains focus.

## Task 5.1: user identity (decide first)

Entitlements in RevenueCat belong to an **app user ID**. Desktop and phone must share one, or a purchase on one won't unlock the other. Two options:

**A. Real accounts (recommended for launch).** Email magic link or Sign in with Apple/Google, using a hosted auth service (Supabase Auth or Firebase Auth). The auth user ID becomes the RevenueCat app user ID on both devices (`Purchases.logIn(uid)` on mobile, the same ID in REST calls on desktop).
- Desktop sign-in: open the browser, receive the callback on a loopback URL (`http://127.0.0.1:<random port>/callback`) or a `ski://` deep link registered by the installer.
- Works across reinstalls and multiple machines, and supports restore.
- Apple requires Sign in with Apple when other social logins are offered on iOS.

**B. Pairing-linked anonymous IDs (simpler, weaker).** The desktop creates a random app user ID on first run. When the phone pairs, `Welcome` carries that ID and the phone calls `Purchases.logIn(desktopUserId)`.
- No account system to build.
- Lost on desktop reinstall unless the user exports a recovery code. Two desktops can't share a purchase.

Recommendation: ship B for the private beta (fast), move to A before the public launch. Design `Billing.logIn` so swapping IDs is supported from the start (RevenueCat aliases/transfers purchases on `logIn`; check the project's transfer behaviour setting in the dashboard).

## Task 5.2: gating the desktop

- `canPresent = Entitlement.Present in active` feeds the `canPresent` input added in Task 2.4.
- Present button when not entitled: opens the paywall window (offering + "Buy" → Web Billing, "Restore", "I bought on my phone" → refresh).
- **Offline grace:** cache the last `EntitlementState` in `<appdata>/entitlements.json`, signed with a key stored in the keychain so it's not trivially editable. Treat the entitlement as active until `max(expiresAt, fetchedAt + 7 days)`. Presenters are often offline on stage; a paywall popping up mid-talk is the worst outcome.
- Never block a presentation that is already running because a refresh failed or the entitlement expired during it.

## Task 5.3: gating the phone

- The remote app is free to install. Pairing works without paying, so the user sees it work, then the Remote screen shows the paywall.
- Entitlement check happens **on the phone** via the SDK. The desktop's `Welcome.entitled` flag is only a hint for the UI (e.g. desktop entitled via Web Billing, phone not yet refreshed → call `refresh()`).
- Use RevenueCat Paywalls (`purchases-kmp-ui`) for the paywall screen so copy and layout can be changed from the dashboard.
- **iOS rule:** a paid feature in the iOS app must be purchasable through in-app purchase in that app. Web Billing on desktop is fine, but iOS also needs its own IAP products. Before release, check the current App Store Review Guidelines on linking to outside purchases, since they changed in some regions in 2025.
- **Google Play:** same: Play Billing for in-app purchase of digital features.

## Task 5.4: dashboard and store setup (non-code checklist)

- [ ] RevenueCat project with three apps: iOS (App Store), Android (Play Store), Web (Web Billing, with a Stripe account connected).
- [ ] Entitlements `present`, `remote`. Products in App Store Connect, Play Console and Web Billing, all attached to both entitlements.
- [ ] One Offering `default` with monthly and yearly packages.
- [ ] Sandbox testers on iOS, license testers on Play, Stripe test mode for Web Billing.
- [ ] Webhooks (optional): send RevenueCat events to an endpoint later, if we need server-side features such as the relay (04, Risks) gated by entitlement.
- [ ] Privacy policy and terms URLs (required by both stores and by Web Billing checkout).

## Task 5.5: tests

- Fake `Billing` for UI tests (entitled / not entitled / expired / offline).
- Unit tests for the grace logic (offline for 3 days with an expired sub → still active; 8 days → inactive; tampered cache → inactive, refresh required).
- Manual matrix before release: buy on iOS → unlocks desktop; buy on Android → unlocks desktop; buy on desktop → unlocks phone; cancel → access ends at period end; restore on a new device.

## Risks

- **Desktop piracy.** A JVM app can be patched to skip the check. Accept it; the target audience (developers giving talks) mostly pays when the price is fair. Don't spend time on obfuscation in v1.
- **Store review.** Apple may reject a remote app that "doesn't work without another product". Make sure the review notes explain pairing and include a demo mode (a built-in sample deck the phone can present locally) so reviewers can test it alone.
