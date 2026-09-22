package pe.upeu.andinasalud

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.andinasalud.presentation.citas.CitasScreen
import pe.upeu.andinasalud.presentation.citas.InicioScreen
import pe.upeu.andinasalud.presentation.detalle.DetalleCitaScreen
import pe.upeu.andinasalud.presentation.solicitar.SolicitarCitaScreen
import pe.upeu.andinasalud.presentation.theme.AndinaSaludTheme
import org.koin.compose.KoinApplication
import pe.upeu.andinasalud.di.appModule

sealed class Pantalla(val ruta: String, val titulo: String, val icono: ImageVector? = null) {
    object Inicio : Pantalla("inicio", "Inicio", Icons.Default.Home)
    object Citas : Pantalla("citas", "Mis Citas", Icons.Default.DateRange)
    object DetalleCita : Pantalla("detalle_cita/{citaId}", "Detalle de Cita") {
        fun crearRuta(citaId: Int) = "detalle_cita/$citaId"
    }
    object SolicitarCita : Pantalla("solicitar_cita", "Nueva Cita")
}



@Composable
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        AndinaSaludTheme {
            AndinaSaludApp()
        }
    }
}

@Composable
fun AndinaSaludApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    val itemsBottomNav = listOf(
        Pantalla.Inicio,
        Pantalla.Citas
    )

    val mostrarBottomBar = rutaActual in itemsBottomNav.map { it.ruta }

    Scaffold(
        bottomBar = {
            if (mostrarBottomBar) {
                NavigationBar {
                    itemsBottomNav.forEach { pantalla ->
                        NavigationBarItem(
                            icon = { pantalla.icono?.let { Icon(it, contentDescription = pantalla.titulo) } },
                            label = { Text(pantalla.titulo) },
                            selected = rutaActual == pantalla.ruta,
                            onClick = {
                                navController.navigate(pantalla.ruta) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (mostrarBottomBar) {
                FloatingActionButton(
                    onClick = { navController.navigate(Pantalla.SolicitarCita.ruta) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Solicitar Cita"
                    )
                }
            }
        }
    ) { paddingValores ->
        NavHost(
            navController = navController,
            startDestination = Pantalla.Inicio.ruta,
            modifier = Modifier.padding(paddingValores)
        ) {
            // 1. Pantalla Inicio (Inyectada vía Koin)
            composable(Pantalla.Inicio.ruta) {
                InicioScreen(
                    viewModel = koinViewModel(),
                    onNavigateToCitas = {
                        navController.navigate(Pantalla.Citas.ruta) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToSolicitud = {
                        navController.navigate(Pantalla.SolicitarCita.ruta)
                    },
                    onNavigateToDetalle = { citaId: Int ->
                        navController.navigate(Pantalla.DetalleCita.crearRuta(citaId))
                    }
                )
            }

            // 2. Pantalla Citas (Inyectada vía Koin)
            composable(Pantalla.Citas.ruta) {
                CitasScreen(
                    viewModel = koinViewModel(),
                    onNavigateToDetalle = { citaId: Int ->
                        navController.navigate(Pantalla.DetalleCita.crearRuta(citaId))
                    }
                )
            }

            // 3. Pantalla Detalle Cita (Inyectada vía Koin + extracción segura del id)
            composable(
                route = Pantalla.DetalleCita.ruta,
                arguments = listOf(
                    navArgument("citaId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                // Extrae el valor directo parseando la ruta de entrada activa
                val citaId = backStackEntry.arguments
                    ?.toString()
                    ?.substringAfter("citaId=")
                    ?.substringBefore(",")
                    ?.substringBefore("}")
                    ?.toIntOrNull()
                    ?: 0

                DetalleCitaScreen(
                    citaId = citaId,
                    viewModel = koinViewModel(),
                    onBack = { navController.popBackStack() }
                )
            }

            // 4. Pantalla Solicitar Cita (Inyectada vía Koin)
            composable(Pantalla.SolicitarCita.ruta) {
                SolicitarCitaScreen(
                    viewModel = koinViewModel(),
                    onBack = { navController.popBackStack() },
                    onCitaCreada = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}