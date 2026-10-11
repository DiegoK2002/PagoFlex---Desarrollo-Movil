package cl.app.pagoflex.model

// =====================================================================
//  Rol.kt  —  LOS 3 PERFILES DE LA APP  (Anexo 2 del caso)
//  ---------------------------------------------------------------------
//  Los tres roles usan la MISMA app; cada uno ve pantallas distintas.
//  "etiqueta" es el texto visible (igual que NivelExperiencia en MiRegistro).
// =====================================================================

enum class Rol(val etiqueta: String) {
    USUARIO_FINAL("Usuario final"),
    AGENTE("Agente PagoFlex"),
    EJECUTIVO("Ejecutivo de empresa")
}
