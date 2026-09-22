package pe.upeu.andinasalud.presentation.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.EstadoCita

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCitaScreen(
    citaId: Int,
    viewModel: DetalleCitaViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var mostrarDialogoCancelar by remember { mutableStateOf(false) }
    var motivoCancelacion by remember { mutableStateOf("") }

    LaunchedEffect(citaId) {
        viewModel.cargarDetalle(citaId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Cita") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is DetalleCitaUiState.Cargando -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DetalleCitaUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = state.mensaje, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.cargarDetalle(citaId) }) {
                            Text("Reintentar")
                        }
                    }
                }
                is DetalleCitaUiState.Exito -> {
                    val cita = state.cita
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (state.mensajeExito != null) {
                            Text(
                                text = state.mensajeExito,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = cita.especialidad,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Médico: ${cita.medico}")
                                Text(text = "Sede: ${cita.sede}")
                                Text(text = "Fecha: ${cita.fecha}")
                                Text(text = "Hora: ${cita.hora}")
                            }
                        }

                        // Información del Estado
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Estado de la Cita",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                when (val estado = cita.estado) {
                                    is EstadoCita.Programada -> {
                                        Text("Estado: Programada")
                                        Text("Recordatorio activo: ${if (estado.recordatorioActivo) "Sí" else "No"}")
                                    }
                                    is EstadoCita.Atendida -> {
                                        Text("Estado: Atendida")
                                        Text("Indicaciones: ${estado.indicaciones}")
                                    }
                                    is EstadoCita.Cancelada -> {
                                        Text("Estado: Cancelada")
                                        Text("Motivo: ${estado.motivo}")
                                        Text("Cancelada por paciente: ${if (estado.canceladaPorPaciente) "Sí" else "No"}")
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Botón Cancelar (RF-03 & RN-03)
                        if (cita.estado is EstadoCita.Programada) {
                            Button(
                                onClick = { mostrarDialogoCancelar = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Cancelar cita")
                            }
                        }
                    }
                }
            }
        }

        // Diálogo de confirmación
        if (mostrarDialogoCancelar) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoCancelar = false },
                title = { Text("Confirmar cancelación") },
                text = {
                    Column {
                        Text("¿Está seguro que desea cancelar esta cita?")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = motivoCancelacion,
                            onValueChange = { motivoCancelacion = it },
                            label = { Text("Motivo de cancelación") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            mostrarDialogoCancelar = false
                            viewModel.cancelarCita(motivoCancelacion.ifBlank { "Cancelado por el paciente" })
                        }
                    ) {
                        Text("Sí, cancelar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoCancelar = false }) {
                        Text("Volver")
                    }
                }
            )
        }
    }
}