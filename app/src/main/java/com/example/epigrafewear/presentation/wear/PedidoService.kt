package com.example.epigrafewear.presentation.wear

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

data class PedidoReal(
    val id: Int,
    val numeroPedido: Int,
    val estado: String,
    val cliente: String
)

suspend fun obtenerPedidoReciente(): PedidoReal? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("${ProtocoloEndpoint.ENDPOINT_PEDIDOS}/reciente") // O tu ruta correspondiente
            val conexion = url.openConnection() as HttpURLConnection
            conexion.requestMethod = "GET"

            if (conexion.responseCode == HttpURLConnection.HTTP_OK) {
                val respuesta = conexion.inputStream.bufferedReader().use { it.readText() }
                // Parsear el JSON real que te manda Render / Neon
                // Aquí devuelves el objeto con los datos reales de tu base de datos
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}