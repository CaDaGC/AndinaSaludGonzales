package pe.upeu.andinasalud.presentation.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.usecase.ContarCitasProgramadasUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase

class InicioViewModel(
    private val obtenerCitasUseCase: ObtenerCitasUseCase,
    private val contarCitasProgramadasUseCase: ContarCitasProgramadasUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<InicioUiState>(InicioUiState.Cargando)
    val uiState: StateFlow<InicioUiState> = _uiState.asStateFlow()

    // SC-B: Cantidad de citas programadas leídas desde dominio (RN-02)
    private val _citasProgramadasCount = MutableStateFlow(0)
    val citasProgramadasCount: StateFlow<Int> = _citasProgramadasCount.asStateFlow()

    // SC-B: Control para habilitar/deshabilitar la acción de solicitar nueva cita (< 3)
    private val _puedeSolicitarNuevaCita = MutableStateFlow(true)
    val puedeSolicitarNuevaCita: StateFlow<Boolean> = _puedeSolicitarNuevaCita.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _uiState.value = InicioUiState.Cargando
            try {
                val citas = obtenerCitasUseCase()
                val proximaCita = citas.firstOrNull { it.estado is EstadoCita.Programada }

                // SC-B: Evaluación de regla RN-02
                val programadasCount = contarCitasProgramadasUseCase()
                _citasProgramadasCount.value = programadasCount
                _puedeSolicitarNuevaCita.value = programadasCount < 3

                // Instancia de Paciente ajustada a tu data class
                val paciente = Paciente(
                    id = "1",
                    nombre = "Juan Perez",
                    correo = "juan.perez@email.com",
                    documento = "70000000",
                    telefono = "987654321"
                )

                _uiState.value = InicioUiState.Exito(
                    paciente = paciente,
                    proximaCita = proximaCita
                )
            } catch (e: Exception) {
                _uiState.value = InicioUiState.Error(e.message ?: "Error al cargar inicio")
            }
        }
    }
}