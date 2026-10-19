package cl.fintech.pagoflex.model

/**
 * Roles que puede tener una cuenta. Solo hay dos, los que se comunican entre si
 * a traves de los reportes: el usuario final reporta un problema y el agente lo responde.
 *
 * El [id] coincide con la fila correspondiente de la tabla "roles" de la base de datos.
 */
enum class Rol(val id: Long, val etiqueta: String) {
    USUARIO_FINAL(1, "Usuario final"),
    AGENTE(2, "Agente PagoFlex");

    companion object {
        /** Devuelve el rol que corresponde a un id guardado en la base de datos. */
        fun desdeId(id: Long): Rol = entries.firstOrNull { it.id == id } ?: USUARIO_FINAL
    }
}
