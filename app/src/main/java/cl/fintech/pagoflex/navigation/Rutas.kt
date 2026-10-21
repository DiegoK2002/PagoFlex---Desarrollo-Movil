package cl.fintech.pagoflex.navigation

/**
 * Rutas de las pantallas de la app. Usar una clase sellada en lugar de textos sueltos permite
 * que el compilador avise si una ruta esta mal escrita.
 *
 * Las rutas con "{folio}" reciben un dato al navegar, por ejemplo:
 * Ruta.Detalle.crear("CP-2026-0101") produce "detalle/CP-2026-0101".
 */
sealed class Ruta(val ruta: String) {
    data object InicioSesion : Ruta("inicio_sesion")
    data object Registro : Ruta("registro")
    data object Compromisos : Ruta("compromisos")

    data object HistorialPagos : Ruta("historial_pagos")

    data object Reconocimientos : Ruta("reconocimientos")

    data object MiPerfil : Ruta("mi_perfil")
    data object InicioAgente : Ruta("inicio_agente")

    data object Detalle : Ruta("detalle/{folio}") {
        fun crear(folio: String) = "detalle/$folio"
    }

    data object Comprobante : Ruta("comprobante/{folio}") {
        fun crear(folio: String) = "comprobante/$folio"
    }
}
