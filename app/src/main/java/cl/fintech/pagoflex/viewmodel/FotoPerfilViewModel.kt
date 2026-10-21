
package cl.fintech.pagoflex.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import cl.fintech.pagoflex.data.PagoFlexDbHelper
import cl.fintech.pagoflex.util.ConvertidorImagen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FotoPerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val bd = PagoFlexDbHelper.obtener(application)

    private val _fotoPerfil = MutableStateFlow<ByteArray?>(null)
    val fotoPerfil: StateFlow<ByteArray?> = _fotoPerfil.asStateFlow()

    private var idUsuarioActual: Long = 0L

    fun cargar(idUsuario: Long) {
        if (idUsuario <= 0L) {
            limpiar()
            return
        }

        idUsuarioActual = idUsuario
        _fotoPerfil.value = bd.fotoPerfilDeUsuario(idUsuario)
    }

    fun guardarFoto(bitmap: Bitmap): Boolean {
        if (idUsuarioActual <= 0L) {
            return false
        }

        val bytes = ConvertidorImagen.aBytes(bitmap)

        if (bytes.isEmpty()) {
            return false
        }

        val guardado = bd.guardarFotoPerfil(
            idUsuarioActual,
            bytes
        )

        if (guardado) {
            _fotoPerfil.value = bytes
        }

        return guardado
    }

    fun limpiar() {
        idUsuarioActual = 0L
        _fotoPerfil.value = null
    }
}
