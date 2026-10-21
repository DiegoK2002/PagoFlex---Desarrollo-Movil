
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
import android.util.Log
import java.io.IOException

data class EstadoCotizaciones(
    val cargando: Boolean = false,
    val valorEuro: Double? = null,
    val fechaEuro: String? = null,
    val valorDolar: Double? = null,
    val fechaDolar: String? = null,
    val error: String? = null
)


class CotizacionesViewModel : ViewModel() {

    private val _estado = MutableStateFlow(EstadoCotizaciones())
    val estado = _estado.asStateFlow()


    fun consultar() {
        if (_estado.value.cargando) return

        viewModelScope.launch {
            _estado.value = EstadoCotizaciones(cargando = true)

            try {
                val resultado = withContext(Dispatchers.IO) {
                    val direcciones = listOf(
                        "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/clp.min.json",
                        "https://latest.currency-api.pages.dev/v1/currencies/clp.min.json"
                    )

                    var cotizaciones: EstadoCotizaciones? = null
                    var ultimoError: Exception? = null

                    for (direccion in direcciones) {
                        try {
                            val conexion =
                                URL(direccion).openConnection() as HttpURLConnection

                            try {
                                conexion.requestMethod = "GET"
                                conexion.connectTimeout = 8000
                                conexion.readTimeout = 8000
                                conexion.setRequestProperty(
                                    "Accept",
                                    "application/json"
                                )

                                val codigoHttp = conexion.responseCode

                                if (codigoHttp != HttpURLConnection.HTTP_OK) {
                                    throw IOException("HTTP $codigoHttp")
                                }

                                val respuesta = conexion.inputStream
                                    .bufferedReader()
                                    .use { it.readText() }

                                val datos = JSONObject(respuesta)
                                val tasas = datos.getJSONObject("clp")

                                val eurosPorPeso = tasas.getDouble("eur")
                                val dolaresPorPeso = tasas.getDouble("usd")

                                if (
                                    !eurosPorPeso.isFinite() ||
                                    !dolaresPorPeso.isFinite() ||
                                    eurosPorPeso <= 0.0 ||
                                    dolaresPorPeso <= 0.0
                                ) {
                                    throw IOException("Cotizaciones inválidas")
                                }

                                val fecha = datos.getString("date")

                                cotizaciones = EstadoCotizaciones(
                                    valorEuro = 1.0 / eurosPorPeso,
                                    fechaEuro = fecha,
                                    valorDolar = 1.0 / dolaresPorPeso,
                                    fechaDolar = fecha
                                )

                                break
                            } finally {
                                conexion.disconnect()
                            }
                        } catch (e: Exception) {
                            ultimoError = e
                            Log.w(
                                "PagoFlexCotizaciones",
                                "Falló una fuente de cotizaciones",
                                e
                            )
                        }
                    }

                    cotizaciones
                        ?: throw IOException(
                            "No se pudo consultar ninguna fuente",
                            ultimoError
                        )
                }

                _estado.value = resultado
            } catch (e: Exception) {
                Log.e(
                    "PagoFlexCotizaciones",
                    "Error al consultar EUR y USD",
                    e
                )

                _estado.value = EstadoCotizaciones(
                    error = "No se pudieron obtener las cotizaciones de EUR y USD."
                )
            }
        }
    }

}
