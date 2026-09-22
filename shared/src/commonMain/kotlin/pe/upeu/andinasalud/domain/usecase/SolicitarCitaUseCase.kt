package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.repository.CitaRepository

class SolicitarCitaUseCase(
    private val repository: CitaRepository
) {

    suspend operator fun invoke(
        cita: Cita,
        citasExistentes: List<Cita>
    ): Result<Cita> {

        if (cita.fecha.isBlank()) {
            return Result.failure(
                IllegalArgumentException("La fecha es obligatoria")
            )
        }

        if (cita.hora.isBlank()) {
            return Result.failure(
                IllegalArgumentException("La hora es obligatoria")
            )
        }

        if (cita.estado !is EstadoCita.Programada) {
            return Result.failure(
                IllegalArgumentException(
                    "Una nueva cita debe estar Programada"
                )
            )
        }

        val citasProgramadas = citasExistentes.count {
            it.estado is EstadoCita.Programada
        }

        if (citasProgramadas >= 3) {
            return Result.failure(
                IllegalArgumentException(
                    "El paciente no puede tener más de tres citas programadas"
                )
            )
        }

        val citaDuplicada = citasExistentes.any {
            it.estado is EstadoCita.Programada &&
                    it.fecha == cita.fecha &&
                    it.hora == cita.hora
        }

        if (citaDuplicada) {
            return Result.failure(
                IllegalArgumentException(
                    "Ya existe una cita programada en esa fecha y hora"
                )
            )
        }

        return repository.solicitarCita(cita).map { cita }
    }
}