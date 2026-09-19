# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0] - 2026-09-19

### Added
- **Core Payment Splitting Engine**: Integer-based paise arithmetic guaranteeing `sum(parts) == totalAmount`.
- **UPI URI Construction**: Standard NPCI `upi://pay` deep link generation with parameters (`pa`, `pn`, `am`, `cu`, `tr`, `tn`).
- **Local QR Rendering**: ZXing QR code generator with Error Correction Level H.
- **Auto-Clear Data Retention**: Configurable history retention policies (Never, Immediately After Viewing, 1 Day, 30 Days, Custom Days).
- **Encrypted Preferences**: Hardware-backed Android KeyStore encryption for merchant defaults.
- **Premium Monochrome UI**: Dark/Light high-contrast Material 3 UI.
