
package cl.fintech.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class EstadoUf(
    val cargando: Boolean = false,
    val valor: Double? = null,
    val fecha: String? = null,
    val valorEuro: Double? = null,
    val fechaEuro: String? = null,
    val valorDolar: Double? = null,
    val fechaDolar: String? = null,
    val error: String? = null
)


class UfViewModel : ViewModel() {

    private val _estado = MutableStateFlow(EstadoUf())
    val estado = _estado.asStateFlow()

    fun consultar() {
        if (_estado.value.cargando) return

        viewModelScope.launch {
            _estado.value = EstadoUf(cargando = true)

            try {
                val resultado = withContext(Dispatchers.IO) {
                    val conexion = URL(
                        "https://mindicador.cl/api"
                    ).openConnection() as HttpURLConnection

                    try {
                        conexion.requestMethod = "GET"
                        conexion.connectTimeout = 10_000
                        conexion.readTimeout = 10_000

                        if (conexion.responseCode != 200) {
                            error("Error HTTP ${conexion.responseCode}")
                        }

                        val respuesta = conexion.inputStream
                            .bufferedReader()
                            .use { it.readText() }

                        val indicadores = JSONObject(respuesta)

                        val uf = indicadores.getJSONObject("uf")
                        val euro = indicadores.getJSONObject("euro")
                        val dolar = indicadores.getJSONObject("dolar")

                        EstadoUf(
                            valor = uf.getDouble("valor"),
                            fecha = uf.getString("fecha").take(10),
                            valorEuro = euro.getDouble("valor"),
                            fechaEuro = euro.getString("fecha").take(10),
                            valorDolar = dolar.getDouble("valor"),
                            fechaDolar = dolar.getString("fecha").take(10)
                        )

                    } finally {
                        conexion.disconnect()
                    }
                }

                _estado.value = resultado
            } catch (e: Exception) {
                _estado.value = EstadoUf(
                    error = "No se pudo consultar la UF. Comprueba tu conexión."
                )
            }
        }
    }
}
