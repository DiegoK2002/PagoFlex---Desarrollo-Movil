package cl.app.pagoflex.data

import cl.app.pagoflex.model.Compromiso
import cl.app.pagoflex.model.Usuario

// =====================================================================
//  RepositorioPagoFlex.kt  —  DE DÓNDE SALEN LOS DATOS
//  ---------------------------------------------------------------------
//  Las pantallas y ViewModels piden los datos aquí y NO saben de dónde
//  vienen. Hoy vienen de DatosEjemplo (en memoria). Si más adelante se
//  usa Room o una API, solo cambia este archivo.
// =====================================================================

object RepositorioPagoFlex {

    // Las sesiones simuladas (sin login real)
    val sesionesSimuladas: List<Usuario> =
        listOf("UF-01", "UF-02", "AG-01", "EJ-01").map { codigo ->
            DatosEjemplo.usuarios.first { it.codigo == codigo }
        }

    fun compromisosDe(codigoUsuario: String): List<Compromiso> =
        DatosEjemplo.compromisos
            .filter { it.codigoUsuario == codigoUsuario }
            .sortedBy { it.fechaVencimiento }

    fun nombreEmpresa(codigo: String): String =
        DatosEjemplo.empresas.firstOrNull { it.codigo == codigo }?.nombreFantasia ?: codigo

    // Recargo por atraso que cobra cada empresa (RN-03)
    fun recargoDe(codigoEmpresa: String): Int =
        DatosEjemplo.empresas.firstOrNull { it.codigo == codigoEmpresa }?.recargoAtraso ?: 0
}
