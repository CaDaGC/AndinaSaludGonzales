package pe.upeu.andinasalud.presentation.citas

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.Paciente

sealed interface InicioUiState {
    object Cargando : InicioUiState
    data class Exito(
        val paciente: Paciente,
        val proximaCita: Cita?
    ) : InicioUiState
    data class Error(val mensaje: String) : InicioUiState
}