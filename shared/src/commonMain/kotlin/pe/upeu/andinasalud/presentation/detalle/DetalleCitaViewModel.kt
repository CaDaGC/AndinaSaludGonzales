package pe.upeu.andinasalud.presentation.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase

class DetalleCitaViewModel(
    private val obtenerCitasUseCase: ObtenerCitasUseCase,
    private val cancelarCitaUseCase: CancelarCitaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetalleCitaUiState>(DetalleCitaUiState.Cargando)
    val uiState: StateFlow<DetalleCitaUiState> = _uiState.asStateFlow()

    private var citaActual: Cita? = null

    fun cargarDetalle(citaId: Int) {
        viewModelScope.launch {
            _uiState.value = DetalleCitaUiState.Cargando
            try {
                delay(800) // Simulación de retardo RF-08
                val citas = obtenerCitasUseCase()
                citaActual = citas.firstOrNull { it.id == citaId }

                if (citaActual != null) {
                    _uiState.value = DetalleCitaUiState.Exito(cita = citaActual!!)
                } else {
                    _uiState.value = DetalleCitaUiState.Error("No se encontró la cita especificada.")
                }
            } catch (e: Exception) {
                _uiState.value = DetalleCitaUiState.Error("Error al obtener detalle: ${e.message}")
            }
        }
    }

    fun cancelarCita(motivo: String) {
        val cita = citaActual ?: return
        viewModelScope.launch {
            // Le pasamos el objeto 'cita' completo y el 'motivo'
            cancelarCitaUseCase(cita, motivo)
                .onSuccess { citaCancelada ->
                    citaActual = citaCancelada
                    _uiState.value = DetalleCitaUiState.Exito(
                        cita = citaCancelada,
                        mensajeExito = "Cita cancelada correctamente."
                    )
                }
                .onFailure { error ->
                    _uiState.value = DetalleCitaUiState.Error(
                        error.message ?: "No se pudo cancelar la cita."
                    )
                }
        }
    }
}