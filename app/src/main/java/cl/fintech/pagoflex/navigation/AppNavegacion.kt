package cl.fintech.pagoflex.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.fintech.pagoflex.data.RepositorioPagoFlex
import cl.fintech.pagoflex.model.EstadoCompromiso
import cl.fintech.pagoflex.model.Pago
import cl.fintech.pagoflex.model.Rol
import cl.fintech.pagoflex.model.Usuario
import cl.fintech.pagoflex.ui.data.DatosEjemplo
import cl.fintech.pagoflex.ui.screens.ComprobanteScreen
import cl.fintech.pagoflex.ui.screens.CompromisosScreen
import cl.fintech.pagoflex.ui.screens.DetalleCompromisoScreen
import cl.fintech.pagoflex.ui.screens.InicioSesionScreen
import cl.fintech.pagoflex.ui.screens.MiPerfilScreen
import cl.fintech.pagoflex.util.calcularDiasDeAtraso
import cl.fintech.pagoflex.util.compromisoAnterior
import cl.fintech.pagoflex.util.motivoNoPagable
import cl.fintech.pagoflex.util.ocultarRut
import java.time.LocalDateTime
import java.time.LocalTime

// =====================================================================
//  AppNavegacion.kt  —  EL "MAPA" DE LA APP
//  ---------------------------------------------------------------------
//  CAMBIOS de este archivo:
//   1) Cada rol ve solo lo que le corresponde (RN-22):
//        usuario final -> SUS compromisos
//        ejecutivo     -> los de SU empresa
//   2) El detalle calcula días de atraso, recargo y total a pagar
//      (RN-02, RN-03) y decide si se puede pagar (RN-04, RN-09).
//   3) El pago se registra con el total (monto + recargo), un número de
//      comprobante nuevo y la fecha de referencia de los datos de ejemplo.
//   4) El comprobante muestra el RUT parcialmente oculto (RN-05).
// =====================================================================

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

    // "Hoy" para los cálculos: la fecha de referencia de los datos de ejemplo (06-10-2026)
    val hoy = DatosEjemplo.FECHA_REFERENCIA

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

                // Cada rol ve solo lo suyo (RN-22)
                val compromisosUsuario = compromisosRegistrados
                    .filter { compromiso ->
                        when (usuario.rol) {
                            Rol.USUARIO_FINAL -> compromiso.codigoUsuario == usuario.codigo
                            Rol.EJECUTIVO -> compromiso.codigoEmpresa == usuario.codigoEmpresa
                            // El agente trabaja con reportes; hasta tener su pantalla ve la lista completa
                            Rol.AGENTE -> true
                        }
                    }
                    .sortedBy { it.fechaVencimiento }

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

                // ----- Cálculos (no se guardan, se calculan cada vez) -----
                val diasAtraso = calcularDiasDeAtraso(compromiso, hoy)
                val recargo = if (diasAtraso > 0) {
                    RepositorioPagoFlex.recargoDe(compromiso.codigoEmpresa)
                } else {
                    0
                }
                val total = compromiso.monto + recargo

                // ----- Reglas: ¿se puede pagar? -----
                val anterior = compromisoAnterior(compromiso, compromisosRegistrados)
                val motivoNoPago = motivoNoPagable(compromiso, anterior)

                DetalleCompromisoScreen(
                    compromiso = compromiso,
                    nombreEmpresa = nombreEmpresa,
                    diasAtraso = diasAtraso,
                    recargo = recargo,
                    total = total,
                    motivoNoPago = motivoNoPago,
                    anterior = anterior,

                    onVolver = {
                        navController.popBackStack()
                    },

                    onConfirmarPago = {

                        val usuario = usuarioActual

                        // Solo se paga si realmente se puede pagar
                        if (usuario != null && motivoNoPago == null) {

                            // Número nuevo: el mayor que existe + 1
                            val siguiente = pagosRegistrados.maxOf {
                                it.numeroComprobante.removePrefix("CPR-").toInt()
                            } + 1

                            val nuevoPago = Pago(
                                numeroComprobante = "CPR-" + siguiente.toString().padStart(6, '0'),
                                folioCompromiso = compromiso.folio,
                                codigoUsuario = usuario.codigo,
                                codigoEmpresa = compromiso.codigoEmpresa,
                                concepto = compromiso.concepto,
                                fechaHora = LocalDateTime.of(hoy, LocalTime.now()),
                                montoPagado = total,
                                canal = "App",
                                // A tiempo si se paga hasta el día del vencimiento (RN-10)
                                aTiempo = !hoy.isAfter(compromiso.fechaVencimiento),
                                medioPago = "Pago simulado"
                            )

                            pagosRegistrados = pagosRegistrados + nuevoPago

                            // Cambiar el estado del compromiso a PAGADO
                            compromisosRegistrados = compromisosRegistrados.map { registrado ->
                                if (registrado.folio == compromiso.folio) {
                                    registrado.copy(estado = EstadoCompromiso.PAGADO)
                                } else {
                                    registrado
                                }
                            }

                            // Abrir el comprobante; "atrás" vuelve a la lista, no al detalle
                            navController.navigate("comprobante/${compromiso.folio}") {
                                popUpTo("detalle/{folio}") {
                                    inclusive = true
                                }
                            }
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

            // Si hubiera más de un pago del mismo folio, se muestra el último
            val pago = pagosRegistrados.findLast {
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
                    // RUT parcialmente oculto (RN-05, RNF-07)
                    rutParcial = if (usuario != null) ocultarRut(usuario.rut) else "",

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

                    Text(text = "No se encontró el pago o el compromiso.")

                    Text(text = "Folio buscado: ${folio ?: "sin folio"}")

                    Button(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Text("Volver")
                    }
                }
            }
        }
    }
}
