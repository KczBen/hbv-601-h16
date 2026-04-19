# RecipeHub

The app is built in Android Studio, for API level 31 (Android 12).

## Building

To build the app, run the following command in the terminal:

### Windows

```powershell
gradlew.bat assembleRelease
```

### Linux/Mac

```sh
./gradlew assembleRelease
```

## Running

After building, the app will be located in the `app/build/outputs/apk/release/` directory. The APK can be installed on any Android device or emulator with API level 31 or above.

Alternatively, use the built-in functionality in Android Studio to build and run the app in an emulator, or on a device connected via ADB.

## External API

The app connects to the API at https://hbv-501-h24.onrender.com/. Before running the app, load the URL in a browser to wake up the Render instance so that the app can load.
