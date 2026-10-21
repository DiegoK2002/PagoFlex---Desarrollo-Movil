package cl.fintech.pagoflex.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import cl.fintech.pagoflex.model.Pago
import cl.fintech.pagoflex.model.Rol
import cl.fintech.pagoflex.model.Usuario
import cl.fintech.pagoflex.util.Seguridad
import java.time.LocalDate
import java.time.LocalDateTime
import cl.fintech.pagoflex.model.Reporte

/**
 * Base de datos SQLite de la app (archivo pagoflex.db). Crea las tablas la primera vez que se abre,
 * las llena con datos de ejemplo ficticios y ofrece las operaciones de lectura y escritura.
 *
 * Tablas y relaciones:
 *   roles <- usuarios <- compromisos -> empresas
 *                 ^            ^
 *                 |            +-- pagos
 *                 +-- reportes -+
 *
 * Las claves foraneas (REFERENCES) se activan en [onConfigure], porque SQLite las trae desactivadas.
 * Las fechas se guardan como texto ISO ("2026-10-10") y se convierten al leerlas.
 */

class PagoFlexDbHelper private constructor(
    context: Context,
    nombreBase: String
) : SQLiteOpenHelper(context, nombreBase, null, VERSION_BD) {

    companion object {
        private const val NOMBRE_BD = "pagoflex.db"

        // Al subir este numero, SQLite llama a onUpgrade y la base se vuelve a crear
        private const val VERSION_BD = 4

        // Contrasena de las cuentas de ejemplo precargadas
        private const val CLAVE_DEMO = "Demo1234"

        private const val TABLA_ROLES = "roles"
        private const val TABLA_USUARIOS = "usuarios"
        private const val TABLA_EMPRESAS = "empresas"
        private const val TABLA_COMPROMISOS = "compromisos"
        private const val TABLA_PAGOS = "pagos"
        private const val TABLA_REPORTES = "reportes"

        private const val TABLA_RECONOCIMIENTOS = "reconocimientos"

        private const val CODIGO_EJEMPLAR = "CUMPLIMIENTO_EJEMPLAR"

        private const val SQL_CREAR_ROLES =
            "CREATE TABLE $TABLA_ROLES (" +
                "id_rol INTEGER PRIMARY KEY, " +
                "nombre_rol TEXT NOT NULL UNIQUE)"

        private const val SQL_CREAR_USUARIOS =
            "CREATE TABLE $TABLA_USUARIOS (" +
                "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "rut TEXT NOT NULL UNIQUE, " +
                "nombres TEXT NOT NULL, " +
                "apellidos TEXT NOT NULL, " +
                "correo TEXT NOT NULL UNIQUE, " +
                "contrasena_hash TEXT NOT NULL, " +
                "comuna TEXT NOT NULL, " + "id_rol INTEGER NOT NULL REFERENCES $TABLA_ROLES(id_rol), " +
                "foto_perfil BLOB)"


        private const val SQL_CREAR_EMPRESAS =
            "CREATE TABLE $TABLA_EMPRESAS (" +
                "codigo_empresa TEXT PRIMARY KEY, " +
                "razon_social TEXT NOT NULL, " +
                "nombre_fantasia TEXT NOT NULL, " +
                "rubro TEXT NOT NULL, " +
                "recargo_atraso INTEGER NOT NULL)"

        private const val SQL_CREAR_COMPROMISOS =
            "CREATE TABLE $TABLA_COMPROMISOS (" +
                "folio TEXT PRIMARY KEY, " +
                "id_usuario INTEGER NOT NULL REFERENCES $TABLA_USUARIOS(id_usuario), " +
                "codigo_empresa TEXT NOT NULL REFERENCES $TABLA_EMPRESAS(codigo_empresa), " +
                "concepto TEXT NOT NULL, " +
                "numero_cuota INTEGER, " +
                "total_cuotas INTEGER, " +
                "monto INTEGER NOT NULL, " +
                "fecha_vencimiento TEXT NOT NULL, " +
                "estado TEXT NOT NULL)"

        private const val SQL_CREAR_PAGOS =
            "CREATE TABLE $TABLA_PAGOS (" +
                "numero_comprobante TEXT PRIMARY KEY, " +
                "folio TEXT NOT NULL REFERENCES $TABLA_COMPROMISOS(folio), " +
                "id_usuario INTEGER NOT NULL REFERENCES $TABLA_USUARIOS(id_usuario), " +
                "codigo_empresa TEXT NOT NULL REFERENCES $TABLA_EMPRESAS(codigo_empresa), " +
                "concepto TEXT NOT NULL, " +
                "fecha_hora TEXT NOT NULL, " +
                "monto_pagado INTEGER NOT NULL, " +
                "canal TEXT NOT NULL, " +
                "a_tiempo INTEGER NOT NULL, " +
                "medio_pago TEXT NOT NULL)"

        private const val SQL_CREAR_REPORTES =
            "CREATE TABLE $TABLA_REPORTES (" +
                "id_reporte INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "folio TEXT NOT NULL REFERENCES $TABLA_COMPROMISOS(folio), " +
                "id_usuario INTEGER NOT NULL REFERENCES $TABLA_USUARIOS(id_usuario), " +
                "motivo TEXT NOT NULL, " +
                "comentario TEXT, " +
                "estado TEXT NOT NULL, " +
                "id_agente INTEGER REFERENCES $TABLA_USUARIOS(id_usuario), " +
                "respuesta TEXT, " +
                "fecha_creacion TEXT NOT NULL)"


        private const val SQL_CREAR_RECONOCIMIENTOS =
            "CREATE TABLE IF NOT EXISTS $TABLA_RECONOCIMIENTOS (" +
                    "id_usuario INTEGER NOT NULL REFERENCES $TABLA_USUARIOS(id_usuario), " +
                    "codigo TEXT NOT NULL, " +
                    "fecha_obtenida TEXT NOT NULL, " +
                    "PRIMARY KEY (id_usuario, codigo))"


        @Volatile
        private var instancia: PagoFlexDbHelper? = null

        /** Devuelve la unica instancia del helper, para que toda la app use una sola conexion. */
        fun obtener(context: Context): PagoFlexDbHelper =
            instancia ?: synchronized(this) {
                instancia ?: PagoFlexDbHelper(context.applicationContext, NOMBRE_BD).also { instancia = it }
            }

        fun crearParaPruebas(context: Context): PagoFlexDbHelper =
            PagoFlexDbHelper(
                context.applicationContext,
                "pagoflex_reconocimientos_test.db"
            )
    }

    fun registrarReconocimientoEjemplar(
        idUsuario: Long,
        fecha: LocalDate
    ) {
        if (idUsuario <= 0L) return

        val valores = ContentValues().apply {
            put("id_usuario", idUsuario)
            put("codigo", CODIGO_EJEMPLAR)
            put("fecha_obtenida", fecha.toString())
        }

        writableDatabase.insertWithOnConflict(
            TABLA_RECONOCIMIENTOS,
            null,
            valores,
            SQLiteDatabase.CONFLICT_IGNORE
        )
    }


    fun fechaReconocimientoEjemplar(idUsuario: Long): LocalDate? {
        if (idUsuario <= 0L) return null

        return readableDatabase.rawQuery(
            "SELECT fecha_obtenida FROM $TABLA_RECONOCIMIENTOS " +
                    "WHERE id_usuario = ? AND codigo = ?",
            arrayOf(
                idUsuario.toString(),
                CODIGO_EJEMPLAR
            )
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                LocalDate.parse(cursor.getString(0))
            } else {
                null
            }
        }
    }


    /** Se ejecuta antes de abrir la base: activa la verificacion de claves foraneas. */
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    /** Se ejecuta una sola vez, cuando el archivo de la base no existe. Primero las tablas "padre". */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREAR_ROLES)
        db.execSQL(SQL_CREAR_USUARIOS)
        db.execSQL(SQL_CREAR_EMPRESAS)
        db.execSQL(SQL_CREAR_COMPROMISOS)
        db.execSQL(SQL_CREAR_PAGOS)
        db.execSQL(SQL_CREAR_REPORTES)
        db.execSQL(SQL_CREAR_RECONOCIMIENTOS)
        precargarDatos(db)
    }

    /** Al cambiar la version se borran las tablas (en orden inverso a su creacion) y se vuelven a crear. */
    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        if (oldVersion < 3) {
            db.execSQL(
                "ALTER TABLE $TABLA_USUARIOS ADD COLUMN foto_perfil BLOB"
            )
        }
        if (oldVersion < 4) {
            db.execSQL(SQL_CREAR_RECONOCIMIENTOS)
        }
    }


    // ------------------------------------------------------------------
    // Usuarios
    // ------------------------------------------------------------------

    /** Inserta una cuenta nueva. Devuelve el id generado, o -1 si no se pudo guardar (por ejemplo, correo repetido). */
    fun registrarUsuario(
        rut: String, nombres: String, apellidos: String, correo: String,
        contrasenaHash: String, comuna: String, rol: Rol
    ): Long {
        val valores = ContentValues().apply {
            put("rut", rut)
            put("nombres", nombres)
            put("apellidos", apellidos)
            put("correo", correo.trim().lowercase())   // el correo siempre se guarda en minusculas
            put("contrasena_hash", contrasenaHash)
            put("comuna", comuna)
            put("id_rol", rol.id)
        }
        return writableDatabase.insert(TABLA_USUARIOS, null, valores)
    }

    fun guardarFotoPerfil(idUsuario: Long, foto: ByteArray): Boolean {
        if (idUsuario <= 0L || foto.isEmpty()) {
            return false
        }

        val valores = ContentValues().apply {
            put("foto_perfil", foto)
        }

        val filasActualizadas = writableDatabase.update(
            TABLA_USUARIOS,
            valores,
            "id_usuario = ?",
            arrayOf(idUsuario.toString())
        )

        return filasActualizadas == 1
    }


    fun fotoPerfilDeUsuario(idUsuario: Long): ByteArray? {
        if (idUsuario <= 0L) {
            return null
        }

        return readableDatabase.rawQuery(
            "SELECT foto_perfil FROM $TABLA_USUARIOS WHERE id_usuario = ?",
            arrayOf(idUsuario.toString())
        ).use { cursor ->
            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                cursor.getBlob(0)
            } else {
                null
            }
        }
    }


    /** Indica si ya existe una cuenta con ese correo. */
    fun existeCorreo(correo: String): Boolean =
        readableDatabase.rawQuery(
            "SELECT 1 FROM $TABLA_USUARIOS WHERE correo = ?", arrayOf(correo.trim().lowercase())
        ).use { it.moveToFirst() }

    /** Indica si ya existe una cuenta con ese RUT (con formato "12.345.678-5"). */
    fun existeRut(rut: String): Boolean =
        readableDatabase.rawQuery(
            "SELECT 1 FROM $TABLA_USUARIOS WHERE rut = ?", arrayOf(rut)
        ).use { it.moveToFirst() }

    /**
     * Busca la cuenta cuyo correo y hash de contrasena coinciden. Devuelve null si no hay coincidencia.
     * Los valores viajan en los signos "?" y no concatenados al texto SQL, para evitar inyeccion SQL.
     */
    fun buscarPorCredenciales(correo: String, contrasenaHash: String): Usuario? =
        readableDatabase.rawQuery(
            "SELECT * FROM $TABLA_USUARIOS WHERE correo = ? AND contrasena_hash = ?",
            arrayOf(correo.trim().lowercase(), contrasenaHash)
        ).use { cursor -> if (cursor.moveToFirst()) leerUsuario(cursor) else null }

    // ------------------------------------------------------------------
    // Compromisos y empresas
    // ------------------------------------------------------------------

    /** Compromisos de una persona, ordenados por fecha de vencimiento. */
    fun compromisosDeUsuario(idUsuario: Long): List<Compromiso> {

        val valores = ContentValues().apply {
            put("estado", EstadoCompromiso.VENCIDO.name)
        }

        writableDatabase.update(
            TABLA_COMPROMISOS,
            valores,
            "id_usuario = ? AND estado = ? AND fecha_vencimiento < ?",
            arrayOf(
                idUsuario.toString(),
                EstadoCompromiso.PENDIENTE.name,
                LocalDate.now().toString()
            )
        )

        val lista = mutableListOf<Compromiso>()
        readableDatabase.rawQuery(
            "SELECT * FROM $TABLA_COMPROMISOS WHERE id_usuario = ? ORDER BY fecha_vencimiento",
            arrayOf(idUsuario.toString())
        ).use { cursor ->
            // Cada vuelta del ciclo convierte una fila en un objeto Compromiso
            while (cursor.moveToNext()) {
                lista.add(leerCompromiso(cursor))
            }
        }
        return lista
    }

    /** Nombre comercial de la empresa; si no se encuentra, devuelve el codigo recibido. */
    fun nombreEmpresa(codigo: String): String =
        readableDatabase.rawQuery(
            "SELECT nombre_fantasia FROM $TABLA_EMPRESAS WHERE codigo_empresa = ?", arrayOf(codigo)
        ).use { if (it.moveToFirst()) it.getString(0) else codigo }

    /** Recargo por atraso que cobra la empresa, en pesos. */
    fun recargoDe(codigoEmpresa: String): Int =
        readableDatabase.rawQuery(
            "SELECT recargo_atraso FROM $TABLA_EMPRESAS WHERE codigo_empresa = ?", arrayOf(codigoEmpresa)
        ).use { if (it.moveToFirst()) it.getInt(0) else 0 }

    // ------------------------------------------------------------------
    // Pagos
    // ------------------------------------------------------------------

    /** Ultimo pago registrado para un compromiso, o null si no tiene. */


    fun pagoPorFolioDeUsuario(
        folio: String,
        idUsuario: Long
    ): Pago? {
        if (idUsuario <= 0L || folio.isBlank()) return null

        return readableDatabase.rawQuery(
            "SELECT * FROM $TABLA_PAGOS " +
                    "WHERE folio = ? AND id_usuario = ? " +
                    "ORDER BY fecha_hora DESC, numero_comprobante DESC " +
                    "LIMIT 1",
            arrayOf(folio, idUsuario.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                leerPago(cursor)
            } else {
                null
            }
        }
    }

    fun pagosDeUsuario(idUsuario: Long): List<Pago> {
        if (idUsuario <= 0L) {
            return emptyList()
        }

        val pagos = mutableListOf<Pago>()

        readableDatabase.rawQuery(
            "SELECT * FROM $TABLA_PAGOS " +
                    "WHERE id_usuario = ? " +
                    "ORDER BY fecha_hora DESC, numero_comprobante DESC",
            arrayOf(idUsuario.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) {
                pagos.add(leerPago(cursor))
            }
        }

        return pagos
    }

    /** Monto efectivamente pagado por cada compromiso de una persona, indexado por folio. */
    fun montosPagadosDeUsuario(idUsuario: Long): Map<String, Int> {
        val montos = mutableMapOf<String, Int>()
        readableDatabase.rawQuery(
            "SELECT folio, monto_pagado FROM $TABLA_PAGOS WHERE id_usuario = ?",
            arrayOf(idUsuario.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) {
                montos[cursor.getString(0)] = cursor.getInt(1)
            }
        }
        return montos
    }

    /** Numero del proximo comprobante: el mayor existente mas uno (por ejemplo, de CPR-000453 pasa a 454). */
    fun siguienteNumeroComprobante(): Int =
        readableDatabase.rawQuery(
            // SUBSTR(..., 5) quita el prefijo "CPR-" y CAST convierte el resto a numero
            "SELECT MAX(CAST(SUBSTR(numero_comprobante, 5) AS INTEGER)) FROM $TABLA_PAGOS", null
        ).use { if (it.moveToFirst()) it.getInt(0) + 1 else 1 }

    /**
     * Guarda el pago y marca el compromiso como Pagado dentro de una transaccion: se guardan
     * las dos cosas o ninguna. Devuelve false si el compromiso ya no estaba pendiente o vencido
     * (por ejemplo, por un doble toque en el boton), en cuyo caso no queda nada guardado.
     */
    fun registrarPago(pago: Pago): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            val valoresPago = ContentValues().apply {
                put("numero_comprobante", pago.numeroComprobante)
                put("folio", pago.folioCompromiso)
                put("id_usuario", pago.idUsuario)
                put("codigo_empresa", pago.codigoEmpresa)
                put("concepto", pago.concepto)
                put("fecha_hora", pago.fechaHora.toString())
                put("monto_pagado", pago.montoPagado)
                put("canal", pago.canal)
                put("a_tiempo", if (pago.aTiempo) 1 else 0)
                put("medio_pago", pago.medioPago)
            }
            val idPago = db.insert(TABLA_PAGOS, null, valoresPago)

            val valoresEstado = ContentValues().apply {
                put("estado", EstadoCompromiso.PAGADO.name)
            }
            // Solo se actualiza si el compromiso sigue sin pagar; asi no se puede pagar dos veces

            val filas = db.update(
                TABLA_COMPROMISOS,
                valoresEstado,
                "folio = ? AND id_usuario = ? AND codigo_empresa = ? " +
                        "AND estado IN (?, ?)",
                arrayOf(
                    pago.folioCompromiso,
                    pago.idUsuario.toString(),
                    pago.codigoEmpresa,
                    EstadoCompromiso.PENDIENTE.name,
                    EstadoCompromiso.VENCIDO.name
                )
            )


            if (idPago != -1L && filas == 1) {
                db.setTransactionSuccessful()   // confirma los dos cambios
                true
            } else {
                false                           // sin confirmar: se deshace todo al terminar
            }
        } finally {
            db.endTransaction()
        }
    }

    fun compromisoPerteneceAUsuario(
        folio: String,
        idUsuario: Long
    ): Boolean {
        return readableDatabase.rawQuery(
            "SELECT 1 FROM $TABLA_COMPROMISOS WHERE folio = ? AND id_usuario = ?",
            arrayOf(folio, idUsuario.toString())
        ).use { cursor ->
            cursor.moveToFirst()
        }
    }

    fun compromisoPermiteReporte(folio: String): Boolean {
        return readableDatabase.rawQuery(
            "SELECT 1 FROM $TABLA_COMPROMISOS WHERE folio = ? AND estado IN (?, ?)",
            arrayOf(
                folio,
                EstadoCompromiso.PENDIENTE.name,
                EstadoCompromiso.VENCIDO.name
            )
        ).use { cursor ->
            cursor.moveToFirst()
        }
    }

    fun existeReporteActivo(folio: String): Boolean {
        return readableDatabase.rawQuery(
            "SELECT 1 FROM $TABLA_REPORTES WHERE folio = ? AND estado IN (?, ?) LIMIT 1",
            arrayOf(folio, "Abierto", "En gestión")
        ).use { cursor ->
            cursor.moveToFirst()
        }
    }

    fun registrarReporte(
        folio: String,
        idUsuario: Long,
        motivo: String,
        comentario: String?
    ): Long {
        val motivoLimpio = motivo.trim()
        val comentarioLimpio = comentario?.trim()

        val motivosPermitidos = setOf(
            "Ya pagué por otro medio",
            "No reconozco este cobro",
            "El monto no corresponde",
            "Otro"
        )

        if (folio.isBlank() || idUsuario <= 0L || motivoLimpio !in motivosPermitidos) {
            return -1L
        }

        if (motivoLimpio == "Otro" && comentarioLimpio.isNullOrBlank()) {
            return -1L
        }

        val db = writableDatabase
        db.beginTransaction()

        try {
            if (!compromisoPerteneceAUsuario(folio, idUsuario)) {
                return -1L
            }

            if (!compromisoPermiteReporte(folio)) {
                return -1L
            }

            if (existeReporteActivo(folio)) {
                return -1L
            }

            val valores = ContentValues().apply {
                put("folio", folio)
                put("id_usuario", idUsuario)
                put("motivo", motivoLimpio)
                put("comentario", comentarioLimpio)
                put("estado", "Abierto")
                put("fecha_creacion", LocalDateTime.now().toString())
            }

            val idReporte = db.insert(TABLA_REPORTES, null, valores)

            if (idReporte == -1L) {
                return -1L
            }

            val valoresEstado = ContentValues().apply {
                put("estado", EstadoCompromiso.EN_REVISION.name)
            }

            val filasActualizadas = db.update(
                TABLA_COMPROMISOS,
                valoresEstado,
                "folio = ? AND id_usuario = ? AND estado IN (?, ?)",
                arrayOf(
                    folio,
                    idUsuario.toString(),
                    EstadoCompromiso.PENDIENTE.name,
                    EstadoCompromiso.VENCIDO.name
                )
            )

            if (filasActualizadas != 1) {
                return -1L
            }

            db.setTransactionSuccessful()
            return idReporte

        } finally {
            db.endTransaction()
        }
    }


    fun reportesDeUsuario(idUsuario: Long): List<Reporte> {
        val lista = mutableListOf<Reporte>()

        readableDatabase.rawQuery(
            "SELECT * FROM $TABLA_REPORTES WHERE id_usuario = ? ORDER BY fecha_creacion DESC, id_reporte DESC",
            arrayOf(idUsuario.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(leerReporte(cursor))
            }
        }

        return lista
    }

    fun reportesParaAgente(): List<Reporte> {
        val lista = mutableListOf<Reporte>()

        readableDatabase.rawQuery(
            "SELECT * FROM $TABLA_REPORTES ORDER BY fecha_creacion DESC, id_reporte DESC",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(leerReporte(cursor))
            }
        }

        return lista
    }


    fun tomarReporte(idReporte: Long, idAgente: Long): Boolean {
        if (idReporte <= 0L || idAgente <= 0L) {
            return false
        }

        val db = writableDatabase

        val esAgente = db.rawQuery(
            "SELECT 1 FROM $TABLA_USUARIOS WHERE id_usuario = ? AND id_rol = ? LIMIT 1",
            arrayOf(idAgente.toString(), Rol.AGENTE.id.toString())
        ).use { cursor ->
            cursor.moveToFirst()
        }

        if (!esAgente) {
            return false
        }

        val valores = ContentValues().apply {
            put("id_agente", idAgente)
            put("estado", "En gestión")
        }

        val filasActualizadas = db.update(
            TABLA_REPORTES,
            valores,
            "id_reporte = ? AND estado = ? AND id_agente IS NULL",
            arrayOf(idReporte.toString(), "Abierto")
        )

        return filasActualizadas == 1
    }


    fun resolverReporte(
        idReporte: Long,
        idAgente: Long,
        respuesta: String
    ): Boolean {
        val respuestaLimpia = respuesta.trim()

        if (idReporte <= 0L || idAgente <= 0L || respuestaLimpia.isBlank()) {
            return false
        }

        val db = writableDatabase
        db.beginTransaction()

        try {
            val compromiso = db.rawQuery(
                """
            SELECT c.folio, c.fecha_vencimiento
            FROM $TABLA_REPORTES r
            JOIN $TABLA_COMPROMISOS c ON r.folio = c.folio
            JOIN $TABLA_USUARIOS u ON r.id_agente = u.id_usuario
            WHERE r.id_reporte = ?
            AND r.id_agente = ?
            AND r.estado = ?
            AND c.estado = ?
            AND u.id_rol = ?
            LIMIT 1
            """.trimIndent(),
                arrayOf(
                    idReporte.toString(),
                    idAgente.toString(),
                    "En gestión",
                    EstadoCompromiso.EN_REVISION.name,
                    Rol.AGENTE.id.toString()
                )
            ).use { cursor ->
                if (cursor.moveToFirst()) {
                    Pair(cursor.getString(0), cursor.getString(1))
                } else {
                    null
                }
            } ?: return false

            val valoresReporte = ContentValues().apply {
                put("estado", "Resuelto")
                put("respuesta", respuestaLimpia)
            }

            val filasReporte = db.update(
                TABLA_REPORTES,
                valoresReporte,
                "id_reporte = ? AND id_agente = ? AND estado = ?",
                arrayOf(idReporte.toString(), idAgente.toString(), "En gestión")
            )

            val vencimiento = LocalDate.parse(compromiso.second)

            val nuevoEstado = if (vencimiento.isBefore(LocalDate.now())) {
                EstadoCompromiso.VENCIDO
            } else {
                EstadoCompromiso.PENDIENTE
            }

            val valoresCompromiso = ContentValues().apply {
                put("estado", nuevoEstado.name)
            }

            val filasCompromiso = db.update(
                TABLA_COMPROMISOS,
                valoresCompromiso,
                "folio = ? AND estado = ?",
                arrayOf(compromiso.first, EstadoCompromiso.EN_REVISION.name)
            )

            if (filasReporte != 1 || filasCompromiso != 1) {
                return false
            }

            db.setTransactionSuccessful()
            return true
        } finally {
            db.endTransaction()
        }
    }


    // ------------------------------------------------------------------
    // Conversion de filas a objetos
    // ------------------------------------------------------------------

    private fun leerUsuario(c: Cursor) = Usuario(
        id = c.getLong(c.getColumnIndexOrThrow("id_usuario")),
        rut = c.getString(c.getColumnIndexOrThrow("rut")),
        nombres = c.getString(c.getColumnIndexOrThrow("nombres")),
        apellidos = c.getString(c.getColumnIndexOrThrow("apellidos")),
        correo = c.getString(c.getColumnIndexOrThrow("correo")),
        comuna = c.getString(c.getColumnIndexOrThrow("comuna")),
        rol = Rol.desdeId(c.getLong(c.getColumnIndexOrThrow("id_rol")))
    )

    /** Lee una columna entera que puede ser nula (por ejemplo, el numero de cuota). */
    private fun enteroONulo(c: Cursor, columna: String): Int? {
        val indice = c.getColumnIndexOrThrow(columna)
        return if (c.isNull(indice)) null else c.getInt(indice)
    }

    private fun leerCompromiso(c: Cursor) = Compromiso(
        folio = c.getString(c.getColumnIndexOrThrow("folio")),
        idUsuario = c.getLong(c.getColumnIndexOrThrow("id_usuario")),
        codigoEmpresa = c.getString(c.getColumnIndexOrThrow("codigo_empresa")),
        concepto = c.getString(c.getColumnIndexOrThrow("concepto")),
        numeroCuota = enteroONulo(c, "numero_cuota"),
        totalCuotas = enteroONulo(c, "total_cuotas"),
        monto = c.getInt(c.getColumnIndexOrThrow("monto")),
        fechaVencimiento = LocalDate.parse(c.getString(c.getColumnIndexOrThrow("fecha_vencimiento"))),
        estado = EstadoCompromiso.valueOf(c.getString(c.getColumnIndexOrThrow("estado")))
    )

    private fun leerPago(c: Cursor) = Pago(
        numeroComprobante = c.getString(c.getColumnIndexOrThrow("numero_comprobante")),
        folioCompromiso = c.getString(c.getColumnIndexOrThrow("folio")),
        idUsuario = c.getLong(c.getColumnIndexOrThrow("id_usuario")),
        codigoEmpresa = c.getString(c.getColumnIndexOrThrow("codigo_empresa")),
        concepto = c.getString(c.getColumnIndexOrThrow("concepto")),
        fechaHora = LocalDateTime.parse(c.getString(c.getColumnIndexOrThrow("fecha_hora"))),
        montoPagado = c.getInt(c.getColumnIndexOrThrow("monto_pagado")),
        canal = c.getString(c.getColumnIndexOrThrow("canal")),
        aTiempo = c.getInt(c.getColumnIndexOrThrow("a_tiempo")) == 1,
        medioPago = c.getString(c.getColumnIndexOrThrow("medio_pago"))
    )

    private fun leerReporte(c: Cursor): Reporte {
        val indiceAgente = c.getColumnIndexOrThrow("id_agente")

        return Reporte(
            idReporte = c.getLong(c.getColumnIndexOrThrow("id_reporte")),
            folio = c.getString(c.getColumnIndexOrThrow("folio")),
            idUsuario = c.getLong(c.getColumnIndexOrThrow("id_usuario")),
            motivo = c.getString(c.getColumnIndexOrThrow("motivo")),
            comentario = c.getString(c.getColumnIndexOrThrow("comentario")),
            estado = c.getString(c.getColumnIndexOrThrow("estado")),
            idAgente = if (c.isNull(indiceAgente)) null else c.getLong(indiceAgente),
            respuesta = c.getString(c.getColumnIndexOrThrow("respuesta")),
            fechaCreacion = LocalDateTime.parse(
                c.getString(c.getColumnIndexOrThrow("fecha_creacion"))
            )
        )
    }

    // ------------------------------------------------------------------
    // Datos de ejemplo (todos ficticios)
    // ------------------------------------------------------------------

    /** Inserta una fila en [tabla] a partir de pares columna-valor. Acepta Int, Long, String y null. */
    private fun insertar(db: SQLiteDatabase, tabla: String, vararg datos: Pair<String, Any?>) {
        val valores = ContentValues()
        for ((columna, valor) in datos) {
            when (valor) {
                null -> valores.putNull(columna)
                is Int -> valores.put(columna, valor)
                is Long -> valores.put(columna, valor)
                is String -> valores.put(columna, valor)
                else -> error("Tipo de dato no soportado")
            }
        }
        db.insertOrThrow(tabla, null, valores)
    }

    private fun precargarDatos(db: SQLiteDatabase) {
        // Roles: uno por cada valor del enum Rol, con el mismo id
        for (rol in Rol.entries) {
            insertar(db, TABLA_ROLES, "id_rol" to rol.id, "nombre_rol" to rol.etiqueta)
        }

        // Empresas que cobran los compromisos
        fun empresa(codigo: String, razon: String, fantasia: String, rubro: String, recargo: Int) =
            insertar(
                db, TABLA_EMPRESAS,
                "codigo_empresa" to codigo, "razon_social" to razon,
                "nombre_fantasia" to fantasia, "rubro" to rubro, "recargo_atraso" to recargo
            )
        empresa("EC-01", "Inmobiliaria Altos del Maipo SpA", "Altos del Maipo", "Inmobiliaria", 8500)
        empresa("EC-02", "Corporación Educacional Bosque Nativo", "Colegio Bosque Nativo", "Educación escolar", 5000)
        empresa("EC-03", "Crédito Andino S.A.", "Crédito Andino", "Crédito de consumo", 12000)
        empresa("EC-04", "Seguros Horizonte Sur S.A.", "Horizonte Sur", "Seguros", 3000)
        empresa("EC-05", "Fundación Manos Abiertas", "Manos Abiertas", "ONG", 0)
        empresa("EC-06", "Club Deportivo Los Halcones", "Club Los Halcones", "Club deportivo", 2000)
        empresa("EC-07", "Instituto Técnico Cordillera SpA", "Instituto Cordillera", "Educación superior", 6000)
        empresa("EC-08", "Arriendos Plaza Oriente Ltda.", "Plaza Oriente", "Administración de arriendos", 7500)

        // Cuentas de ejemplo: todas usan la contrasena CLAVE_DEMO (se guarda su hash)
        fun usuario(id: Long, rut: String, nombres: String, apellidos: String, correo: String, comuna: String, rol: Rol) =
            insertar(
                db, TABLA_USUARIOS,
                "id_usuario" to id, "rut" to rut, "nombres" to nombres, "apellidos" to apellidos,
                "correo" to correo, "contrasena_hash" to Seguridad.hashContrasena(correo, CLAVE_DEMO),
                "comuna" to comuna, "id_rol" to rol.id
            )
        val uf = Rol.USUARIO_FINAL
        usuario(1, "15.834.207-3", "Camila Andrea", "Rojas Pe\u00f1a", "crojas@correo.ejemplo", "Maip\u00fa", uf)
        usuario(2, "12.476.318-5", "Luis Alberto", "Mu\u00f1oz Tapia", "lmunoz@correo.ejemplo", "Puente Alto", uf)
        usuario(3, "6.893.541-5", "Rosa Elena", "Contreras Díaz", "rcontreras@correo.ejemplo", "San Miguel", uf)
        usuario(4, "21.045.873-5", "Matías Ignacio", "Soto Vergara", "msoto@correo.ejemplo", "La Florida", uf)
        usuario(5, "17.562.904-1", "Fernanda Paz", "Godoy Lagos", "fgodoy@correo.ejemplo", "\u00d1u\u00f1oa", uf)
        usuario(6, "10.318.265-4", "Héctor Manuel", "Pizarro Campos", "hpizarro@correo.ejemplo", "Quilicura", uf)
        usuario(7, "19.287.430-0", "Javiera Constanza", "Reyes Molina", "jreyes@correo.ejemplo", "San Bernardo", uf)
        usuario(8, "18.704.396-4", "Benjamín Esteban", "Araya Fuentes", "baraya@correo.ejemplo", "Estación Central", uf)
        val ag = Rol.AGENTE
        usuario(9, "16.248.317-K", "Andrés Felipe", "Lagos Meza", "alagos@pagoflex.ejemplo", "Santiago", ag)
        usuario(10, "18.352.091-1", "Daniela Sofía", "Ortiz Bravo", "dortiz@pagoflex.ejemplo", "Santiago", ag)
        usuario(11, "17.781.435-0", "Valentina Ignacia", "Paredes Silva", "vparedes@pagoflex.ejemplo", "Santiago", ag)

        // Compromisos: folio, persona, empresa, concepto, cuota, total de cuotas, monto, vencimiento, estado
        fun compromiso(
            folio: String, idUsuario: Long, empresa: String, concepto: String,
            cuota: Int?, total: Int?, monto: Int, vence: String, estado: EstadoCompromiso
        ) = insertar(
            db, TABLA_COMPROMISOS,
            "folio" to folio, "id_usuario" to idUsuario, "codigo_empresa" to empresa,
            "concepto" to concepto, "numero_cuota" to cuota, "total_cuotas" to total,
            "monto" to monto, "fecha_vencimiento" to vence, "estado" to estado.name
        )
        val pen = EstadoCompromiso.PENDIENTE
        val ven = EstadoCompromiso.VENCIDO
        val pag = EstadoCompromiso.PAGADO
        compromiso("CP-2026-0072", 2, "EC-01", "Cuota pie departamento", 13, 36, 310000, "2026-08-31", pag)
        compromiso("CP-2026-0089", 1, "EC-08", "Arriendo departamento septiembre", 9, 12, 520000, "2026-09-10", pag)
        compromiso("CP-2026-0090", 1, "EC-06", "Cuota social septiembre", 9, 12, 15000, "2026-09-05", pag)
        compromiso("CP-2026-0091", 1, "EC-05", "Aporte mensual septiembre", null, null, 10000, "2026-09-15", pag)
        compromiso("CP-2026-0098", 5, "EC-02", "Mensualidad septiembre", 7, 10, 185000, "2026-09-10", pag)
        compromiso("CP-2026-0101", 1, "EC-08", "Arriendo departamento octubre", 10, 12, 520000, "2026-10-10", pen)
        compromiso("CP-2026-0102", 1, "EC-06", "Cuota social octubre", 10, 12, 15000, "2026-10-05", ven)
        compromiso("CP-2026-0103", 1, "EC-05", "Aporte mensual octubre", null, null, 10000, "2026-10-15", pen)
        compromiso("CP-2026-0104", 1, "EC-03", "Crédito de consumo", 7, 24, 86400, "2026-09-25", pag)
        compromiso("CP-2026-0105", 1, "EC-03", "Crédito de consumo", 8, 24, 86400, "2026-10-25", pen)
        compromiso("CP-2026-0106", 2, "EC-01", "Cuota pie departamento", 14, 36, 310000, "2026-09-30", ven)
        compromiso("CP-2026-0107", 2, "EC-01", "Cuota pie departamento", 15, 36, 310000, "2026-10-30", pen)
        compromiso("CP-2026-0108", 3, "EC-04", "Seguro de hogar octubre", 10, 12, 18900, "2026-10-01", pag)
        compromiso("CP-2026-0109", 4, "EC-07", "Arancel octubre", 8, 10, 142000, "2026-10-05", EstadoCompromiso.EN_REVISION)
        compromiso("CP-2026-0110", 5, "EC-02", "Mensualidad octubre", 8, 10, 185000, "2026-10-10", pen)
        compromiso("CP-2026-0111", 6, "EC-03", "Crédito de consumo", 19, 36, 54200, "2026-09-20", EstadoCompromiso.ANULADO)
        compromiso("CP-2026-0112", 7, "EC-06", "Cuota social octubre", 10, 12, 15000, "2026-10-05", pag)

        // Pagos anteriores: comprobante, folio, persona, empresa, concepto, fecha y hora, monto, canal, a tiempo
        fun pago(
            numero: String, folio: String, idUsuario: Long, empresa: String, concepto: String,
            fechaHora: String, monto: Int, canal: String, aTiempo: Boolean
        ) = insertar(
            db, TABLA_PAGOS,
            "numero_comprobante" to numero, "folio" to folio, "id_usuario" to idUsuario,
            "codigo_empresa" to empresa, "concepto" to concepto, "fecha_hora" to fechaHora,
            "monto_pagado" to monto, "canal" to canal, "a_tiempo" to (if (aTiempo) 1 else 0),
            "medio_pago" to "Pago simulado"
        )
        pago("CPR-000377", "CP-2026-0072", 2, "EC-01", "Cuota pie departamento 13/36", "2026-09-02T21:14", 318500, "Agente conversacional", false)
        pago("CPR-000398", "CP-2026-0089", 1, "EC-08", "Arriendo departamento septiembre", "2026-09-09T13:05", 520000, "Portal de pagos", true)
        pago("CPR-000402", "CP-2026-0090", 1, "EC-06", "Cuota social septiembre", "2026-09-07T18:40", 17000, "Portal de pagos", false)
        pago("CPR-000405", "CP-2026-0091", 1, "EC-05", "Aporte mensual septiembre", "2026-09-15T08:22", 10000, "Agente conversacional", true)
        pago("CPR-000447", "CP-2026-0098", 5, "EC-02", "Mensualidad septiembre", "2026-09-10T22:31", 185000, "Portal de pagos", true)
        pago("CPR-000451", "CP-2026-0104", 1, "EC-03", "Crédito de consumo 7/24", "2026-09-24T10:42", 86400, "Portal de pagos", true)
        pago("CPR-000452", "CP-2026-0108", 3, "EC-04", "Seguro de hogar octubre", "2026-09-30T19:05", 18900, "Agente conversacional", true)
        pago("CPR-000453", "CP-2026-0112", 7, "EC-06", "Cuota social octubre", "2026-10-04T08:15", 15000, "App", true)

        // Reportes de ejemplo: una persona informa un problema y un agente lo responde
        insertar(
            db, TABLA_REPORTES,
            "folio" to "CP-2026-0109", "id_usuario" to 4L, "motivo" to "No reconozco este cobro",
            "comentario" to "No estoy matriculado en este programa", "estado" to "Abierto",
            "id_agente" to null, "respuesta" to null, "fecha_creacion" to "2026-10-05T09:12"
        )
        insertar(
            db, TABLA_REPORTES,
            "folio" to "CP-2026-0111", "id_usuario" to 6L, "motivo" to "El monto no corresponde",
            "comentario" to "El monto no coincide con mi contrato", "estado" to "Resuelto",
            "id_agente" to 9L, "respuesta" to "Se revisó el contrato y el compromiso fue anulado por error de registro",
            "fecha_creacion" to "2026-09-22T11:30"
        )
    }
}
