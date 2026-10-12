
package cl.fintech.pagoflex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.fintech.pagoflex.ui.data.DatosEjemplo.compromisos
import cl.fintech.pagoflex.ui.data.DatosEjemplo.empresas
import cl.fintech.pagoflex.ui.data.DatosEjemplo.usuarios
import cl.fintech.pagoflex.ui.screens.CompromisosScreen
import cl.fintech.pagoflex.ui.theme.PagoflexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PagoflexTheme {
                CompromisosScreen(usuarios[0], compromisos, { codigo ->
                    empresas.find { it.codigo == codigo }?.nombreFantasia
                        ?: "Empresa desconocida"
                }, { Unit }, { Unit })
            }
        }
    }
}
