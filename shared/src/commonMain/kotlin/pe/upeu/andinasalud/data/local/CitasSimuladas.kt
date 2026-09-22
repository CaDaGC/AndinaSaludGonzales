package pe.upeu.andinasalud.data.local

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.Medico
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.model.Sede

object CitasSimuladas {

    val paciente = Paciente(
        id = "P-0417",
        nombre = "Lucía Quispe Mamani",
        documento = "70154823",
        correo = "lucia.quispe@correo.pe",
        telefono = "987654321"
    )

    val sedes = listOf(
        Sede("S-01", "Ñaña"),
        Sede("S-02", "Chosica"),
        Sede("S-03", "Chaclacayo"),
        Sede("S-04", "Santa Anita")
    )

    val especialidades = listOf(
        "Medicina General",
        "Odontología",
        "Pediatría",
        "Nutrición",
        "Psicología"
    )

    val medicos = listOf(
        Medico(
            id = "M-01",
            nombre = "Dr. Iván Rojas",
            especialidad = "Medicina General",
            sedes = listOf(sedes[0], sedes[1])
        ),
        Medico(
            id = "M-02",
            nombre = "Dra. María Torres",
            especialidad = "Medicina General",
            sedes = listOf(sedes[2], sedes[3])
        ),
        Medico(
            id = "M-03",
            nombre = "Dra. Rosa Flores",
            especialidad = "Odontología",
            sedes = listOf(sedes[1], sedes[3])
        ),
        Medico(
            id = "M-04",
            nombre = "Dr. Carlos Mendoza",
            especialidad = "Odontología",
            sedes = listOf(sedes[0], sedes[2])
        ),
        Medico(
            id = "M-05",
            nombre = "Dra. Carla Núñez",
            especialidad = "Pediatría",
            sedes = listOf(sedes[2], sedes[3])
        ),
        Medico(
            id = "M-06",
            nombre = "Dr. Jorge Salazar",
            especialidad = "Pediatría",
            sedes = listOf(sedes[0], sedes[1])
        ),
        Medico(
            id = "M-07",
            nombre = "Lic. Ana Bermúdez",
            especialidad = "Nutrición",
            sedes = listOf(sedes[3], sedes[0])
        ),
        Medico(
            id = "M-08",
            nombre = "Lic. Pedro Vargas",
            especialidad = "Nutrición",
            sedes = listOf(sedes[1], sedes[2])
        ),
        Medico(
            id = "M-09",
            nombre = "Ps. Luis Tapia",
            especialidad = "Psicología",
            sedes = listOf(sedes[0], sedes[2])
        ),
        Medico(
            id = "M-10",
            nombre = "Ps. Daniela Ruiz",
            especialidad = "Psicología",
            sedes = listOf(sedes[1], sedes[3])
        )
    )

    val citas = listOf(
        Cita(
            id = 1,
            especialidad = "Medicina General",
            medico = "Dr. Iván Rojas",
            sede = "Ñaña",
            fecha = "2026-10-05",
            hora = "09:00",
            estado = EstadoCita.Programada(
                recordatorioActivo = true
            )
        ),
        Cita(
            id = 2,
            especialidad = "Odontología",
            medico = "Dra. Rosa Flores",
            sede = "Chosica",
            fecha = "2026-10-08",
            hora = "16:30",
            estado = EstadoCita.Programada(
                recordatorioActivo = false
            )
        ),
        Cita(
            id = 3,
            especialidad = "Nutrición",
            medico = "Lic. Ana Bermúdez",
            sede = "Santa Anita",
            fecha = "2026-10-12",
            hora = "11:15",
            estado = EstadoCita.Programada(
                recordatorioActivo = true
            )
        ),
        Cita(
            id = 4,
            especialidad = "Pediatría",
            medico = "Dra. Carla Núñez",
            sede = "Chaclacayo",
            fecha = "2026-08-30",
            hora = "08:45",
            estado = EstadoCita.Atendida(
                indicaciones = "Control en tres meses"
            )
        ),
        Cita(
            id = 5,
            especialidad = "Psicología",
            medico = "Ps. Luis Tapia",
            sede = "Ñaña",
            fecha = "2026-09-02",
            hora = "15:00",
            estado = EstadoCita.Atendida(
                indicaciones = "Continuar sesiones quincenales"
            )
        ),
        Cita(
            id = 6,
            especialidad = "Medicina General",
            medico = "Dr. Iván Rojas",
            sede = "Chosica",
            fecha = "2026-09-05",
            hora = "10:30",
            estado = EstadoCita.Cancelada(
                motivo = "Viaje del paciente",
                canceladaPorPaciente = true
            )
        )
    )
}