# Architecture & Logic

## 1. Overview
The Baby Monitor app follows a modular, layered architecture using modern Android best practices. It leverages MVVM (Model-View-ViewModel) for separation of concerns, Kotlin for implementation, and integrates with Firebase for backend services.

## 2. Main Components
- **UI Layer (View):** Activities and Fragments in `app/src/main/java/` handle user interaction and display data.
- **ViewModel Layer:** ViewModels manage UI-related data and business logic, exposing LiveData/StateFlow to the UI.
- **Model Layer:** Data classes and repositories handle data operations, including local storage and remote (Firebase) access.
- **Service Layer:** Background services (e.g., monitoring, notifications) run tasks independently of UI.
- **Firebase Integration:** Handles authentication, real-time database, and push notifications.

## 3. Data Flow
1. **User Interaction:** UI components (Activity/Fragment) capture user actions (e.g., start monitoring).
2. **ViewModel Processing:** UI calls ViewModel methods, which process logic and update state.
3. **Repository/Data Source:** ViewModel interacts with repositories to fetch/store data (local DB or Firebase).
4. **LiveData/StateFlow:** ViewModel exposes observable data to UI, which reacts to changes automatically.
5. **Notifications:** Services trigger notifications via Android NotificationManager and Firebase Cloud Messaging.

## 4. Core Logic
- **Monitoring:**
    - App starts a background service to monitor baby’s environment (audio, video, or sensor data).
    - Service processes data and triggers alerts if thresholds are exceeded (e.g., noise level, movement).
- **Notifications:**
    - Local notifications for immediate alerts.
    - Push notifications via Firebase for remote alerts.
- **Data Storage:**
    - Local: SharedPreferences or Room DB for settings and logs.
    - Remote: Firebase Realtime Database for syncing events and user data.
- **User Authentication:**
    - Firebase Auth for secure login and user management.

## 5. Key Classes & Files
- `MainActivity.kt` / `MonitorFragment.kt`: UI entry points.
- `MonitorViewModel.kt`: Handles monitoring logic and state.
- `MonitorService.kt`: Background service for continuous monitoring.
- `NotificationHelper.kt`: Utility for sending notifications.
- `Repository.kt`: Data access layer for local and remote sources.
- `FirebaseManager.kt`: Handles Firebase interactions.

## 6. Example Flow Diagram
```
[User Action] → [UI (Activity/Fragment)] → [ViewModel] → [Repository] → [Firebase/Local DB]
         ↑                                                        ↓
   [Notification] ← [Service] ← [Sensor/Event]
```

## 7. Extensibility & Best Practices
- Follows MVVM for testability and maintainability.
- Uses Kotlin Coroutines/Flow for async operations.
- Dependency Injection (e.g., Hilt/Dagger) recommended for scaling.
- Modular codebase for easy feature addition.

## 8. References
- [Android MVVM Guide](https://developer.android.com/jetpack/guide)
- [Firebase Android Setup](https://firebase.google.com/docs/android/setup)
