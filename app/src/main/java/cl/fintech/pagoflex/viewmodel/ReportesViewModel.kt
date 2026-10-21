package cl.fintech.pagoflex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.fintech.pagoflex.data.PagoFlexDbHelper
import cl.fintech.pagoflex.model.Reporte
import cl.fintech.pagoflex.model.Rol
import cl.fintech.pagoflex.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReportesViewModel(application: Application) : AndroidViewModel(application) {

    private val bd = PagoFlexDbHelper.obtener(application)

    private val _reportes = MutableStateFlow<List<Reporte>>(emptyList())
    val reportes: StateFlow<List<Reporte>> = _reportes.asStateFlow()

    private var usuarioActual: Usuario? = null

    fun cargar(usuario: Usuario) {
        usuarioActual = usuario
        actualizar()
    }

    fun actualizar() {
        val usuario = usuarioActual

        _reportes.value = when (usuario?.rol) {
            Rol.AGENTE -> bd.reportesParaAgente()
            Rol.USUARIO_FINAL -> bd.reportesDeUsuario(usuario.id)
            null -> emptyList()
        }
    }

    fun tomarReporte(idReporte: Long): Boolean {
        val usuario = usuarioActual ?: return false

        if (usuario.rol != Rol.AGENTE) {
            return false
        }

        val resultado = bd.tomarReporte(idReporte, usuario.id)

        actualizar()

        return resultado
    }


    fun resolverReporte(idReporte: Long, respuesta: String): Boolean {
        val usuario = usuarioActual ?: return false

        if (usuario.rol != Rol.AGENTE || respuesta.isBlank()) {
            return false
        }

        val resultado = bd.resolverReporte(
            idReporte,
            usuario.id,
            respuesta
        )

        if (resultado) {
            actualizar()
        }

        return resultado
    }


    fun crearReporte(
        folio: String,
        motivo: String,
        comentario: String
    ): Boolean {
        val usuario = usuarioActual ?: return false

        if (usuario.rol != Rol.USUARIO_FINAL) {
            return false
        }

        val idReporte = bd.registrarReporte(
            folio = folio,
            idUsuario = usuario.id,
            motivo = motivo,
            comentario = comentario.trim().takeIf { it.isNotBlank() }
        )

        if (idReporte <= 0L) {
            return false
        }

        actualizar()
        return true
    }


    fun limpiar() {
        usuarioActual = null
        _reportes.value = emptyList()
    }
}
