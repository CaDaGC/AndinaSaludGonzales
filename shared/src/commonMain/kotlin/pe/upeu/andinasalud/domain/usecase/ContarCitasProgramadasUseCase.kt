package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.repository.CitaRepository

class ContarCitasProgramadasUseCase(
    private val repository: CitaRepository
) {
    suspend operator fun invoke(): Int {
        return repository.obtenerCitas().count { it.estado is EstadoCita.Programada }
    }
}