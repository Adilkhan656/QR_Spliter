# UPI QR Splitter (Open Source)

[![Android CI](https://github.com/musclesos/upi-qr-splitter/actions/workflows/android.yml/badge.svg)](https://github.com/musclesos/upi-qr-splitter/actions)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)

**UPI QR Splitter** is a privacy-first, 100% offline, local Android utility that splits large payment amounts into multiple smaller, compliant payment chunks and generates valid NPCI-standard UPI QR codes entirely on your device.

> **Important Regulatory & Legal Disclaimer:**
> This application is an open-source technical utility for generating and managing multiple UPI payment requests. It is not a bank, payment system provider (PSP), third-party application provider (TPAP), or payment gateway. It does NOT process, hold, route, or settle funds.
> 
> Users and merchants are solely responsible for complying with all applicable NPCI rules, Reserve Bank of India (RBI) regulations, bank/acquirer agreements, tax laws, and commercial payment regulations.
> 
> **Do NOT treat this application as a guaranteed MDR avoidance tool, tax-bypass tool, or government-rule bypass.** Splitting a commercial transaction into smaller transaction chunks does NOT alter the legal nature or regulatory obligations of the underlying commercial transaction.

---

## 🔒 Core Privacy & Security Principles

- **100% Offline & Local-First**: Works completely in Airplane Mode. Zero network calls, zero server communication, zero analytics, zero tracking, zero remote configuration.
- **Zero Dangerous Runtime Permissions**: Requires NO Internet permission, NO Location, NO Contacts, NO SMS, NO Camera, NO Storage permissions.
- **Configurable Auto-Clear Data Retention**:
  - `Never`: Keeps local history permanently.
  - `Immediately After Viewing`: Automatically purges all session history as soon as you exit the History view.
  - `1 Day / 30 Days`: Automatically purges sessions older than 24 hours or 30 days.
  - `Custom Days`: User-defined auto-clear threshold (e.g. 7 days).
- **Monetary Accuracy**: Performs all financial calculations using exact integer arithmetic (**paise**, `Long`) to guarantee `sum(parts) == totalAmount`.
- **Hardware-Backed Encryption**: Encrypts merchant settings at rest using Android's `EncryptedSharedPreferences` backed by the **Android KeyStore** (AES-256-GCM / AES-256-SIV).
- **Standard NPCI URIs**: Constructs official `upi://pay` deep links URL-encoded with standard parameters (`pa`, `pn`, `am`, `cu`, `tr`, `tn`).

---

## 🚀 How It Works

Example:
- **Total Amount**: ₹6,000
- **Max Chunk**: ₹1,999

**Result**:
- Chunk 1: ₹1,999
- Chunk 2: ₹1,999
- Chunk 3: ₹1,999
- Chunk 4: ₹3

*Invariant*: `1,999 + 1,999 + 1,999 + 3 = 6,000`

---

## 🏗️ Architecture & Tech Stack

```
app/src/main/java/com/musclesos/qrspliter/
├── core/            # Low-level utilities (QR generator, URI builder, Security, Validation)
├── domain/          # Pure Kotlin domain logic (Models, Repositories, Use cases, PaymentSplitEngine)
├── data/            # Data layer (Room Entities, DAOs, Mappers, Repository Implementations)
└── presentation/    # Presentation layer (MVVM, ViewBinding, Navigation Component, Fragments)
```

- **Language**: Kotlin 2.0+
- **Architecture**: MVVM + Repository Pattern + Clean Architecture
- **Concurrency**: Kotlin Coroutines & StateFlow
- **Database**: Room Database
- **UI**: Material 3 (Dark/Light Premium Monochrome Theme)
- **QR Engine**: ZXing (Local rendering)
- **Security**: Android KeyStore & EncryptedSharedPreferences

---

## 🔨 Build & Testing

### Requirements
- Android Studio 2026.1 / Ladybug or newer
- JDK 17
- Android SDK 35

### Commands
```bash
# Build Debug APK
./gradlew assembleDebug

# Run Unit Tests
./gradlew testDebugUnitTest

# Run Android Lint
./gradlew lintDebug
```

---

## 📄 Open Source License

Distributed under the [Apache 2.0 License](LICENSE).
