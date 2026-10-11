package cl.app.pagoflex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.app.pagoflex.navigation.AppNavegacion
import cl.app.pagoflex.ui.theme.PagoFlexTheme

// =====================================================================
//  MainActivity.kt  —  PUNTO DE ENTRADA DE LA APP
//  ---------------------------------------------------------------------
//      setContent {
//          PagoFlexTheme {        <- colores / tipografía / formas
//              AppNavegacion()    <- el NavHost decide qué pantalla mostrar
//          }
//      }
// =====================================================================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PagoFlexTheme {
                AppNavegacion()
            }
        }
    }
}
