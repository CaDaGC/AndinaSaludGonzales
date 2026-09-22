package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.repository.CitaRepository

class ReprogramarCitaUseCase(
    private val repository: CitaRepository
) {
    suspend operator fun invoke(id: Int, nuevaFecha: String, nuevaHora: String): Result<Unit> {
        if (nuevaFecha.isBlank() || nuevaHora.isBlank()) {
            return Result.failure(Exception("La fecha y la hora no pueden estar vacías"))
        }
        return repository.reprogramarCita(id, nuevaFecha, nuevaHora)
    }
}