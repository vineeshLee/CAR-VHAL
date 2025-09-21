package com.vineesh.carvhal

import android.annotation.SuppressLint
import android.car.Car
import android.car.VehiclePropertyIds
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vineesh.carvhal.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var car: Car
    private lateinit var carPropertyManager: CarPropertyManager

    companion object {
        const val ENGINE_COOLANT_TEMP = VehiclePropertyIds.ENGINE_COOLANT_TEMP
        const val ABS_ACTIVE = VehiclePropertyIds.ABS_ACTIVE
        const val ENGINE_OIL_TEMP = VehiclePropertyIds.ENGINE_OIL_TEMP
        const val HAZARD_LIGHTS_SWITCH = VehiclePropertyIds.HAZARD_LIGHTS_SWITCH
        const val TIRE_PRESSURE = VehiclePropertyIds.TIRE_PRESSURE
        const val FUEL_LEVEL = VehiclePropertyIds.FUEL_LEVEL
        const val EV_BATTERY_LEVEL = 291504905
        const val ENV_OUTSIDE_TEMPERATURE = 291505923

        private const val PERMISSION_REQUEST_CODE = 1234
        private const val DYNAMICS_STATE_PERMISSION_REQUEST_CODE = 1002 // New for CAR_DYNAMICS_STATE
        private const val CAR_ENGINE_DETAILED_PERMISSION_REQUEST_CODE = 1003 // New for CAR_DYNAMICS_STATE
    }

    private lateinit var binding: ActivityMainBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkSelfPermission("android.car.permission.CAR_ENERGY") != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf("android.car.permission.CAR_ENERGY"),
                PERMISSION_REQUEST_CODE
            )
        } else {
            setupCar()
        }

        binding.buttonReadAbs.setOnClickListener { readAbsState() }
        binding.buttonReadTemp.setOnClickListener { readEngineCoolantTemp() }
        //binding.buttonReadOilTemp.setOnClickListener { readEngineCoolantOilTemp() }
        //binding.buttonReadHazard.setOnClickListener { readHazardLightState() }
        binding.buttonReadTirePressure.setOnClickListener { readTirePressure() }
        binding.buttonReadFuel.setOnClickListener { readFuelLevel() }
        binding.buttonReadEvBattery.setOnClickListener { readEvBatteryLevel() }
        binding.buttonReadOutsideTemp.setOnClickListener { readOutsideTemperature() }
    }

    private fun setupCar() {
        car = Car.createCar(this)
        if (!car.isConnected) car.connect()
        carPropertyManager = car.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
    }

    @SuppressLint("SetTextI18n")
    private fun readAbsState() {
        if (ContextCompat.checkSelfPermission(
                this,
                "android.car.permission.CAR_DYNAMICS_STATE"
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (car.isConnected) {
                val absActiveValue: CarPropertyValue<Boolean>? =
                    carPropertyManager.getProperty(Boolean::class.java, ABS_ACTIVE, 0)
                binding.textResult.text = "ABS Status: ${absActiveValue?.value ?: "UNKNOWN"}"
            } else {
                binding.textResult.text =
                    "Car API not ready. Ensure all permissions granted and car connected."
            }
        } else {
            binding.textResult.text = "CAR_DYNAMICS_STATE permission not granted. Cannot read ABS state."
            checkAndRequestDynamicsStatePermission()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkAndRequestDynamicsStatePermission() {
        val permission = "android.car.permission.CAR_DYNAMICS_STATE"
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            Log.i("Permissions", "$permission not granted. Requesting...")
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                // This will LIKELY NOT be true for CAR_DYNAMICS_STATE as it's not a typical "dangerous" permission.
                // But including for completeness of the pattern.
                Log.i("Permissions", "Showing rationale for $permission.")
                binding.textResult.text =
                    "This app needs Dynamics State access to read sensor data like ABS status. Please grant the permission."
                // Show a dialog here in a real app to explain, then request.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    DYNAMICS_STATE_PERMISSION_REQUEST_CODE
                )
            } else {
                // No explanation needed (or permission is system-level and can't be requested this way)
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    DYNAMICS_STATE_PERMISSION_REQUEST_CODE
                )
            }
        } else {
            // Permission has already been granted (likely because the app is privileged)
            Log.i("Permissions", "$permission already granted.")
            // If all permissions are now granted, ensure car is set up
            val hasDynamicsState = ContextCompat.checkSelfPermission(this, "android.car.permission.CAR_DYNAMICS_STATE") == PackageManager.PERMISSION_GRANTED
            if (hasDynamicsState) {
                setupCar()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkCarEngineDetailedPermission() {
        val permission = "android.car.permission.CAR_ENGINE_DETAILED"
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            Log.i("Permissions", "$permission not granted. Requesting...")
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                // This will LIKELY NOT be true for CAR_DYNAMICS_STATE as it's not a typical "dangerous" permission.
                // But including for completeness of the pattern.
                Log.i("Permissions", "Showing rationale for $permission.")
                binding.textResult.text =
                    "This app needs engine detailed access to read sensor data like cool temperature. Please grant the permission."
                // Show a dialog here in a real app to explain, then request.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    CAR_ENGINE_DETAILED_PERMISSION_REQUEST_CODE
                )
            } else {
                // No explanation needed (or permission is system-level and can't be requested this way)
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    CAR_ENGINE_DETAILED_PERMISSION_REQUEST_CODE
                )
            }
        } else {
            // Permission has already been granted (likely because the app is privileged)
            Log.i("Permissions", "$permission already granted.")
            val hasEngineDetailed = ContextCompat.checkSelfPermission(this, "android.car.permission.CAR_ENGINE_DETAILED") == PackageManager.PERMISSION_GRANTED
            if (hasEngineDetailed) {
                setupCar()
            }
        }
    }



@SuppressLint("SetTextI18n")
private fun readEngineCoolantTemp() {
    if (ContextCompat.checkSelfPermission(
            this,
            "android.car.permission.CAR_ENGINE_DETAILED"
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        if (car.isConnected) {
            val coolantTempValue: CarPropertyValue<Float>? =
                carPropertyManager.getProperty(Float::class.java, ENGINE_COOLANT_TEMP, 0)
            binding.textResult.text =
                "Coolant temperature: ${coolantTempValue?.value ?: "UNKNOWN"} °C"
        } else {
            binding.textResult.text =
                "Car API not ready. Ensure all permissions granted and car connected."
        }
    } else {
        binding.textResult.text = "CAR_DYNAMICS_STATE permission not granted. Cannot read ABS state."
        checkCarEngineDetailedPermission()
    }
}

@SuppressLint("SetTextI18n")
private fun readEngineCoolantOilTemp() {
    val coolantOilTempValue: CarPropertyValue<Float>? =
        carPropertyManager.getProperty(Float::class.java, ENGINE_OIL_TEMP, 0)
    binding.textResult.text = "Engine oil temperature: ${coolantOilTempValue?.value ?: "UNKNOWN"} °C"
}

@SuppressLint("SetTextI18n")
private fun readHazardLightState() {
    val value: CarPropertyValue<Int>? =
        carPropertyManager.getProperty(Int::class.java, HAZARD_LIGHTS_SWITCH, 0)
    val state = if (value?.value == 1) "Activés" else "Disabled"
    binding.textResult.text = "Hazard lights: $state"
}

@SuppressLint("SetTextI18n")
private fun readTirePressure() {
    val config = carPropertyManager.propertyList.find { it.propertyId == TIRE_PRESSURE }
    val tireAreaIds = config?.areaIds
    if (tireAreaIds != null && tireAreaIds.isNotEmpty()) {
        val pressures = tireAreaIds.map { areaId ->
            val pressure = carPropertyManager.getProperty(Float::class.java, TIRE_PRESSURE, areaId)
            "Zone $areaId : ${pressure?.value ?: "N/A"} kPa"
        }
        binding.textResult.text = pressures.joinToString("\n")
    } else {
        binding.textResult.text = "Tire pressure not available"
    }
}

@SuppressLint("SetTextI18n")
private fun readFuelLevel() {
    try {
        val value = carPropertyManager.getProperty(java.lang.Float::class.java, FUEL_LEVEL, 0)
        binding.textResult.text = "Fuel level : ${value?.value ?: "UNKNOWN"} mL"
    } catch (e: SecurityException) {
        binding.textResult.text = "⚠️ Permission denied to read fuel level"
    } catch (e: Exception) {
        binding.textResult.text = "Fuel error: ${e.message}"
    }
}

@SuppressLint("SetTextI18n")
private fun readEvBatteryLevel() {
    try {
        val value = carPropertyManager.getProperty(java.lang.Float::class.java, EV_BATTERY_LEVEL, 0)
        binding.textResult.text = "EV battery level: ${value?.value ?: "UNKNOWN"} Wh"
    } catch (e: SecurityException) {
        binding.textResult.text = "⚠️ Permission denied to read EV battery level\n"
    } catch (e: Exception) {
        binding.textResult.text = "EV battery reading error: ${e.message}"
    }
}

@SuppressLint("SetTextI18n")
private fun readOutsideTemperature() {
    try {
        val value =
            carPropertyManager.getProperty(Float::class.javaObjectType, ENV_OUTSIDE_TEMPERATURE, 0)
        binding.textResult.text = "Outdoor temperature : ${value?.value ?: "Unknown"} °C"
    } catch (e: SecurityException) {
        binding.textResult.text = "⚠️ Permission denied to read outside temperature"
    } catch (e: Exception) {
        binding.textResult.text = "Temperature error: ${e.message}"
    }
}


@SuppressLint("SetTextI18n")
override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
        PERMISSION_REQUEST_CODE -> {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.textResult.text = "✅ Permission CAR_ENERGY granted"
                setupCar()
            } else {
                binding.textResult.text = "❌ Permission CAR_ENERGY refused"
            }
        }
        DYNAMICS_STATE_PERMISSION_REQUEST_CODE -> {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("PermissionsResult", "CAR_DYNAMICS_STATE permission granted via request. (Likely app is privileged)")
                binding.textResult.text = "✅ CAR_DYNAMICS_STATE permission granted!"
                setupCar()
            } else {
                Log.w("PermissionsResult", "CAR_DYNAMICS_STATE permission DENIED via request. App may need to be privileged.")
                binding.textResult.text = "❌ CAR_DYNAMICS_STATE permission denied. This app may require system privileges for this feature."
            }
        }
        CAR_ENGINE_DETAILED_PERMISSION_REQUEST_CODE -> {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("PermissionsResult", "CAR_DYNAMICS_STATE permission granted via request. (Likely app is privileged)")
                binding.textResult.text = "✅ CAR_ENGINE_DETAILED permission granted!"
                setupCar()
            } else {
                Log.w("PermissionsResult", "CAR_ENGINE_DETAILED permission DENIED via request. App may need to be privileged.")
                binding.textResult.text = "❌ CAR_ENGINE_DETAILED permission denied. This app may require system privileges for this feature."
            }
        }
    }
}

override fun onDestroy() {
    super.onDestroy()
    if (::car.isInitialized && car.isConnected) {
        car.disconnect()
    }
}
}