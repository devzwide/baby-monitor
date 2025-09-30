# Baby Monitor

## Overview
Baby Monitor is an Android application designed to help parents remotely monitor their baby's activities and environment. The app leverages modern Android technologies and integrates with Google services for enhanced features.

## Features
- Real-time monitoring
- Notifications and alerts
- Secure data handling
- User-friendly interface

## Project Structure
```
baby-monitor/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/         # Application source code
│   │   │   ├── res/          # Resources (layouts, drawables, etc.)
│   │   │   └── AndroidManifest.xml
│   │   ├── test/             # Unit tests
│   │   └── androidTest/      # Instrumentation tests
│   ├── build.gradle.kts      # App-level Gradle config
│   └── google-services.json  # Google services config
├── build.gradle.kts          # Project-level Gradle config
├── settings.gradle.kts       # Gradle settings
├── gradle/                   # Gradle wrapper and config
└── README.md                 # Project documentation
```

## Prerequisites
- Android Studio (latest recommended)
- JDK 17+
- Gradle (wrapper included)
- Google account for Firebase integration

## Setup Instructions
1. **Clone the repository:**
   ```bash
   git clone https://github.com/devzwide/baby-monitor.git
   cd baby-monitor
   ```
2. **Open in Android Studio:**
   - Select `Open an Existing Project` and choose the `baby-monitor` folder.
3. **Configure Google Services:**
   - Ensure `google-services.json` is present in `app/` for Firebase features.
4. **Build and Run:**
   - Click `Run` in Android Studio or use:
     ```bash
     ./gradlew assembleDebug
     ```

## Usage
- Launch the app on your device/emulator.
- Follow on-screen instructions to set up monitoring.
- Configure notifications and preferences as needed.

## Contributing
Contributions are welcome! Please fork the repository and submit a pull request. For major changes, open an issue first to discuss your ideas.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact
For questions or support, please contact the maintainer at: nxumalobukeka66@gmail.com

