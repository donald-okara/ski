# Writing speaker notes

Speaker notes are the lines you read from while you present. They appear in the presenter panel (`DeckMode.Local`), under the slide's label. The audience window never shows them.

## Where notes live

- One named constant per slide, in `composeApp/src/commonMain/kotlin/ke/don/ski/presentation/ui/SlidesNotes.kt`.
- Each constant is a `List<AnnotatedString>`, and each item is one point.
- The slide list in `SkiPresentationSlides.kt` references the constant: `slide("Kode Viewer", notes = kodeViewerNotes) { ... }`.

Keep notes out of the slide list itself. The list then reads as a table of contents, and the notes can be reviewed on their own.

Name the constant after the slide, in camel case, with a `Notes` suffix: `kodeViewerNotes`, `layoutInspectorDemoNotes`.

## Shape of a good note

Each slide gets 2 to 4 points. Write them the way you would say them.

1. **Transition or orientation.** What this slide is for, or how it follows from the last one. ("Now that we know the rule, here is the demo that shows it.")
2. **The main point.** The one thing the audience should take away.
3. **A takeaway or cue.** What to say or do next, or the phrase to return to. ("Takeaway to return to: ...")

Skip the points that do not earn their place. A slide with one useful point is better than one with four vague ones.

## Say what a number does not measure

If a slide shows a counter, a timer, or any other metric, the notes must say what it does not measure. An audience reads a number as a result, so state the limit next to it.

Compare:

- Vague: "The counter shows the performance difference."
- Precise: "The counter shows how many items were composed. It does not measure memory or frame time."

The same applies to any demo whose behavior depends on the device or the build. Name the condition that makes the demo true.

## Write for the presenter, not the slide

Notes are for you on stage, so write them as spoken sentences rather than as bullet fragments copied from the slide.

- Put numbers, names, and the exact claim in the notes, so you do not have to remember them mid-sentence.
- Keep each point short enough to read at a glance from the panel. If a point needs a paragraph, split it.
- Do not repeat the on-screen title as a note. Say the thing the title does not say.

## Placeholder notes

Use a clear placeholder when you have not written the real notes yet, so an unfinished slide is obvious during a dry run. The template's introduction slide currently uses one ("Remember to say hallo"). Replace it before the talk.

## Checklist before you present

- Every slide has a named constant in `SlidesNotes.kt`, and no slide passes inline `notes = listOf(...)`.
- Every metric on screen has a note saying what it does not measure.
- No placeholder text remains.
- Run the deck in `DeckMode.Local` and step through it with the presenter panel open.
