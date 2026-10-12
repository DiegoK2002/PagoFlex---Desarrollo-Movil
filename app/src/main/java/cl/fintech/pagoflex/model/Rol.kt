package cl.fintech.pagoflex.model

// =====================================================================
//  Rol.kt
//  ---------------------------------------------------------------------
//  Los tres roles ven pantallas distintas.
//  "etiqueta" es el texto visible.
// =====================================================================

enum class Rol(val etiqueta: String) {
    USUARIO_FINAL("Usuario final"),
    AGENTE("Agente PagoFlex"),
    EJECUTIVO("Ejecutivo de empresa")
}
