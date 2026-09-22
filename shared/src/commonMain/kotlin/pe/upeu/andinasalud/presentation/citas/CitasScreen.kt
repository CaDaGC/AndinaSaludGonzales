package pe.upeu.andinasalud.presentation.citas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.ModalidadAtencion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitasScreen(
    viewModel: CitasViewModel = koinViewModel(),
    onNavigateToDetalle: (Int) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.cargarCitas()
    }

    val uiState by viewModel.uiState.collectAsState()
    var soloHoySeleccionado by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Citas") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            when (val state = uiState) {
                is CitasUiState.Cargando -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is CitasUiState.Exito -> {
                    // Campo de búsqueda opcional
                    OutlinedTextField(
                        value = state.textoBusqueda,
                        onValueChange = { viewModel.actualizarBusqueda(it) },
                        label = { Text("Buscar por médico o especialidad") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Filtros
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        // SC-A: Chip "Hoy"
                        item {
                            FilterChip(
                                selected = soloHoySeleccionado,
                                onClick = {
                                    soloHoySeleccionado = !soloHoySeleccionado
                                    viewModel.alternarFiltroHoy(soloHoySeleccionado)
                                },
                                label = { Text("Hoy") }
                            )
                        }

                        // Filtros por Estado
                        items(FiltroEstado.entries.toTypedArray()) { filtro ->
                            FilterChip(
                                selected = state.filtroEstado == filtro,
                                onClick = { viewModel.cambiarFiltroEstado(filtro) },
                                label = {
                                    Text(
                                        when (filtro) {
                                            FiltroEstado.TODAS -> "Todas"
                                            FiltroEstado.PROGRAMADAS -> "Programadas"
                                            FiltroEstado.ATENDIDAS -> "Atendidas"
                                            FiltroEstado.CANCELADAS -> "Canceladas"
                                        }
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (state.citas.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No se encontraron citas.")
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.citas) { cita ->
                                TarjetaCitaItem(
                                    cita = cita,
                                    onClick = { onNavigateToDetalle(cita.id) }
                                )
                            }
                        }
                    }
                }
                is CitasUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.mensaje,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaCitaItem(
    cita: Cita,
    onClick: () -> Unit
) {
    val textoEstado = when (cita.estado) {
        is EstadoCita.Programada -> "Programada"
        is EstadoCita.Atendida -> "Atendida"
        is EstadoCita.Cancelada -> "Cancelada"
    }

    // SC-C: Selección de ícono por modalidad
    val iconoModalidad = if (cita.modalidad == ModalidadAtencion.TELECONSULTA) {
        Icons.Default.VideoCall
    } else {
        Icons.Default.LocationOn
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = iconoModalidad,
                        contentDescription = cita.modalidad.name,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = cita.especialidad,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = textoEstado,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = cita.medico,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${cita.fecha} - ${cita.hora} hs",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}