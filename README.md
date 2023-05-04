# Clocky ⏱ 
[![Project Status: WIP – Initial development is in progress, but there has not yet been a stable, usable release suitable for the public.](https://www.repostatus.org/badges/latest/wip.svg)](https://www.repostatus.org/#wip)

A fun little stopwatch app for Wear OS. The main aim of creating this app was to try out Jetpack compose for Wear OS, and also to try building a
complex animation using the Canvas API, with the help of Trigonometry. The design is heavily inspired from the default stopwatch app. 

## Table of contents
1. [Demo](#demo)
2. [Screenshots](#screenshots)
3. [Tech Stack](#tech-stack)
4. [Source code & Architecture](#source-code-and-architecture)

# Demo
https://user-images.githubusercontent.com/54663474/236147643-166161d4-f5dd-48a7-a179-5c718269ab00.mp4

# Screenshots
![screenshot_of_stopwatch_in_reset_state](screenshots/reset.png) &nbsp; &nbsp; ![screenshot_of_stopwatch_in_running_state](screenshots/running.png)

## Tech Stack
- Entirely written in [Kotlin](https://kotlinlang.org/).
- [Jetpack Compose for Wear OS](https://developer.android.com/training/wearables/compose) for creating the UI.
- [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines/coroutines-guide.html) for threading.
- [Kotlin Flows](https://developer.android.com/kotlin/flow) for creating reactive streams.
- [Java 8 Date/Time API](https://www.oracle.com/technical-resources/articles/java/jf14-date-time.html) for dealing with date and time.
- [Android Services](https://developer.android.com/guide/components/services) for running the stopwatch in the background.

## Source code and Architecture
- All concrete implementations are prefixed by the term "Clocky".
- Commit messages follow the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) specification.
- Manual Dependency injection.

### A note on Modularization Strategy
In this project, a package is considered as single module. It tries to conform to the modularisation strategy specified in the 
[Guide to Android app modularization](https://developer.android.com/topic/modularization) article of the offical Android Developers website.
