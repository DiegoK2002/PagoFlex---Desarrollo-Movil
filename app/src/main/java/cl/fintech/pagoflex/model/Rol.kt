package cl.fintech.pagoflex.model

enum class Rol(val id: Long, val etiqueta: String) {
    USUARIO_FINAL(1, "Usuario final"),
    AGENTE(2, "Agente PagoFlex");

    companion object {
        fun desdeId(id: Long): Rol = entries.firstOrNull { it.id == id } ?: USUARIO_FINAL
    }
}
