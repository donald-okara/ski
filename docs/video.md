# Video in slides

Ski plays video through `VideoPlayer` and `VideoSlide` in `:segments:demos`. A slide passes a `videoUri`, and the player turns it into something the current platform can open.

## Two ways to supply a video

| Source | Pass | Works offline | Notes |
|---|---|---|---|
| Remote URL | `"https://…/clip.mp4"` | No | Nothing to commit. Check the URL before the talk. |
| Bundled file | `Resources.Videos.SAMPLE_VIDEO_PATH` | Yes | Lives in `shared/resources/src/commonMain/composeResources/files/`. Keep it small: the sample is 6.5 MB and is committed to git. |

Pass a value starting with `http://` or `https://` and it is used as-is on every platform. Anything else is treated as a bundled path relative to the resources root, such as `files/sample_video.mov`.

## Where the video files go

```
shared/resources/src/commonMain/composeResources/files/
    sample_video.mov      # bundled sample used by the Video slide
```

To add one, copy the file into this directory, then add a path constant to `Resources.Videos` in `shared/resources/src/commonMain/kotlin/ke/don/resources/Resources.kt`. Use the constant from the slide, not a hand-typed path.

## How resolution works

`resolveVideoUriForPlayer` is declared in `segments/demos/src/commonMain/.../VideoPlayer.kt`. Each platform implements it in its own source set.

| Platform | File | Bundled path becomes | Status |
|---|---|---|---|
| JVM (desktop) | `jvmMain/…/VideoPlayer.jvm.kt` | `file:` URI of a copy in the temp directory. The copy is reused while its size matches. | Compiles. Not played on a device in this repo yet. |
| Wasm | `wasmJsMain/…/VideoPlayer.wasmJs.kt` | `Res.getUri(path)`, a URL served with the app | Compiles. Not played in a browser yet. |
| JS | `jsMain/…/VideoPlayer.js.kt` | `Res.getUri(path)` | Not compiled yet. |
| Android | `androidMain/…/VideoPlayer.android.kt` | `Res.getUri(path)` | Not compiled or run. |
| iOS | `iosMain/…/VideoPlayer.ios.kt` | `Res.getUri(path)` | Not compiled or run. |

If resolution throws, `VideoPlayer` falls back to the raw `videoUri`, so a bad path shows up as a player error rather than a crash. Check the player on the target you present from.

## Using it in a slide

```kotlin
slide("Video", notes = videoNotes) {
    VideoSlide(
        videoUri = Resources.Videos.SAMPLE_VIDEO_PATH,
        title = "Video in Slides",
        explanation = listOf("Plays bundled or remote video", "Controls hide during playback"),
        code = "VideoSlide(videoUri = ..., title = ..., explanation = ..., code = ...)",
    )
}
```

## Before presenting

- Play the video on the machine and browser you present from. Only the JVM and Wasm paths have been compiled, and none has been played on a device.
- For a remote URL, confirm it loads on the venue network.
- Keep the speaker notes honest about the video's length and any audio.
