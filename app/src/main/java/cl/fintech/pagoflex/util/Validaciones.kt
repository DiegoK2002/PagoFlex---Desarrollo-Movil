package cl.fintech.pagoflex.util

data class Requisito(val texto: String, val cumplido: Boolean)

object Validaciones {

    private val REGEX_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")

    private val REGEX_TRES_IGUALES = Regex("(.)\\1\\1")

    private val CONTRASENAS_COMUNES = setOf(
        "12345678", "123456789", "1234567890", "password", "password1", "qwertyui",
        "abcd1234", "admin123", "contrasena", "contrasena1", "iloveyou", "letmein1"
    )

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

    fun normalizarNombre(valor: String): String =
        valor.trim()
            .split(Regex("\\s+"))
            .joinToString(" ") { palabra ->
                palabra.lowercase().replaceFirstChar { it.uppercase() }
            }

    fun calcularDigitoVerificador(cuerpo: String): Char {
        var suma = 0
        var factor = 2

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

    fun validarRut(numero: String, dv: String): String? = when {
        numero.isEmpty() -> "Ingresa tu RUT"
        numero.length < 7 -> "El RUT debe tener 7 u 8 dígitos"
        dv.isEmpty() -> "Ingresa el dígito verificador"
        calcularDigitoVerificador(numero) != dv.first().uppercaseChar() ->
            "El dígito verificador no coincide con el RUT"
        else -> null
    }

    fun formatearRut(numero: String, dv: String): String {
        val conPuntos = numero.reversed().chunked(3).joinToString(".").reversed()
        return "$conPuntos-${dv.uppercase()}"
    }

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

    private fun contieneDatosPersonales(contrasena: String, correo: String, nombres: String): Boolean {
        val clave = contrasena.lowercase()
        val parteCorreo = correo.substringBefore('@').lowercase()

        val contieneCorreo = parteCorreo.length >= 3 && clave.contains(parteCorreo)

        val contieneNombre = nombres.lowercase().split(" ").any { it.length >= 4 && clave.contains(it) }
        return contieneCorreo || contieneNombre
    }

    private fun esContrasenaComun(contrasena: String): Boolean {
        val base = contrasena.lowercase().trimEnd { !it.isLetterOrDigit() }
        return base in CONTRASENAS_COMUNES
    }

    private fun tieneCaracterEspecial(contrasena: String): Boolean =
        contrasena.any { !it.isLetterOrDigit() && !it.isWhitespace() }

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

    fun validarRepeticion(contrasena: String, repetida: String): String? = when {
        repetida.isEmpty() -> "Repite tu contrase\u00f1a"
        repetida != contrasena -> "Las contrase\u00f1as no coinciden"
        else -> null
    }
}
