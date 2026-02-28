<div align="center">
    <img src="ski_logo.png" alt="Description" style="border-radius: 16px; max-width: 80%;">
</div>

# PROMPT.md

This file provides a structured prompt engineers can give to Gemini or any Android Studio AI assistant to generate a slide deck using Ski.

The goal is to generate a fully structured Ski presentation using components available in `:shared:components`, following Compose best practices and idiomatic Kotlin. Each slide should include speaker notes.

---

# How to Use

1. Copy the **Prompt Template** section below.
2. Replace the placeholders with your talk title and talking points.
3. Paste it into Gemini or your IDE AI assistant.
4. Refine slide-by-slide if needed.

---

# Prompt Template

You are generating a presentation using Ski, a Compose Multiplatform presentation framework.

The presentation must:

* Use idiomatic Kotlin
* Use Compose Multiplatform APIs
* Use components from `:shared:components` where appropriate
* Structure slides using `SlideConfig` (include `label`, `notes: List<AnnotatedString>`, `frame`, `transition`, `showHeader`, `content`, and `timer`)
* Include speaker notes for each slide in `notes`
* Be compatible with `PresentationDeck`
* Avoid duplicating slides for simple bullet reveals (use state instead)
* Follow clean Compose architecture

## Talk Title

<INSERT TITLE HERE>

## Audience

<INSERT TARGET AUDIENCE HERE>

## Duration

<INSERT ESTIMATED DURATION>

## Talking Points

<INSERT BULLET POINT OUTLINE HERE>

---

# Generation Requirements

1. Create a `segments/<talk_name>` structure.
2. Generate:

    * A list of `SlideConfig` including `notes` for each slide
    * Individual composables per slide
    * State-driven bullet reveals where appropriate
    * Usage of shared components (titles, content blocks, layouts, etc.)
3. Prefer reusable composables over inline UI duplication.
4. Embed interactive composables directly in the slide for UI demos.
5. Keep slide composables small and readable.
6. Use animations via Compose animation APIs when progression benefits from it.
7. Avoid hardcoded magic numbers unless necessary.
8. Use theming from the shared design system.

---

# Slide Construction Guidelines

* Each slide should map to one idea.
* Include notes for each slide in `notes` as `AnnotatedString` for presenter reference.
* Avoid overloading slides with too much content.
* Use state for progressive disclosure.
* Extract reusable patterns into private composables.
* Keep previewability in mind (slides should compile independently).

---

# Example Instruction to AI

Generate a Ski presentation for a 30-minute talk titled:

"State Management in Compose Multiplatform"

Audience: Android engineers familiar with Compose.

Talking points:

* Why state matters in UI
* Stateless vs stateful composables
* Hoisting patterns
* remember vs rememberSaveable
* State in multiplatform contexts
* Common mistakes
* Live demo of state-driven UI

Produce:

* SlideConfig list including `notes` for each slide
* All slide composables
* Minimal but clean animations
* Clear separation of concerns

Do not include explanatory prose outside the generated Kotlin code unless clarifying assumptions.

---

# Notes

* Slides are composables. Think in UI trees.
* Include speaker notes as `AnnotatedString`.
* Prefer state-driven reveals over duplicate slides.
* Keep architecture clean.
* Optimize for readability during live presentation.

This prompt is intended to produce a clean, production-ready Ski presentation structure with minimal manual refactoring required.
