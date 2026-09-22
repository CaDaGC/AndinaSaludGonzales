package pe.upeu.andinasalud.presentation.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.Paciente

@Composable
fun InicioScreen(
    viewModel: InicioViewModel,
    onNavigateToCitas: () -> Unit,
    onNavigateToSolicitud: () -> Unit,
    onNavigateToDetalle: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is InicioUiState.Cargando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is InicioUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = state.mensaje, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.cargarDatos() }) {
                            Text("Reintentar")
                        }
                    }
                }
                is InicioUiState.Exito -> {
                    ContenidoInicio(
                        paciente = state.paciente,
                        proximaCita = state.proximaCita,
                        onNavigateToCitas = onNavigateToCitas,
                        onNavigateToSolicitud = onNavigateToSolicitud,
                        onNavigateToDetalle = onNavigateToDetalle
                    )
                }
            }
        }
    }
}

@Composable
private fun ContenidoInicio(
    paciente: Paciente,
    proximaCita: Cita?,
    onNavigateToCitas: () -> Unit,
    onNavigateToSolicitud: () -> Unit,
    onNavigateToDetalle: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Saludo con el nombre del paciente
        Text(
            text = "¡Hola, ${paciente.nombre}!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Bienvenido a AndinaSalud",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tarjeta destacada con la próxima cita programada
        Text(
            text = "Próxima Cita",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        if (proximaCita != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                onClick = { onNavigateToDetalle(proximaCita.id) }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = proximaCita.especialidad,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "Médico: ${proximaCita.medico}")
                    Text(text = "Sede: ${proximaCita.sede}")
                    Text(text = "Fecha: ${proximaCita.fecha} - ${proximaCita.hora}")
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes citas próximas programadas")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Accesos rápidos
        Text(
            text = "Accesos Rápidos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onNavigateToCitas,
                modifier = Modifier.weight(1f)
            ) {
                Text("Mis Citas")
            }
            Button(
                onClick = onNavigateToSolicitud,
                modifier = Modifier.weight(1f)
            ) {
                Text("Solicitar Cita")
            }
        }
    }
}