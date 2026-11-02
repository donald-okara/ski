# Ski Studio: plan overview

Ski Studio is a desktop app built on Ski. People build decks by chatting with an AI they choose, present from the app, and drive the talk from their phone. RevenueCat handles payments.

**Two repos.** Ski Studio lives in its own repo, `ski-studio`, which can stay private. It depends on the engine that `ski` publishes to Maven Central. These plans sit in `ski/docs/studio/` for now so they can be reviewed next to the engine plan. When `ski-studio` is created, copy this folder to its `docs/` and update the links into `ski` to GitHub URLs.

| File | Repo | Area | Phase |
|---|---|---|---|
| [../engine/README.md](../engine/README.md) | `ski` | Repo split, publishing fix (Task 0), versioning, developing both repos together | 0 |
| [../engine/02-custom-components.md](../engine/02-custom-components.md) | `ski` | Your own composables as slide elements: `@SkiComponent`, Gradle plugin, `skiPreview` with hot reload, component packs, custom web player | 1b |
| [../engine/01-engine.md](../engine/01-engine.md) | `ski` | Slides as data: `DeckDocument`, renderer, edit operations, demo-ready code elements, web player, Kotlin export | 1 |
| [02-studio-shell.md](02-studio-shell.md) | `ski-studio` | Desktop app shell: files, preview, assets panel, present, publish to web (Vercel) | 2 |
| [03-ai-chat.md](03-ai-chat.md) | `ski-studio` | Chat-only editing with the user's own AI, skills, attached project code, MCP server | 3 |
| [04-mobile-remote.md](04-mobile-remote.md) | `ski-studio` | Phone remote over LAN, QR pairing | 4 |
| [05-monetisation.md](05-monetisation.md) | `ski-studio` | RevenueCat entitlements, accounts, paywalls | 5 |
| [06-packaging.md](06-packaging.md) | `ski-studio` | Signing, notarization, auto-update, release CI | 6 |

## The product in one paragraph

A desktop app (macOS first, then Windows and Linux) with a chat on the left, a live deck preview in the middle, and an Assets side panel on the right where users upload images, GIFs and videos. There is no manual slide editor: the Assets panel manages files, and the chat puts them on slides. Every change comes from the AI, which calls typed tools such as `add_slide` or `update_element` against a structured deck document. Because Ski is for demoing code, users can load **their own composables** as component packs built by the Ski Gradle plugin in their project, and the AI puts them on slides in their example states. `./gradlew skiStudio` keeps the studio in sync as they edit. The AI can also read an attached project folder (read-only) and use skills: built-in ones such as step-by-step code walkthroughs, and users' own in the Agent Skills format. Presenting opens Ski's two-window setup (audience window and presenter panel). A phone app pairs with the desktop by scanning a QR code and acts as a remote with notes, timer and next-slide preview. One button publishes a deck to Vercel as a static web app (or exports a folder for any host), so decks stay portable. Editing, previewing and publishing to the web are free. Presenting full-screen and using the phone remote need a paid entitlement.

## Decisions already made

These were settled when planning started. Reopen them only with a written reason.

1. **Slides are data, not compiled Kotlin.** A deck is a serializable `DeckDocument`, drawn at runtime by a renderer built on Ski's components. The studio never compiles anything: built-in components ship with it, and the user's own composables arrive already compiled by their own build (component packs, decision 8). Reasons: install size, compile speed, safety of running AI-written code, and the phone needs to draw the same slides. See [01-engine.md](../engine/01-engine.md).
2. **Every change goes through one set of edit operations** (`DeckOp` in `ski-engine-ops`). The AI tools, the MCP server, the assets panel, undo/redo and any future manual editing all use them. Nothing else mutates a document.
3. **Users bring their own AI.** We do not proxy model calls or pay for tokens. Keys live in the OS keychain.
4. **The desktop app is the server for the remote.** It runs Ktor over WebSockets on the LAN. No cloud is needed to present.
5. **Direct desktop distribution** (signed DMG/MSI) with RevenueCat Web Billing, not the Mac App Store, at least for v1.
6. **Ski is the open-source engine; the studio is a separate product.** `ski` stays Apache 2.0 and keeps the code-first framework, gallery and demo deck. The studio consumes it only through published artifacts (or a local composite build while developing). "Export to Kotlin" gives users a path back to the code-first workflow.
7. **Decks stay portable.** Every deck can leave the studio three ways: the `.ski` file, a static website built on `ski`'s open-source web player (Publish to web / Vercel, 02 Task 2.8), and Export to Kotlin. Nothing about a deck should only work inside the studio.
8. **Ski's core job comes first: show your own composables at a moment's notice.** That job is done in `ski` by code-first decks and the free `./gradlew skiPreview` (hot reload, from the user's own project). The studio builds on it through component packs; it never replaces it. The packaged studio needs no developer tools; adding your own components uses the toolchain you already have.

## `ski-studio` repo layout

```
ski-studio/
  build-logic/            own convention plugins, IDs ke.don.studio.* (don't reuse ke.don.ski.*; see engine README)
  gradle/libs.versions.toml   pins ski-bom; Koog, Ktor, MCP SDK, RevenueCat, java-keyring…
  settings.gradle.kts     composite build of ../ski when use_local_ski=true

  :studio                 desktop studio UI (chat, preview, assets panel, settings, paywall)       JVM
  :ai:agent               provider setup, tool definitions, chat loop                              JVM
  :ai:mcp                 MCP server exposing the same tools                                       JVM
  :remote:protocol        messages shared by desktop and phone (no Compose)                        all targets
  :remote:server          Ktor WebSocket + asset HTTP server                                       JVM
  :remote:client          Ktor client, pairing, reconnection                                       Android, iOS, JVM (tests)
  :billing                entitlement interface; RevenueCat on mobile, REST on desktop            Android, iOS, JVM
  :mobileRemote           phone remote UI (shared Compose)                                         Android, iOS

  :desktopApp             studio entry point + packaging
  :androidApp             remote app entry point
  iosApp/                 remote app (Xcode project consuming the shared framework)
```

From `ski` (via Maven Central or the composite build): `ski-bom`, `ski` (components), `ski-domain`, `ski-engine-model`, `ski-engine-ops`, `ski-engine-renderer`, and `ski-engine-export` for the Export menu.

Rules for the graph:
- `:remote:protocol` must not depend on Compose. It depends on `ski-engine-model` only, so the phone can decode documents.
- Only `:studio` and `:mobileRemote` depend on Compose UI and Ski components.
- The studio's `build-logic` declares only the targets it needs: JVM for the desktop modules, Android + iOS (+ JVM for tests) for mobile ones. It doesn't need web targets.
- If a studio feature needs a change in the engine (a new element type, a new op), make it in `ski` first, publish or use the composite build, then use it here. Don't fork engine code into the studio.

## Phases and order

```
Phase 0  Publishing fix (ski)     ──┐
Phase 1  Engine (ski)             ──┤ 0 before 1; 1's model + renderer must draw one deck before 2
Phase 1b Custom components (ski)  ──┤ after 1; before the studio's component packs (2, Task 2.9)
Phase 2  Studio shell             ──┤
Phase 3  AI chat                  ──┼── 3 and 4 can run in parallel once 1 and 2 are stable
Phase 4  Mobile remote            ──┘
Phase 5  Monetisation                 needs 2 (present button) and 4 (remote app)
Phase 6  Packaging                    can start any time after 2; must finish before a public release
```

Milestones worth demoing:
- **M0 (end of Phase 0, `ski`):** a blank Gradle project resolves `ski-bom` + `ski` from Maven Local with no missing dependencies.
- **M1 (end of Phase 1, `ski`):** the demo deck is rewritten as `samples/demo.ski.json` and renders the same as the Kotlin version, including reveal steps. First engine release published.
- **M1b (end of Phase 1b, `ski`):** in a sample project, an `@SkiComponent` composable shows on a slide with `./gradlew skiPreview` and updates on edit without a restart; `./gradlew skiWebPlayer` renders it on the web.
- **M2 (end of Phase 2):** open a `.ski` file in the studio, preview it, add an image in the Assets panel, press Present and get both windows.
- **M2b (end of Phase 2):** Publish deploys that deck to a Vercel preview URL, and the web presenter popup works there.
- **M2c (end of Phase 2):** `./gradlew skiStudio` in the sample project opens the studio with its component pack, and rebuilding reloads the components in place.
- **M3 (end of Phase 3):** "Make me a 5-slide talk about Compose state" produces a deck you can present; with a project folder attached, "walk through `DeckNavigator.next()`" uses the `code-walkthrough` skill with real code and line-by-line focus.
- **M4 (end of Phase 4):** a phone on the same Wi-Fi pairs by QR and moves slides with notes on screen.
- **M5 (end of Phase 5):** a sandbox purchase on the phone unlocks Present on the desktop.

## Where to start (day one)

In `ski`:
1. [Engine README](../engine/README.md), Task 0: fix publishing and confirm with a blank consumer project.
2. [01-engine.md](../engine/01-engine.md), Tasks 1.1–1.2: the `:engine:model` module and `DeckDocument`, test-first.
3. The Compose Hot Reload spike from [02-custom-components.md](../engine/02-custom-components.md), Task 1b.4.

In `ski-studio` (once M1 is close):
1. Create the repo, `build-logic`, and `settings.gradle.kts` with the composite build switch (engine README, "Developing ski and ski-studio together").
2. [02-studio-shell.md](02-studio-shell.md), Task 2.1.

`ski` has no tests today (see its `CLAUDE.md`). The engine is where that changes: `:engine:model` and `:engine:ops` are built test-first. In `ski-studio`, `:remote:protocol`, `:ai:agent` and `:billing` get tests from the start.

## Cross-cutting concerns

- **Versioning the file format.** `DeckDocument.schemaVersion` starts at 1, owned by `ski` (01, Task 1.5). The studio's phone and desktop exchange both the schema version and `PROTOCOL_VERSION` during pairing.
- **Engine version drift.** Desktop and phone may ship with different `ski` versions (app store review delays). The phone must render documents from a newer engine in degraded mode (placeholders for unknown elements), which the engine's `Element.Unknown` handles.
- **Analytics and crash reporting.** Out of scope for v1 plans. Pick one (Sentry or PostHog are already connected to this workspace) before the first public beta.
- **Privacy.** Deck content and API keys never leave the machine except to the user's chosen AI provider. Say this plainly in onboarding and in the privacy policy.
- **`context.md` rules.** `ski`'s `context.md` becomes a source for the AI system prompt (03, Task 3.4). Copy and adapt it into the studio rather than reading it from `ski` at runtime. Rules about Kotlin specifics (named composables, `:segments:demos`) apply only to the code-first path and to Kotlin export.
- **Licences.** `ski` is Apache 2.0, which allows a closed-source studio built on it. Keep the NOTICE/attribution in the studio's About screen.

## Open questions

Track these here and move answers into the relevant plan.

- Brand: keep "Ski" for the app, or "Ski Studio"?
- Is `ski-studio` private/closed source, or open core?
- Pricing: subscription, one-off "Pro" unlock, or both? (see 05)
- Publish to web: free for everyone (recommended), or free with a "Made with Ski" badge that Pro removes? (see 02, Task 2.8)
- Skills: open a curated skill list / marketplace later, or keep to folders and zips?
- Component packs: free for editing and preview (recommended), or Pro-only?
- Is Windows/Linux in v1, or macOS only? Affects keychain, packaging and firewall work.
