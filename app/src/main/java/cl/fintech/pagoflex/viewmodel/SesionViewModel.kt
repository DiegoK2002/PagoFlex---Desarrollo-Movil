package cl.fintech.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import cl.fintech.pagoflex.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Guarda quien tiene la sesion iniciada. Se crea una sola vez en la navegacion y lo comparten
 * todas las pantallas. La sesion vive solo en memoria: al cerrar la app hay que iniciar sesion de nuevo.
 * No importa nada de Compose, para que la logica no dependa de la interfaz.
 */
class SesionViewModel : ViewModel() {

    // Valor editable, privado: solo este ViewModel lo modifica. Null significa "sin sesion"
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)

    // Version de solo lectura que observan las pantallas
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    /** Registra a la persona que inicio sesion. */
    fun iniciarSesion(usuario: Usuario) {
        _usuarioActual.value = usuario
    }

    /** Deja la app sin sesion iniciada. */
    fun cerrarSesion() {
        _usuarioActual.value = null
    }
}
