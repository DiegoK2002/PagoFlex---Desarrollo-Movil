package cl.fintech.pagoflex.navigation

import androidx.compose.runtime.Composable
import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.math.BigDecimal
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.fintech.pagoflex.model.Rol
import cl.fintech.pagoflex.ui.screens.CambiarContrasenaScreen
import cl.fintech.pagoflex.ui.screens.ComprobanteScreen
import cl.fintech.pagoflex.ui.screens.EditarPerfilScreen
import cl.fintech.pagoflex.ui.screens.RecuperarContrasenaScreen
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
import cl.fintech.pagoflex.viewmodel.CotizacionesViewModel
import cl.fintech.pagoflex.ui.screens.HistorialPagosScreen
import cl.fintech.pagoflex.ui.screens.ReconocimientosScreen
import androidx.compose.runtime.LaunchedEffect
import cl.fintech.pagoflex.viewmodel.PagosViewModel
import cl.fintech.pagoflex.viewmodel.FotoPerfilViewModel
import cl.fintech.pagoflex.ui.screens.AdministradorScreen


@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val sesionViewModel: SesionViewModel = viewModel()
    val compromisosViewModel: CompromisosViewModel = viewModel()
    val pagosViewModel: PagosViewModel = viewModel()
    val reportesViewModel: ReportesViewModel = viewModel()
    val cotizacionesViewModel: CotizacionesViewModel = viewModel()
    val fotoPerfilViewModel: FotoPerfilViewModel = viewModel()

    val usuarioActual by sesionViewModel.usuarioActual.collectAsStateWithLifecycle()

    val volverSiCorresponde: (String) -> Unit = { ruta ->
        if (navController.currentDestination?.route == ruta) {
            navController.navigateUp()
        }
    }

    val compromisos by compromisosViewModel.compromisos.collectAsStateWithLifecycle()
    val resumenCompromisos by compromisosViewModel.resumenCompromisos.collectAsStateWithLifecycle()
    val cargandoCompromisos by compromisosViewModel.cargando.collectAsStateWithLifecycle()
    val errorCompromisos by compromisosViewModel.errorCarga.collectAsStateWithLifecycle()
    val registrandoPago by pagosViewModel.registrando.collectAsStateWithLifecycle()
    val errorRegistroPago by pagosViewModel.errorRegistro.collectAsStateWithLifecycle()
    val indicadorCumplimiento by compromisosViewModel.indicadorCumplimiento.collectAsStateWithLifecycle()
    val reconocimientos by compromisosViewModel.reconocimientos.collectAsStateWithLifecycle()
    val reportes by reportesViewModel.reportes.collectAsStateWithLifecycle()
    val cargandoReportes by reportesViewModel.cargando.collectAsStateWithLifecycle()
    val operandoReporte by reportesViewModel.operando.collectAsStateWithLifecycle()
    val errorReporteRemoto by reportesViewModel.error.collectAsStateWithLifecycle()
    val estadoCotizaciones by cotizacionesViewModel.estado.collectAsStateWithLifecycle()
    val fotoPerfil by fotoPerfilViewModel.fotoPerfil.collectAsStateWithLifecycle()
    val guardandoFoto by fotoPerfilViewModel.guardandoFoto.collectAsStateWithLifecycle()

    val cerrarSesion = {
        fotoPerfilViewModel.limpiar()
        sesionViewModel.cerrarSesion()
        compromisosViewModel.limpiar()
        pagosViewModel.limpiar()
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
                    pagosViewModel.limpiar()
                    sesionViewModel.iniciarSesion(usuario)
                    compromisosViewModel.cargar(usuario.id)
                    reportesViewModel.cargar(usuario)
                    if (usuario.rol == Rol.USUARIO_FINAL) {
                        cotizacionesViewModel.consultar()
                    }
                    val destino = when (usuario.rol) {
                        Rol.USUARIO_FINAL -> Ruta.Compromisos.ruta
                        Rol.AGENTE -> Ruta.InicioAgente.ruta
                        Rol.ADMINISTRADOR -> Ruta.Administrador.ruta
                    }
                    navController.navigate(destino) {
                        popUpTo(Ruta.InicioSesion.ruta) { inclusive = true }
                    }
                },
                onCrearCuenta = { navController.navigate(Ruta.Registro.ruta) },
                onRecuperarContrasena = {
                    navController.navigate(Ruta.RecuperarContrasena.ruta)
                }
            )
        }

        composable(Ruta.RecuperarContrasena.ruta) {
            RecuperarContrasenaScreen(
                onVolver = { volverSiCorresponde(Ruta.RecuperarContrasena.ruta) }
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
                    cargandoCompromisos = cargandoCompromisos,
                    errorCompromisos = errorCompromisos,
                    onActualizar = compromisosViewModel::actualizar,
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
                val pagosRemotos by pagosViewModel.pagos.collectAsStateWithLifecycle()
                val cargandoPagos by pagosViewModel.cargando.collectAsStateWithLifecycle()
                val errorPagos by pagosViewModel.error.collectAsStateWithLifecycle()

                LaunchedEffect(usuario.id) {
                    pagosViewModel.cargar(usuario.id)
                }

                HistorialPagosScreen(
                    pagos = pagosRemotos,
                    compromisos = compromisos,
                    nombreEmpresa = compromisosViewModel::nombreEmpresa,
                    cargando = cargandoPagos,
                    error = errorPagos,
                    onActualizar = pagosViewModel::actualizar,
                    onVerComprobante = { folio ->
                        navController.navigate(Ruta.Comprobante.crear(folio))
                    },
                    onVolver = {
                        volverSiCorresponde(Ruta.HistorialPagos.ruta)
                    }
                )
            }
        }

        composable(Ruta.Reconocimientos.ruta) {
            val usuario = usuarioActual

            if (usuario != null && usuario.rol == Rol.USUARIO_FINAL) {
                ReconocimientosScreen(
                    reconocimientos = reconocimientos,
                    onVolver = { volverSiCorresponde(Ruta.Reconocimientos.ruta) }
                )
            }
        }

        composable(Ruta.MiPerfil.ruta) {
            val usuario = usuarioActual
            if (usuario != null) {
                MiPerfilScreen(
                    usuario = usuario,
                    fotoPerfil = fotoPerfil,
                    onGuardarFoto = { bitmap, onResultado ->
                        fotoPerfilViewModel.guardarFoto(bitmap, onResultado)
                    },
                    guardandoFoto = guardandoFoto,
                    onEditarDatos = { navController.navigate(Ruta.EditarPerfil.ruta) },
                    onCambiarContrasena = {
                        navController.navigate(Ruta.CambiarContrasena.ruta)
                    },
                    onVolver = { volverSiCorresponde(Ruta.MiPerfil.ruta) }
                )
            }
        }

        composable(Ruta.EditarPerfil.ruta) {
            val usuario = usuarioActual
            if (usuario != null) {
                EditarPerfilScreen(
                    usuario = usuario,
                    onUsuarioActualizado = { actualizado ->
                        sesionViewModel.actualizarUsuario(actualizado)
                    },
                    onVolver = { volverSiCorresponde(Ruta.EditarPerfil.ruta) }
                )
            }
        }

        composable(Ruta.CambiarContrasena.ruta) {
            val usuario = usuarioActual
            if (usuario != null) {
                CambiarContrasenaScreen(
                    usuario = usuario,
                    onVolver = { volverSiCorresponde(Ruta.CambiarContrasena.ruta) }
                )
            }
        }

        composable(Ruta.Administrador.ruta) {
            val usuario = usuarioActual
            if (usuario != null && usuario.rol == Rol.ADMINISTRADOR) {
                var mostrarConfirmacionSalida by rememberSaveable { mutableStateOf(false) }
                BackHandler(enabled = !mostrarConfirmacionSalida) {
                    mostrarConfirmacionSalida = true
                }
                if (mostrarConfirmacionSalida) {
                    ConfirmacionCerrarSesion(
                        onCancelar = { mostrarConfirmacionSalida = false },
                        onConfirmar = {
                            mostrarConfirmacionSalida = false
                            cerrarSesion()
                        }
                    )
                }
                AdministradorScreen(
                    onSalir = { mostrarConfirmacionSalida = true },
                    idUsuarioActual = usuario.id
                )
            }
        }

        composable(Ruta.InicioAgente.ruta) {
            val usuario = usuarioActual
            if (usuario != null && usuario.rol == Rol.AGENTE) {
                var mostrarConfirmacionSalida by rememberSaveable { mutableStateOf(false) }
                BackHandler(enabled = !mostrarConfirmacionSalida) {
                    mostrarConfirmacionSalida = true
                }
                if (mostrarConfirmacionSalida) {
                    ConfirmacionCerrarSesion(
                        onCancelar = { mostrarConfirmacionSalida = false },
                        onConfirmar = {
                            mostrarConfirmacionSalida = false
                            cerrarSesion()
                        }
                    )
                }
                InicioAgenteScreen(
                    usuario = usuario,
                    onSalir = { mostrarConfirmacionSalida = true },
                    reportes = reportes,
                    operandoReporte = operandoReporte,
                    errorReporte = errorReporteRemoto,
                    cargandoReportes = cargandoReportes,
                    onActualizar = reportesViewModel::actualizar,
                    onTomarReporte = { idReporte ->
                        reportesViewModel.tomarReporte(idReporte)
                    },
                    onResolverReporte = { idReporte, respuesta, onResultado ->
                        reportesViewModel.resolverReporte(idReporte, respuesta, onResultado)
                    }
                )
            }
        }

        composable(
            route = Ruta.Detalle.ruta,
            arguments = listOf(navArgument("folio") { type = NavType.StringType })
        ) { entrada ->
            val folio = entrada.arguments?.getString("folio") ?: ""
            val usuario = usuarioActual
            LaunchedEffect(folio) {
                pagosViewModel.limpiarEstadoRegistro()
            }
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
                    onVolver = { volverSiCorresponde(Ruta.Detalle.ruta) },
                    onConfirmarPago = {
                        val idCompromiso = compromiso.folio
                            .removePrefix("REM-")
                            .toLongOrNull()

                        if (
                            usuario != null &&
                            usuario.rol == Rol.USUARIO_FINAL &&
                            idCompromiso != null &&
                            compromiso.idUsuario == usuario.id &&
                            compromiso.monto > 0 &&
                            compromisosViewModel.motivoDe(compromiso) == null
                        ) {
                            pagosViewModel.registrarPago(
                                idUsuario = usuario.id,
                                idCompromiso = idCompromiso,
                                monto = BigDecimal.valueOf(compromiso.monto.toLong()),
                                onRegistrado = {
                                    compromisosViewModel.actualizar()
                                    if (
                                        navController.currentDestination?.route == Ruta.Detalle.ruta &&
                                        navController.currentBackStackEntry
                                            ?.arguments?.getString("folio") == folio
                                    ) {
                                        navController.navigate(Ruta.Comprobante.crear(folio)) {
                                            popUpTo(Ruta.Detalle.ruta) { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }
                    },
                    onVerComprobante = {
                        navController.navigate(Ruta.Comprobante.crear(compromiso.folio))
                    },

                    onCrearReporte = { motivo, comentario, onResultado ->
                        reportesViewModel.crearReporte(
                            compromiso.folio,
                            motivo,
                            comentario,
                            onResultado
                        )
                    },

                    reporteActual = reportes.firstOrNull {
                        it.folio == compromiso.folio
                    },
                    operacionesHabilitadas = usuario != null &&
                            usuario.rol == Rol.USUARIO_FINAL &&
                            compromiso.idUsuario == usuario.id &&
                            compromiso.folio.startsWith("REM-"),
                    procesandoReporte = operandoReporte,
                    pagosHabilitados = usuario != null &&
                            usuario.rol == Rol.USUARIO_FINAL &&
                            compromiso.idUsuario == usuario.id &&
                            compromiso.folio.startsWith("REM-") &&
                            compromiso.folio.removePrefix("REM-").toLongOrNull() != null,
                    registrandoPago = registrandoPago,
                    errorPago = errorRegistroPago
                )
            }
        }


        composable(
            route = Ruta.Comprobante.ruta,
            arguments = listOf(navArgument("folio") { type = NavType.StringType })
        ) { entrada ->
            val folio = entrada.arguments?.getString("folio") ?: ""

            val usuario = usuarioActual
            val idCompromiso = folio.removePrefix("REM-").toLongOrNull()
            val pago by pagosViewModel.comprobante.collectAsStateWithLifecycle()
            val cargando by pagosViewModel.cargandoComprobante.collectAsStateWithLifecycle()
            val error by pagosViewModel.errorComprobante.collectAsStateWithLifecycle()

            LaunchedEffect(folio, usuario?.id) {
                if (usuario != null && idCompromiso != null) {
                    pagosViewModel.cargarComprobante(usuario.id, idCompromiso)
                }
            }

            val comprobanteActual = pago?.takeIf {
                it.idUsuario == usuario?.id && it.idCompromiso == idCompromiso
            }

            if (usuario != null && comprobanteActual != null) {
                val compromiso = compromisos.firstOrNull { it.folio == folio }
                ComprobanteScreen(
                    pago = comprobanteActual,
                    folio = folio,
                    concepto = compromiso?.concepto ?: "Compromiso #$idCompromiso",
                    nombreEmpresa = compromiso?.let {
                        compromisosViewModel.nombreEmpresa(it.codigoEmpresa)
                    } ?: "Empresa no disponible",
                    nombrePagador = usuario.nombreCompleto,
                    rutParcial = ocultarRut(usuario.rut),
                    onVolver = { volverSiCorresponde(Ruta.Comprobante.ruta) }
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (cargando) {
                        CircularProgressIndicator()
                        Text("Cargando comprobante...")
                    } else {
                        Text(error ?: "Comprobante no disponible.")
                        Button(onClick = { volverSiCorresponde(Ruta.Comprobante.ruta) }) {
                            Text("Volver")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfirmacionCerrarSesion(
    onCancelar: () -> Unit,
    onConfirmar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Cerrar sesión") },
        text = { Text("¿Estás seguro que deseas cerrar tu sesión actual?") },
        confirmButton = {
            TextButton(onClick = onConfirmar) { Text("Cerrar sesión") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
