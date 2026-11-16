package cl.fintech.pagoflex.data.remote

class CompromisosRepository(
    private val api: CompromisosApi = ApiClient.compromisosApi
) {

    suspend fun listarPorUsuario(
        idUsuario: Long
    ): List<CompromisoRemoto> {

        require(idUsuario > 0L)

        return api.listarPorUsuario(idUsuario)
            .filter { compromiso ->
                compromiso.idUsuario == idUsuario &&
                        compromiso.activo
            }
            .sortedBy { compromiso ->
                compromiso.fechaVencimiento
            }
    }
}
