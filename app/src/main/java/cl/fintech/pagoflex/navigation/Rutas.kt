package cl.fintech.pagoflex.navigation

// =====================================================================
//  Rutas.kt  —  LAS "DIRECCIONES" DE CADA PANTALLA  (NUEVO, Bloque 1)
//  ---------------------------------------------------------------------
//  Cada pantalla tiene un nombre único, como las URL de una web:
//      "formulario"   "perfil"   "usuarios"
//
//  ¿Por qué sealed class y no Strings sueltos?
//  Si escribo navigate("perfli") por error, compila y la app falla.
//  Con Ruta.Perfil.ruta el compilador revisa el nombre por nosotros.
//
//  CAMBIO (Bloque 4): ruta CON ARGUMENTO  "editar/{indice}".
//  {indice} es un "hueco" que se rellena al navegar: "editar/0", "editar/2"...
//  Así el dato (qué usuario editar) viaja DENTRO de la ruta, como en una URL.
// =====================================================================

sealed class Ruta(val ruta: String) {
    data object InicioSesion : Ruta("sesion")   // pantalla inicial
    data object Perfil : Ruta("perfil")
    data object compromiso : Ruta("compromiso")
    data object detalle : Ruta("detalle")
    data object comprobante : Ruta("comprobante")
}
