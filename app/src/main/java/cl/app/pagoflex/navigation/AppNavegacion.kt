package cl.app.pagoflex.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.app.pagoflex.R
import cl.app.pagoflex.data.RepositorioPagoFlex
import cl.app.pagoflex.model.Rol
import cl.app.pagoflex.ui.screens.InicioProvisorioScreen
import cl.app.pagoflex.ui.screens.InicioUsuarioScreen
import cl.app.pagoflex.ui.screens.SeleccionSesionScreen
import cl.app.pagoflex.viewmodel.SesionViewModel
import androidx.compose.ui.res.stringResource

// =====================================================================
//  AppNavegacion.kt  —  EL "MAPA" DE LA APP (igual que en MiRegistro)
//  ---------------------------------------------------------------------
//  NavController -> el "conductor": sabe dónde estamos y el historial.
//  NavHost       -> el contenedor: dibuja la pantalla de la ruta actual.
//  composable()  -> registra una ruta y QUÉ pantalla mostrar.
//
//  Regla: las pantallas NO reciben el NavController, solo lambdas.
//  El SesionViewModel se crea AQUÍ (fuera del NavHost) para compartirlo.
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
    val usuarioActual by sesionViewModel.usuarioActual.collectAsStateWithLifecycle()

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
                    compromisos = RepositorioPagoFlex.compromisosDe(usuario.codigo),
                    nombreEmpresa = RepositorioPagoFlex::nombreEmpresa,
                    onSalir = cerrarSesion
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
