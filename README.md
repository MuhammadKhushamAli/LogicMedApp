# 🩺 LogicMed

> A premium dark-themed Android medical appointment & consultation platform connecting **Patients** with **Doctors** — powered by Firebase, Cloudinary, and an AI Medical Agent.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Architecture](#project-architecture)
- [App Flow](#app-flow)
- [Screens & Activities](#screens--activities)
- [Firebase Collections Schema](#firebase-collections-schema)
- [Third-Party Integrations](#third-party-integrations)
- [Prerequisites](#prerequisites)
- [Setup & Installation](#setup--installation)
- [Future Amendments](#future-amendments)
- [License](#license)

---

## Overview

**LogicMed** is a native Android application (Java) that provides a seamless healthcare experience for two distinct user roles:

| Role        | Capabilities |
|-------------|-------------|
| **Patient** | Browse & search doctors, book appointments, chat with doctors, consult an AI medical agent, provide voice-based feedback, manage profile with photo upload |
| **Doctor**  | Register with specialties / schedule / fee, view & manage appointments, chat with patients, resolve appointments with voice feedback |

The app features a **futuristic dark medical aesthetic** with a curated teal/amber/purple accent palette, glassmorphism panels, and smooth entrance animations.

---

## Features

### ✅ Implemented

| Module | Details |
|--------|---------|
| **Splash Screen** | Animated entry with slide-to-start custom widget, auto-redirects based on auth state (first-time → OnBoard, returning → Auth, logged-in → Home) |
| **Onboarding** | Single-screen onboard with slide-to-continue gesture; sets `isFirstTime` flag in SharedPreferences |
| **Authentication** | Tab-based Login / Signup with `ViewPager2` + `TabLayout`; animated height transitions between tabs |
| **Role-Based Signup** | Multi-step signup: basic info → Doctor details (if Doctor role) → Profile photo upload; dynamic city dropdown fetched from [Countries Now API](https://countriesnow.space) |
| **Doctor Registration** | Specialty category/sub-category selection, day-of-week duty chip selection with time-picker for from/to hours, consultation fee, and appointment slot duration |
| **Profile Image Upload** | Camera capture or gallery pick → uploaded to **Cloudinary** via unsigned preset `logicmed` |
| **Home Dashboard** | Greeting card with user name & role, motivational word carousel, upcoming pending appointments in a grid `RecyclerView` (Firestore real-time via `FirestoreRecyclerAdapter`) |
| **Doctor Search** | Grid list of all registered doctors (Patient-only tab, hidden for Doctor role); tap to view full doctor detail |
| **Doctor Detail** | Profile image, fee display, schedule timings grid, specialties list, start-conversation FAB, book-appointment button |
| **Appointment Booking** | Material Date Picker constrained to doctor's available days (current month + next month), dynamic slot generation from schedule, filters already-booked and past-time slots, stores to Firestore |
| **All Appointments** | Drawer-navigable fragment showing all appointments for the current user; supports voice-based feedback to mark appointments as "Resolved" |
| **Voice Feedback** | Uses `RecognizerIntent` (Speech-to-Text) to capture appointment feedback, confirmation via `MaterialAlertDialog`, then updates Firestore status + feedback fields |
| **Real-Time Chat** | 1-on-1 messaging between Patient ↔ Doctor; chat signature ensures no duplicate conversations; real-time updates via `FirestoreRecyclerAdapter` |
| **Image Messaging** | Send images in chat via camera capture or gallery upload → Cloudinary → message with image URL |
| **AI Medical Agent** | Chat interface connecting to a hosted AI agent at `https://logicmedagent-production.up.railway.app/talk`; maintains conversation history via `prev_history_id`; responses displayed as "Dr. LogicMed" |
| **Navigation** | Bottom Navigation Bar (Home, Search, Chat, AI) + Drawer Navigation (All Appointments); back-stack synced with bottom nav selection |
| **Custom Slide-To-Start Widget** | Fully custom `RelativeLayout`-based slider with touch handling, cover animation, and configurable XML attributes (`slider_text`, `slider_text_color`, `slider_cover_color`) |
| **Design System** | Semantic color palette in `colors.xml` (dark backgrounds, teal/amber/purple accents, layered surfaces), custom styles in `styles.xml`, glassmorphism drawables, entrance animations (`top_to_current`, `bottom_to_current`, `rotate_to_current`, `invisible_to_visible`) |
| **Edge-to-Edge UI** | All activities use `EdgeToEdge.enable()` with proper system bar insets handling |
| **Session Management** | SharedPreferences-based session with keys for login state, role, name, email, profile URL; logout clears prefs and redirects to auth |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 11 |
| **UI Framework** | Android XML Layouts, Material Design 3 Components |
| **Min SDK** | 24 (Android 7.0 Nougat) |
| **Target SDK** | 36 |
| **Backend / Database** | Firebase Firestore (NoSQL real-time) |
| **Authentication** | Firebase Authentication (Email/Password) |
| **Image Storage** | Cloudinary (unsigned upload preset) |
| **Real-Time Adapters** | FirebaseUI Firestore (`FirestoreRecyclerAdapter`) |
| **Networking** | OkHttp 4 |
| **Image Loading** | Glide |
| **AI Backend** | External REST API hosted on Railway |
| **Speech Recognition** | Android `RecognizerIntent` (Speech-to-Text) |
| **Build System** | Gradle (Groovy DSL) with Version Catalog |

---

## Project Architecture

```
LogicMed/
├── app/src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/example/logicmed/
│   │   ├── MyApplication.java          # Global app state, Firebase & Cloudinary init
│   │   ├── KeyUtils.java               # Constants: pref keys, collection names, intents
│   │   │
│   │   ├── # --- Activities ---
│   │   ├── SplashScreen.java           # Animated splash with auth-based redirect
│   │   ├── OnBoardScreen.java          # First-time onboarding
│   │   ├── AuthenticationScreen.java   # Login/Signup tab host (ViewPager2)
│   │   ├── MainActivity.java           # Bottom nav + drawer host
│   │   ├── DoctorDetailActivity.java   # Doctor profile, booking, chat initiation
│   │   ├── ChatActivity.java           # 1-on-1 messaging with image support
│   │   ├── AIActivity.java             # AI medical agent chat
│   │   │
│   │   ├── # --- Fragments ---
│   │   ├── LoginFragment.java          # Email/password login
│   │   ├── SignupFragment.java         # Multi-step registration
│   │   ├── DoctorDetailSignUpFragment.java  # Doctor-specific signup fields
│   │   ├── ProfileSetupFragment.java   # Profile photo capture/upload
│   │   ├── HomeFragment.java           # Dashboard with upcoming appointments
│   │   ├── SearchFragment.java         # Doctor search grid
│   │   ├── ChatFragment.java           # Chat list
│   │   ├── AllAppointmentsFragment.java     # All appointments with feedback
│   │   ├── AppointmentBookingFragment.java  # Date/slot picker for booking
│   │   │
│   │   ├── # --- Models ---
│   │   ├── User.java                   # Base user model
│   │   ├── Doctor.java                 # Extends User with fee, schedule, categories
│   │   ├── Chat.java                   # Chat room with participants & signature
│   │   ├── Message.java               # Chat message (text/image)
│   │   ├── Appointment.java           # Appointment with status & feedback
│   │   ├── ParticipantDetail.java     # Participant info embedded in chat/appointment
│   │   ├── Schedule.java              # Day + from/to time
│   │   ├── SlotsOfDay.java            # Day + computed time slots
│   │   ├── AiChat.java                # AI chat message model
│   │   ├── DoctorCategoriesAndSubCategories.java  # Category grouping
│   │   ├── SignupViewModel.java        # LiveData for signup page state
│   │   ├── SliderButton.java          # Custom slider button view
│   │   │
│   │   ├── # --- Adapters ---
│   │   ├── AuthViewPagerAdapter.java          # Login/Signup pager
│   │   ├── AppointmentRecyclerAdapter.java    # Home appointments
│   │   ├── AllAppointmentsRecyclerAdapter.java # All appointments
│   │   ├── AppointmentSlotsAdapter.java       # Available time slots
│   │   ├── DoctorRecyclerViewAdapter.java     # Doctor search cards
│   │   ├── DoctorTimingAndCategoryAdapter.java # Doctor schedule/categories
│   │   ├── CategoriesAdapter.java             # Specialty categories
│   │   ├── SubCategoriesAdapter.java          # Specialty sub-categories
│   │   ├── ChatPersonRecyclerAdapter.java     # Chat list items
│   │   ├── MessageRecyclerAdapter.java        # Chat messages
│   │   ├── AIChatRecyclerAdapter.java         # AI chat messages
│   │   │
│   │   └── # --- Custom Views ---
│   │       └── SlideToStart.java              # Custom slide-to-action widget
│   │
│   └── res/
│       ├── anim/          # 4 entrance animations
│       ├── drawable/      # Icons, backgrounds, glassmorphism shapes
│       ├── layout/        # 26 XML layouts
│       ├── menu/          # Bottom nav + drawer menus
│       ├── values/        # colors, strings, styles, themes, attrs
│       └── values-night/  # Dark mode overrides
```

---

## App Flow

```
┌─────────────┐
│ SplashScreen │
│ (Slide-to-  │
│   Start)    │
└──────┬──────┘
       │
       ├── User logged in? ──────────────► MainActivity
       │
       ├── First time? ──► OnBoardScreen ──► AuthenticationScreen
       │
       └── Returning user ──────────────► AuthenticationScreen
                                                │
                                         ┌──────┴──────┐
                                         │  Login Tab  │  Signup Tab
                                         └──────┬──────┘──────┬──────
                                                │             │
                                                │     [Patient] → Profile Setup
                                                │     [Doctor]  → Doctor Details → Profile Setup
                                                │             │
                                                └──────┬──────┘
                                                       │
                                                       ▼
                                              ┌─────────────┐
                                              │ MainActivity │
                                              │              │
                                              │ Bottom Nav:  │
                                              │ Home|Search| │
                                              │ Chat|AI     │
                                              │              │
                                              │ Drawer:      │
                                              │ All Appts   │
                                              └─────────────┘
```

---

## Screens & Activities

| Screen | File | Description |
|--------|------|-------------|
| Splash | `SplashScreen.java` | Animated logo + slide-to-start widget |
| Onboard | `OnBoardScreen.java` | Welcome screen for first-time users |
| Auth | `AuthenticationScreen.java` | Login/Signup tabs |
| Home | `HomeFragment.java` | Dashboard with greeting & pending appointments |
| Search | `SearchFragment.java` | Browse all doctors (patients only) |
| Doctor Detail | `DoctorDetailActivity.java` | Full doctor profile + book/chat |
| Appointment Booking | `AppointmentBookingFragment.java` | Date picker + slot selector |
| All Appointments | `AllAppointmentsFragment.java` | Full appointment history |
| Chat List | `ChatFragment.java` | Active conversations |
| Chat Room | `ChatActivity.java` | Real-time messaging with image support |
| AI Chat | `AIActivity.java` | AI medical agent conversation |

---

## Firebase Collections Schema

### `users`
```json
{
  "fullName": "string",
  "profileImageUrl": "string | null",
  "role": "Doctor | Patient",
  "city": "string",
  // Doctor-only fields:
  "fee": "float",
  "slotDuration": "float",
  "docCategories": ["string"],
  "docTimings": [
    { "day": "string", "fromTime": "HH:mm", "endTime": "HH:mm" }
  ]
}
```

### `chats`
```json
{
  "participantsUId": ["uid1", "uid2"],
  "chatSignature": "uid1_uid2",
  "participantDetails": [
    { "participantId": "string", "name": "string", "profileUrl": "string" }
  ]
}
```

### `messages`
```json
{
  "message": "string (text or image URL)",
  "senderId": "string",
  "chatId": "string",
  "senderName": "string",
  "isImage": "boolean",
  "timeStamps": "Timestamp (server)"
}
```

### `appointments`
```json
{
  "patientId": "string",
  "doctorId": "string",
  "date": "yyyy/MM/dd",
  "day": "string",
  "timeSlot": "HH:mm - HH:mm",
  "status": "Pending | Resolved",
  "checkUpFeedBack": "string | null",
  "appointMentTimeStamp": "Timestamp (server)",
  "patientDetails": { "participantId": "", "name": "", "profileUrl": "" },
  "doctorDetails": { "participantId": "", "name": "", "profileUrl": "" }
}
```

---

## Third-Party Integrations

| Service | Purpose | Configuration |
|---------|---------|---------------|
| **Firebase Auth** | Email/password authentication | `google-services.json` (not committed) |
| **Firebase Firestore** | Real-time NoSQL database | Same config file |
| **Cloudinary** | Image upload & CDN | Cloud name: `dlsbqnmnb`, Unsigned preset: `logicmed` |
| **Countries Now API** | City list for location dropdown | `https://countriesnow.space/api/v0.1/countries/cities` |
| **Railway (AI Agent)** | AI medical chatbot backend | `https://logicmedagent-production.up.railway.app/talk` |

---

## Prerequisites

- **Android Studio** Ladybug or later
- **JDK 11+**
- **Android SDK** with API 36
- **Firebase Project** with Firestore & Auth enabled
- **Cloudinary Account** with unsigned upload preset named `logicmed`

---

## Setup & Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/LogicMed.git
   cd LogicMed
   ```

2. **Add Firebase config**
   - Download `google-services.json` from your Firebase Console
   - Place it in `app/` directory

3. **Configure Cloudinary** (optional — already hardcoded)
   - Update cloud name in `MyApplication.java` if using a different account

4. **Set up environment variables**
   - The `.env` file contains `CLOUDINARY_DB_NAME=dlsbqnmnb`

5. **Build & Run**
   - Open in Android Studio
   - Sync Gradle
   - Run on emulator or physical device (min API 24)

---

## Future Amendments

The following features/concepts are **not yet implemented** and are planned for future releases:

### 🔐 Security & Auth
- [ ] **Forgot Password / Password Reset** — No password recovery flow exists
- [ ] **Email Verification** — Users can sign up without verifying their email
- [ ] **OAuth / Social Login** — No Google, Facebook, or other OAuth sign-in
- [ ] **Biometric Authentication** — No fingerprint or face unlock support
- [ ] **API Key Security** — Cloudinary cloud name and AI agent URL are hardcoded; should use `BuildConfig` or secure backend proxy

### 👤 User Management
- [ ] **Edit Profile** — No way to update name, city, profile picture, or password after signup
- [ ] **Delete Account** — No account deletion option
- [ ] **Doctor Profile Editing** — Cannot update schedule, fee, or specialties after registration

### 🔍 Search & Discovery
- [ ] **Search Functionality (Active Filtering)** — `SearchView` exists in Search and Chat fragments but is not wired to any filtering logic
- [ ] **Filter by Specialty/City/Fee** — No advanced filter or sort options for doctors
- [ ] **Search by Doctor Name** — Text-based search not implemented

### 📅 Appointments
- [ ] **Appointment Cancellation** — No cancel option for patients or doctors
- [ ] **Appointment Rescheduling** — Cannot modify date/time of existing appointments
- [ ] **Appointment Reminders / Notifications** — No push notifications or local reminders
- [ ] **Appointment History Pagination** — All appointments loaded at once, no pagination

### 💬 Chat
- [ ] **Push Notifications for Messages** — No FCM integration for chat notifications
- [ ] **Message Read Receipts** — No seen/delivered indicators
- [ ] **Typing Indicators** — No real-time typing status
- [ ] **Message Deletion / Editing** — Cannot delete or edit sent messages
- [ ] **Chat Search** — Cannot search within conversations
- [ ] **File/Document Sharing** — Only images supported, no PDFs or documents
- [ ] **Video/Voice Calling** — No real-time audio/video call feature

### 🤖 AI Agent
- [ ] **Offline Fallback** — No response if the Railway server is down; no error handling UI
- [ ] **Chat History Persistence** — AI chat history is session-only (in-memory `ArrayList`), lost on activity destroy
- [ ] **Markdown Rendering** — AI responses may contain formatting that is displayed as raw text

### 🔔 Notifications
- [ ] **Firebase Cloud Messaging (FCM)** — Not integrated at all
- [ ] **In-App Notifications** — No notification center or unread indicators
- [ ] **Appointment Status Notifications** — No alerts when appointments are booked or resolved

### 💳 Payments
- [ ] **Online Payment Integration** — No Stripe, PayPal, or any payment gateway
- [ ] **Fee Display at Booking** — Fee shown on doctor profile but not on booking confirmation

### 📊 Analytics & Admin
- [ ] **Admin Dashboard** — No admin panel for managing users/doctors
- [ ] **Analytics / Reporting** — No usage analytics or appointment statistics
- [ ] **Doctor Ratings & Reviews** — No rating system for doctors

### 🧪 Testing
- [ ] **Unit Tests** — No unit test classes exist
- [ ] **UI/Instrumentation Tests** — No Espresso or UI Automator tests
- [ ] **CI/CD Pipeline** — No GitHub Actions or automated build pipeline

### ♿ Accessibility & i18n
- [ ] **Accessibility (a11y)** — No content descriptions on icons/images, no TalkBack optimization
- [ ] **Multi-Language Support** — Only English; no `strings.xml` translations
- [ ] **RTL Layout Support** — `supportsRtl=true` in manifest but not tested

### 📱 UX Improvements
- [ ] **Empty State UI** — No placeholder illustrations when lists are empty (no appointments, no chats, no doctors found)
- [ ] **Loading Skeletons** — No shimmer/skeleton loading states
- [ ] **Pull-to-Refresh** — No swipe-to-refresh on any list
- [ ] **Pagination / Infinite Scroll** — All Firestore queries load full datasets
- [ ] **Dark/Light Mode Toggle** — Dark theme is default; no user toggle exists despite `values-night` resources
- [ ] **Error Handling UI** — Network errors shown as Toasts; no retry or offline-mode screens

### 🏗️ Code Quality
- [ ] **Dependency Injection** — No Dagger/Hilt; all dependencies manually created
- [ ] **MVVM Architecture** — Only `SignupViewModel` exists; most logic lives in Activities/Fragments
- [ ] **Repository Pattern** — Direct Firestore calls in UI layer; no data layer abstraction
- [ ] **ProGuard / R8 Minification** — `minifyEnabled` is `false` in release builds
- [ ] **Crashlytics / Error Reporting** — No Firebase Crashlytics integration
- [ ] **Environment-Based Config** — `.env` file exists but is not programmatically read; secrets hardcoded

---

## License

This project is developed for educational/personal purposes. No license has been specified.

---

<p align="center">
  <b>LogicMed</b> — Smart Healthcare, Smarter Connections 🩺
</p>
