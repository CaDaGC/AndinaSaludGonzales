package pe.upeu.andinasalud.presentation.solicitar

import pe.upeu.andinasalud.domain.model.Cita

sealed interface SolicitarCitaUiState {
    object Inicial : SolicitarCitaUiState
    object Cargando : SolicitarCitaUiState
    data class Exito(val citaCreada: Cita) : SolicitarCitaUiState
    data class Error(val mensaje: String) : SolicitarCitaUiState
}