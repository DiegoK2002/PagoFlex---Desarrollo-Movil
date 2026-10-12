package cl.fintech.pagoflex.model

//  ---------------------------------------------------------------------
//  Todo lo que la pantalla necesita para dibujarse: lo escrito en cada
//  campo, las opciones marcadas y los mensajes de error.
//  Es una data class INMUTABLE (val): para cambiarla se crea una copia
//  con .copy(...). Así Compose detecta el cambio y vuelve a dibujar.
//
//  CAMBIO (Bloque 2): campos "ciudad" (la elegida) y "ciudades" (opciones).
// =====================================================================

data class SesionUiState(
    // Valores de los campos
    val correo: String = "",
    val contrasena: String = "",
    val rol: Rol = Rol.USUARIO_FINAL,

    // Estado visual
    val contrasenaVisible: Boolean = false,

    // Errores de validación (null = sin error)
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,

    // Resultado
    val inicioExitoso: Boolean = false
)
