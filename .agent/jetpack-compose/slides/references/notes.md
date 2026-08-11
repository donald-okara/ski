# Notes and Hints

Notes and hints provide additional context and guidance for the presenter and the audience.

## NotesComponent

Renders a slide-note panel inside a `SkiFrame`.

```kotlin
NotesComponent(
    notes = Notes(
        title = "Implementation Details",
        points = listOf(
            AnnotatedString("Use BackgroundBuilder for backgrounds"),
            AnnotatedString("Use FrameBuilder for frames")
        )
    ),
    frame = myFrame
)
```

### Parameters
- `notes: Notes?`: The notes to display. Includes a `title` and a list of `points` (as `AnnotatedString`). If null, a placeholder message is shown.
- `frame: SkiFrame`: The frame to render the notes inside.

## NotesHint

Displays a hint row suggesting keystrokes for showing slide notes and dismissing the hint.

```kotlin
NotesHint(frame = myFrame)
```

### Parameters
- `frame: SkiFrame`: The frame to render the hint inside.

## Usage

Typically, notes are shown in a dedicated "Notes" view or as an overlay, while hints are shown briefly to guide the user.

- **Notes Key**: `N` to show.
- **Hint Key**: `H` to dismiss.
