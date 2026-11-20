package cl.fintech.pagoflex.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val EsquemaClaro = lightColorScheme(
    primary = Primario,
    onPrimary = Blanco,
    primaryContainer = PrimarioClaro,
    onPrimaryContainer = PrimarioOscuro,
    secondary = Secundario,
    onSecondary = Blanco,
    secondaryContainer = Color(0xFFD9F5EF),
    onSecondaryContainer = Color(0xFF004D45),
    tertiary = Acento,
    onTertiary = Blanco,
    tertiaryContainer = Color(0xFFF0E4FF),
    onTertiaryContainer = Color(0xFF3F176D),
    background = FondoClaro,
    onBackground = TextoOscuro,
    surface = SuperficieClaro,
    onSurface = TextoOscuro,
    surfaceVariant = Color(0xFFE9EEF8),
    onSurfaceVariant = Color(0xFF45516A),
    outline = Color(0xFF8391A8),
    outlineVariant = Color(0xFFD7DEEC),
    error = Color(0xFFB62F45),
    onError = Blanco,
    errorContainer = Color(0xFFFFDFE5),
    onErrorContainer = Color(0xFF651528),
    inverseSurface = Color(0xFF25324C),
    inverseOnSurface = Color(0xFFF5F7FF),
    inversePrimary = PrimarioSobreOscuro
)

private val EsquemaOscuro = darkColorScheme(
    primary = PrimarioSobreOscuro,
    onPrimary = PrimarioOscuro,
    primaryContainer = Color(0xFF293A9B),
    onPrimaryContainer = Color(0xFFE4E8FF),
    secondary = Color(0xFF7AE0D0),
    onSecondary = Color(0xFF003D36),
    secondaryContainer = Color(0xFF075D54),
    onSecondaryContainer = Color(0xFFC8FFF3),
    tertiary = Color(0xFFD8BDFF),
    onTertiary = Color(0xFF3C1769),
    tertiaryContainer = Color(0xFF59318B),
    onTertiaryContainer = Color(0xFFF4E8FF),
    background = FondoOscuro,
    onBackground = TextoClaro,
    surface = SuperficieOscuro,
    onSurface = TextoClaro,
    surfaceVariant = Color(0xFF27334E),
    onSurfaceVariant = Color(0xFFBEC9DD),
    outline = Color(0xFF92A0B9),
    outlineVariant = Color(0xFF42506A),
    error = Color(0xFFFFB3BE),
    onError = Color(0xFF650017),
    errorContainer = Color(0xFF86243A),
    onErrorContainer = Color(0xFFFFE0E6),
    inverseSurface = Color(0xFFEDF1FC),
    inverseOnSurface = Color(0xFF1A2740),
    inversePrimary = Primario
)

private val Formas = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

@Composable
fun PagoflexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro

    MaterialTheme(
        colorScheme = colorScheme,
        typography = tipografia,
        shapes = Formas,
        content = content
    )
}
