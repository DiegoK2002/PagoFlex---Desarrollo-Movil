package cl.fintech.pagoflex.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.fintech.pagoflex.data.remote.ArchivosRepository
import cl.fintech.pagoflex.util.ConvertidorImagen
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FotoPerfilViewModel : ViewModel() {

    private val repositorio = ArchivosRepository()

    private val _fotoPerfil = MutableStateFlow<ByteArray?>(null)
    val fotoPerfil: StateFlow<ByteArray?> = _fotoPerfil.asStateFlow()

    private val _guardandoFoto = MutableStateFlow(false)
    val guardandoFoto: StateFlow<Boolean> = _guardandoFoto.asStateFlow()

    private var idUsuarioActual = 0L
    private var version = 0L
    private var trabajoCarga: Job? = null
    private var trabajoGuardado: Job? = null

    fun cargar(idUsuario: Long) {
        if (idUsuario <= 0L) {
            limpiar()
            return
        }

        trabajoCarga?.cancel()
        trabajoGuardado?.cancel()
        version++

        val versionActual = version
        idUsuarioActual = idUsuario
        _fotoPerfil.value = null
        _guardandoFoto.value = false

        trabajoCarga = viewModelScope.launch {
            try {
                val remoto = repositorio.obtenerAvatar(idUsuario)
                if (idUsuarioActual == idUsuario && version == versionActual) {
                    _fotoPerfil.value = remoto
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                if (idUsuarioActual == idUsuario && version == versionActual) {
                    _fotoPerfil.value = null
                }
            }
        }
    }

    fun guardarFoto(bitmap: Bitmap, onResultado: (Boolean) -> Unit) {
        val idUsuario = idUsuarioActual

        if (idUsuario <= 0L || _guardandoFoto.value) {
            onResultado(false)
            return
        }

        trabajoCarga?.cancel()
        version++
        val versionActual = version
        _guardandoFoto.value = true

        trabajoGuardado = viewModelScope.launch {
            var guardado = false

            try {
                val bytes = withContext(Dispatchers.Default) {
                    ConvertidorImagen.aBytes(bitmap)
                }

                if (bytes.isNotEmpty()) {
                    val registro = repositorio.subirAvatar(idUsuario, bytes)

                    if (
                        registro.idUsuario == idUsuario &&
                        registro.tipo == "AVATAR" &&
                        registro.activo &&
                        idUsuarioActual == idUsuario &&
                        version == versionActual
                    ) {
                        _fotoPerfil.value = bytes
                        guardado = true
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                guardado = false
            } finally {
                if (idUsuarioActual == idUsuario && version == versionActual) {
                    _guardandoFoto.value = false
                    onResultado(guardado)
                }
            }
        }
    }

    fun limpiar() {
        trabajoCarga?.cancel()
        trabajoGuardado?.cancel()
        trabajoCarga = null
        trabajoGuardado = null
        version++
        idUsuarioActual = 0L
        _fotoPerfil.value = null
        _guardandoFoto.value = false
    }
}
