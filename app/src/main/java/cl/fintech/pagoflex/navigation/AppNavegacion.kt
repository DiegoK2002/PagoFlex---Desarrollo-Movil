package cl.fintech.pagoflex.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.fintech.pagoflex.model.Rol
import cl.fintech.pagoflex.ui.screens.ComprobanteScreen
import cl.fintech.pagoflex.ui.screens.CompromisosScreen
import cl.fintech.pagoflex.ui.screens.DetalleCompromisoScreen
import cl.fintech.pagoflex.ui.screens.InicioAgenteScreen
import cl.fintech.pagoflex.ui.screens.InicioSesionScreen
import cl.fintech.pagoflex.ui.screens.MiPerfilScreen
import cl.fintech.pagoflex.ui.screens.RegistroScreen
import cl.fintech.pagoflex.util.ocultarRut
import cl.fintech.pagoflex.viewmodel.CompromisosViewModel
import cl.fintech.pagoflex.viewmodel.SesionViewModel
import cl.fintech.pagoflex.viewmodel.ReportesViewModel
import cl.fintech.pagoflex.viewmodel.FotoPerfilViewModel
import cl.fintech.pagoflex.viewmodel.CotizacionesViewModel
import cl.fintech.pagoflex.ui.screens.HistorialPagosScreen
import cl.fintech.pagoflex.ui.screens.ReconocimientosScreen

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val sesionViewModel: SesionViewModel = viewModel()
    val compromisosViewModel: CompromisosViewModel = viewModel()
    val reportesViewModel: ReportesViewModel = viewModel()
    val cotizacionesViewModel: CotizacionesViewModel = viewModel()
    val fotoPerfilViewModel: FotoPerfilViewModel = viewModel()

    val usuarioActual by sesionViewModel.usuarioActual.collectAsStateWithLifecycle()
    val compromisos by compromisosViewModel.compromisos.collectAsStateWithLifecycle()
    val resumenCompromisos by compromisosViewModel.resumenCompromisos.collectAsStateWithLifecycle()
    val pagos by compromisosViewModel.pagos.collectAsStateWithLifecycle()
    val indicadorCumplimiento by compromisosViewModel.indicadorCumplimiento.collectAsStateWithLifecycle()
    val reconocimientos by compromisosViewModel.reconocimientos.collectAsStateWithLifecycle()
    val reportes by reportesViewModel.reportes.collectAsStateWithLifecycle()
    val estadoCotizaciones by cotizacionesViewModel.estado.collectAsStateWithLifecycle()
    val fotoPerfil by fotoPerfilViewModel.fotoPerfil.collectAsStateWithLifecycle()

    val cerrarSesion = {
        fotoPerfilViewModel.limpiar()
        sesionViewModel.cerrarSesion()
        compromisosViewModel.limpiar()
        navController.navigate(Ruta.InicioSesion.ruta) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
        reportesViewModel.limpiar()
    }

    NavHost(navController = navController, startDestination = Ruta.InicioSesion.ruta) {

        composable(Ruta.InicioSesion.ruta) {
            InicioSesionScreen(
                onInicioExitoso = { usuario ->
                    fotoPerfilViewModel.cargar(usuario.id)
                    sesionViewModel.iniciarSesion(usuario)
                    compromisosViewModel.cargar(usuario.id)
                    reportesViewModel.cargar(usuario)
                    if (usuario.rol == Rol.USUARIO_FINAL) {
                        cotizacionesViewModel.consultar()
                    }
                    val destino = when (usuario.rol) {
                        Rol.USUARIO_FINAL -> Ruta.Compromisos.ruta
                        Rol.AGENTE -> Ruta.InicioAgente.ruta
                    }
                    navController.navigate(destino) {
                        popUpTo(Ruta.InicioSesion.ruta) { inclusive = true }
                    }
                },
                onCrearCuenta = { navController.navigate(Ruta.Registro.ruta) }
            )
        }

        composable(Ruta.Registro.ruta) {
            RegistroScreen(
                onVolver = { navController.navigateUp() },
                onRegistroExitoso = { navController.navigateUp() }
            )
        }

        composable(Ruta.Compromisos.ruta) {
            val usuario = usuarioActual
            if (usuario != null) {
                CompromisosScreen(
                    usuario = usuario,
                    compromisos = compromisos,
                    resumenCompromisos = resumenCompromisos,
                    estadoCotizaciones = estadoCotizaciones,
                    nombreEmpresa = compromisosViewModel::nombreEmpresa,
                    importeDe = compromisosViewModel::importeDe,
                    onVerDetalle = { folio -> navController.navigate(Ruta.Detalle.crear(folio)) },
                    onVerHistorial = {
                        navController.navigate(Ruta.HistorialPagos.ruta)
                    },
                    onVerReconocimientos = {
                        navController.navigate(Ruta.Reconocimientos.ruta)
                    },
                    onVerPerfil = { navController.navigate(Ruta.MiPerfil.ruta) },
                    onSalir = cerrarSesion

                )
            }
        }

        composable(Ruta.HistorialPagos.ruta) {
            val usuario = usuarioActual

            if (usuario != null && usuario.rol == Rol.USUARIO_FINAL) {
                HistorialPagosScreen(
                    pagos = pagos,
                    nombreEmpresa = compromisosViewModel::nombreEmpresa,
                    indicador = indicadorCumplimiento,
                    onVerComprobante = { folio ->
                        navController.navigate(
                            Ruta.Comprobante.crear(folio)
                        )
                    },
                    onVolver = {
                        navController.navigateUp()
                    }
                )
            }
        }

        composable(Ruta.Reconocimientos.ruta) {
            val usuario = usuarioActual

            if (usuario != null && usuario.rol == Rol.USUARIO_FINAL) {
                ReconocimientosScreen(
                    reconocimientos = reconocimientos,
                    onVolver = {
                        navController.navigateUp()
                    }
                )
            }
        }

        composable(Ruta.MiPerfil.ruta) {
            val usuario = usuarioActual
            if (usuario != null) {
                MiPerfilScreen(
                    usuario = usuario,
                    fotoPerfil = fotoPerfil,
                    onGuardarFoto = { bitmap ->
                        fotoPerfilViewModel.guardarFoto(bitmap)
                    },
                    onVolver = { navController.navigateUp() }
                )
            }
        }

        composable(Ruta.InicioAgente.ruta) {
            val usuario = usuarioActual
            if (usuario != null) {
                InicioAgenteScreen(
                    usuario = usuario,
                    onSalir = cerrarSesion,
                    reportes = reportes,
                    onTomarReporte = { idReporte ->
                        reportesViewModel.tomarReporte(idReporte)
                    },
                    onResolverReporte = { idReporte, respuesta ->
                        reportesViewModel.resolverReporte(idReporte, respuesta)
                    }
                )
            }
        }

        composable(
            route = Ruta.Detalle.ruta,
            arguments = listOf(navArgument("folio") { type = NavType.StringType })
        ) { entrada ->
            val folio = entrada.arguments?.getString("folio") ?: ""
            val compromiso = compromisos.firstOrNull { it.folio == folio }
            if (compromiso != null) {
                DetalleCompromisoScreen(
                    compromiso = compromiso,
                    nombreEmpresa = compromisosViewModel.nombreEmpresa(compromiso.codigoEmpresa),
                    diasAtraso = compromisosViewModel.diasDeAtraso(compromiso),
                    recargo = compromisosViewModel.recargoDe(compromiso),
                    total = compromisosViewModel.importeDe(compromiso),
                    motivoNoPago = compromisosViewModel.motivoDe(compromiso),
                    anterior = compromisosViewModel.anteriorPendiente(compromiso),
                    onVolver = { navController.navigateUp() },
                    onConfirmarPago = {

                        val pago = compromisosViewModel.pagar(compromiso)
                        if (pago != null) {
                            navController.navigate(Ruta.Comprobante.crear(compromiso.folio)) {

                                popUpTo(Ruta.Detalle.ruta) { inclusive = true }
                            }
                        }
                    },
                    onVerComprobante = {
                        navController.navigate(Ruta.Comprobante.crear(compromiso.folio))
                    },

                    onCrearReporte = { motivo, comentario ->
                        val guardado = reportesViewModel.crearReporte(
                            compromiso.folio,
                            motivo,
                            comentario
                        )

                        if (guardado) {
                            compromisosViewModel.actualizar()
                        }

                        guardado
                    },

                    reporteActual = reportes.firstOrNull {
                        it.folio == compromiso.folio
                    }


                )
            }
        }


        composable(
            route = Ruta.Comprobante.ruta,
            arguments = listOf(navArgument("folio") { type = NavType.StringType })
        ) { entrada ->
            val folio = entrada.arguments?.getString("folio") ?: ""

            val pago = remember(folio) { compromisosViewModel.pagoDe(folio) }
            val usuario = usuarioActual
            if (pago != null && usuario != null) {
                ComprobanteScreen(
                    pago = pago,
                    nombreEmpresa = compromisosViewModel.nombreEmpresa(pago.codigoEmpresa),
                    nombrePagador = usuario.nombreCompleto,
                    rutParcial = ocultarRut(usuario.rut),
                    onVolver = { navController.navigateUp() }
                )
            }
        }
    }
}
