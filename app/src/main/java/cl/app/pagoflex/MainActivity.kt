package cl.app.pagoflex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cl.app.pagoflex.ui.styles.EstilosBoton
import cl.app.pagoflex.ui.styles.EstilosCampo
import cl.app.pagoflex.ui.styles.EstilosTarjeta
import cl.app.pagoflex.ui.styles.estiloAltoBoton
import cl.app.pagoflex.ui.theme.ColoresEstado
import cl.app.pagoflex.ui.theme.Dimens
import cl.app.pagoflex.ui.theme.PagoFlexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PagoFlexTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VistaPreviaTema(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Pantalla temporal para comprobar el tema y los estilos. Se reemplaza cuando haya navegación.
@Composable
fun VistaPreviaTema(modifier: Modifier = Modifier) {
    var texto by remember { mutableStateOf("") }

    Column(
        modifier = modifier.padding(Dimens.espacioPantalla),
        verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
    ) {
        Text("Tus compromisos", style = MaterialTheme.typography.titleLarge)

        Card(
            shape = EstilosTarjeta.forma,
            colors = EstilosTarjeta.colores(),
            elevation = EstilosTarjeta.elevacion(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(Dimens.espacioMedio),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioMini)
            ) {
                Text("Plaza Oriente", style = MaterialTheme.typography.titleMedium)
                Text("Arriendo departamento octubre", style = MaterialTheme.typography.bodyMedium)
                Text("\$ 520.000", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Pendiente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColoresEstado.Pendiente
                )
            }
        }

        Button(
            onClick = {},
            shape = EstilosBoton.forma,
            colors = EstilosBoton.colorPrincipal(),
            elevation = EstilosBoton.elevacion(),
            contentPadding = EstilosBoton.relleno,
            modifier = Modifier
                .fillMaxWidth()
                .estiloAltoBoton()
        ) {
            Text("Pagar", style = MaterialTheme.typography.labelLarge)
        }

        OutlinedButton(
            onClick = {},
            shape = EstilosBoton.forma,
            colors = EstilosBoton.coloresSecundarios(),
            border = EstilosBoton.bordesSecundarios(),
            contentPadding = EstilosBoton.relleno,
            modifier = Modifier
                .fillMaxWidth()
                .estiloAltoBoton()
        ) {
            Text("Ver detalle", style = MaterialTheme.typography.labelLarge)
        }

        OutlinedTextField(
            value = texto,
            onValueChange = { texto = it },
            label = { Text("Buscar empresa") },
            shape = EstilosCampo.forma,
            colors = EstilosCampo.colores(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VistaPreviaTemaPreview() {
    PagoFlexTheme {
        VistaPreviaTema()
    }
}
