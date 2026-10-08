package cl.app.pagoflex.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta sobria que transmite confianza (Aclaración 4: diseño propio, sin copiar la marca de PagoFlex)
val Primario = Color(0xFF0B4F6C)
val PrimarioClaro = Color(0xFFBFE3F2)
val PrimarioOscuro = Color(0xFF073447)
val PrimarioSobreOscuro = Color(0xFF7CC4E0)
val Secundario = Color(0xFF2E7D6B)
val Acento = Color(0xFF9A6700)
val Blanco = Color(0xFFFFFFFF)

// Modo claro
val FondoClaro = Color(0xFFF6F8FA)
val SuperficieClaro = Color(0xFFFFFFFF)
val TextoOscuro = Color(0xFF1B2733)

// Modo oscuro
val FondoOscuro = Color(0xFF0F1A22)
val SuperficieOscuro = Color(0xFF16252F)
val TextoClaro = Color(0xFFE6EDF2)

val GrisDeshabilitado = Color(0xFFB0B8BF)

// Estados de un compromiso (RN-01). El vencido usa un tono cálido y no un rojo fuerte
// para no asustar ni culpar a la persona (RNF-04).
object ColoresEstado {
    val Pagado = Color(0xFF1B7F4B)
    val Pendiente = Color(0xFF9A6700)
    val Vencido = Color(0xFFC2410C)
    val EnRevision = Color(0xFF3949AB)
    val Anulado = Color(0xFF5F6368)
}
