package pe.upeu.andinasalud.presentation.detalle

import pe.upeu.andinasalud.domain.model.Cita

sealed interface DetalleCitaUiState {
    object Cargando : DetalleCitaUiState
    data class Exito(
        val cita: Cita,
        val mensajeExito: String? = null
    ) : DetalleCitaUiState
    data class Error(val mensaje: String) : DetalleCitaUiState
}