package com.example.epigrafewear.presentation.wear

import android.content.Context
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Práctica 6, actividad 2: genera mediciones simuladas y las envía al teléfono
 * con DataClient (Wearable Data Layer).
 *
 * DataClient guarda el DataItem en el reloj y Google Play services lo sincroniza
 * cuando hay enlace con el teléfono; si el enlace se interrumpe, queda pendiente
 * y se entrega al reconectar.
 */
object ActivitySender {
    private var ultimo: ActivityRecord? = null

    private val _estado = MutableStateFlow("Sin envíos")
    val estado: StateFlow<String> = _estado.asStateFlow()

    /** Botón "Registrar actividad": genera una medición nueva y la envía. */
    fun registrarActividad(context: Context) {
        val registro = ActivityRecord.simulado()
        ultimo = registro
        enviar(context, registro, "Registro")
    }

    /** Prueba de duplicados: vuelve a enviar el último registro con el mismo id. */
    fun reenviarUltimo(context: Context) {
        val registro = ultimo
        if (registro == null) {
            _estado.value = "Primero registra una actividad"
        } else {
            enviar(context, registro, "Reenvío")
        }
    }

    /** Prueba de validación: envía un registro con valores fuera de rango. */
    fun enviarInvalido(context: Context) {
        enviar(context, ActivityRecord.invalidoDePrueba(), "Registro inválido")
    }

    private fun enviar(context: Context, registro: ActivityRecord, etiqueta: String) {
        // Cada registro usa su propia ruta (con su id), así se distinguen mediciones nuevas.
        val request = PutDataMapRequest.create(DataLayerPaths.RECORD_PREFIX + registro.id).apply {
            dataMap.putString(DataLayerPaths.KEY_JSON, registro.toJson())
            // Cambia en cada envío para que un reenvío del mismo id también llegue al teléfono.
            dataMap.putLong(DataLayerPaths.KEY_SENT_AT, System.currentTimeMillis())
        }.asPutDataRequest().setUrgent()

        Wearable.getDataClient(context.applicationContext).putDataItem(request)
            .addOnSuccessListener {
                _estado.value = "$etiqueta ${registro.id.take(8)}: guardado, se sincroniza con el teléfono"
            }
            .addOnFailureListener { error ->
                _estado.value = "Error al enviar: ${error.message}"
            }
    }
}
