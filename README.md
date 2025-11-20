# Manchester United App

An Android application built with Jetpack Compose that provides Manchester United fans with real-time updates on matches, news, squad information, and league standings.

## Features

- **Matches**: View upcoming and past Manchester United fixtures with scores and match details
- **News**: Stay updated with the latest news about the club
- **Squad**: Browse the current Manchester United squad with player details and positions
- **Standings**: Check the Premier League table and Manchester United's current position

## Tech Stack

- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern Android UI toolkit
- **Material Design 3** - UI components and theming
- **Retrofit** - REST API client
- **Coil** - Image loading library
- **Navigation Compose** - In-app navigation
- **ViewModel** - MVVM architecture pattern

## Architecture

The app follows the MVVM (Model-View-ViewModel) architecture pattern:

- **Models**: Data classes representing API responses
- **Repository**: Data layer handling API calls
- **ViewModel**: Business logic and state management
- **UI**: Composable screens for each feature

## Requirements

- Android Studio Hedgehog or later
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)
- Kotlin 1.9.0+

## Setup

1. Clone the repository:
```bash
git clone https://github.com/Joshua-shields/Man-United-app.git
```

2. Open the project in Android Studio

3. Sync Gradle dependencies

4. Run the app on an emulator or physical device

## API

This app uses a Football API service to fetch live data. You may need to configure your own API key in the `FootballApiService.kt` file.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Manchester United Football Club for inspiration
- Football API providers for data access
