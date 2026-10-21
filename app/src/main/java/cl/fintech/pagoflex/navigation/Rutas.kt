package cl.fintech.pagoflex.navigation

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
