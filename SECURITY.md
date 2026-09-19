# Security Policy & Threat Model

## Reporting a Vulnerability

If you discover a security vulnerability within **UPI QR Splitter**, please report it responsibly by opening a private security advisory on GitHub.

Do NOT create public issues for undisclosed security vulnerabilities.

---

## Threat Model & Risk Analysis

| Threat | Risk / Impact | Mitigation | Limitation / Out of Scope |
| :--- | :--- | :--- | :--- |
| **Malformed Input / URI Injection** | Malicious characters in VPA/Name attempt to inject unauthorized UPI parameters | Strict local regex (`InputValidator`) sanitizes VPA, Name, and Reference inputs before URI encoding. | Cannot prevent user from manually typing a typo in their own VPA. |
| **Wrong VPA Entered** | Payment routed to wrong merchant account | Pre-generation confirmation dialog displays merchant Name, VPA, and total amount before rendering QR code. | User must visually verify that the VPA belongs to them. |
| **Local Data Theft at Rest** | Physical device compromise or extraction of local settings | Preferences encrypted using `EncryptedSharedPreferences` backed by hardware-backed Android KeyStore. | Device-level root compromise or unencrypted physical memory access on rooted devices. |
| **Dependency Supply Chain** | Compromised 3rd party library | Minimal dependencies (only Room, Material 3, Navigation, ZXing, Timber). No network-enabled libraries. | Periodic Dependabot updates & SHA integrity checks. |
| **False Payment Confirmation** | User assumes QR scan equals settled money in bank | Explicit payment status states (`PENDING`, `USER_REPORTED_PAID`, `VERIFIED`). App clearly communicates offline status. | Offline app cannot query bank APIs directly. Merchant must verify settlement in bank app. |

---

## Security Practices
- **No Secret Storage**: Signing keys, keystores, and passwords are never committed to version control.
- **No PIN/Credential Handling**: The application never prompts for, stores, or processes UPI PINs, bank account passwords, or OTPs.
