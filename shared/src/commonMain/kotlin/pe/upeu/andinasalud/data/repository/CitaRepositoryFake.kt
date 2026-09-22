package pe.upeu.andinasalud.data.repository

import kotlinx.coroutines.delay
import pe.upeu.andinasalud.data.local.CitasSimuladas
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.repository.CitaRepository

class CitaRepositoryFake : CitaRepository {

    private val citas = CitasSimuladas.citas.toMutableList()

    override suspend fun obtenerCitas(): List<Cita> {
        delay(800)
        return citas.toList()
    }

    override suspend fun obtenerCitaPorId(id: Int): Cita? {
        delay(300)
        return citas.find { it.id == id }
    }

    override suspend fun solicitarCita(
        cita: Cita
    ): Result<Cita> {
        delay(500)

        if (citas.any { it.id == cita.id }) {
            return Result.failure(
                IllegalArgumentException(
                    "Ya existe una cita con ese identificador"
                )
            )
        }

        citas.add(cita)

        return Result.success(cita)
    }

    override suspend fun cancelarCita(
        id: Int,
        motivo: String
    ): Result<Cita> {
        delay(500)

        val posicion = citas.indexOfFirst {
            it.id == id
        }

        if (posicion == -1) {
            return Result.failure(
                IllegalArgumentException(
                    "La cita no existe"
                )
            )
        }

        val citaActual = citas[posicion]

        if (citaActual.estado !is EstadoCita.Programada) {
            return Result.failure(
                IllegalArgumentException(
                    "La cita no está programada"
                )
            )
        }

        val citaCancelada = citaActual.copy(
            estado = EstadoCita.Cancelada(
                motivo = motivo,
                canceladaPorPaciente = true
            )
        )

        citas[posicion] = citaCancelada

        return Result.success(citaCancelada)
    }
}