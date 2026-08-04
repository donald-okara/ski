# Bullets and Logos

Small visual components for lists and branding.

## LinearBullet

A vertical line with a gradient, often used as a bullet point or decorative element.

```kotlin
LinearBullet(
    accentColor = Color.Red,
    height = 40
)
```

## DotBullet

A circular bullet point with a gradient.

```kotlin
DotBullet(
    size = 8,
    accentColor = Color.Blue
)
```

## SkiLogo

A branding component consisting of two `LinearBullet`s side-by-side.

```kotlin
SkiLogo(
    accentColor = Color.Orange,
    height = 50,
    spacing = 10.dp
)
```
