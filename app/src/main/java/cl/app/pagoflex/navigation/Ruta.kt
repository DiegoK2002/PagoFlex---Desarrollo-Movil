package cl.app.pagoflex.navigation

// =====================================================================
//  Ruta.kt  —  LAS "DIRECCIONES" DE CADA PANTALLA (igual que en MiRegistro)
//  ---------------------------------------------------------------------
//  sealed class en vez de Strings sueltos: si me equivoco al escribir
//  una ruta, el compilador me avisa.
//
//  CAMBIO: rutas CON ARGUMENTO, igual que "editar/{indice}" en MiRegistro.
//  {folio} y {numero} son "huecos" que se rellenan al navegar:
//      crear("CP-2026-0101") -> "detalle/CP-2026-0101"
// =====================================================================

sealed class Ruta(val ruta: String) {
    data object SeleccionSesion : Ruta("seleccion_sesion")   // pantalla inicial
    data object InicioUsuario : Ruta("inicio_usuario")
    data object InicioAgente : Ruta("inicio_agente")
    data object InicioEjecutivo : Ruta("inicio_ejecutivo")

    data object Detalle : Ruta("detalle/{folio}") {
        fun crear(folio: String) = "detalle/$folio"
    }

    data object Comprobante : Ruta("comprobante/{numero}") {
        fun crear(numero: String) = "comprobante/$numero"
    }
}
