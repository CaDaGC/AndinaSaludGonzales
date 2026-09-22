package pe.upeu.andinasalud.domain.model

enum class ModalidadAtencion {
    PRESENCIAL,
    TELECONSULTA
}

data class Cita(
    val id: Int,
    val especialidad: String,
    val medico: String,
    val sede: String,
    val fecha: String,
    val hora: String,
    val estado: EstadoCita,
    val modalidad: ModalidadAtencion = ModalidadAtencion.PRESENCIAL
)