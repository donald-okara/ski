# Phase 3: chat with the user's own AI

Goal: the chat pane is the only way to edit a deck. The user picks a provider, pastes a key (or points at a local model), and the AI changes the deck by calling tools that map 1:1 to `:engine:ops`. The same tools are also available over MCP, so people can drive Ski from Claude Desktop, Claude Code or Cursor without giving Ski a key.

Done when (milestone M3): on an empty deck, "Make me a 5-slide talk about Compose state hoisting, with a code example and speaker notes" produces a valid deck you can present, and one Undo removes the whole turn.

## Modules

- **`:ai:agent`** (JVM, package `ke.don.ai.agent`): provider config, tool definitions, the chat loop, the system prompt. Depends on `:engine:model`, `:engine:ops`. No Compose.
- **`:ai:mcp`** (JVM, package `ke.don.ai.mcp`): MCP server exposing the same tools. Depends on `:ai:agent` for the tool definitions.
- Chat UI lives in `:studio` (`ke.don.studio.chat`).

## Task 3.1: pick the client library

Use **Koog** (JetBrains' Kotlin agent framework, `ai.koog:koog-agents`). It has executors for OpenAI, Anthropic, Google, OpenRouter and Ollama, typed tool definitions and streaming. Check the latest stable version and its Kotlin requirement against ours (`kotlin = 2.4.20` in `libs.versions.toml`) before adding it.

Fallback if Koog gets in the way: write a thin `ChatProvider` interface over Ktor client with one implementation per API (Anthropic Messages, OpenAI Chat Completions/Responses, Gemini, plus "OpenAI-compatible" with a custom base URL, which covers Ollama, LM Studio, OpenRouter and Groq). Either way, keep the rest of the app behind our own interface:

```kotlin
interface ChatProvider {
    val id: ProviderId
    suspend fun listModels(): List<ModelInfo>
    fun stream(request: ChatRequest): Flow<ChatEvent>   // TextDelta, ToolCall, Done, Error
}
```

so we can switch libraries without touching the UI.

## Task 3.2: provider settings and key storage

- Supported at launch: **Anthropic, OpenAI, Google Gemini, OpenRouter, Ollama (local), Custom OpenAI-compatible URL.**
- Settings → AI provider: provider picker, model picker (filled from `listModels()`, with a free-text fallback), key field, "Test connection" button that sends a tiny request and reports success or the provider's error text.
- **Keys go in the OS keychain**, never in `settings.json` or logs. Use `java-keyring` (macOS Keychain, Windows Credential Manager, Linux Secret Service) behind a `SecretStore` interface. On Linux without Secret Service, refuse to save and explain why, rather than writing plain text.
- Don't hardcode a model list. Suggest a default per provider (the provider's current recommended general model) but always show what `listModels()` returns.
- Show a clear note: "Your deck content is sent to <provider> when you chat. Ski never sees it."

## Task 3.3: tools

Generate tool definitions from `DeckOp`, so the two can't drift. Each tool's JSON schema comes from the op's serializer (kotlinx.serialization descriptors → JSON Schema; write a small generator in `:ai:agent` and snapshot-test its output).

Write tools (one per `DeckOp`):
`add_slide`, `remove_slide`, `move_slide`, `update_slide`, `add_element`, `update_element`, `remove_element`, `set_theme`, `set_title`.

Read tools (no ops, return JSON):
- `get_deck_outline`: titles, slide IDs, element IDs and types, no full content. Cheap; the model calls this first.
- `get_slide(slideId)`: the full slide JSON.
- `get_selection`: the slide the user selected in the strip, if any.
- `list_components`: every component in the current `ComponentRegistry`: Ski's built-ins plus the user's own from loaded component packs ([engine 02](../engine/02-custom-components.md), 02 Task 2.9). Returns `id`, `name`, `description`, `steps` and the source (built-in or pack name).
- `get_component(id)`: full parameter schema and `@SkiExample`s, so the model fills `params` correctly.
- `list_assets`: assets from the Assets panel (02, Task 2.7) with `id`, `kind` (image/gif/video), `name`, `description`, dimensions and duration. The model places them with `image`/`video` elements by `assetId`. It can't upload or delete files; asset add/remove happens only in the panel. `update_asset` (rename, description) is allowed so the AI can fill in alt text.

Rules for tool execution:
- Each tool call is validated and applied to a **staging copy** of the document during the turn. Validation errors go back to the model as the tool result (text it can act on, e.g. `"revealAt values on slide s3 skip from 1 to 3"`), so it can fix them itself.
- When the turn ends, everything staged is committed as one `DeckTransaction` labelled with a short summary. One Undo reverts the whole turn.
- The preview updates live from the staging copy while the model works, so the user sees slides appear.
- Cap tool calls per turn (e.g. 40) and turn time to stop runaway loops. Show "Stopped after 40 changes" if hit.
- IDs: the model may propose IDs; the server replaces duplicates or missing ones and returns the final IDs in the tool result.

## Task 3.4: system prompt

Put it in `ai/agent/src/jvmMain/resources/prompts/system.md`, versioned with the app. Sources:
- `context.md` rules that still apply to data decks: reveals instead of duplicate slides, detailed speaker notes, `code` elements for code, respecting the session length.
- The element catalog: one line per element type with when to use it (generated from the schema plus hand-written hints, so new elements show up automatically).
- Style rules: one idea per slide, ≤ 6 bullets, titles ≤ 8 words, notes written as what the speaker will say.
- Tool workflow: call `get_deck_outline` first; prefer `update_element` over remove+add; group related changes in one turn.
- The current selection and deck outline are added to each request as context, not to the system prompt, so caching the system prompt stays effective (Anthropic prompt caching, OpenAI automatic caching).

## Task 3.5: chat UI (`:studio`)

- Message list with streaming text. Each tool call shows as a compact chip: "Added slide *State hoisting*", "Updated code on slide 3". Click a chip to select that slide in the preview.
- At the end of each AI turn: a summary row with **Undo this turn**.
- Input box with: Send (Enter), new line (Shift+Enter), Stop (cancels the stream and rolls back the staged copy), and `@` mentions for assets from the Assets panel (02, Task 2.7). Files dropped on the chat box go through the same import pipeline as the panel and then appear as mention chips. A mention resolves to the `assetId`. Send image pixels (or a video's poster frame) to the model only for providers with vision support; otherwise send name + description.
- Chat history saved per deck in `<appdata>/chats/<deck-id>.json`, not inside the `.ski` file, so shared decks don't leak private chats.
- Errors from the provider (bad key, rate limit, context too long) show inline with a fix ("Open AI settings").
- Context length: when the conversation gets long, drop old turns and keep the deck outline fresh. The deck itself is always the source of truth, not the chat.

## Task 3.6: MCP server (`:ai:mcp`)

Use the official Kotlin MCP SDK (`io.modelcontextprotocol:kotlin-sdk`). Check the latest version first.

- Transport: Streamable HTTP on `127.0.0.1` only, on a fixed default port (pick one, e.g. 47821, configurable). Requests need a bearer token generated at first run and shown in Settings → AI provider → "Use from another AI app".
- Also ship a tiny **stdio shim** (`ski-mcp`, a launcher bundled in the app) for clients that only do stdio. It forwards stdio to the local HTTP server.
- Tools: the same read/write tools as 3.3, acting on the **frontmost open document**, plus `list_open_decks` and `focus_deck(id)`.
- Each MCP write is its own transaction (no turn boundary exists), but group calls that arrive within ~2 s into one undo step so a burst of edits from Claude Code undoes together.
- Settings shows copy-paste config snippets for Claude Desktop, Claude Code (`claude mcp add --transport http ski http://127.0.0.1:47821/mcp --header "Authorization: Bearer …"`) and Cursor.
- The MCP server is off by default; one toggle in settings turns it on.

## Task 3.7: tests

- Tool schema snapshot tests (catch accidental breaking changes to tool names/fields).
- A fake `ChatProvider` that replays scripted tool calls, to test the staging → commit → undo loop without network.
- A manual "eval" checklist of 10 prompts (empty deck, edit selected slide, add code, reorder, change theme, fix a validation error…) run against each provider before a release. Keep the prompts in `ai/agent/src/jvmTest/resources/evals/`.

## Task 3.8: skills (and reading real code)

Ski exists to demo code, so the AI needs to be good at code slides, and users need to teach it their own way of presenting. Skills are packaged instructions the AI loads when relevant. Use the **Agent Skills format** (a folder with a `SKILL.md` that has `name` and `description` front matter, plus optional reference files). Skills written for Claude Code or Claude.ai then work in Ski, and the reverse.

**Where skills come from**
0. **The base skill, `ski-slides`**, from the `ski` repo (`skills/ski-slides/`, see [engine 03](../engine/03-third-party.md), Task T.8). It's bundled at the version matching the engine, always on, and holds the general rules for good Ski slides. The built-in skills below add specific patterns on top of it and don't repeat its rules.
1. **Built-in** (shipped in `:ai:agent` resources, updated with the app):
   - `code-walkthrough`: one `code` element revealed with `focusSteps`, a few lines per Next press, with notes explaining each step (needs 01, Task 1.9b).
   - `before-after`: `split` layout with the old and new code side by side, the diff explained in notes, optional `commit` tags.
   - `live-demo`: code next to a `device` frame showing a screenshot, GIF or video from the Assets panel.
   - `api-tour`: one slide per function/class from an attached project, with signatures and a usage snippet.
   - `talk-structure`: hook → problem → solution → demo → recap, sized to `sessionMinutes`.
   - `component-showcase`: for the user's own `@SkiComponent`s. Shows the component live in its example states (one reveal step each, or side by side), optionally inside a `device` frame, next to the component's source code read from the attached project (found through the spec's `fqName`). This is the skill that does Ski's original job: "show my component now".
2. **User skills:** Settings → Skills → "Add skill" from a folder or `.zip`, copied to `<appdata>/skills/<name>/`. Toggle each on or off, and see its description and source.
3. **Deck skills (optional):** a deck can carry skills in `skills/` inside the `.ski` file so a team's house style travels with the deck. Off by default when opening someone else's deck; ask first ("This deck includes 2 skills. Enable them?").

Later: install from a Git URL or a curated list.

**How the AI uses them** (progressive loading, so the prompt stays small):
- The system prompt lists each enabled skill's `name` + `description` only.
- Tool `load_skill(name)` returns the `SKILL.md` body; `read_skill_file(name, path)` returns a reference file (templates, example `deck.json` snippets).
- Skills are instructions and examples only. **Scripts inside skills are not run** in v1. Show a note on install if a skill contains scripts.
- In the chat, show "Using skill: code-walkthrough" as a chip, and let the user force one with `/code-walkthrough make slides for this file`.
- Over MCP (Task 3.6), expose enabled skills as **MCP prompts**, so Claude Desktop/Code users get the same behaviour.

**Attach a project (read-only code source)**

Demo decks are better when the code comes from the real project. The chat has an "Attach project folder" button (also per deck in settings):
- Tools: `list_project_files(glob?)`, `read_project_file(path, fromLine?, toLine?)`, `search_project(query)` (plain text search, capped results).
- Scope: only inside attached folders; respect `.gitignore`; never read files matching a deny list (`.env*`, `*.keystore`, `*.jks`, `local.properties`, `*.pem`, `id_*`, `google-services.json`, `GoogleService-Info.plist`). Cap file size and lines per call.
- Code added from a file gets `source = CodeSource(path, lines, commit)` (git HEAD if it's a repo), and the slide strip offers **Refresh code from project** when the file changed since. That's the main reason to record `source`.
- Attached folders are remembered per deck in `<appdata>`, never inside the `.ski` file (paths are personal).
- Tell the user plainly that file contents they let the AI read are sent to their AI provider.

Code: `ke.don.ai.agent.skills` (`SkillRegistry`, `SkillLoader` for front matter and file access) and `ke.don.ai.agent.project` (`ProjectSource`, deny-list matcher). UI in `ke.don.studio.skills`.

Tests: skill parsing (valid, missing front matter, duplicate names); deny-list and path traversal (`../`, symlinks out of the folder) for project tools; the fake provider loading a skill and producing a valid walkthrough slide.

## Risks

- **Weaker models break schemas.** Local models via Ollama often send malformed tool calls. Validation + error-as-tool-result handles most of it; also show "This model struggled with your request. Try a stronger model" after repeated failures.
- **Cost surprises.** Show token usage per turn when the provider returns it.
- **Prompt injection via pasted content, project files or third-party skills.** The AI can only change the deck through tools, and every turn is undoable, which keeps the blast radius small. The only file access is read-only and limited to folders the user attached (Task 3.8). Don't add tools that write files, run code or reach the network.
