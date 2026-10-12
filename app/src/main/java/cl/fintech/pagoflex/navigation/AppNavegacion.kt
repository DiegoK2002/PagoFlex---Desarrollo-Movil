
package cl.fintech.pagoflex.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import java.time.LocalDateTime

import cl.fintech.pagoflex.model.Pago
import cl.fintech.pagoflex.model.EstadoCompromiso
import cl.fintech.pagoflex.model.MotivoNoPago
import cl.fintech.pagoflex.model.Usuario
import cl.fintech.pagoflex.ui.screens.MiPerfilScreen
import cl.fintech.pagoflex.ui.data.DatosEjemplo
import cl.fintech.pagoflex.ui.screens.InicioSesionScreen
import cl.fintech.pagoflex.ui.screens.CompromisosScreen
import cl.fintech.pagoflex.ui.screens.DetalleCompromisoScreen
import cl.fintech.pagoflex.ui.screens.ComprobanteScreen

@Composable
fun AppNavegacion() {

    val navController = rememberNavController()

    var usuarioActual by remember {
        mutableStateOf<Usuario?>(null)
    }

    var pagosRegistrados by remember {
        mutableStateOf(DatosEjemplo.pagos)
    }

    var compromisosRegistrados by remember {
        mutableStateOf(DatosEjemplo.compromisos)
    }

    var pagoActual by remember {
        mutableStateOf<Pago?>(null)
    }

    NavHost(
        navController = navController,
        startDestination = "inicio_sesion"
    ) {

        // Pantalla de inicio de sesión
        composable("inicio_sesion") {

            InicioSesionScreen(
                onInicioExitoso = { usuario ->

                    usuarioActual = usuario

                    navController.navigate("compromisos") {
                        popUpTo("inicio_sesion") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        // Pantalla de Mi perfil
        composable("mi_perfil") {
            val usuario = usuarioActual

            println("Usuario: ${usuario?.codigo}")
            println("Código empresa: ${usuario?.codigoEmpresa}")

            if (usuario != null) {
                MiPerfilScreen(
                    usuario = usuario,
                    nombreEmpresa = { codigo ->
                        DatosEjemplo.empresas.find {
                            it.codigo == codigo
                        }?.nombreFantasia ?: "Empresa no encontrada"
                    },
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Pantalla con los compromisos del usuario
        composable("compromisos") {

            val usuario = usuarioActual

            if (usuario != null) {

                val compromisosUsuario =
                    compromisosRegistrados.filter { compromiso ->
                        usuario.codigoEmpresa == null ||
                                compromiso.codigoEmpresa == usuario.codigoEmpresa
                    }

                CompromisosScreen(
                    usuario = usuario,
                    compromisos = compromisosUsuario,

                    nombreEmpresa = { codigoEmpresa ->
                        DatosEjemplo.empresas.find { empresa ->
                            empresa.codigo == codigoEmpresa
                        }?.nombreFantasia ?: "Empresa no encontrada"
                    },

                    onVerDetalle = { folio ->
                        navController.navigate("detalle/$folio")
                    },

                    onVerPerfil = {
                        navController.navigate("mi_perfil")
                    },

                    onSalir = {
                        usuarioActual = null

                        navController.navigate("inicio_sesion") {
                            popUpTo("compromisos") {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }

        // Pantalla de detalle de un compromiso
        composable(
            route = "detalle/{folio}",
            arguments = listOf(
                navArgument("folio") {
                    type = NavType.StringType
                }
            )
        ) { entrada ->

            val folio = entrada.arguments?.getString("folio")

            val compromiso = compromisosRegistrados.find {
                it.folio == folio
            }

            if (compromiso != null) {

                val nombreEmpresa = DatosEjemplo.empresas.find {
                    it.codigo == compromiso.codigoEmpresa
                }?.nombreFantasia ?: "Empresa no encontrada"

                DetalleCompromisoScreen(
                    compromiso = compromiso,
                    nombreEmpresa = nombreEmpresa,
                    diasAtraso = 0,
                    recargo = 0,
                    total = compromiso.monto,

                    motivoNoPago = if (
                        compromiso.estado == EstadoCompromiso.PAGADO
                    ) {
                        MotivoNoPago.YA_PAGADO
                    } else {
                        null
                    },

                    anterior = null,

                    onVolver = {
                        navController.popBackStack()
                    },

                    onConfirmarPago = {

                        val usuario = usuarioActual

                        // No permitir volver a pagar un compromiso pagado
                        if (
                            usuario != null &&
                            compromiso.estado != EstadoCompromiso.PAGADO
                        ) {

                            val pagoExistente = pagosRegistrados.find {
                                it.folioCompromiso == compromiso.folio
                            }

                            if (pagoExistente != null) {

                                // Reutilizar el pago existente
                                pagoActual = pagoExistente

                            } else {

                                // Crear el registro del nuevo pago
                                val nuevoPago = Pago(
                                    numeroComprobante = "CPR-${(pagosRegistrados.size + 454).toString().padStart(6, '0')}",
                                    folioCompromiso = compromiso.folio,
                                    codigoUsuario = usuario.codigo,
                                    codigoEmpresa = compromiso.codigoEmpresa,
                                    concepto = compromiso.concepto,
                                    fechaHora = LocalDateTime.now(),
                                    montoPagado = compromiso.monto,
                                    canal = "App",
                                    aTiempo = !compromiso.fechaVencimiento.isBefore(
                                        LocalDateTime.now().toLocalDate()
                                    ),
                                    medioPago = "Pago simulado"
                                )

                                pagosRegistrados =
                                    pagosRegistrados + nuevoPago

                                pagoActual = nuevoPago
                            }

                            // Cambiar el estado del compromiso a PAGADO
                            compromisosRegistrados =
                                compromisosRegistrados.map {
                                        compromisoRegistrado ->

                                    if (
                                        compromisoRegistrado.folio ==
                                        compromiso.folio
                                    ) {
                                        compromisoRegistrado.copy(
                                            estado = EstadoCompromiso.PAGADO
                                        )
                                    } else {
                                        compromisoRegistrado
                                    }
                                }

                            // Abrir el comprobante
                            navController.navigate(
                                "comprobante/${compromiso.folio}"
                            )
                        }
                    }
                )
            }
        }

        // Pantalla del comprobante de pago
        composable(
            route = "comprobante/{folio}",
            arguments = listOf(
                navArgument("folio") {
                    type = NavType.StringType
                }
            )
        ) { entrada ->

            val folio = entrada.arguments?.getString("folio")

            val pago = pagosRegistrados.find {
                it.folioCompromiso == folio
            }

            val compromiso = compromisosRegistrados.find {
                it.folio == folio
            }

            if (pago != null && compromiso != null) {

                val nombreEmpresa = DatosEjemplo.empresas.find {
                    it.codigo == compromiso.codigoEmpresa
                }?.nombreFantasia ?: "Empresa no encontrada"

                val usuario = usuarioActual

                ComprobanteScreen(
                    pago = pago,
                    nombreEmpresa = nombreEmpresa,
                    nombrePagador = usuario?.nombreCompleto ?: "Usuario",
                    rutParcial = usuario?.rut ?: "",

                    onVolver = {
                        navController.popBackStack()
                    }
                )

            } else {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {

                    androidx.compose.material3.Text(
                        text = "No se encontró el pago o el compromiso."
                    )

                    androidx.compose.material3.Text(
                        text = "Folio buscado: ${folio ?: "sin folio"}"
                    )

                    androidx.compose.material3.Button(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        androidx.compose.material3.Text("Volver")
                    }
                }
            }
        }
    }
}
