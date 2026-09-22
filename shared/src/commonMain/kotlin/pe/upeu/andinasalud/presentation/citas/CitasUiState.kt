package pe.upeu.andinasalud.presentation.citas

import pe.upeu.andinasalud.domain.model.Cita

sealed interface CitasUiState {
    object Cargando : CitasUiState
    data class Exito(
        val citas: List<Cita>,
        val filtroEstado: FiltroEstado = FiltroEstado.TODAS,
        val textoBusqueda: String = ""
    ) : CitasUiState
    data class Error(val mensaje: String) : CitasUiState
}

enum class FiltroEstado {
    TODAS,
    PROGRAMADAS,
    ATENDIDAS,
    CANCELADAS
}