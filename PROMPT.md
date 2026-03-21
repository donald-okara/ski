<p align="center">
  <img src="ski_logo.png" 
       alt="Ski Logo" 
       width="180" />
</p>

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

1. Have a clear plan of what goes into which slides, including notes
2. Look into the :segments:demos module
3. Add a folder for each slide with the components it will use
4. Write a slide using compose (look at the slides already present in ke.don.demo)\
5. For each slide, add them to ke.don.ski.presentation.ui.skiPresentationSlides() and replace the place holders
6. Edit the IntroductionScreen() to match the current presentation
7. Keep the defaults at Deck.kt as is
8. Make sure to ask the user the length of this session so you can edit SlidesConstants.SESSION_DURATION in ke.don.ski.Deck.kt
9. Use KotlinCoderViewer for code snippets
--

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
