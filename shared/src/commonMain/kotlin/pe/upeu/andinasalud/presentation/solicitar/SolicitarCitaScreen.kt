package pe.upeu.andinasalud.presentation.solicitar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitarCitaScreen(
    viewModel: SolicitarCitaViewModel,
    onBack: () -> Unit,
    onCitaCreada: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var especialidad by remember { mutableStateOf("") }
    var medico by remember { mutableStateOf("") }
    var sede by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is SolicitarCitaUiState.Exito) {
            onCitaCreada()
            viewModel.reiniciarEstado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitar Nueva Cita") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Complete la información de su cita",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = especialidad,
                onValueChange = { especialidad = it },
                label = { Text("Especialidad (ej. Cardiología)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = medico,
                onValueChange = { medico = it },
                label = { Text("Médico (ej. Dr. Juan Pérez)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = sede,
                onValueChange = { sede = it },
                label = { Text("Sede (ej. Sede Central)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha (AAAA-MM-DD)") },
                placeholder = { Text("2026-10-15") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = hora,
                onValueChange = { hora = it },
                label = { Text("Hora (HH:MM)") },
                placeholder = { Text("09:00") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (uiState is SolicitarCitaUiState.Error) {
                Text(
                    text = (uiState as SolicitarCitaUiState.Error).mensaje,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.solicitarCita(
                        especialidad = especialidad,
                        medico = medico,
                        sede = sede,
                        fecha = fecha,
                        hora = hora
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is SolicitarCitaUiState.Cargando
            ) {
                if (uiState is SolicitarCitaUiState.Cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Confirmar Reserva")
                }
            }
        }
    }
}