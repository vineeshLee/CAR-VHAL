# CarVHAL Android Automotive App

This Android Automotive application, `com.vineesh.carvhal`, demonstrates how to interact with the Vehicle Hardware Abstraction Layer (VHAL) to read various vehicle properties. It showcases requesting necessary permissions and retrieving data such as engine temperature, ABS status, tire pressure, fuel level, and more.

## Features

*   **Connects to the Car Service:** Establishes a connection to the Android Automotive Car service.
*   **Permission Handling:**
    *   Requests the `android.car.permission.CAR_ENERGY` permission at runtime.
    *   Checks and requests `android.car.permission.CAR_DYNAMICS_STATE` for accessing properties like ABS status.
    *   Checks and requests `android.car.permission.CAR_ENGINE_DETAILED` for accessing properties like engine coolant temperature.
    *   Handles permission grant and denial results.
*   **Reads Vehicle Properties:**
    *   **ABS Status:** Reads the Anti-lock Braking System active status (`VehiclePropertyIds.ABS_ACTIVE`).
    *   **Engine Coolant Temperature:** Reads the engine coolant temperature (`VehiclePropertyIds.ENGINE_COOLANT_TEMP`).
    *   **Engine Oil Temperature:** (Functionality present but commented out in `onCreate`) Reads the engine oil temperature (`VehiclePropertyIds.ENGINE_OIL_TEMP`).
    *   **Hazard Lights:** (Functionality present but commented out in `onCreate`) Reads the hazard lights switch status (`VehiclePropertyIds.HAZARD_LIGHTS_SWITCH`).
    *   **Tire Pressure:** Reads the tire pressure for available tire areas (`VehiclePropertyIds.TIRE_PRESSURE`).
    *   **Fuel Level:** Reads the current fuel level (`VehiclePropertyIds.FUEL_LEVEL`).
    *   **EV Battery Level:** Reads the Electric Vehicle battery level (using a custom property ID `291504905`).
    *   **Outside Temperature:** Reads the environmental outside temperature (using a custom property ID `291505923`).
*   **User Interface:**
    *   Uses ViewBinding (`ActivityMainBinding`) for safe and easy view access.
    *   Displays the retrieved vehicle data in a `TextView`.
    *   Provides buttons to trigger the reading of specific vehicle properties.
*   **Lifecycle Management:** Properly disconnects from the Car service in `onDestroy`.

## Key Components

*   **`MainActivity.kt`:** The main and only `Activity` in this application. It handles all the logic for connecting to the car, requesting permissions, and reading/displaying vehicle data.
*   **`activity_main.xml`:** (Assumed) The layout file defining the UI with buttons to trigger data reads and a `TextView` to display results.
*   **`AndroidManifest.xml`:** Declares necessary permissions and application components. It includes:
    *   `android.hardware.type.automotive` feature requirement.
    *   A list of CAR permissions required by the app (e.g., `CAR_POWERTRAIN`, `CAR_ENGINE_DETAILED`, `CAR_TIRES`, `CAR_DYNAMICS_STATE`, `READ_CAR_ENGINE`, `CAR_ENERGY`, `CAR_EXTERIOR_ENVIRONMENT`).

## How it Works

1.  **Initialization (`onCreate`)**:
    *   Sets up ViewBinding.
    *   Checks if the `android.car.permission.CAR_ENERGY` permission is granted.
        *   If not granted, it requests the permission.
        *   If granted, it proceeds to call `setupCar()`.
    *   Sets `OnClickListener`s for various buttons to trigger the respective data reading functions.

2.  **Car Service Setup (`setupCar`)**:
    *   Creates an instance of the `Car` service using `Car.createCar(this)`.
    *   Connects to the `Car` service if it's not already connected.
    *   Gets an instance of `CarPropertyManager` from the `Car` service.

3.  **Reading Vehicle Properties (e.g., `readAbsState`, `readEngineCoolantTemp`)**:
    *   **Permission Check:** Before attempting to read a property, it checks if the required permission (e.g., `CAR_DYNAMICS_STATE` for ABS, `CAR_ENGINE_DETAILED` for coolant temp) is granted using `ContextCompat.checkSelfPermission`.
    *   **Request Permission (if needed):**
        *   If the permission is not granted, it calls a specific function (e.g., `checkAndRequestDynamicsStatePermission`, `checkCarEngineDetailedPermission`) to request it.
        *   These functions might show a rationale (though less common for system-level car permissions) before making the `ActivityCompat.requestPermissions` call.
    *   **Property Retrieval:**
        *   If permissions are granted and the car is connected, it uses `carPropertyManager.getProperty()` to fetch the specific `CarPropertyValue`.
        *   The property ID (e.g., `ABS_ACTIVE`, `ENGINE_COOLANT_TEMP`) and the expected data type (e.g., `Boolean::class.java`, `Float::class.java`) are passed to this method.
    *   **Display Data:** The retrieved value is then displayed in the `textResult` TextView.
    *   **Error Handling:** Includes `try-catch` blocks for `SecurityException` (permission denial) and other potential exceptions during property reads, especially for properties like fuel level, EV battery, and outside temperature.

4.  **Permission Result Handling (`onRequestPermissionsResult`)**:
    *   This callback receives the result of permission requests.
    *   It checks the `requestCode` to identify which permission request this result belongs to.
    *   If a permission is granted, it typically calls `setupCar()` to ensure the car service is ready.
    *   It updates the `textResult` TextView to inform the user about the permission status.
    *   Logs messages indicating whether permissions were granted or denied, which is useful for debugging, especially for privileged permissions.

5.  **Cleanup (`onDestroy`)**:
    *   Disconnects from the `Car` service to release resources.

## Setup and Usage

1.  **Environment:** This app is designed for an Android Automotive OS environment (either an emulator or a real vehicle).
2.  **Build:** Build and install the APK onto the target Android Automotive device.
3.  **Permissions:**
    *   The app will request `android.car.permission.CAR_ENERGY` on launch.
    *   For features like reading ABS status or engine coolant temperature, the app might require privileged permissions (`android.car.permission.CAR_DYNAMICS_STATE`, `android.car.permission.CAR_ENGINE_DETAILED`). On standard Android Automotive builds, these often need to be pre-granted or the app needs to be signed with the system key and whitelisted. The app attempts to request them, but success may depend on the system's configuration.
4.  **Interaction:**
    *   Launch the "CARVHAL" app.
    *   Grant the `CAR_ENERGY` permission when prompted.
    *   Click the various "Read..." buttons to attempt to fetch and display the corresponding vehicle data.
    *   Observe the `textResult` TextView for the data or any error/permission messages.
    *   Check Logcat for more detailed permission status messages.

## Custom Property IDs

The application uses the following integer values for vehicle properties that might not be standard `VehiclePropertyIds` constants or for which the developer chose to use direct integer values:

*   `EV_BATTERY_LEVEL = 291504905`
*   `ENV_OUTSIDE_TEMPERATURE = 291505923`

These values should correspond to actual property IDs supported by the VHAL on the target vehicle.

## Notes

*   The effectiveness of reading certain properties (especially those requiring privileged permissions like `CAR_DYNAMICS_STATE` or `CAR_ENGINE_DETAILED`) heavily depends on the Android Automotive OS build and how the app is installed and privileged.
*   The UI is basic and primarily for demonstrating the core functionality of VHAL interaction.
*   The comments in `onCreate` for `buttonReadOilTemp` and `buttonReadHazard` suggest these features were either under development or temporarily disabled.

This README provides an overview of the `com.vineesh.carvhal` package and its `MainActivity`.
