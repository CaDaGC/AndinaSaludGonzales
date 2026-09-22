package pe.upeu.andinasalud.presentation.detalle

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.ModalidadAtencion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCitaScreen(
    citaId: Int,
    onNavigateBack: () -> Unit,
    viewModel: DetalleCitaViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val mensajeAccion by viewModel.mensajeAccion.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var mostrarDialogoCancelar by remember { mutableStateOf(false) }
    var mostrarDialogoReprogramar by remember { mutableStateOf(false) }

    var motivoCancelar by remember { mutableStateOf("") }
    var nuevaFecha by remember { mutableStateOf("") }
    var nuevaHora by remember { mutableStateOf("") }

    LaunchedEffect(citaId) {
        viewModel.cargarDetalle(citaId)
    }

    LaunchedEffect(mensajeAccion) {
        mensajeAccion?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Cita") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when (val state = uiState) {
                DetalleUiState.Cargando -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DetalleUiState.Error -> {
                    Text(
                        text = state.mensaje,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is DetalleUiState.Exito -> {
                    val cita = state.cita
                    val esProgramada = cita.estado is EstadoCita.Programada
                    val esTeleconsulta = cita.modalidad == ModalidadAtencion.TELECONSULTA

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = cita.especialidad,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Médico: ${cita.medico}")
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Fecha/Hora: ${cita.fecha} - ${cita.hora} hs")
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val icono = if (esTeleconsulta) Icons.Default.VideoCall else Icons.Default.LocationOn
                                    Icon(icono, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Modalidad: ${cita.modalidad.name} - ${cita.sede}")
                                }

                                if (cita.estado is EstadoCita.Cancelada) {
                                    val estadoCancelado = cita.estado as EstadoCita.Cancelada
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Motivo de cancelación: ${estadoCancelado.motivo}",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        // SC-D: Botones de Acción condicionales según Estado y Modalidad
                        if (esProgramada) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (esTeleconsulta) {
                                    Button(
                                        onClick = { /* Abrir enlace de teleconsulta */ },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.VideoCall, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Unirse a la llamada")
                                    }
                                }

                                OutlinedButton(
                                    onClick = { mostrarDialogoReprogramar = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Reprogramar Cita")
                                }

                                Button(
                                    onClick = { mostrarDialogoCancelar = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Cancelar Cita")
                                }
                            }
                        }
                    }

                    // Diálogo Reprogramar (SC-D)
                    if (mostrarDialogoReprogramar) {
                        AlertDialog(
                            onDismissRequest = { mostrarDialogoReprogramar = false },
                            title = { Text("Reprogramar Cita") },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = nuevaFecha,
                                        onValueChange = { nuevaFecha = it },
                                        label = { Text("Nueva Fecha (AAAA-MM-DD)") }
                                    )
                                    OutlinedTextField(
                                        value = nuevaHora,
                                        onValueChange = { nuevaHora = it },
                                        label = { Text("Nueva Hora (HH:MM)") }
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        viewModel.reprogramarCita(cita.id, nuevaFecha, nuevaHora)
                                        mostrarDialogoReprogramar = false
                                    }
                                ) {
                                    Text("Guardar")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { mostrarDialogoReprogramar = false }) {
                                    Text("Cancelar")
                                }
                            }
                        )
                    }

                    // Diálogo Cancelar
                    if (mostrarDialogoCancelar) {
                        AlertDialog(
                            onDismissRequest = { mostrarDialogoCancelar = false },
                            title = { Text("Cancelar Cita") },
                            text = {
                                OutlinedTextField(
                                    value = motivoCancelar,
                                    onValueChange = { motivoCancelar = it },
                                    label = { Text("Motivo de la cancelación") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        viewModel.cancelarCita(cita, motivoCancelar)
                                        mostrarDialogoCancelar = false
                                    }
                                ) {
                                    Text("Confirmar")
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
        }
    }
}