# Sportify (step 1: UI)

Spotify-style Android UI built with Jetpack Compose and its own original branding.

## Setup
1. Android Studio -> New Project -> Empty Activity (Compose), package `com.sportify.app`, min SDK 26.
2. Copy the files from `app/src/main/` into your project (keep the same paths).
3. Add to `app/build.gradle(.kts)` dependencies:
   `implementation("androidx.compose.material:material-icons-extended")`
4. Launcher icon: right-click `res` -> New -> Image Asset -> choose `ic_sportify_logo.xml` as the foreground.

## What's in step 1
- Home, Search, Library screens
- Mini-player that expands into a full-screen player (slides up, back button closes)
- Animated gradient backdrop on the player where the looping video goes later

## Next
- Step 2: Spotify login (OAuth PKCE) and real playlists in Library
- Step 3: audio playback and a looping video backdrop (Media3 ExoPlayer)

## Downloads (added)
Files: `download/Downloads.kt`, `download/DownloadWorker.kt`. Saved to the app's Music folder and listed under Library > Downloads.

Extra setup:
- Dependency: `implementation("androidx.work:work-runtime-ktx:2.9.1")`
- Manifest: `<uses-permission android:name="android.permission.INTERNET"/>`
- Set `audioUrl` on a `Track` to a direct audio link from a source that allows downloads
  (Jamendo, Audius, Internet Archive, your own files). Tracks with no `audioUrl` show a "no downloadable source" message.
- Not supported on purpose: downloading from YouTube or Spotify. Their terms forbid it.

## GitHub
```
cd sportify            # your Android Studio project root (contains gradlew)
git init
git add .
git commit -m "Sportify: UI and downloads"
git branch -M main
git remote add origin https://github.com/<you>/sportify.git
git push -u origin main
```
Every push to `main` builds a debug APK via `.github/workflows/build.yml`. Download it from the run's Artifacts section.
Keep secrets (Spotify client secret, keystores) out of the repo. The Spotify PKCE login only needs a client ID, so no secret is required.
