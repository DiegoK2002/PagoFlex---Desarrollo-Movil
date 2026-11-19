package cl.fintech.pagoflex.data.remote

import android.os.Build
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

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
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
