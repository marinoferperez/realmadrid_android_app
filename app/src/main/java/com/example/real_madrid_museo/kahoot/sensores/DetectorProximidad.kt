package com.example.real_madrid_museo.kahoot.sensores

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class DetectorProximidad(
    context: Context,
    private val onNear: () -> Unit,
    private val onFar: () -> Unit
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val proximitySensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    private val maxRange: Float =
        proximitySensor?.maximumRange ?: 0f

    fun start() {
        proximitySensor?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val distancia = event.values[0]

        if (distancia < maxRange) {
            onNear()
        } else {
            onFar()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}