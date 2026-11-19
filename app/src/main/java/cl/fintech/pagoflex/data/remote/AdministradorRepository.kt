package cl.fintech.pagoflex.data.remote

class AdministradorRepository(
    private val usuariosApi: UsuariosApi = ApiClient.usuariosApi,
    private val empresasApi: EmpresasApi = ApiClient.empresasApi,
    private val compromisosApi: CompromisosApi = ApiClient.compromisosApi
) {

    suspend fun anularCompromiso(idCompromiso: Long) {
        require(idCompromiso > 0L) {
            "El ID del compromiso debe ser válido."
        }

        compromisosApi.anularCompromiso(idCompromiso)
    }

    suspend fun listarCompromisos(): List<CompromisoRemoto> {
        return compromisosApi.listarTodos()
    }

    suspend fun listarCompromisosPorUsuario(
        idUsuario: Long
    ): List<CompromisoRemoto> {
        require(idUsuario > 0L)

        return compromisosApi.listarPorUsuario(idUsuario)
    }


    suspend fun listarUsuarios(): List<UsuarioResponse> {
        return usuariosApi.listarUsuarios()
    }

    suspend fun listarEmpresas(): List<EmpresaRemota> {
        return empresasApi.listarTodas()
    }

    suspend fun asignarCompromiso(
        datos: CrearCompromisoRequest
    ): CompromisoRemoto {
        return compromisosApi.crearCompromiso(datos)
    }


    suspend fun actualizarCompromiso(
        idCompromiso: Long,
        datos: CrearCompromisoRequest
    ): CompromisoRemoto {
        return compromisosApi.actualizarCompromiso(
            idCompromiso,
            datos
        )
    }


    suspend fun actualizarUsuario(
        idUsuario: Long,
        datos: ActualizarUsuarioRequest
    ): UsuarioResponse {
        require(idUsuario > 0L) {
            "El ID del usuario debe ser válido."
        }

        return usuariosApi.actualizarUsuario(
            idUsuario,
            datos
        )
    }


    suspend fun crearEmpresa(
        nombre: String,
        categoria: String
    ): EmpresaRemota {
        require(nombre.isNotBlank()) {
            "El nombre de la empresa es obligatorio."
        }

        require(categoria.isNotBlank()) {
            "La categoría es obligatoria."
        }

        return empresasApi.crearEmpresa(
            CrearEmpresaRequest(
                nombre = nombre.trim(),
                categoria = categoria.trim()
            )
        )
    }


    suspend fun actualizarEmpresa(
        idEmpresa: Long,
        nombre: String,
        categoria: String
    ): EmpresaRemota {
        require(idEmpresa > 0L) {
            "El ID de la empresa debe ser válido."
        }

        require(nombre.isNotBlank()) {
            "El nombre de la empresa es obligatorio."
        }

        require(categoria.isNotBlank()) {
            "La categoría es obligatoria."
        }

        return empresasApi.actualizarEmpresa(
            idEmpresa,
            CrearEmpresaRequest(
                nombre = nombre.trim(),
                categoria = categoria.trim()
            )
        )
    }


    suspend fun anularEmpresa(idEmpresa: Long) {
        require(idEmpresa > 0L) {
            "El ID de la empresa debe ser válido."
        }

        empresasApi.anularEmpresa(idEmpresa)
    }


    suspend fun desactivarUsuario(idUsuario: Long) {
        require(idUsuario > 0L) {
            "El ID del usuario debe ser válido."
        }

        usuariosApi.desactivarUsuario(idUsuario)
    }


    suspend fun registrarUsuario(
        datos: RegistroUsuarioRequest
    ): UsuarioResponse {
        return usuariosApi.registrarUsuario(datos)
    }



}
