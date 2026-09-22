package pe.upeu.andinasalud.presentation.navigation

sealed class Destino(val ruta: String) {
    object Inicio : Destino("inicio")
    object Citas : Destino("citas")
    object Perfil : Destino("perfil")
    object SolicitudCita : Destino("solicitud_cita")
    object DetalleCita : Destino("detalle_cita/{citaId}") {
        fun crearRuta(citaId: Int) = "detalle_cita/$citaId"
    }
}