package cl.fintech.pagoflex.data

import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.Usuario
import cl.fintech.pagoflex.ui.data.DatosEjemplo

// =====================================================================
//  RepositorioPagoFlex.kt  —  DE DÓNDE SALEN LOS DATOS
//  ---------------------------------------------------------------------
//  Las pantallas y ViewModels piden los datos aquí y NO saben de dónde
//  vienen. Si más adelante se
//  usa Room, solo cambia este archivo.
// =====================================================================

object RepositorioPagoFlex {

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
