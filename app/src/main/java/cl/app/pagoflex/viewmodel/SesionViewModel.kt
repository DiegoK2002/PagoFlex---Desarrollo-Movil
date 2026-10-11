package cl.app.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import cl.app.pagoflex.data.RepositorioPagoFlex
import cl.app.pagoflex.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// =====================================================================
//  SesionViewModel.kt  —  VIEWMODEL COMPARTIDO (como UsuariosViewModel)
//  ---------------------------------------------------------------------
//  Guarda QUIÉN tiene la sesión iniciada (null = nadie). Se crea en
//  AppNavegacion, fuera del NavHost, así sobrevive al cambio de pantalla.
//  La sesión es SIMULADA: el caso no pide login real (RF-01).
//  Regla: el ViewModel NUNCA importa nada de Compose.
// =====================================================================

class SesionViewModel : ViewModel() {

    val sesionesSimuladas: List<Usuario> = RepositorioPagoFlex.sesionesSimuladas

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    fun iniciarSesion(usuario: Usuario) {
        _usuarioActual.value = usuario
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
    }
}
