# Code Viewer

The code viewer components provide syntax highlighting and focusing capabilities for Kotlin code.

## KotlinCodeViewerCard

A high-level component that wraps `KotlinCodeViewer` in a surface with theme toggling and fullscreen capabilities.

```kotlin
KotlinCodeViewerCard(
    code = {
        """
        fun main() {
            println("Hello, Ski!")
        }
        """.trimIndent()
    },
    darkTheme = true,
    toggleFocus = { /* Handle fullscreen */ },
    toggleTheme = { /* Handle theme toggle */ }
)
```

## KotlinCodeViewer

The base component for rendering syntax-highlighted Kotlin code.

```kotlin
KotlinCodeViewer(
    code = myCodeString,
    darkTheme = true,
    shouldFoldLambdas = true
)
```

### Parameters
- `code: String`: The Kotlin source code to display.
- `darkTheme: Boolean`: Whether to use a dark or light syntax theme.
- `shouldFoldLambdas: Boolean`: Whether to automatically fold lambdas for conciseness.
- `textScale: Float`: Scale factor for the code font size (default: 0.75f).

## FocusKotlinViewer

Used when a specific code block needs to be focused or displayed in a full-screen/modal fashion.

### Usage Tip
Wrap the code viewer in a `DraggableBox` if you want it to be movable within the slide.
