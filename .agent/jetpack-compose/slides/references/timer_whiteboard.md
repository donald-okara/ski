# Timer and Whiteboard

The timer and whiteboard components provide interactive utilities for slides.

## TimerComponent

A functional timer with a "wavy" progress visualization.

```kotlin
TimerComponent(
    timerState = myTimerState,
    onIntent = { intent -> /* Handle TimerIntentHandler (Start, Pause, Reset, etc.) */ }
)
```

### Components
- `TimerState`: Holds `timeLeft`, `totalTime`, and `status`.
- `TimerStatus`: `Idle`, `Resumed`, `Paused`, `Stopped`.
- `TimerIntentHandler`: Actions like `Start`, `Pause`, `Reset`.

### Visualization
- The background fills vertically with a wavy pattern as time progresses.
- Colors change based on time remaining (e.g., turns red when time is up).

## WhiteboardComponent

A specialized text input area that looks like a whiteboard or code editor with line numbers.

```kotlin
WhiteboardCard(
    value = textState,
    onValueChange = { textState = it },
    darkTheme = true,
    toggleFocus = { /* Fullscreen toggle */ },
    toggleTheme = { /* Theme toggle */ }
)
```

### Features
- **Line Numbers**: Automatically displayed on the left.
- **Syntax Highlighting**: Basic highlighting for links and special patterns.
- **Themes**: Supports light and dark modes via `WhiteboardTheme`.
- **Text Scaling**: Adjust font size via `textScale`.

## Usage Tip
Use `WhiteboardCard` for a ready-to-use component with header actions (fullscreen/theme), or `WhiteboardComponent` for just the input area.
