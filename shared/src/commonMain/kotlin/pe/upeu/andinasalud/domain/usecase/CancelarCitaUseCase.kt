package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.repository.CitaRepository

class CancelarCitaUseCase(
    private val repository: CitaRepository
) {
    suspend operator fun invoke(cita: Cita, motivo: String): Result<Cita> {
        if (motivo.isBlank()) {
            return Result.failure(
                IllegalArgumentException("El motivo de cancelación es obligatorio")
            )
        }

        return repository.cancelarCita(
            id = cita.id,
            motivo = motivo
        ).map {
            // Pasamos motivo y canceladaPorPaciente = true
            cita.copy(
                estado = EstadoCita.Cancelada(
                    motivo = motivo,
                    canceladaPorPaciente = true
                )
            )
        }
    }
}