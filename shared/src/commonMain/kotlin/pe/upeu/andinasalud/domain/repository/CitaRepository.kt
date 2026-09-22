package pe.upeu.andinasalud.domain.repository

import pe.upeu.andinasalud.domain.model.Cita

interface CitaRepository {
    suspend fun obtenerCitas(): List<Cita>
    suspend fun obtenerCitaPorId(id: Int): Cita?
    suspend fun solicitarCita(cita: Cita): Result<Unit>
    suspend fun cancelarCita(id: Int, motivo: String): Result<Unit>
    // SC-D: Método para reprogramar
    suspend fun reprogramarCita(id: Int, nuevaFecha: String, nuevaHora: String): Result<Unit>
}