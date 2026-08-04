# Slides Skill

This skill enables the AI to utilize the shared slide components for creating and modifying presentation slides.

## Overview

The slides are built using a modular system of backgrounds, frames, and specialized content components. Always refer to the reference documentation for detailed usage of each component.

## Key Components

### 1. Visual Foundation
- **[Backgrounds and Patterns](references/backgrounds.md)**: Use `BackgroundBuilder` to create dynamic backgrounds with wavy patterns and decorative images.
- **[Frames](references/frames.md)**: Use `FrameBuilder` to wrap content in `SnakeFrame` or `BasicFrame`. Frames provide standard layout areas for headers and footers.

### 2. Specialized Content
- **[Code Viewer](references/code_viewer.md)**: Display Kotlin code with syntax highlighting using `KotlinCodeViewerCard`.
- **[Device Frames](references/device_frames.md)**: Showcase app screenshots or UI within realistic device chassis like Pixel 8 or iPhone 17.
- **[Timer and Whiteboard](references/timer_whiteboard.md)**: Add interactive elements like a countdown timer or a whiteboard for live notes/coding.
- **[Bullets and Logos](references/bullets_logos.md)**: Use `LinearBullet`, `DotBullet`, and `SkiLogo` for lists and branding.

### 3. Presenter Tools
- **[Notes and Hints](references/notes.md)**: Add presenter notes using `NotesComponent` and display keyboard shortcuts with `NotesHint`.

## General Guidelines

- **Consistent Theming**: Use `Values` (like `Values.cornerRadius`, `Values.Dimens.mediumPadding`) for consistent spacing and styling.
- **Composition**: Most components are designed to be used within a `SkiFrame.Render` content block.
- **Adaptability**: Use `LocalDeviceInsets` when rendering content inside a `DeviceFrame` to avoid notches and cutouts.
- **DSL Pattern**: Prefer using builders (`BackgroundBuilder`, `FrameBuilder`) for complex configurations.
