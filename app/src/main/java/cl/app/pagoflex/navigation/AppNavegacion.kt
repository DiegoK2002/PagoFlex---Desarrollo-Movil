package cl.app.pagoflex.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.app.pagoflex.R
import cl.app.pagoflex.data.RepositorioPagoFlex
import cl.app.pagoflex.model.Rol
import cl.app.pagoflex.ui.screens.ComprobanteScreen
import cl.app.pagoflex.ui.screens.DetalleCompromisoScreen
import cl.app.pagoflex.ui.screens.InicioProvisorioScreen
import cl.app.pagoflex.ui.screens.InicioUsuarioScreen
import cl.app.pagoflex.ui.screens.SeleccionSesionScreen
import cl.app.pagoflex.util.ocultarRut
import cl.app.pagoflex.viewmodel.CompromisosViewModel
import cl.app.pagoflex.viewmodel.SesionViewModel

// =====================================================================
//  AppNavegacion.kt  —  EL "MAPA" DE LA APP (igual que en MiRegistro)
//  ---------------------------------------------------------------------
//  NavController -> el "conductor": sabe dónde estamos y el historial.
//  NavHost       -> el contenedor: dibuja la pantalla de la ruta actual.
//  composable()  -> registra una ruta y QUÉ pantalla mostrar.
//
//  Regla: las pantallas NO reciben el NavController, solo lambdas.
//  Los ViewModels compartidos se crean AQUÍ (fuera del NavHost).
//
//  Dos formas de pasar datos entre pantallas (como en MiRegistro):
//    1) ViewModel compartido  -> compromisos y pagos
//    2) Argumento de ruta     -> "detalle/{folio}" y "comprobante/{numero}"
// =====================================================================

private fun rutaInicioDe(rol: Rol): String = when (rol) {
    Rol.USUARIO_FINAL -> Ruta.InicioUsuario.ruta
    Rol.AGENTE -> Ruta.InicioAgente.ruta
    Rol.EJECUTIVO -> Ruta.InicioEjecutivo.ruta
}

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val sesionViewModel: SesionViewModel = viewModel()
    val compromisosViewModel: CompromisosViewModel = viewModel()
    val usuarioActual by sesionViewModel.usuarioActual.collectAsStateWithLifecycle()
    val compromisos by compromisosViewModel.compromisos.collectAsStateWithLifecycle()
    val pagos by compromisosViewModel.pagos.collectAsStateWithLifecycle()

    // Cierra la sesión y vuelve a la selección, sin dejar pantallas en el historial
    val cerrarSesion = {
        sesionViewModel.cerrarSesion()
        navController.navigate(Ruta.SeleccionSesion.ruta) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = Ruta.SeleccionSesion.ruta) {

        composable(Ruta.SeleccionSesion.ruta) {
            SeleccionSesionScreen(
                sesiones = sesionViewModel.sesionesSimuladas,
                onElegir = { usuario ->
                    sesionViewModel.iniciarSesion(usuario)
                    navController.navigate(rutaInicioDe(usuario.rol)) {
                        popUpTo(Ruta.SeleccionSesion.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(Ruta.InicioUsuario.ruta) {
            val usuario = usuarioActual
            if (usuario != null) {
                InicioUsuarioScreen(
                    usuario = usuario,
                    compromisos = compromisos
                        .filter { it.codigoUsuario == usuario.codigo }
                        .sortedBy { it.fechaVencimiento },
                    nombreEmpresa = RepositorioPagoFlex::nombreEmpresa,
                    // el folio VIAJA en la ruta: "detalle/CP-2026-0101"
                    onVerDetalle = { folio -> navController.navigate(Ruta.Detalle.crear(folio)) },
                    onSalir = cerrarSesion
                )
            }
        }

        // Ruta con argumento: se lee el folio desde la entrada del historial
        composable(
            route = Ruta.Detalle.ruta,
            arguments = listOf(navArgument("folio") { type = NavType.StringType })
        ) { entrada ->
            val folio = entrada.arguments?.getString("folio") ?: ""
            val compromiso = compromisos.firstOrNull { it.folio == folio }
            if (compromiso != null) {
                DetalleCompromisoScreen(
                    compromiso = compromiso,
                    nombreEmpresa = RepositorioPagoFlex.nombreEmpresa(compromiso.codigoEmpresa),
                    diasAtraso = compromisosViewModel.diasDeAtraso(compromiso),
                    recargo = compromisosViewModel.recargoDe(compromiso),
                    total = compromisosViewModel.totalAPagar(compromiso),
                    motivoNoPago = compromisosViewModel.motivoNoPagable(compromiso),
                    anterior = compromisosViewModel.compromisoAnterior(compromiso),
                    onVolver = { navController.navigateUp() },
                    onConfirmarPago = {
                        // 1) el ViewModel valida y registra el pago  2) se muestra el comprobante
                        val pago = compromisosViewModel.pagar(compromiso)
                        if (pago != null) {
                            navController.navigate(Ruta.Comprobante.crear(pago.numeroComprobante)) {
                                popUpTo(Ruta.Detalle.ruta) { inclusive = true }   // atrás vuelve a la lista
                            }
                        }
                    }
                )
            }
        }

        composable(
            route = Ruta.Comprobante.ruta,
            arguments = listOf(navArgument("numero") { type = NavType.StringType })
        ) { entrada ->
            val numero = entrada.arguments?.getString("numero") ?: ""
            val pago = pagos.firstOrNull { it.numeroComprobante == numero }
            val usuario = usuarioActual
            if (pago != null && usuario != null) {
                ComprobanteScreen(
                    pago = pago,
                    nombreEmpresa = RepositorioPagoFlex.nombreEmpresa(pago.codigoEmpresa),
                    nombrePagador = usuario.nombreCompleto,
                    rutParcial = ocultarRut(usuario.rut),
                    onVolver = { navController.navigateUp() }
                )
            }
        }

        composable(Ruta.InicioAgente.ruta) {
            val usuario = usuarioActual
            if (usuario != null) {
                InicioProvisorioScreen(
                    titulo = stringResource(R.string.titulo_agente),
                    usuario = usuario,
                    onSalir = cerrarSesion
                )
            }
        }

        composable(Ruta.InicioEjecutivo.ruta) {
            val usuario = usuarioActual
            if (usuario != null) {
                InicioProvisorioScreen(
                    titulo = stringResource(R.string.titulo_ejecutivo),
                    usuario = usuario,
                    onSalir = cerrarSesion
                )
            }
        }
    }
}
