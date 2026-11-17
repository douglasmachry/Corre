# Corre — Android running and location prototype

A legacy Android application prototype exploring running/exercise experiences through maps, device location, social sign-in and an exercise-history model.

> **Status:** Historical study project, last updated in 2017. Its Android build stack is obsolete and has not been validated against current Android Studio, Android Gradle Plugin or SDK releases.

## What is represented in the code

- **Facebook sign-in:** an authentication screen built with the Facebook Android SDK.
- **Map experience:** a Google Maps screen that requests location permission and uses the fused location provider to receive location updates and move the map camera.
- **User profile:** the map screen displays the signed-in user's name and profile picture.
- **Exercise history prototype:** an `Exercicio` model contains date, distance, duration and score fields; a history screen reads an exercise record from Firebase Realtime Database.

The repository contains early application flows and models. It should be treated as a prototype rather than a complete, verified fitness tracker.

## Technology

- Java and native Android views
- Google Maps SDK and Google Play Services Location
- Firebase Realtime Database
- Facebook Android SDK
- Glide for profile image loading
- Gson
- Android Gradle Plugin 2.3.1 and Gradle 3.3
- Compile / target SDK 25

## Project layout

```text
app/src/main/java/com/example/iossenac/corre/
  ctrl/
    LoginFacebook.java
    MapsActivity.java
    HistoricoActivity.java
    MainActivity.java
  model/
    Usuario.java
    Exercicio.java
    Picture.java
    DataFace.java
app/src/main/res/
```

## Running the historical project

The original project requires a legacy Android build environment compatible with Android Gradle Plugin 2.3.1 and Gradle 3.3. Current Android Studio installations may not support this configuration without migration.

The app also depends on externally configured Facebook, Google Maps and Firebase projects. Configure your own application IDs and restricted API credentials before trying to run it. Do not reuse credentials from a checked-in configuration file or commit private service credentials.

## Security and privacy

The prototype requests device location and uses third-party identity and mapping services. A production implementation would require current permission handling, clear user consent, secure Firebase rules, per-user data access, lifecycle-safe location subscriptions, credential restrictions and tests. None of those production guarantees are claimed here.
