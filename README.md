# Ski: A Compose Multiplatform Slide Template

Ski is a template for creating presentations and slides using Compose Multiplatform, with a primary focus on **Web** and **Desktop** applications. It provides a foundation for building beautiful, animated, and interactive slide decks with the power and flexibility of Kotlin and Compose.

## Features

*   **Compose Multiplatform:** Write your presentation logic once in Kotlin and run it on the web (Wasm), desktop (JVM), and other platforms.
*   **Web & Desktop First:** Optimized for web and desktop experiences, providing a seamless presentation experience on larger screens.
*   **Modular Architecture:** The project is structured with a modular approach, making it easy to manage and scale your presentation content.
*   **Customizable Theming:** Easily customize the look and feel of your slides to match your branding.
*   **Ready-to-use Components:** Includes a set of pre-built components to get you started with your slides quickly.

## Project Structure

*   `./composeApp`: Shared code for your Compose Multiplatform applications.
    *   `commonMain`: Code common to all targets.
    *   Platform-specific folders (`jvmMain`, `wasmJsMain`, etc.): Code specific to each platform.
*   `./shared`: Contains shared modules like design, resources, and components.
*   `./segments`:  Individual slide decks or sections of your presentation.

## Getting Started

### Building and Running

#### Web (Wasm)

```shell
# macOS/Linux
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Windows
.\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
```

#### Desktop (JVM)

```shell
# macOS/Linux
./gradlew :composeApp:run

# Windows
.\gradlew.bat :composeApp:run
```

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html) and [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform).
