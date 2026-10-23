package com.example.epigrafewear.presentation.wear

import java.time.Instant
import java.util.UUID
import kotlin.random.Random
import org.json.JSONObject

/**
 * Modelo de una medición de actividad física (Práctica 6).
 * En el reloj solo se genera y se serializa; la validación vive en el teléfono.
 */
data class ActivityRecord(
    val id: String,
    val timestamp: String, // ISO-8601 en UTC, p. ej. 2026-10-19T20:15:30Z
    val heartRate: Int, // latidos por minuto (simulado)
    val steps: Int,
    val durationSeconds: Int
) {
    fun toJson(): String = JSONObject()
        .put("id", id)
        .put("timestamp", timestamp)
        .put("heartRate", heartRate)
        .put("steps", steps)
        .put("durationSeconds", durationSeconds)
        .toString()

    companion object {
        /** Medición de ejemplo, con un identificador distinto cada vez. */
        fun simulado(): ActivityRecord = ActivityRecord(
            id = UUID.randomUUID().toString(),
            timestamp = Instant.now().toString(),
            heartRate = Random.nextInt(60, 161),
            steps = Random.nextInt(0, 2001),
            durationSeconds = Random.nextInt(60, 3601)
        )

        /** Registro con valores fuera de rango, solo para probar la validación del teléfono. */
        fun invalidoDePrueba(): ActivityRecord =
            simulado().copy(steps = -50, heartRate = 400)
    }
}
