package pe.upeu.andinasalud.data.repository


import pe.upeu.andinasalud.data.local.CitasSimuladas
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.repository.CitaRepository

class CitaRepositoryFake : CitaRepository {
    private val listaCitas = CitasSimuladas.citas.toMutableList()

    override suspend fun obtenerCitas(): List<Cita> = listaCitas

    override suspend fun obtenerCitaPorId(id: Int): Cita? = listaCitas.find { it.id == id }

    override suspend fun solicitarCita(cita: Cita): Result<Unit> {
        listaCitas.add(cita)
        return Result.success(Unit)
    }

    override suspend fun cancelarCita(id: Int, motivo: String): Result<Unit> {
        val index = listaCitas.indexOfFirst { it.id == id }
        if (index != -1) {
            val cita = listaCitas[index]
            listaCitas[index] = cita.copy(
                estado = pe.upeu.andinasalud.domain.model.EstadoCita.Cancelada(motivo, true)
            )
            return Result.success(Unit)
        }
        return Result.failure(Exception("Cita no encontrada"))
    }

    // SC-D: Reprogramar fecha y hora conservando el estado
    override suspend fun reprogramarCita(id: Int, nuevaFecha: String, nuevaHora: String): Result<Unit> {
        val index = listaCitas.indexOfFirst { it.id == id }
        if (index != -1) {
            val cita = listaCitas[index]
            listaCitas[index] = cita.copy(fecha = nuevaFecha, hora = nuevaHora)
            return Result.success(Unit)
        }
        return Result.failure(Exception("No se pudo reprogramar la cita"))
    }
}