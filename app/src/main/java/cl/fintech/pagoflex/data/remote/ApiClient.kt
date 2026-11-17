package cl.fintech.pagoflex.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

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
}
