package com.example.epigrafewear.presentation.wear

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SessionStatus(val etiqueta: String) {
    DETENIDA("Detenida"),
    EN_CURSO("En curso"),
    PAUSADA("Pausada"),
    FINALIZADA("Finalizada")
}

/**
 * Estado de la sesión de entrenamiento del reloj (Práctica 5).
 * Lo modifica el servicio que recibe los comandos y lo observa la interfaz.
 */
object SessionState {
    data class Resultado(val ok: Boolean, val mensaje: String)

    private val _estado = MutableStateFlow(SessionStatus.DETENIDA)
    val estado: StateFlow<SessionStatus> = _estado.asStateFlow()

    private val _ultimoComando = MutableStateFlow("Ninguno")
    val ultimoComando: StateFlow<String> = _ultimoComando.asStateFlow()

    /** Valida el comando, actualiza el estado y devuelve si fue procesado. */
    @Synchronized
    fun procesar(comando: String): Resultado {
        val actual = _estado.value
        val resultado = when (comando) {
            "INICIAR" ->
                if (actual == SessionStatus.EN_CURSO) {
                    Resultado(false, "La sesión ya está en curso")
                } else {
                    _estado.value = SessionStatus.EN_CURSO
                    Resultado(true, "Sesión iniciada")
                }
            "PAUSAR" ->
                if (actual != SessionStatus.EN_CURSO) {
                    Resultado(false, "Solo se puede pausar una sesión en curso")
                } else {
                    _estado.value = SessionStatus.PAUSADA
                    Resultado(true, "Sesión pausada")
                }
            "FINALIZAR" ->
                if (actual != SessionStatus.EN_CURSO && actual != SessionStatus.PAUSADA) {
                    Resultado(false, "No hay una sesión activa que finalizar")
                } else {
                    _estado.value = SessionStatus.FINALIZADA
                    Resultado(true, "Sesión finalizada")
                }
            else -> Resultado(false, "Comando no reconocido: $comando")
        }
        _ultimoComando.value = if (resultado.ok) comando else "$comando (rechazado)"
        return resultado
    }
}
