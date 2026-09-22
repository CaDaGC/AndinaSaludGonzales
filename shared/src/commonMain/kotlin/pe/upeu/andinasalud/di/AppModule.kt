package pe.upeu.andinasalud.di

import org.koin.dsl.module
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.upeu.andinasalud.presentation.citas.CitasViewModel
import pe.upeu.andinasalud.presentation.citas.InicioViewModel

val appModule = module {
    // Repositorio
    single<CitaRepository> { CitaRepositoryFake() }

    // Casos de Uso
    factory { ObtenerCitasUseCase(get()) }
    factory { SolicitarCitaUseCase(get()) }
    factory { CancelarCitaUseCase(get()) }

    // ViewModels
    factory { InicioViewModel(get()) }
    factory { CitasViewModel(get()) }
}