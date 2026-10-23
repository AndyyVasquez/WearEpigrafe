package com.example.epigrafewear.presentation.wear

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import org.json.JSONObject

/**
 * Práctica 5, actividad 3: recibe los comandos del teléfono, los valida,
 * actualiza el estado de la sesión y responde con una confirmación.
 */
class CommandListenerService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        // 1. Identificar la ruta del mensaje
        if (messageEvent.path != DataLayerPaths.COMMAND) return

        // 2. Leer el comando recibido
        val comando = String(messageEvent.data, Charsets.UTF_8).trim().uppercase()

        // 3 y 4. Validar que sea reconocido y actualizar el estado de la sesión
        val resultado = SessionState.procesar(comando)

        // 5. Enviar la respuesta al teléfono que mandó el comando
        val respuesta = JSONObject()
            .put("command", comando)
            .put("ok", resultado.ok)
            .put("state", SessionState.estado.value.name)
            .put("message", resultado.mensaje)
            .toString()

        Wearable.getMessageClient(this).sendMessage(
            messageEvent.sourceNodeId,
            DataLayerPaths.RESPONSE,
            respuesta.toByteArray(Charsets.UTF_8)
        )
    }
}
