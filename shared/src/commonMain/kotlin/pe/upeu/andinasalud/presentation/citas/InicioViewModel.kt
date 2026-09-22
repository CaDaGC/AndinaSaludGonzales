package pe.upeu.andinasalud.presentation.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase

class InicioViewModel(
    private val obtenerCitasUseCase: ObtenerCitasUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<InicioUiState>(InicioUiState.Cargando)
    val uiState: StateFlow<InicioUiState> = _uiState.asStateFlow()

    init {
        cargarDatosInicio()
    }

    fun cargarDatosInicio() {
        viewModelScope.launch {
            _uiState.value = InicioUiState.Cargando
            try {
                // Simulación de retardo de 800 ms según RF-08
                delay(800)

                // Obtenemos todas las citas usando invoke() de ObtenerCitasUseCase
                val citas = obtenerCitasUseCase()

                // Buscamos la primera cita que esté en estado Programada
                val proximaCita = citas.firstOrNull { it.estado is EstadoCita.Programada }

                // Paciente fijo según la especificación del proyecto
                val paciente = pe.upeu.andinasalud.data.local.CitasSimuladas.paciente

                _uiState.value = InicioUiState.Exito(
                    paciente = paciente,
                    proximaCita = proximaCita
                )
            } catch (e: Exception) {
                _uiState.value = InicioUiState.Error("Error al cargar los datos de inicio: ${e.message}")
            }
        }
    }
}