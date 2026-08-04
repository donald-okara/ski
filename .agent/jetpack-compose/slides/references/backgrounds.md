# Backgrounds and Patterns

The slides utilize a flexible background system that combines patterns and decorative images.

## BackgroundBuilder

Use `BackgroundBuilder` to configure and build slide backgrounds.

```kotlin
val background = BackgroundBuilder()
    .setPattern(Pattern.Wavy(colors = listOf(Color.Blue, Color.Cyan)))
    .setDecoratorImage(DecoratorImage(Res.drawable.logo, size = 150.dp))
    .setAlignment(Alignment.BottomEnd)
    .build()
```

### Methods
- `setPattern(pattern: Pattern)`: Sets the background pattern.
- `setDecoratorImage(decoratorImage: DecoratorImage)`: Sets a decorative image to overlay.
- `setAlignment(alignment: Alignment)`: Positions the decorative image (default: `Alignment.BottomEnd`).
- `build()`: Returns a `Background` instance (must be called within a `@Composable`).

## Patterns

Patterns are defined in the `Pattern` sealed interface:

- `Pattern.Wavy`: Standard wavy lines.
- `Pattern.DiagonalWavy`: Wavy lines with a diagonal slope.
- `Pattern.AnimatedDiagonalWavyBackground`: Animated diagonal wavy lines.

### Common Pattern Properties
- `colors: List<Color>`: The colors used for the waves.
- `waveHeight: Dp`: Height of the waves.
- `waveLength: Dp`: Length of one wave cycle.
- `waveCount: Int`: Number of waves.
- `strokeWidth: Dp`: Thickness of the wave lines.

## DecoratorImage

A simple wrapper for a `DrawableResource` with a specified size.

```kotlin
data class DecoratorImage(
    val image: DrawableResource,
    val size: Dp = 200.dp
)
```

## Usage in Slides

Backgrounds are typically rendered using the `Render()` method:

```kotlin
background.Render()
```
