package cl.fintech.pagoflex.data.remote

import android.os.Build
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val BASE_URL: String
        get() {
            val esEmulador =
                Build.FINGERPRINT.startsWith("generic") ||
                        Build.FINGERPRINT.contains("emulator") ||
                        Build.HARDWARE == "ranchu" ||
                        Build.HARDWARE == "goldfish"
            return if (esEmulador) {
                "http://10.0.2.2:8080/"
            } else {
                "https://tqc4cr9w-8080.brs.devtunnels.ms/"
            }
        }

    private val clienteHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { cadena ->
                val original = cadena.request()
                val ruta = original.url.encodedPath

                val esPublica =
                    ruta == "/api/v1/usuarios/login" ||
                    ruta == "/api/v1/usuarios/registro" ||
                    ruta == "/api/v1/usuarios/recuperar-contrasena"

                val autorizacion = if (esPublica) {
                    null
                } else {
                    CredencialesSesion.obtener()
                }

                val solicitud = if (autorizacion != null) {
                    original.newBuilder()
                        .header("Authorization", autorizacion)
                        .build()
                } else {
                    original
                }

                cadena.proceed(solicitud)
            }
            .build()
    }
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val usuariosApi: UsuariosApi by lazy {
        retrofit.create(UsuariosApi::class.java)
    }

    val compromisosApi: CompromisosApi by lazy {
        retrofit.create(CompromisosApi::class.java)
    }

    val empresasApi: EmpresasApi by lazy {
        retrofit.create(EmpresasApi::class.java)
    }

    val pagosApi: PagosApi by lazy {
        retrofit.create(PagosApi::class.java)
    }

    val reportesApi: ReportesApi by lazy {
        retrofit.create(ReportesApi::class.java)
    }
}
