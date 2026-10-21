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

/**
 * Mapa de navegacion de la app: define que pantalla se muestra en cada ruta y como se pasa
 * de una a otra. Las pantallas no reciben el controlador de navegacion, solo funciones
 * que avisan lo que ocurrio.
 *
 * Dos ViewModels se crean aqui, fuera del NavHost, para que todas las pantallas compartan sus datos:
 *  - [SesionViewModel]: quien inicio sesion.
 *  - [CompromisosViewModel]: compromisos y pagos, leidos desde SQLite.
 */
@Composable
fun AppNavegacion() {
    // Controlador que lleva el historial de pantallas y navega entre ellas
    val navController = rememberNavController()
    val sesionViewModel: SesionViewModel = viewModel()
    val compromisosViewModel: CompromisosViewModel = viewModel()
    val reportesViewModel: ReportesViewModel = viewModel()
    val fotoPerfilViewModel: FotoPerfilViewModel = viewModel()

    // Estados observados: la pantalla se redibuja cuando cambian
    val usuarioActual by sesionViewModel.usuarioActual.collectAsStateWithLifecycle()
    val compromisos by compromisosViewModel.compromisos.collectAsStateWithLifecycle()
    val reportes by reportesViewModel.reportes.collectAsStateWithLifecycle()
    val fotoPerfil by fotoPerfilViewModel.fotoPerfil.collectAsStateWithLifecycle()

    // Cierra la sesion, borra los datos en memoria y vuelve al inicio de sesion sin dejar historial
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

        // Inicio de sesion: segun el rol de la cuenta se abre la pantalla que le corresponde
        composable(Ruta.InicioSesion.ruta) {
            InicioSesionScreen(
                onInicioExitoso = { usuario ->
                    fotoPerfilViewModel.cargar(usuario.id)
                    sesionViewModel.iniciarSesion(usuario)
                    compromisosViewModel.cargar(usuario.id)
                    reportesViewModel.cargar(usuario)
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

        // Registro: al terminar vuelve al inicio de sesion
        composable(Ruta.Registro.ruta) {
            RegistroScreen(
                onVolver = { navController.navigateUp() },
                onRegistroExitoso = { navController.navigateUp() }
            )
        }

        // Lista de compromisos del usuario final
        composable(Ruta.Compromisos.ruta) {
            val usuario = usuarioActual
            if (usuario != null) {
                CompromisosScreen(
                    usuario = usuario,
                    compromisos = compromisos,
                    nombreEmpresa = compromisosViewModel::nombreEmpresa,
                    importeDe = compromisosViewModel::importeDe,
                    onVerDetalle = { folio -> navController.navigate(Ruta.Detalle.crear(folio)) },
                    onVerPerfil = { navController.navigate(Ruta.MiPerfil.ruta) },
                    onSalir = cerrarSesion
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

        // Detalle: el folio viaja en la ruta y con el se busca el compromiso en la lista cargada
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
                        // El ViewModel valida y guarda el pago en SQLite; si resulta bien, se abre el comprobante
                        val pago = compromisosViewModel.pagar(compromiso)
                        if (pago != null) {
                            navController.navigate(Ruta.Comprobante.crear(compromiso.folio)) {
                                // Se quita el detalle del historial: "atras" desde el comprobante vuelve a la lista
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

        // Comprobante de un compromiso pagado
        composable(
            route = Ruta.Comprobante.ruta,
            arguments = listOf(navArgument("folio") { type = NavType.StringType })
        ) { entrada ->
            val folio = entrada.arguments?.getString("folio") ?: ""
            // remember evita repetir la consulta a la base de datos en cada redibujado
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
