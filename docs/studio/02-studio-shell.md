# Phase 2: the desktop studio shell

Goal: a desktop app that opens and saves decks, shows a live preview, and presents them using today's two-window setup. The chat pane is a placeholder until Phase 3.

Done when (milestone M2): you can open `samples/demo.ski`, see it in the preview, press Present, and get the audience window plus the presenter panel, both driven by one `DeckNavigator` as in `ski`'s `desktopApp/.../main.kt` today.

## Where the code goes

- **`:studio`** (new, JVM-only Compose module, package `ke.don.studio`): all studio UI and state. JVM-only because it needs the file system, the keychain, the Ktor server and AI clients.
- **`:desktopApp`** (in `ski-studio`): thin entry point (`ke.don.studio.StudioMainKt`) plus `compose.desktop` packaging config. `ski`'s own `:desktopApp` stays the code-first demo deck and isn't touched.

The studio gets Ski's UI through published artifacts (`ski`, `ski-domain`, `ski-engine-*`, pinned by `ski-bom`), or through the composite build while developing (engine README). Add a run command to the studio's `CLAUDE.md` once it exists:

```shell
./gradlew :desktopApp:run
```

## Task 2.1: app skeleton

- `StudioMain.kt`: `application { }` with one main window, `AppTheme` from `:shared:design`, and a menu bar (`MenuBar` in Compose Desktop): File (New, Open…, Open Recent, Save, Save As…, Export to Kotlin… [disabled until Phase 1 export]), Edit (Undo, Redo), View (Present, Toggle chat), Help.
- `StudioState` (one per open document): holds `DeckEditor` (from `:engine:ops`), the file path, a `dirty` flag and the selected slide ID. Expose as `StateFlow`s. Plain classes with constructor injection are enough; no DI framework in v1.
- One window per document is simplest. Tabs can come later.

## Task 2.2: file format and storage

- A `.ski` file is a zip of the folder from 01 ("Assets"): `deck.json` + `assets/`. Zips are easy to share by email and Slack, unlike folders. Inside the app, unzip to a working directory under the app data dir and zip again on save.
- App data dir: `~/Library/Application Support/Ski` (macOS), `%APPDATA%\Ski` (Windows), `$XDG_DATA_HOME/ski` (Linux). Put that lookup in a small `AppDirs` object.
- Autosave every 30 s and on window blur to `<appdata>/autosave/<deck-id>.ski`. On launch, offer to restore autosaves newer than the file on disk.
- Recent files list in `<appdata>/settings.json` (kotlinx.serialization).
- Register the `.ski` extension with the OS in packaging (06).

## Task 2.3: main window layout

```
┌───────────────────────────────────────────────────────────────────────────┐
│  Toolbar: deck title · Undo · Redo · [Assets] · [Present ▶] · account     │
├───────────────┬───────────────────────────────────────────┬───────────────┤
│               │                                           │               │
│  Chat         │   Live preview (current slide, 16:9)      │  Assets       │
│  (Phase 3,    │                                           │  (Task 2.7,   │
│  placeholder  │   ◀  step 2/3  ▶                          │  collapsible) │
│  for now)     │                                           │               │
│               ├───────────────────────────────────────────┤               │
│               │   Slide strip: thumbnails (Task 1.11)     │               │
└───────────────┴───────────────────────────────────────────┴───────────────┘
```

- The preview renders with `Deck(mode = DeckMode.Presenter, …)` inside a 16:9 box, or directly with `SlideConfig.Render()` if the deck chrome gets in the way. Try `Deck` first since it brings frames and background for free.
- Clicking a thumbnail selects that slide (sets `selectedSlideId`); the chat will use the selection as context ("make this slide shorter").
- Arrow keys in the preview move through reveal steps, reusing `DeckNavigator.next()/previous()`.
- No drag-and-drop on slides, no inline text editing (dropping files on the Assets panel is fine, see 2.7). The product is chat-only (see README, decision 2). Reordering thumbnails by drag can come later as a `MoveSlide` op if users ask.

## Task 2.4: Present

- The Present button opens the two windows the way `ski`'s `desktopApp/.../main.kt` does now: an audience window (`DeckMode.Presenter`, fullscreen) and a presenter window (`DeckMode.Local`), sharing one `DeckNavigator`.
- Put the audience window on the second display when there is one: use `GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices`, then set the window's position to that screen's bounds before going fullscreen. Fall back to the main display.
- While presenting, the document is **read-only**. `DeckEditor` rejects commits and the chat shows "Presenting. Edits are paused." This avoids slides moving under the presenter mid-talk.
- Esc in the presenter window ends the presentation and closes both windows.
- This is the action gated by the `present` entitlement in Phase 5. Build it with a `canPresent: Boolean` input from day one so the paywall slots in without restructuring.

## Task 2.5: settings screen

Sections (most are filled in by later phases):
- **AI provider** (03): provider, model, API key status.
- **Remote** (04): pairing QR, paired devices, port.
- **Skills** (03, Task 3.8): built-in and user skills, on/off, add from folder or zip.
- **Publishing** (Task 2.8): connected Vercel account/token, default team, publish history.
- **Component packs** (Task 2.9): loaded packs, their components, versions, reload/remove.
- **Account & purchases** (05).
- **Appearance**: light/dark/system.

Store non-secret settings in `settings.json`; secrets go in the keychain (03, Task 3.2).

## Task 2.6: first-run experience

- A welcome window with "New deck", "Open…", and "Open the sample deck" (bundled `demo.ski`).
- New deck = a one-slide document with a `title` element, so `DeckNavigator`'s non-empty rule always holds.

## Task 2.7: Assets side panel

A collapsible panel on the right of the preview (toolbar button and View → Toggle assets) where users upload and manage images, GIFs and videos for the deck. It is the only non-chat editing surface, and it manages files only. Putting an asset on a slide still goes through the chat.

```
┌──────────────┬──────────────────────────────────┬──────────────┐
│ Chat         │ Preview                          │ Assets   [+] │
│              │                                  │ 🔍 filter    │
│              │                                  │ ┌───┐ ┌───┐  │
│              │                                  │ │img│ │gif│  │
│              │                                  │ └───┘ └───┘  │
│              ├──────────────────────────────────┤ ┌───┐        │
│              │ Slide strip                      │ │ ▶ │ video  │
└──────────────┴──────────────────────────────────┴──────────────┘
```

**Adding assets**
- The `+` button opens a native file picker (`java.awt.FileDialog` for the native look on macOS; filter by supported types).
- Drag and drop files from Finder/Explorer onto the panel or the chat box (Compose Desktop `Modifier.dragAndDropTarget`).
- Paste an image from the clipboard (Cmd/Ctrl+V while the panel is focused).
- Supported: PNG, JPEG, WebP, SVG, GIF, MP4, WebM, MOV. Anything else gets an inline "Ski can't use .xyz files" message.

**Import pipeline** (`ke.don.studio.assets.AssetImporter`, runs off the main thread with a progress row in the panel):
1. Hash the file (`sha256`). If the deck already has that hash, reuse the existing asset and say "Already in this deck".
2. Read metadata: dimensions for images, frame count and duration for GIFs, dimensions and duration for videos.
3. Size limits: warn above 20 MB for images/GIFs and 200 MB for videos, since they make the `.ski` file heavy to share and slow to send to the phone. Offer "Import anyway".
4. Videos: grab a poster frame (first frame via the media player, saved as a PNG asset linked by `posterAssetId`). Optionally convert MOV to MP4 later; v1 keeps the original.
5. Copy to `assets/<id>.<ext>` in the working folder and record the `Asset` with a single op, `AddAsset(asset)`. Add `AddAsset`, `UpdateAsset` (rename, description) and `RemoveAsset` to `DeckOp` in Task 1.6 so asset changes are undoable like everything else.

**Grid and item actions**
- Thumbnails in a grid, with a type badge (GIF, video duration), filter by type, and search by name/description.
- Hover a GIF to play it; video items show the poster with a play-preview on hover.
- Select an item to open a detail sheet: big preview, name (editable), description/alt text (editable; the AI uses it to pick assets), size, dimensions, and "Used on slides 2, 5" with links.
- **Use in chat:** inserts a mention chip (`@hero-photo`) into the chat input so the user can write "put @hero-photo on the intro slide". The chip resolves to the `assetId` for the AI.
- **Delete:** blocked with a list of slides when the asset is in use ("Remove it from slides 2 and 5 first, or ask the chat to"). Unused assets delete through `RemoveAsset` (undoable). The file stays in the working folder until save, so undo works.
- On save, unreferenced files in `assets/` that no `Asset` points to are dropped from the zip.

**Description suggestions (optional, Phase 3):** when an AI provider with vision is set up, offer "Describe with AI" to fill in alt text for images, which also helps the AI choose assets.

## Task 2.8: Publish to web (Vercel)

Decks made in the studio must stay portable: anyone with a browser can view them, and the presenter can present from any machine. The studio publishes a deck as a static website built on `ski`'s web player (01, Task 1.13), then deploys it to Vercel with one button.

**Export (no account needed):** File → Export → Website… writes a folder (or zip) with:
```
index.html, *.wasm, *.js        prebuilt ski-web-player bundle (version matched to the engine the studio uses)
deck.json                       the document
assets/                         images, GIFs, videos the deck uses (unused assets are left out)
vercel.json                     rewrite all paths to index.html, so /?slides works (same rule as ski's webApp)
```
The player bundle is downloaded from the `ski` GitHub release at **build time** and packed into the studio's resources (Gradle task in `:studio`). Exporting then works offline and always matches the engine version.

The folder works on any static host (Netlify, GitHub Pages, Cloudflare Pages, S3). Say so in the export dialog.

**Deploy to Vercel (toolbar button "Publish"):**
1. First time: connect Vercel. v1: the user pastes a Vercel access token (link to the token page), stored in the keychain like AI keys (03, Task 3.2). Later: "Sign in with Vercel" through a Vercel integration (OAuth), which avoids copy-pasting tokens.
2. Dialog: project name (default: slug of the deck title, remembered per deck in `<appdata>/settings.json`), team (if the token has several), Production or Preview, "Include speaker notes" (on by default because the web presenter popup needs them; turn off for an audience-only link).
3. The studio builds the export folder in a temp dir and deploys with the Vercel REST API, not the CLI, so users don't need Node:
   - upload each file (`POST /v2/files` with its SHA-1 digest; Vercel skips files it already has, so re-deploys only send changed assets);
   - create the deployment (`POST /v13/deployments`) listing files by SHA, `projectSettings.framework = null`, and `target = production` when chosen;
   - poll the deployment until `READY` or `ERROR`, showing progress.
   Check the current endpoint versions and payloads in Vercel's REST API docs before building. Pass the project name explicitly every time, for the same reason `ski`'s README passes `--project ski-slides`: there's no local `.vercel` link to rely on.
4. Show the URL with Copy and Open buttons, plus a QR code so the audience can follow along on their phones.
5. Keep a per-deck publish history (URL, date, target), and offer "Update the published deck" on later publishes.

**Web presenting:** the published site uses the web player's existing popup flow: the main tab is the presenter view and `/?slides` is the audience window, synced through `localStorage`. The phone remote (04) doesn't connect to web decks in v1.

**Decks with your own components:** the prebuilt player only has Ski's built-in components. When a deck uses components from a pack, the Publish dialog says which ones and offers two choices: "Use a custom player folder" (built in the user's project with `./gradlew skiWebPlayer`, [engine 02](../engine/02-custom-components.md), Task 1b.6; the studio remembers the folder per deck and can run the project's own `gradlew` for the user when a project folder is attached), or "Publish anyway" with placeholders for those components.

**Gating (open question, see README):** recommended free, since it's how decks get shared and spread the word. Optionally show a small "Made with Ski" badge that `present`/Pro users can turn off.

Put the code in `ke.don.studio.publish` with an interface `DeckPublisher { suspend fun publish(export: ExportedSite, options): Flow<PublishProgress> }`, so Netlify or GitHub Pages can be added later without touching the UI.

## Task 2.9: component packs (your own composables)

The packaged studio has no compiler, so it gets user components already compiled, as component packs built by the Ski Gradle plugin in the user's project ([engine 02](../engine/02-custom-components.md), Task 1b.5).

- **Loading:** drag a `.skipack` onto the window, use File → Load Component Pack…, or run `./gradlew skiStudio` in the project, which opens `ski://open-pack?path=…`. Packs are referenced by path (not copied), remembered in `<appdata>/packs.json`, and listed in Settings → Component packs.
- **Tell the plugin what this studio is:** write `~/.ski/studio.json` (version, install path, engine/Compose/Kotlin versions, supported pack formats) on every launch, and answer `ski://info`. The Ski Gradle plugin uses it to check versions at build time and to find the app ([engine 03](../engine/03-third-party.md), Tasks T.1–T.3).
- **Single instance:** a `ski://` URL or `.skipack`/`.ski` file opened while the studio is running goes to the running instance (on macOS through `Desktop.setOpenURIHandler`/`setOpenFileHandler`; on Windows and Linux through a local socket handshake on launch).
- **Isolation:** one child classloader per pack, with the studio's classloader (Compose, Ski engine) as parent. Find providers with `ServiceLoader<SkiComponentProvider>`.
- **Compatibility:** read `pack.json` first. Refuse packs built against a newer engine or a different Compose minor version, and show the exact fix (rebuild with ski x.y, or update the studio).
- **Duplicate IDs** across packs, or with Ski's built-ins: the first loaded wins. Show a warning naming both packs.
- **Live reload:** watch each pack file. When it changes (from `./gradlew skiStudio -t`), dispose compositions that use the old classloader, load the new one, and re-render. The deck, selection and chat stay. Show a small "Reloaded rating-bar, 2 components" toast.
- **Errors inside a component** (exceptions while composing): catch them at the element boundary and show an error card with the stack trace's first lines. One broken component must not take down the preview or a presentation.
- **Decks remember which packs they need:** `deck.json` gets `requiredComponents: [{ id, pack }]`, written by the studio on save. Opening a deck without a pack shows placeholders and a banner ("This deck uses 3 components from `my-ui-kit`. Load the pack").
- **Presenting** works with packs exactly like built-ins: they're in the same JVM.
- **Gating:** open question in the README. Recommended: packs work in the free tier for editing and preview; presenting stays gated by `present` as usual.

## Risks

- **Two `DeckNavigator`s.** The preview and the presentation each need one. Use a separate navigator for the preview so presenting starts from slide 1 (or from the selected slide, which is a nice touch) without disturbing the editor view.
- **Fullscreen on macOS** creates a new Space. Test with two monitors and with mirroring on.
