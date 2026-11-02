# Using Ski without access to the repos

Most people using Ski will never have push access to `ski` or `ski-studio`. This plan makes sure everything they need works from **published artifacts, installed apps and files**, with their own project as the only repo they change. It lists the contracts that make that safe and the work still missing.

Lives in **`ski`**, with the studio-side pieces cross-referenced in [`docs/studio/`](../studio/README.md).

Done when (milestone M1c): someone with no repo access goes from zero to their own `@SkiComponent` on a slide, in the studio and on a Vercel URL, following only the public docs. Test this with a person who hasn't seen the code, or failing that a clean machine and a new GitHub account.

## What a third party gets

| Channel | What | Where it's planned |
|---|---|---|
| Maven Central | `ski-bom`, `ski`, `ski-domain`, `ski-engine-model/ops/renderer/export`, `ski-annotations`, `ski-ksp` | [README](README.md), Task 0 |
| Gradle Plugin Portal | `io.github.donald-okara.ski` (`skiPreview`, `skiPack`, `skiStudio`, `skiWebPlayer`, `skiDoctor`) | [02](02-custom-components.md), Task T.3 below |
| GitHub releases (`ski`) | `ski-web-player-<version>.zip` | [01](01-engine.md), Task 1.13 |
| GitHub template | Code-first starter project | Task T.4 below |
| Download site / stores | Ski Studio (desktop), Ski Remote (phone) | studio [06](../studio/06-packaging.md) |
| Files they create and share | `.ski` decks, `.skipack` component packs, skill folders/zips, published web folders | 01, 02, studio 03 |

## The four ways people use Ski

1. **Code-first.** Click "Use this template" (a new repo they own, not a fork), or add `ski-bom` + `ski` to an existing project. They get updates by bumping a version, never by merging your commits.
2. **Studio only, no code.** Install the app, bring an AI key, build by chatting, publish to their own Vercel account. No repo involved.
3. **Their own components in decks.** Apply the Gradle plugin in their project, annotate composables, then use `skiPreview` (free, hot reload), `skiStudio` (studio + live pack reload) and `skiWebPlayer` (web with their components).
4. **Sharing and contributing.** Share decks, packs and skills as files or through their own repos. Contribute to Ski itself through issues and PRs from a fork. Extension points (components, packs, skills) mean people rarely need to change Ski itself.

## Public contracts

These are what a third party depends on. Each one is versioned, documented and protected by a check. The full list goes in a `CONTRACTS.md` at the root of `ski`, linked from the README.

| Contract | Version marker | Protected by | Breaking change process |
|---|---|---|---|
| `deck.json` schema | `schemaVersion` | migration tests over golden files (01, Task 1.5) | new migration; never edit an old one |
| `DeckOp` / AI tool names | schema version | tool schema snapshot tests (studio 03, Task 3.7) | add a new op; deprecate the old one for one minor release |
| Kotlin API of `ski-*` artifacts | semantic version | ABI dumps (README) | only in a minor version while `0.x`, a major version after 1.0; noted in the changelog |
| `@SkiComponent` annotation + generated code shape | `ski-annotations` version | KSP compile tests (02, Task 1b.2) | as for the Kotlin API |
| `.skipack` format | `packFormat` in `pack.json` | load test in the studio against packs built by older plugin versions | the studio reads the last two pack formats |
| Ski/Compose versions a studio accepts | `~/.ski/studio.json` + compatibility table (Task T.1) | `skiDoctor` and the build-time check (Task T.2) | studio release notes |
| `ski://` URL scheme | path + `v=` parameter | plugin ↔ studio integration test | add parameters only; never change meanings |
| Skill format | Agent Skills `SKILL.md` | parser tests (studio 03, Task 3.8) | no Ski-only front matter fields that other tools would reject |
| Web player bundle layout (`deck.json`, `assets/`, `vercel.json`) | player version | export test (studio 02, Task 2.8) | as for the Kotlin API |

## Task T.1: the studio says what it is (studio side)

On every launch, Ski Studio writes `~/.ski/studio.json` (on Windows, `%USERPROFILE%\.ski\studio.json`):

```json
{
  "studioVersion": "1.2.0",
  "installPath": "/Applications/Ski Studio.app",
  "launcher": "/Applications/Ski Studio.app/Contents/MacOS/Ski Studio",
  "engine": { "ski": "0.4.2", "compose": "1.12.0", "kotlin": "2.4.20", "packFormats": [1, 2] },
  "updatedAt": "2026-11-02T10:00:00Z"
}
```

- Multiple installs (e.g. a beta channel) write `installs: [...]` instead, newest first.
- The file is written by the app only. It holds no secrets and is safe for any local process to read.
- The studio also exposes the same data over `ski://info` for tools that prefer asking the running app.

Add this to studio [02](../studio/02-studio-shell.md), Task 2.9.

## Task T.2: the plugin checks versions at build time (ski side)

The main failure for outsiders is a pack built against a different Ski or Compose version than their installed studio. Today they'd only find out when the studio refuses the pack. Catch it at build time instead:

- `skiPack` and `skiStudio` read `studio.json` and compare the project's resolved `ski-*` and Compose versions with the studio's:
  - **Compose minor differs** → fail with: "Ski Studio 1.2.0 uses Compose 1.12.x; this project resolves 1.11.0. Set Compose to 1.12.x, or update Ski Studio."
  - **The project's `ski` is newer than the studio's engine** → fail with: "Update Ski Studio (it supports ski ≤ 0.4.x)."
  - **The project's `ski` is older but compatible** → warning only.
- No `studio.json` (studio not installed) → `skiPack` still builds, with a warning. `skiStudio` fails with a download link.
- Override for CI and odd setups: `ski { studio { path = file(...); skipVersionCheck = false } }`.
- `./gradlew skiDoctor` prints:
  - the project's versions;
  - the installed studio and its versions;
  - whether a JVM target exists for preview;
  - whether every `@SkiComponent` also compiles for wasmJs;
  - a one-line fix for each problem.
- Publish a **compatibility table** in the docs and in each studio release's notes: studio version → supported `ski` range, Compose version, pack formats. Generate it from the studio's build, so it never drifts.

## Task T.3: the plugin finds the studio (ski side)

`skiStudio` opens the studio with `ski://open-pack?path=…&v=1`. To do that it needs to know the app is installed and how to launch it:
1. `ski { studio { path = … } }` if set.
2. Otherwise `launcher` from `studio.json`.
3. Otherwise ask the OS to open the `ski://` URL (`open` on macOS, `start` on Windows, `xdg-open` on Linux), which works whenever the installer registered the scheme.
4. Otherwise fail with the download link.

If the studio is already running, the URL goes to the open instance (single-instance handling in the studio). It doesn't start a second copy.

## Task T.4: the template

`donald-okara/ski` is already marked as a GitHub template. The problem is what it carries: the full engine, components and gallery source, with `use_local_shared_components=true`. People who start from it compile Ski from source and can't easily take updates.

Options:
- **A. A separate slim template repo, `ski-template` (recommended).** It contains only:
  - `:app` (desktop + web entry points) and one `segments` module;
  - the Ski Gradle plugin;
  - a sample `@SkiComponent` and a sample `deck.json`;
  - `ski-bom` pinned to the latest release;
  - a `CLAUDE.md`/`context.md` for AI-assisted authoring.

  Updates are a version bump. Mark it as the template and turn the template flag off on `ski`.
- **B. Keep `ski` as the template** but default `use_local_shared_components=false`, so a copy builds against published artifacts. It still carries a lot of source users don't need.

Whichever you pick:
- Keep the template building in CI against the latest release, so it never ships broken.
- Add a "Deploy to Vercel" section matching the README's existing steps, using `skiWebPlayer` output.

## Task T.5: third-party component libraries

Teams will publish their own `@SkiComponent` libraries (a design system, a chart kit) to Maven. Someone else should be able to use them in decks without owning the source:

- The processor runs in the library's own build (Task 1b.2), so its JAR already contains the generated specs and the `META-INF/services` entry. Consumers just add the dependency.
- `skiPack` gets `ski { packDependencies { include("com.acme:acme-charts") } }`, which puts that library's classes (minus Ski/Compose/kotlinx) into the pack alongside the project's own. A project with no components of its own can then build a pack purely from dependencies.
- `skiPreview` and `skiWebPlayer` pick up providers from any dependency automatically. On wasm this works through the plugin's generated `skiComponents()` aggregation, which scans the dependency graph for libraries that declare providers. The processor writes a small marker resource the plugin can find.
- Component IDs should be namespaced to avoid clashes: recommend `acme/chart-bar` style IDs for libraries, and warn when a published library uses an un-namespaced ID.
- Document a "Publishing a component library for Ski" guide: apply `ski-annotations` + `ski-ksp`, namespace IDs, add `@SkiExample`s, target `jvm` + `wasmJs` (+ android/ios if wanted).

## Task T.6: docs for people who aren't you

Today's docs (`README.md`, `CLAUDE.md`, these plans) assume a maintainer with the repo checked out. Add a docs site, or a `docs/guide/` folder published with the web app on Vercel, with:

1. **Getting started: code-first** (template → first slide → present → deploy).
2. **Getting started: Ski Studio** (install → AI key → first deck → publish).
3. **Show your own components** (plugin → `@SkiComponent` → `skiPreview` → `skiStudio` → `skiWebPlayer`).
4. **Publishing a component library for Ski** (Task T.5).
5. **Writing skills for Ski** (format, built-in skill examples, how the AI loads them).
6. **Compatibility table** (Task T.2) and **CONTRACTS.md**.
7. **Troubleshooting**, organised by error message, starting with everything `skiDoctor` can print.

Each guide gets tested by following it word for word on a clean machine before a release that changes it.

## Task T.7: support channels

- GitHub issue templates in `ski`: bug (asks for `skiDoctor` output), component/engine feature request, docs problem.
- Studio bugs: an in-app "Report a problem" that collects studio version, OS and `studio.json`, and **no deck content or keys** unless the user attaches them. Route these to a public `ski-studio-issues` repo (or a form) so people can report studio bugs without access to a private `ski-studio`.
- `CONTRIBUTING.md` in `ski` explaining fork + PR, the public contracts above, and running the engine tests.

## Order of work

- **With Phase 1b (02):** T.2 and T.3 (version checks and studio discovery), since they're part of the plugin, plus `skiDoctor`.
- **With studio Phase 2:** T.1 (`studio.json`).
- **Before the first public beta:** T.4 (template), T.6 (at least guides 1–3 and the compatibility table) and T.7.
- **After launch, when the first outside component library appears:** T.5.
