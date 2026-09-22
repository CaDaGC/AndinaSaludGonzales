package pe.upeu.andinasalud.presentation.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.usecase.ReprogramarCitaUseCase

sealed interface DetalleUiState {
    object Cargando : DetalleUiState
    data class Exito(val cita: Cita) : DetalleUiState
    data class Error(val mensaje: String) : DetalleUiState
}

class DetalleCitaViewModel(
    private val obtenerCitasUseCase: ObtenerCitasUseCase,
    private val cancelarCitaUseCase: CancelarCitaUseCase,
    private val reprogramarCitaUseCase: ReprogramarCitaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetalleUiState>(DetalleUiState.Cargando)
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    private val _mensajeAccion = MutableStateFlow<String?>(null)
    val mensajeAccion: StateFlow<String?> = _mensajeAccion.asStateFlow()

    fun cargarDetalle(citaId: Int) {
        viewModelScope.launch {
            _uiState.value = DetalleUiState.Cargando
            try {
                // Obtenemos la lista de citas y buscamos por ID de tipo Int
                val citas = obtenerCitasUseCase()
                val cita = citas.find { it.id == citaId }

                if (cita != null) {
                    _uiState.value = DetalleUiState.Exito(cita)
                } else {
                    _uiState.value = DetalleUiState.Error("No se encontró la cita solicitada")
                }
            } catch (e: Exception) {
                _uiState.value = DetalleUiState.Error(e.message ?: "Error al cargar la cita")
            }
        }
    }

    fun cancelarCita(cita: Cita, motivo: String) {
        viewModelScope.launch {
            cancelarCitaUseCase(cita, motivo)
                .onSuccess { citaCancelada ->
                    _uiState.value = DetalleUiState.Exito(citaCancelada)
                    _mensajeAccion.value = "Cita cancelada correctamente"
                }
                .onFailure { error ->
                    _mensajeAccion.value = error.message ?: "Error al cancelar cita"
                }
        }
    }

    fun reprogramarCita(citaId: Int, nuevaFecha: String, nuevaHora: String) {
        viewModelScope.launch {
            reprogramarCitaUseCase(citaId, nuevaFecha, nuevaHora)
                .onSuccess {
                    cargarDetalle(citaId)
                    _mensajeAccion.value = "Cita reprogramada con éxito"
                }
                .onFailure { error ->
                    _mensajeAccion.value = error.message ?: "Error al reprogramar cita"
                }
        }
    }

    fun limpiarMensaje() {
        _mensajeAccion.value = null
    }
}