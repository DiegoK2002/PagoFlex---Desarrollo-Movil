package cl.app.pagoflex.data

import cl.app.pagoflex.model.Compromiso
import cl.app.pagoflex.model.Usuario

//  ---------------------------------------------------------------------
//  Las pantallas y ViewModels piden los datos aquí.
// =====================================================================

object RepositorioPagoFlex {

    // Las 3 sesiones simuladas que entrega el caso (sin login real)
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
}
