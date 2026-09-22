package pe.upeu.andinasalud.presentation.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase

class CitasViewModel(
    private val obtenerCitasUseCase: ObtenerCitasUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CitasUiState>(CitasUiState.Cargando)
    val uiState: StateFlow<CitasUiState> = _uiState.asStateFlow()

    private var todasLasCitas: List<Cita> = emptyList()
    private var filtroActual = FiltroEstado.TODAS
    private var busquedaActual = ""

    init {
        cargarCitas()
    }

    fun cargarCitas() {
        viewModelScope.launch {
            _uiState.value = CitasUiState.Cargando
            try {
                // Simulación de retardo de 800 ms según RF-08
                delay(800)
                todasLasCitas = obtenerCitasUseCase()
                aplicarFiltros()
            } catch (e: Exception) {
                _uiState.value = CitasUiState.Error("Error al cargar las citas: ${e.message}")
            }
        }
    }

    fun seleccionarFiltro(filtro: FiltroEstado) {
        filtroActual = filtro
        aplicarFiltros()
    }

    fun actualizarBusqueda(texto: String) {
        busquedaActual = texto
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        var resultado = todasLasCitas

        // Filtrar por estado (RF-02)
        resultado = when (filtroActual) {
            FiltroEstado.TODAS -> resultado
            FiltroEstado.PROGRAMADAS -> resultado.filter { it.estado is EstadoCita.Programada }
            FiltroEstado.ATENDIDAS -> resultado.filter { it.estado is EstadoCita.Atendida }
            FiltroEstado.CANCELADAS -> resultado.filter { it.estado is EstadoCita.Cancelada }
        }

        // Búsqueda por especialidad o médico sin distinguir mayúsculas/tildes (RF-05)
        if (busquedaActual.isNotBlank()) {
            val query = busquedaActual.normalizar()
            resultado = resultado.filter { cita ->
                cita.especialidad.normalizar().contains(query) ||
                        cita.medico.normalizar().contains(query)
            }
        }

        _uiState.value = CitasUiState.Exito(
            citas = resultado,
            filtroEstado = filtroActual,
            textoBusqueda = busquedaActual
        )
    }

    private fun String.normalizar(): String {
        val tildes = mapOf(
            'á' to 'a', 'é' to 'e', 'í' to 'i', 'ó' to 'o', 'ú' to 'u',
            'Á' to 'a', 'É' to 'e', 'Í' to 'i', 'Ó' to 'o', 'Ú' to 'u', 'ñ' to 'n', 'Ñ' to 'n'
        )
        return this.lowercase().map { tildes[it] ?: it }.joinToString("")
    }
}