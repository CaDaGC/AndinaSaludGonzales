package pe.upeu.andinasalud.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ContarCitasProgramadasUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.usecase.ReprogramarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.upeu.andinasalud.presentation.citas.CitasViewModel
import pe.upeu.andinasalud.presentation.citas.InicioViewModel
import pe.upeu.andinasalud.presentation.detalle.DetalleCitaViewModel
import pe.upeu.andinasalud.presentation.solicitar.SolicitarCitaViewModel

val appModule = module {
    single<CitaRepository> { CitaRepositoryFake() }

    // Casos de Uso
    factory { ObtenerCitasUseCase(get()) }
    factory { SolicitarCitaUseCase(get()) }
    factory { CancelarCitaUseCase(get()) }
    factory { ContarCitasProgramadasUseCase(get()) } // SC-B
    factory { ReprogramarCitaUseCase(get()) }       // SC-D

    // ViewModels
    viewModelOf(::InicioViewModel)
    viewModelOf(::CitasViewModel)
    viewModelOf(::DetalleCitaViewModel)
    viewModelOf(::SolicitarCitaViewModel)
}