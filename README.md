# Android Google Drive Sync

Syncs files from your Android device to your Google Drive account. 

- Does not consider device or drive space.
- Offers both upload and download or just one of either.
- Options to download or upload in event of checksum mismatch.
- For upload options (upload,upload and download), offers options to either delete on upload or delete if file is present in drive (or neither).

## Disclaimer

This app and its contributors are not affiliated with or endorsed by Google or its services.

## How to build / download

I will get a few violations on my Google Play account before I am able to successfully upload this to the Google Play Store, so you must build it yourself.

Also this way you can have your own Google Cloud quotas and not share mine.

1) https://console.cloud.google.com/
2) Create a new Google Cloud project
3) https://console.cloud.google.com/marketplace/product/google/drive.googleapis.com
4) Enable
5) OAuth Consent Screen -> Enter your information -> Add or Remove Scopes -> add ./auth/drive
6) Add yourself as a test user on next screen. Any account you want to sync drive with must be a test user.
7) Credentials (side panel) -> Create Credentials -> OAuth client ID -> Application Type: Android -> package name: com.lockermanwxlf.drivesync, sha1 cert. fingerprint: read below.
8) Pull (or download) this project
9) Gradle sync, build, go plug your phone in with USB debugging and debug on your phone to get the app on it.

## How to get SHA1 certificate fingerprint
1) go Android Studio -> hamburger Menu -> File -> Settings -> Experimental -> Configure all Gradle tasks during Gradle Sync -> sync gradle. you can build app to do this.
2) right side of screen Gradle -> Tasks -> android -> signingReport -> look your SHA1: xx:xx:xx:xx:xx...
3) boom
