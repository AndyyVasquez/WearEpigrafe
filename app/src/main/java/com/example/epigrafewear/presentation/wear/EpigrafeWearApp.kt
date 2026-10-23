package com.example.epigrafewear.presentation.wear

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

// Paleta institucional Epígrafe
private val VerdeEpigrafe = Color(0xFF3B4D36)
private val CremaFondo = Color(0xFFF8F6F0)
private val TextoGris = Color(0xFF7A7669)

data class NotificacionPedido(
    val id: String,
    val titulo: String,
    val mensaje: String,
    val hora: String
)

@Composable
fun EpigrafeWearApp() {
    var listaPedidos by remember { mutableStateOf<List<NotificacionPedido>>(emptyList()) }
    var estadoConexion by remember { mutableStateOf("Sincronizando...") }

    // Función para traer los pedidos reales desde Render y Neon.tech
    suspend fun sincronizarPedidosReales() {
        withContext(Dispatchers.IO) {
            try {
                val url = URL(ProtocoloEndpoint.ENDPOINT_PEDIDOS)
                val conexion = url.openConnection() as HttpURLConnection
                conexion.requestMethod = "GET"
                conexion.connectTimeout = 15000
                conexion.readTimeout = 15000

                if (conexion.responseCode == HttpURLConnection.HTTP_OK) {
                    val respuesta = conexion.inputStream.bufferedReader().use { it.readText() }
                    val jsonArray = JSONArray(respuesta)
                    val tempLista = mutableListOf<NotificacionPedido>()

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val id = obj.optString("id", "$i")
                        val cliente = obj.optString("nombre_cliente", "Cliente")
                        val estado = obj.optString("estado", "En mostrador")
                        val total = obj.optString("total", "0")

                        tempLista.add(
                            NotificacionPedido(
                                id = id,
                                titulo = "Pedido #$id ($cliente)",
                                mensaje = "$estado · Total: $$total",
                                hora = "Reciente"
                            )
                        )
                    }

                    withContext(Dispatchers.Main) {
                        listaPedidos = tempLista
                        estadoConexion = if (tempLista.isEmpty()) "No hay pedidos activos" else "Conectado"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    estadoConexion = "Modo local / Sin servidor"
                    // Datos de respaldo por si el servidor está dormido en Render
                    listaPedidos = listOf(
                        NotificacionPedido("1", "Pedido listo", "Pasa al mostrador 2", "Hace un momento"),
                        NotificacionPedido("2", "Club de lectura", "Hoy a las 7:00 pm", "Recordatorio")
                    )
                }
            }
        }
    }

    // Mantener sincronización en tiempo real cada 8 segundos
    LaunchedEffect(Unit) {
        while (true) {
            sincronizarPedidosReales()
            delay(8000L)
        }
    }

    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(CremaFondo)
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                Text(
                    text = "Notificaciones",
                    color = VerdeEpigrafe,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            }

            item {
                Text(
                    text = estadoConexion,
                    color = TextoGris,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }

            items(listaPedidos, key = { it.id }) { item ->
                Card(
                    onClick = { /* Acción al tocar */ },
                    backgroundPainter = CardDefaults.cardBackgroundPainter(
                        startBackgroundColor = Color.White,
                        endBackgroundColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = item.titulo,
                            color = VerdeEpigrafe,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.mensaje,
                            color = TextoGris,
                            fontSize = 10.sp,
                            maxLines = 2
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        // Forzar sincronización manual
                        // CoroutineScope manejado externamente o simple recarga
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = VerdeEpigrafe,
                        contentColor = CremaFondo
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                ) {
                    Text(text = "Actualizar pedidos", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}