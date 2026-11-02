# Principles: what a good Ski deck looks like

These apply to every Ski deck, code-first or data. The other references explain how to do each one in a specific format.

## Structure of a talk

A technical talk built with Ski usually follows this arc. Adjust the counts to the session length.

| Part | Slides | Purpose |
|---|---|---|
| Hook | 1 | Title plus a promise: what the audience will be able to do afterwards. |
| Problem | 1–3 | The pain, shown concretely: a bug, a slow screen, ugly code. |
| Idea | 1–2 | The concept in one sentence, then one diagram or analogy. |
| Demo / walkthrough | 3–8 | Code and the live result, built up in steps. This is the heart of a Ski talk. |
| Trade-offs | 1–2 | When not to use it, what it costs, what the numbers don't show. |
| Recap | 1 | Three takeaways, matching the promise in the hook. |
| Questions | 1 | Links (repo, gallery, slides URL), no new content. |

Timing: aim for 1–2 minutes per slide; demo slides with several steps can take 3–4 minutes. A 45-minute session is about 20–30 slides. If the deck is far over, cut slides rather than speeding up.

## One idea per slide

- The **label** is short (2–4 words) because it appears in the table of contents and header.
- The **title** on the slide states the idea or claim ("State hoisting makes screens testable"), not just a topic ("State hoisting").
- **Body** text: at most 5–6 bullets, each under about 12 words. If you have more, split the slide or reveal in steps.
- Prefer one strong visual (live composable, code, chart, device frame) over a wall of bullets.

## Reveals and steps

Ski has a step system: each slide declares a number of steps, and the presenter's Next key moves through those steps before moving to the next slide (Previous goes back the same way). The current step is shared between the presenter and audience windows, so both always show the same thing.

Use steps for:
- bullets that should appear one at a time;
- walking through code a few lines at a time;
- building a diagram or comparison piece by piece;
- "before → after" on the same slide.

Rules:
- Step counts must match the items: if a slide reveals 4 bullets, it has 4 steps.
- Hidden items keep their space, so the layout doesn't jump. Don't fight that with conditional layout.
- Don't use local state (`remember { mutableStateOf(...) }`, timers, `LaunchedEffect` counters) for presenter-paced reveals. Local state lives in one window only, so the audience window would show something different from the presenter view. Local state is fine for things the presenter interacts with *inside* a demo (a button on a live composable, a text field, a drag), because that interaction is the demo.
- Entrance animations that play once when a slide appears (titles sliding in, charts growing) are fine; Ski's components already do this.

## Code on slides

- Always use Ski's code viewer (`KotlinCodeViewer` / `KotlinCodeViewerCard`, or a `code` element), never plain text.
- Show the smallest snippet that makes the point. Remove imports, logging and unrelated parameters. Fold lambdas you're not talking about (the viewer can do this).
- About 15 lines maximum on screen at a readable size. For longer code, walk through it in steps or split it across slides.
- Pair code with its result: code on one side, the live composable (or device frame, or chart) on the other. Ski's `DemoLayout` and split layouts exist for this.
- Code shown should compile. If it's simplified pseudo-code, say so in the notes.
- Prefer the user's real code (from their project) over invented examples when it's available.

## Showcasing composables

The thing Ski does best: putting a real component on a slide.
- Render the actual composable, not a screenshot. Mobile UI goes in a device frame (Pixel, Galaxy, iPhone, foldable are available).
- Show states: empty, loading, error, full, or the before/after of a change. Use steps or a side-by-side layout rather than separate slides.
- When the component is interactive, say in the notes what to tap or type during the talk.
- Keep the code that builds it visible on the same slide, or on the slide right before it.

## Speaker notes

Notes are what the presenter reads on stage. They appear in the presenter panel only, and each line also becomes a flashcard (one card per line).
- 2–4 lines per slide, each a full spoken sentence with one idea.
- A typical shape: (1) transition from the previous slide or what this slide is for, (2) the main point, (3) a cue or takeaway ("Takeaway to return to: ...").
- Put exact numbers, names and claims in the notes, so the presenter doesn't need to remember them.
- Don't repeat the on-screen title. Say what the slide doesn't say.
- Any number on screen (counter, timer, benchmark) gets a note saying what it does not measure, and under which conditions it's true.
- Interactive demos get a note saying what to do ("Tap Increment three times, then rotate the device").
- If notes aren't final, use an obvious placeholder so it's caught during a dry run, never plausible-sounding filler.

## Visual consistency

- Use the deck's theme, frames and background. Don't set colours per slide unless the slide is about colour.
- Use Ski's text blocks (slide title, bullet list, callout card, commit tag) so typography stays consistent.
- One accent per slide; use callouts for the single most important line.
- Transitions: Horizontal is the default. Use Fade for title and section slides, Vertical sparingly for "going deeper", None for slides that should feel like the same slide changing.

## Review checklist

- [ ] The hook's promise is answered by the recap.
- [ ] Every slide: short label, claim-style title, notes with 2–4 spoken lines.
- [ ] No duplicated slides for reveals; step counts match items.
- [ ] Code: viewer component, short, compiles (or marked as pseudo-code), paired with its result.
- [ ] Metrics have "what this doesn't measure" notes.
- [ ] Slide count fits the session length.
- [ ] Stepped through once in the presenter view.
