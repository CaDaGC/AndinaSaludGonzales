package pe.upeu.andinasalud.presentation.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private var filtroEstadoActual: FiltroEstado = FiltroEstado.TODAS
    private var soloHoyActual: Boolean = false
    private var busquedaActual: String = ""
    private val fechaHoySimulada = "2026-09-22"

    init {
        cargarCitas()
    }

    fun cargarCitas() {
        viewModelScope.launch {
            _uiState.value = CitasUiState.Cargando
            try {
                val listaCompleta = obtenerCitasUseCase()
                val listaFiltrada = aplicarFiltros(listaCompleta)

                _uiState.value = CitasUiState.Exito(
                    citas = listaFiltrada,
                    filtroEstado = filtroEstadoActual,
                    textoBusqueda = busquedaActual
                )
            } catch (e: Exception) {
                _uiState.value = CitasUiState.Error(e.message ?: "Error al cargar citas")
            }
        }
    }

    // SC-A: Lógica combinada de filtros resuelta dentro del ViewModel
    private fun aplicarFiltros(lista: List<Cita>): List<Cita> {
        return lista.filter { cita ->
            val cumpleEstado = when (filtroEstadoActual) {
                FiltroEstado.PROGRAMADAS -> cita.estado is EstadoCita.Programada
                FiltroEstado.ATENDIDAS -> cita.estado is EstadoCita.Atendida
                FiltroEstado.CANCELADAS -> cita.estado is EstadoCita.Cancelada
                FiltroEstado.TODAS -> true
            }

            val cumpleHoy = if (soloHoyActual) cita.fecha == fechaHoySimulada else true

            val cumpleBusqueda = if (busquedaActual.isBlank()) {
                true
            } else {
                cita.especialidad.contains(busquedaActual, ignoreCase = true) ||
                        cita.medico.contains(busquedaActual, ignoreCase = true)
            }

            cumpleEstado && cumpleHoy && cumpleBusqueda
        }
    }

    fun cambiarFiltroEstado(nuevoFiltro: FiltroEstado) {
        filtroEstadoActual = nuevoFiltro
        cargarCitas()
    }

    fun alternarFiltroHoy(activado: Boolean) {
        soloHoyActual = activado
        cargarCitas()
    }

    fun actualizarBusqueda(texto: String) {
        busquedaActual = texto
        cargarCitas()
    }
}