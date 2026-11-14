package cl.fintech.pagoflex

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import cl.fintech.pagoflex.navigation.AppNavegacion
import cl.fintech.pagoflex.ui.theme.PagoflexTheme

class MainActivity : ComponentActivity() {

    private val solicitarPermisoRedLocal =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { concedido ->

            if (!concedido) {
                Toast.makeText(
                    this,
                    "Permite el acceso a dispositivos cercanos para conectar PagoFlex",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= 37) {

            if (checkSelfPermission(
                    Manifest.permission.ACCESS_LOCAL_NETWORK
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                solicitarPermisoRedLocal.launch(
                    Manifest.permission.ACCESS_LOCAL_NETWORK
                )
            }
        }

        enableEdgeToEdge()

        setContent {
            PagoflexTheme {
                AppNavegacion()
            }
        }
    }
}
