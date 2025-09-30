# Baby Monitor App: Presentation Layer Documentation

## Overview
The presentation layer of the Baby Monitor app is responsible for all user interface (UI) and user experience (UX) interactions. It is built using Jetpack Compose, a modern toolkit for building native Android UIs. The presentation layer communicates with the ViewModel layer to display data and respond to user actions, following the MVVM (Model-View-ViewModel) architecture.

## Architecture

### MVVM Pattern
- **Model:** Represents the data layer (e.g., Feeding, Sleep, Diaper, Health models).
- **ViewModel:** Acts as a bridge between the UI and the data, exposing observable state and business logic (e.g., ActivityViewModel).
- **View (Presentation Layer):** Composables and screens that render the UI and interact with the ViewModel.

### Presentation Layer Structure
- **Screens:** Each major feature (Home, Tracking, Reports) is a composable screen.
- **Components:** Reusable UI elements (e.g., MetricCard, MonitorStatusSection).
- **Navigation:** Uses Jetpack Navigation Compose for screen transitions.
- **State Management:** Uses `mutableStateOf`, `mutableStateListOf`, and `StateFlow` for reactive UI updates.

## System Flow
1. **App Launch:**
   - The app starts and checks authentication state.
   - If authenticated, the user is navigated to the HomeScreen.
2. **HomeScreen:**
   - Displays summary metrics (last feed, sleep, diaper, health) using data from the ViewModel.
   - Provides navigation to activity tracking and reports.
   - Supports manual refresh to fetch the latest data.
3. **TrackingScreen:**
   - Allows users to log new activities (feeding, sleep, diaper, health).
   - On save, updates the ViewModel, which syncs with Firestore.
4. **ReportsScreen:**
   - Shows historical data and analytics for all activities.
   - Fetches data from the ViewModel, which is kept in sync with Firestore.
5. **ViewModel Communication:**
   - The ViewModel listens to Firestore for real-time updates and exposes state to the UI.
   - UI observes ViewModel state and updates automatically.

## Tech Stack
- **Kotlin:** Main programming language for all app logic.
- **Jetpack Compose:** Declarative UI toolkit for building native Android interfaces.
- **Android Architecture Components:** ViewModel, StateFlow, and LiveData for state management.
- **Firebase Firestore:** Cloud NoSQL database for real-time data storage and sync.
- **Firebase Auth:** Handles user authentication.
- **Coil:** Image loading library for displaying baby photos.
- **Material3:** Modern Material Design components for consistent UI/UX.

## How Components Connect
- **UI (Compose Screens/Components):**
  - Observe state from ViewModel using Compose's state management.
  - Trigger actions (e.g., add feeding, refresh) that call ViewModel functions.
- **ViewModel:**
  - Handles business logic, data validation, and communication with Firestore.
  - Updates state, which is observed by the UI.
- **Firestore:**
  - Stores all activity data (feeding, sleep, diaper, health) and user info.
  - Sends real-time updates to the ViewModel via listeners.

## Summary
The presentation layer is a reactive, declarative UI built with Jetpack Compose, following MVVM. It is tightly integrated with the ViewModel, which manages state and data from Firestore. The tech stack ensures a modern, responsive, and maintainable Android application.

