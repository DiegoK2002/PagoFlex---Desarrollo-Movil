package cl.fintech.pagoflex.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ArchivosRepository(
    private val api: ArchivosApi = ApiClient.retrofit.create(ArchivosApi::class.java)
) {

    suspend fun obtenerAvatar(idUsuario: Long): ByteArray? {
        require(idUsuario > 0L)

        val ultimoAvatar = api.listarPorUsuario(idUsuario)
            .filter { it.activo && it.tipo == "AVATAR" && it.idUsuario == idUsuario }
            .maxByOrNull { it.idArchivo }
            ?: return null

        return withContext(Dispatchers.IO) {
            api.descargar(ultimoAvatar.idArchivo).use { it.bytes() }
        }
    }

    suspend fun subirAvatar(idUsuario: Long, bytes: ByteArray): ArchivoRemoto {
        require(idUsuario > 0L)
        require(bytes.isNotEmpty())
        require(bytes.size <= 10 * 1024 * 1024)

        val textoPlano = "text/plain".toMediaType()
        val cuerpoImagen = bytes.toRequestBody("image/jpeg".toMediaType())
        val archivo = MultipartBody.Part.createFormData(
            "archivo",
            "perfil-$idUsuario.jpg",
            cuerpoImagen
        )

        return api.subir(
            idUsuario = idUsuario.toString().toRequestBody(textoPlano),
            tipo = "AVATAR".toRequestBody(textoPlano),
            archivo = archivo
        )
    }
}
