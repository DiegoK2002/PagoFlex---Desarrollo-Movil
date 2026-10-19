package cl.fintech.pagoflex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.fintech.pagoflex.navigation.AppNavegacion
import cl.fintech.pagoflex.ui.theme.PagoflexTheme

/** Actividad unica de la app: aplica el tema y muestra la navegacion. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Permite dibujar bajo las barras del sistema
        enableEdgeToEdge()
        setContent {
            PagoflexTheme {
                AppNavegacion()
            }
        }
    }
}
