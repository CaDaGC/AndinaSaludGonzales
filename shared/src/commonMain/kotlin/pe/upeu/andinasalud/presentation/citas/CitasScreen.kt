package pe.upeu.andinasalud.presentation.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitasScreen(
    viewModel: CitasViewModel,
    onNavigateToDetalle: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Citas") }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is CitasUiState.Cargando -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is CitasUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = state.mensaje, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.cargarCitas() }) {
                            Text("Reintentar")
                        }
                    }
                }
                is CitasUiState.Exito -> {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        // Campo de búsqueda (RF-05)
                        OutlinedTextField(
                            value = state.textoBusqueda,
                            onValueChange = { viewModel.actualizarBusqueda(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Buscar por especialidad o médico...") },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Chips de filtro por estado (RF-02)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = state.filtroEstado == FiltroEstado.TODAS,
                                onClick = { viewModel.seleccionarFiltro(FiltroEstado.TODAS) },
                                label = { Text("Todas") }
                            )
                            FilterChip(
                                selected = state.filtroEstado == FiltroEstado.PROGRAMADAS,
                                onClick = { viewModel.seleccionarFiltro(FiltroEstado.PROGRAMADAS) },
                                label = { Text("Programadas") }
                            )
                            FilterChip(
                                selected = state.filtroEstado == FiltroEstado.ATENDIDAS,
                                onClick = { viewModel.seleccionarFiltro(FiltroEstado.ATENDIDAS) },
                                label = { Text("Atendidas") }
                            )
                            FilterChip(
                                selected = state.filtroEstado == FiltroEstado.CANCELADAS,
                                onClick = { viewModel.seleccionarFiltro(FiltroEstado.CANCELADAS) },
                                label = { Text("Canceladas") }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Lista o estado vacío (RF-08)
                        if (state.citas.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No se encontraron citas",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.citas) { cita ->
                                    ItemCitaCard(cita = cita, onClick = { onNavigateToDetalle(cita.id) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemCitaCard(
    cita: Cita,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cita.especialidad,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TagEstado(estado = cita.estado)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Médico: ${cita.medico}")
            Text(text = "Sede: ${cita.sede}")
            Text(
                text = "Fecha: ${cita.fecha} - ${cita.hora}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TagEstado(estado: EstadoCita) {
    val (texto, color) = when (estado) {
        is EstadoCita.Programada -> "Programada" to MaterialTheme.colorScheme.primary
        is EstadoCita.Atendida -> "Atendida" to MaterialTheme.colorScheme.secondary
        is EstadoCita.Cancelada -> "Cancelada" to MaterialTheme.colorScheme.error
    }
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = texto,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontWeight = FontWeight.Bold
        )
    }
}