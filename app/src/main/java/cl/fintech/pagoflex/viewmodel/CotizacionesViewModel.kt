
package cl.fintech.pagoflex.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.fintech.pagoflex.data.remote.ApiClient
import cl.fintech.pagoflex.data.remote.CotizacionesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    private val api: CotizacionesApi by lazy {
        ApiClient.retrofit.create(CotizacionesApi::class.java)
    }

    private val _estado = MutableStateFlow(EstadoCotizaciones())
    val estado = _estado.asStateFlow()

    fun consultar() {
        if (_estado.value.cargando) return

        viewModelScope.launch {
            _estado.value = EstadoCotizaciones(cargando = true)

            try {
                val direcciones = listOf(
                    "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/clp.min.json",
                    "https://latest.currency-api.pages.dev/v1/currencies/clp.min.json"
                )

                var cotizaciones: EstadoCotizaciones? = null
                var ultimoError: Exception? = null

                for (direccion in direcciones) {
                    try {
                        val datos = api.obtenerCotizaciones(direccion)
                        val tasas = datos.getAsJsonObject("clp")

                        val eurosPorPeso = tasas.get("eur").asDouble
                        val dolaresPorPeso = tasas.get("usd").asDouble

                        if (
                            !eurosPorPeso.isFinite() ||
                            !dolaresPorPeso.isFinite() ||
                            eurosPorPeso <= 0.0 ||
                            dolaresPorPeso <= 0.0
                        ) {
                            throw IOException("Cotizaciones inválidas")
                        }

                        val fecha = datos.get("date").asString

                        cotizaciones = EstadoCotizaciones(
                            valorEuro = 1.0 / eurosPorPeso,
                            fechaEuro = fecha,
                            valorDolar = 1.0 / dolaresPorPeso,
                            fechaDolar = fecha
                        )

                        break
                    } catch (e: Exception) {
                        ultimoError = e
                        Log.w(
                            "PagoFlexCotizaciones",
                            "Falló una fuente de cotizaciones",
                            e
                        )
                    }
                }

                _estado.value = cotizaciones
                    ?: throw IOException(
                        "No se pudo consultar ninguna fuente",
                        ultimoError
                    )

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
