package cl.app.pagoflex.navigation

// =====================================================================
//  Ruta.kt  —  LAS "DIRECCIONES" DE CADA PANTALLA (igual que en MiRegistro)
//  ---------------------------------------------------------------------
//  sealed class en vez de Strings sueltos: si me equivoco al escribir
//  una ruta, el compilador me avisa.
//  Cada rol tiene su propia pantalla de inicio.
// =====================================================================

sealed class Ruta(val ruta: String) {
    data object SeleccionSesion : Ruta("seleccion_sesion")   // pantalla inicial
    data object InicioUsuario : Ruta("inicio_usuario")
    data object InicioAgente : Ruta("inicio_agente")
    data object InicioEjecutivo : Ruta("inicio_ejecutivo")
}
