package pe.upeu.andinasalud.presentation.solicitar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import kotlin.random.Random

class SolicitarCitaViewModel(
    private val obtenerCitasUseCase: ObtenerCitasUseCase,
    private val solicitarCitaUseCase: SolicitarCitaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SolicitarCitaUiState>(SolicitarCitaUiState.Inicial)
    val uiState: StateFlow<SolicitarCitaUiState> = _uiState.asStateFlow()

    fun solicitarCita(
        especialidad: String,
        medico: String,
        sede: String,
        fecha: String,
        hora: String
    ) {
        if (especialidad.isBlank() || medico.isBlank() || sede.isBlank() || fecha.isBlank() || hora.isBlank()) {
            _uiState.value = SolicitarCitaUiState.Error("Todos los campos son obligatorios.")
            return
        }

        viewModelScope.launch {
            _uiState.value = SolicitarCitaUiState.Cargando
            try {
                // 1. Obtenemos las citas existentes para la validación de negocio
                val citasExistentes = obtenerCitasUseCase()

                // 2. Construimos el objeto Cita
                val nuevaCita = Cita(
                    id = Random.nextInt(1000, 9999),
                    especialidad = especialidad,
                    medico = medico,
                    sede = sede,
                    fecha = fecha,
                    hora = hora,
                    estado = EstadoCita.Programada(recordatorioActivo = true)
                )

                // 3. Invocamos el caso de uso pasándole la cita y las citas existentes
                solicitarCitaUseCase(
                    cita = nuevaCita,
                    citasExistentes = citasExistentes
                )
                    .onSuccess { citaCreada ->
                        _uiState.value = SolicitarCitaUiState.Exito(citaCreada)
                    }
                    .onFailure { error ->
                        _uiState.value = SolicitarCitaUiState.Error(
                            error.message ?: "No se pudo procesar la solicitud de cita."
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = SolicitarCitaUiState.Error(
                    e.message ?: "Error al procesar la solicitud."
                )
            }
        }
    }

    fun reiniciarEstado() {
        _uiState.value = SolicitarCitaUiState.Inicial
    }
}