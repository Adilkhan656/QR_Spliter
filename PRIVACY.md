# Privacy Policy for UPI QR Splitter

**Last Updated:** September 19, 2026

**UPI QR Splitter** is built from the ground up as a **privacy-first, local-only, open-source** application.

## 1. Zero Network Data Transmission
- The application does NOT request or use the `android.permission.INTERNET` permission.
- No payment information, merchant names, VPA IDs, or transaction references are ever uploaded to any server, cloud service, database, or third-party analytics provider.

## 2. Zero Unnecessary Permissions
- **No Location**: The application never requests `ACCESS_FINE_LOCATION` or `ACCESS_COARSE_LOCATION`.
- **No Contacts / SMS / Phone**: The application does not access your address book, messages, or phone state.
- **No Camera / Storage**: QR code generation is performed in-memory on the device.

## 3. Data Storage & Local Protection
- Merchant defaults and payment history are stored exclusively in a local database on your device (`EncryptedSharedPreferences` / Android KeyStore).
- You can configure the **Auto-Clear / Data Retention Policy** in Settings to automatically delete local payment history:
  - Immediately after viewing
  - After 1 day
  - After 30 days
  - Custom retention days
  - Or manually clear history at any time.

## 4. No Accounts, No Analytics, No Advertising
- No account registration or login is required.
- No analytics SDKs (e.g. Firebase Analytics, Google Analytics) are included.
- No advertising SDKs or tracking code are included.
