package cl.fintech.pagoflex.util

/** Una condicion que debe cumplir la contrasena y si se cumple en este momento. */
data class Requisito(val texto: String, val cumplido: Boolean)

/**
 * Verificaciones de los datos de los formularios. Cada funcion "validar" devuelve el mensaje de
 * error para mostrar bajo el campo, o null si el valor es correcto.
 */
object Validaciones {

    // Formato general de un correo: usuario@dominio.extension
    private val REGEX_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")

    // Detecta tres caracteres iguales seguidos (por ejemplo "aaa" o "111")
    private val REGEX_TRES_IGUALES = Regex("(.)\\1\\1")

    // Contrasenas muy usadas que no se aceptan
    private val CONTRASENAS_COMUNES = setOf(
        "12345678", "123456789", "1234567890", "password", "password1", "qwertyui",
        "abcd1234", "admin123", "contrasena", "contrasena1", "iloveyou", "letmein1"
    )

    // ------------------------------------------------------------------
    // Nombres y apellidos
    // ------------------------------------------------------------------

    /** Verifica un nombre o apellido. [campo] es el nombre del campo para el mensaje ("nombres" o "apellidos"). */
    fun validarNombre(valor: String, campo: String): String? {
        val texto = valor.trim()
        return when {
            texto.isEmpty() -> "Ingresa tus $campo"
            texto.length < 2 -> "Debe tener al menos 2 letras"
            texto.length > 40 -> "No puede superar los 40 caracteres"
            !texto.all { it.isLetter() || it == ' ' } -> "Solo se permiten letras y espacios"
            else -> null
        }
    }

    /** Deja cada palabra con la primera letra en mayuscula: "  maria   jose " -> "Maria Jose". */
    fun normalizarNombre(valor: String): String =
        valor.trim()
            .split(Regex("\\s+"))
            .joinToString(" ") { palabra ->
                palabra.lowercase().replaceFirstChar { it.uppercase() }
            }

    // ------------------------------------------------------------------
    // RUT chileno
    // ------------------------------------------------------------------

    /** Calcula el digito verificador de un RUT con el metodo del modulo 11. */
    fun calcularDigitoVerificador(cuerpo: String): Char {
        var suma = 0
        var factor = 2
        // Se recorren los digitos de derecha a izquierda multiplicando por 2, 3, 4, 5, 6, 7, 2, 3...
        for (c in cuerpo.reversed()) {
            suma += (c - '0') * factor
            factor = if (factor == 7) 2 else factor + 1
        }
        return when (val resto = 11 - (suma % 11)) {
            11 -> '0'
            10 -> 'K'
            else -> '0' + resto
        }
    }

    /** Verifica el RUT escrito en dos campos: [numero] (solo digitos) y [dv] (digito verificador). */
    fun validarRut(numero: String, dv: String): String? = when {
        numero.isEmpty() -> "Ingresa tu RUT"
        numero.length < 7 -> "El RUT debe tener 7 u 8 dígitos"
        dv.isEmpty() -> "Ingresa el dígito verificador"
        calcularDigitoVerificador(numero) != dv.first().uppercaseChar() ->
            "El dígito verificador no coincide con el RUT"
        else -> null
    }

    /** Une el numero y el digito verificador con el formato "12.345.678-5". */
    fun formatearRut(numero: String, dv: String): String {
        val conPuntos = numero.reversed().chunked(3).joinToString(".").reversed()
        return "$conPuntos-${dv.uppercase()}"
    }

    // ------------------------------------------------------------------
    // Correo y comuna
    // ------------------------------------------------------------------

    fun validarCorreo(valor: String): String? {
        val texto = valor.trim()
        return when {
            texto.isEmpty() -> "Ingresa tu correo"
            texto.length > 60 -> "El correo no puede superar los 60 caracteres"
            texto.contains(" ") -> "El correo no puede tener espacios"
            !REGEX_CORREO.matches(texto) -> "Ingresa un correo válido. Ejemplo: nombre@correo.cl"
            else -> null
        }
    }

    fun validarComuna(valor: String): String? =
        if (valor.isBlank()) "Selecciona tu comuna" else null

    // ------------------------------------------------------------------
    // Contrasena (11 verificaciones distintas)
    // ------------------------------------------------------------------

    /** Indica si la contrasena contiene la parte inicial del correo o alguno de los nombres. */
    private fun contieneDatosPersonales(contrasena: String, correo: String, nombres: String): Boolean {
        val clave = contrasena.lowercase()
        val parteCorreo = correo.substringBefore('@').lowercase()
        // Se ignoran correos muy cortos para no rechazar contrasenas por coincidencias casuales
        val contieneCorreo = parteCorreo.length >= 3 && clave.contains(parteCorreo)
        // Solo se consideran nombres de 4 o mas letras
        val contieneNombre = nombres.lowercase().split(" ").any { it.length >= 4 && clave.contains(it) }
        return contieneCorreo || contieneNombre
    }

    /** Indica si es una contrasena de uso masivo, ignorando los simbolos del final ("Password1!" tambien lo es). */
    private fun esContrasenaComun(contrasena: String): Boolean {
        val base = contrasena.lowercase().trimEnd { !it.isLetterOrDigit() }
        return base in CONTRASENAS_COMUNES
    }

    /** Indica si tiene un simbolo: ni letra, ni digito, ni espacio. */
    private fun tieneCaracterEspecial(contrasena: String): Boolean =
        contrasena.any { !it.isLetterOrDigit() && !it.isWhitespace() }

    /**
     * Verifica la contrasena aplicando, en este orden:
     * 1) no vacia  2) minimo 8 caracteres  3) maximo 30  4) sin espacios  5) una mayuscula
     * 6) una minuscula  7) un numero  8) un caracter especial  9) sin datos personales
     * 10) que no sea una contrasena comun  11) sin tres caracteres iguales seguidos.
     * Devuelve el mensaje de la primera que falle, o null si cumple todas.
     */
    fun validarContrasena(contrasena: String, correo: String = "", nombres: String = ""): String? = when {
        contrasena.isEmpty() -> "Ingresa una contrase\u00f1a"
        contrasena.length < 8 -> "Debe tener al menos 8 caracteres"
        contrasena.length > 30 -> "No puede superar los 30 caracteres"
        contrasena.contains(" ") -> "No puede tener espacios"
        !contrasena.any { it.isUpperCase() } -> "Debe incluir una letra mayúscula"
        !contrasena.any { it.isLowerCase() } -> "Debe incluir una letra minúscula"
        !contrasena.any { it.isDigit() } -> "Debe incluir al menos un número"
        !tieneCaracterEspecial(contrasena) -> "Debe incluir un carácter especial. Ejemplo: ! @ # $ %"
        contieneDatosPersonales(contrasena, correo, nombres) -> "No puede contener tu correo ni tus nombres"
        esContrasenaComun(contrasena) -> "Esa contrase\u00f1a es muy común. Elige otra"
        REGEX_TRES_IGUALES.containsMatchIn(contrasena) -> "No puede tener tres caracteres iguales seguidos"
        else -> null
    }

    /** Lista de condiciones con su estado actual. Se muestra en el registro mientras se escribe la contrasena. */
    fun requisitosContrasena(contrasena: String, correo: String, nombres: String): List<Requisito> {
        val hayTexto = contrasena.isNotEmpty()
        return listOf(
            Requisito("Entre 8 y 30 caracteres", contrasena.length in 8..30),
            Requisito("Sin espacios", hayTexto && !contrasena.contains(" ")),
            Requisito("Al menos una mayúscula", contrasena.any { it.isUpperCase() }),
            Requisito("Al menos una minúscula", contrasena.any { it.isLowerCase() }),
            Requisito("Al menos un número", contrasena.any { it.isDigit() }),
            Requisito("Al menos un carácter especial (! @ # $ %)", tieneCaracterEspecial(contrasena)),
            Requisito(
                "No contiene tu correo ni tus nombres",
                hayTexto && !contieneDatosPersonales(contrasena, correo, nombres)
            ),
            Requisito("No es una contrase\u00f1a común", hayTexto && !esContrasenaComun(contrasena)),
            Requisito("Sin tres caracteres iguales seguidos", hayTexto && !REGEX_TRES_IGUALES.containsMatchIn(contrasena))
        )
    }

    /** Verifica que la contrasena repetida sea igual a la original. */
    fun validarRepeticion(contrasena: String, repetida: String): String? = when {
        repetida.isEmpty() -> "Repite tu contrase\u00f1a"
        repetida != contrasena -> "Las contrase\u00f1as no coinciden"
        else -> null
    }
}
