package cl.fintech.pagoflex.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.fintech.pagoflex.model.RegistroUiState
import cl.fintech.pagoflex.ui.components.CampoContrasena
import cl.fintech.pagoflex.ui.components.ListaDesplegable
import cl.fintech.pagoflex.ui.components.RequisitosContrasena
import cl.fintech.pagoflex.util.Validaciones
import cl.fintech.pagoflex.viewmodel.AdministradorViewModel
import java.math.BigDecimal
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdministradorScreen(
    onSalir: () -> Unit,
    idUsuarioActual: Long,
    viewModel: AdministradorViewModel = viewModel()
) {
    val usuarios by viewModel.usuarios.collectAsStateWithLifecycle()
    val empresas by viewModel.empresas.collectAsStateWithLifecycle()
    val compromisos by viewModel.compromisos.collectAsStateWithLifecycle()
    val cargandoCompromisos by viewModel.cargandoCompromisos.collectAsStateWithLifecycle()
    val cargando by viewModel.cargando.collectAsStateWithLifecycle()
    val guardando by viewModel.guardando.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()

    var seccion by rememberSaveable { mutableStateOf("asignar") }

    var idUsuario by rememberSaveable { mutableStateOf<Long?>(null) }
    var busquedaUsuarioAsignar by rememberSaveable {
        mutableStateOf("")
    }
    var idEmpresa by rememberSaveable { mutableStateOf<Long?>(null) }
    var busquedaEmpresaAsignar by rememberSaveable {
        mutableStateOf("")
    }
    var nombre by rememberSaveable { mutableStateOf("") }
    var montoTexto by rememberSaveable { mutableStateOf("") }
    var fechaTexto by rememberSaveable { mutableStateOf("") }
    var idCompromisoEnEdicion by rememberSaveable { mutableStateOf<Long?>(null) }
    var errorFormulario by rememberSaveable { mutableStateOf<String?>(null) }

    var idUsuarioConsulta by rememberSaveable { mutableStateOf<Long?>(null) }
    var busquedaUsuarioConsulta by rememberSaveable { mutableStateOf("") }
    var idCompromisoParaAnular by rememberSaveable { mutableStateOf<Long?>(null) }

    var busquedaUsuarioGestion by rememberSaveable { mutableStateOf("") }
    var idUsuarioGestion by rememberSaveable { mutableStateOf<Long?>(null) }
    var nombresUsuario by rememberSaveable { mutableStateOf("") }
    var apellidosUsuario by rememberSaveable { mutableStateOf("") }
    var correoUsuario by rememberSaveable { mutableStateOf("") }
    var comunaUsuario by rememberSaveable { mutableStateOf("") }
    var errorUsuario by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmarActualizacionUsuario by rememberSaveable { mutableStateOf(false) }
    var idUsuarioParaDesactivar by rememberSaveable { mutableStateOf<Long?>(null) }

    var mostrarRegistroUsuario by rememberSaveable { mutableStateOf(false) }
    var nombresRegistro by rememberSaveable { mutableStateOf("") }
    var apellidosRegistro by rememberSaveable { mutableStateOf("") }
    var rutNumeroRegistro by rememberSaveable { mutableStateOf("") }
    var rutDvRegistro by rememberSaveable { mutableStateOf("") }
    var correoRegistro by rememberSaveable { mutableStateOf("") }
    var comunaRegistro by rememberSaveable { mutableStateOf("") }
    var contrasenaRegistro by remember { mutableStateOf("") }
    var repetirContrasenaRegistro by remember { mutableStateOf("") }
    var contrasenaRegistroVisible by rememberSaveable { mutableStateOf(false) }
    var repetirContrasenaRegistroVisible by rememberSaveable { mutableStateOf(false) }
    var errorRegistroUsuario by rememberSaveable { mutableStateOf<String?>(null) }
    val comunasRegistro = remember { RegistroUiState().comunas }

    var nombreEmpresaNueva by rememberSaveable { mutableStateOf("") }
    var categoriaEmpresaNueva by rememberSaveable { mutableStateOf("") }
    var errorEmpresaNueva by rememberSaveable { mutableStateOf<String?>(null) }

    var busquedaEmpresaGestion by rememberSaveable { mutableStateOf("") }
    var idEmpresaGestion by rememberSaveable { mutableStateOf<Long?>(null) }
    var nombreEmpresaEditar by rememberSaveable { mutableStateOf("") }
    var categoriaEmpresaEditar by rememberSaveable { mutableStateOf("") }
    var errorEmpresaEditar by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmarActualizacionEmpresa by rememberSaveable { mutableStateOf(false) }
    var idEmpresaParaAnular by rememberSaveable { mutableStateOf<Long?>(null) }

    val usuariosActivos = usuarios.filter { it.activo }
    val empresasActivas = empresas.filter { it.activo != false }
    val compromisosActivos = compromisos.filter { it.activo }
    val coloresCabecera = if (isSystemInDarkTheme()) {
        listOf(Color(0xFF263A91), Color(0xFF543184))
    } else {
        listOf(Color(0xFF3448D4), Color(0xFF7545BA))
    }

    val empresaGestion = empresasActivas.firstOrNull {
        it.idEmpresa == idEmpresaGestion
    }
    val textoBusquedaEmpresas = busquedaEmpresaGestion.trim()
    val empresasFiltradas = if (textoBusquedaEmpresas.length < 2) {
        emptyList()
    } else {
        empresasActivas.filter { empresa ->
            empresa.nombre.contains(textoBusquedaEmpresas, ignoreCase = true) ||
                    empresa.categoria.orEmpty().contains(textoBusquedaEmpresas, ignoreCase = true)
        }.take(20)
    }

    val usuarioSeleccionado = usuariosActivos.firstOrNull {
        it.idUsuario == idUsuario
    }

    val textoAsignar = busquedaUsuarioAsignar.trim()

    val usuariosFiltradosAsignar = if (textoAsignar.length < 2) {
        listOfNotNull(usuarioSeleccionado)
    } else {
        usuariosActivos.filter { usuario ->
            usuario.nombres.contains(textoAsignar, ignoreCase = true) ||
                    usuario.apellidos.contains(textoAsignar, ignoreCase = true) ||
                    usuario.rut.contains(textoAsignar, ignoreCase = true) ||
                    usuario.correo.contains(textoAsignar, ignoreCase = true) ||
                    "${usuario.nombres} ${usuario.apellidos}"
                        .contains(textoAsignar, ignoreCase = true)
        }.take(20)
    }

    val empresaSeleccionada = empresasActivas.firstOrNull {
        it.idEmpresa == idEmpresa
    }

    val textoEmpresaAsignar = busquedaEmpresaAsignar.trim()

    val empresasFiltradasAsignar = if (textoEmpresaAsignar.length < 2) {
        listOfNotNull(empresaSeleccionada)
    } else {
        empresasActivas.filter { empresa ->
            empresa.nombre.contains(
                textoEmpresaAsignar,
                ignoreCase = true
            ) ||
                    empresa.categoria.orEmpty().contains(
                        textoEmpresaAsignar,
                        ignoreCase = true
                    )
        }.take(20)
    }

    val busqueda = busquedaUsuarioConsulta.trim()
    val usuariosFiltrados = usuariosActivos.filter { usuario ->
        busqueda.isBlank() ||
                usuario.nombres.contains(busqueda, ignoreCase = true) ||
                usuario.apellidos.contains(busqueda, ignoreCase = true) ||
                usuario.rut.contains(busqueda, ignoreCase = true) ||
                "${usuario.nombres} ${usuario.apellidos}".contains(busqueda, ignoreCase = true)
    }

    val usuarioGestion = usuariosActivos.firstOrNull {
        it.idUsuario == idUsuarioGestion
    }
    val textoBusquedaGestion = busquedaUsuarioGestion.trim()
    val usuariosFiltradosGestion = if (textoBusquedaGestion.length < 2) {
        emptyList()
    } else {
        usuariosActivos.filter { usuario ->
            usuario.nombres.contains(textoBusquedaGestion, ignoreCase = true) ||
                    usuario.apellidos.contains(textoBusquedaGestion, ignoreCase = true) ||
                    usuario.rut.contains(textoBusquedaGestion, ignoreCase = true) ||
                    usuario.correo.contains(textoBusquedaGestion, ignoreCase = true) ||
                    "${usuario.nombres} ${usuario.apellidos}"
                        .contains(textoBusquedaGestion, ignoreCase = true)
        }.take(20)
    }

    fun limpiarFormulario() {
        idCompromisoEnEdicion = null
        idUsuario = null
        idEmpresa = null
        nombre = ""
        montoTexto = ""
        fechaTexto = ""
        busquedaUsuarioAsignar = ""
        busquedaEmpresaAsignar = ""
        errorFormulario = null
        viewModel.limpiarMensaje()
    }

    LaunchedEffect(Unit) {
        viewModel.cargarDatos()
    }

    LaunchedEffect(idUsuarioConsulta) {
        viewModel.cargarCompromisosPorUsuario(idUsuarioConsulta ?: 0L)
    }

    LaunchedEffect(mensaje) {
        if (mensaje == "Compromiso actualizado correctamente." &&
            idCompromisoEnEdicion != null
        ) {
            idCompromisoEnEdicion = null
            idUsuario = null
            idEmpresa = null
            nombre = ""
            montoTexto = ""
            fechaTexto = ""
            errorFormulario = null
            seccion = "consultar"
        }
        if (mensaje == "Usuario registrado correctamente.") {
            busquedaUsuarioGestion = Validaciones.formatearRut(
                rutNumeroRegistro,
                rutDvRegistro
            )
            idUsuarioGestion = null
            mostrarRegistroUsuario = false
            nombresRegistro = ""
            apellidosRegistro = ""
            rutNumeroRegistro = ""
            rutDvRegistro = ""
            correoRegistro = ""
            comunaRegistro = ""
            contrasenaRegistro = ""
            repetirContrasenaRegistro = ""
            errorRegistroUsuario = null
        }
        if (mensaje == "Usuario desactivado correctamente.") {
            idUsuarioGestion = null
            nombresUsuario = ""
            apellidosUsuario = ""
            correoUsuario = ""
            comunaUsuario = ""
            errorUsuario = null
        }
        if (mensaje == "Empresa registrada correctamente.") {
            nombreEmpresaNueva = ""
            categoriaEmpresaNueva = ""
            errorEmpresaNueva = null
        }
        if (mensaje == "Empresa anulada correctamente.") {
            idEmpresaGestion = null
            nombreEmpresaEditar = ""
            categoriaEmpresaEditar = ""
            errorEmpresaEditar = null
        }
    }

    idCompromisoParaAnular?.let { id ->
        AlertDialog(
            onDismissRequest = {
                if (!guardando) idCompromisoParaAnular = null
            },
            title = { Text("Confirmar anulación") },
            text = {
                Text(
                    "¿Seguro que deseas anular este compromiso? " +
                            "El compromiso quedará inactivo."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        idCompromisoParaAnular = null
                        viewModel.anularCompromiso(id)
                    },
                    enabled = !guardando
                ) {
                    Text("Anular", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { idCompromisoParaAnular = null },
                    enabled = !guardando
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (confirmarActualizacionUsuario && usuarioGestion != null) {
        AlertDialog(
            onDismissRequest = {
                if (!guardando) confirmarActualizacionUsuario = false
            },
            title = { Text("Guardar cambios del usuario") },
            text = {
                Text(
                    "¿Confirmas la actualización de los datos de " +
                            "${usuarioGestion.nombres} ${usuarioGestion.apellidos}?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmarActualizacionUsuario = false
                        viewModel.actualizarUsuario(
                            idUsuario = usuarioGestion.idUsuario,
                            nombres = nombresUsuario,
                            apellidos = apellidosUsuario,
                            correo = correoUsuario,
                            comuna = comunaUsuario
                        )
                    },
                    enabled = !guardando
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { confirmarActualizacionUsuario = false },
                    enabled = !guardando
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    idUsuarioParaDesactivar?.let { id ->
        val usuario = usuariosActivos.firstOrNull { it.idUsuario == id }
        AlertDialog(
            onDismissRequest = {
                if (!guardando) idUsuarioParaDesactivar = null
            },
            title = { Text("Confirmar desactivación") },
            text = {
                Text(
                    "¿Seguro que deseas desactivar la cuenta de " +
                            "${usuario?.nombres ?: "este"} ${usuario?.apellidos ?: "usuario"}? " +
                            "La cuenta no podrá iniciar sesión."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        idUsuarioParaDesactivar = null
                        if (id != idUsuarioActual && usuario != null) {
                            viewModel.desactivarUsuario(id)
                        }
                    },
                    enabled = !guardando && usuario != null && id != idUsuarioActual
                ) {
                    Text("Desactivar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { idUsuarioParaDesactivar = null },
                    enabled = !guardando
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (confirmarActualizacionEmpresa && empresaGestion != null) {
        AlertDialog(
            onDismissRequest = {
                if (!guardando) confirmarActualizacionEmpresa = false
            },
            title = { Text("Guardar cambios de empresa") },
            text = {
                Text("¿Confirmas la actualización de ${empresaGestion.nombre}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmarActualizacionEmpresa = false
                        viewModel.actualizarEmpresa(
                            idEmpresa = empresaGestion.idEmpresa,
                            nombre = nombreEmpresaEditar.trim(),
                            categoria = categoriaEmpresaEditar.trim()
                        )
                    },
                    enabled = !guardando
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { confirmarActualizacionEmpresa = false },
                    enabled = !guardando
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    idEmpresaParaAnular?.let { id ->
        val empresa = empresasActivas.firstOrNull { it.idEmpresa == id }
        AlertDialog(
            onDismissRequest = {
                if (!guardando) idEmpresaParaAnular = null
            },
            title = { Text("Confirmar anulación de empresa") },
            text = {
                Text(
                    "¿Seguro que deseas anular " +
                            "${empresa?.nombre ?: "esta empresa"}? " +
                            "La empresa quedará inactiva."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        idEmpresaParaAnular = null
                        viewModel.anularEmpresa(id)
                    },
                    enabled = !guardando && empresa != null
                ) {
                    Text("Anular", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { idEmpresaParaAnular = null },
                    enabled = !guardando
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administración") },
                actions = {
                    TextButton(
                        onClick = onSalir,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Salir")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = cargando || (seccion == "consultar" && cargandoCompromisos),
            onRefresh = {
                if (!guardando && !cargando && !cargandoCompromisos) {
                    viewModel.cargarDatos()
                    if (seccion == "consultar") {
                        idUsuarioConsulta?.let { viewModel.cargarCompromisosPorUsuario(it) }
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(Brush.linearGradient(coloresCabecera))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "CENTRO DE CONTROL",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFFE4E8FF),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Gestiona tu PagoFlex",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Administra compromisos, usuarios y empresas desde un solo lugar.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFDEE5FF)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricaAdministrador(
                                cantidad = usuariosActivos.size,
                                etiqueta = "Usuarios activos",
                                modifier = Modifier.weight(1f)
                            )
                            MetricaAdministrador(
                                cantidad = empresasActivas.size,
                                etiqueta = "Empresas activas",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    ScrollableTabRow(
                        selectedTabIndex = when (seccion) {
                            "asignar" -> 0
                            "consultar" -> 1
                            "usuarios" -> 2
                            "empresas" -> 3
                            else -> 0
                        },
                        modifier = Modifier.fillMaxWidth(),
                        edgePadding = 0.dp,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        divider = {}
                    ) {
                        Tab(
                            selected = seccion == "asignar",
                            onClick = { seccion = "asignar" },
                            enabled = !guardando,
                            text = { Text("Asignar", maxLines = 1, softWrap = false, fontWeight = FontWeight.SemiBold) }
                        )
                        Tab(
                            selected = seccion == "consultar",
                            onClick = { seccion = "consultar" },
                            enabled = !guardando,
                            text = { Text("Consultar", maxLines = 1, softWrap = false, fontWeight = FontWeight.SemiBold) }
                        )
                        Tab(
                            selected = seccion == "usuarios",
                            onClick = { seccion = "usuarios" },
                            enabled = !guardando,
                            text = { Text("Usuarios", maxLines = 1, softWrap = false, fontWeight = FontWeight.SemiBold) }
                        )
                        Tab(
                            selected = seccion == "empresas",
                            onClick = { seccion = "empresas" },
                            enabled = !guardando,
                            text = { Text("Empresas", maxLines = 1, softWrap = false, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                }

                if (mensaje != null) {
                    val esExito = mensaje == "Compromiso asignado correctamente." ||
                            mensaje == "Compromiso actualizado correctamente." ||
                            mensaje == "Compromiso anulado correctamente." ||
                            mensaje == "Usuario registrado correctamente." ||
                            mensaje == "Usuario actualizado correctamente." ||
                            mensaje == "Usuario desactivado correctamente." ||
                            mensaje == "Empresa registrada correctamente." ||
                            mensaje == "Empresa actualizada correctamente." ||
                            mensaje == "Empresa anulada correctamente."
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (esExito) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else {
                                MaterialTheme.colorScheme.errorContainer
                            }
                        )
                    ) {
                        Text(
                            text = mensaje!!,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (esExito) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            }
                        )
                    }
                }

                if (seccion == "asignar") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            EncabezadoSeccionAdministrador(
                                etiqueta = "COMPROMISOS",
                                titulo = if (idCompromisoEnEdicion == null) {
                                    "Asignar compromiso"
                                } else {
                                    "Editar compromiso"
                                },
                                descripcion = if (idCompromisoEnEdicion == null) {
                                    "Selecciona un usuario y una empresa para registrar un compromiso."
                                } else {
                                    "Modifica los datos y pulsa Guardar cambios."
                                }
                            )

                            if (cargando) {
                                CircularProgressIndicator()
                                Text("Cargando usuarios y empresas...")
                            } else {

                                OutlinedTextField(
                                    value = busquedaUsuarioAsignar,
                                    onValueChange = {
                                        busquedaUsuarioAsignar = it
                                        idUsuario = null
                                        errorFormulario = null
                                    },
                                    label = { Text("Buscar usuario por nombre, RUT o correo") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                ListaDesplegable(
                                    etiqueta = "Usuario",
                                    opciones = usuariosFiltradosAsignar,
                                    seleccionada = usuarioSeleccionado,
                                    textoDe = { "${it.nombres} ${it.apellidos} - ${it.rut}" },
                                    onSeleccionar = {
                                        idUsuario = it.idUsuario
                                        errorFormulario = null
                                    }
                                )

                                OutlinedTextField(
                                    value = busquedaEmpresaAsignar,
                                    onValueChange = {
                                        busquedaEmpresaAsignar = it
                                        idEmpresa = null
                                        errorFormulario = null
                                    },
                                    label = { Text("Buscar empresa por nombre o categoría") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                ListaDesplegable(
                                    etiqueta = "Empresa",
                                    opciones = empresasFiltradasAsignar,
                                    seleccionada = empresaSeleccionada,
                                    textoDe = { it.nombre },
                                    onSeleccionar = {
                                        idEmpresa = it.idEmpresa
                                        errorFormulario = null
                                    }
                                )

                                if (usuariosActivos.isEmpty()) {
                                    Text("No hay usuarios disponibles.")
                                }
                                if (empresasActivas.isEmpty()) {
                                    Text("No hay empresas disponibles.")
                                }
                            }

                            OutlinedTextField(
                                value = nombre,
                                onValueChange = {
                                    nombre = it
                                    errorFormulario = null
                                },
                                label = { Text("Nombre del compromiso") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = montoTexto,
                                onValueChange = {
                                    montoTexto = it
                                    errorFormulario = null
                                },
                                label = { Text("Monto en CLP") },
                                placeholder = { Text("Ejemplo: 25000") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = fechaTexto,
                                onValueChange = {
                                    fechaTexto = it
                                    errorFormulario = null
                                },
                                label = { Text("Fecha de vencimiento") },
                                placeholder = { Text("AAAA-MM-DD") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            if (errorFormulario != null) {
                                Text(
                                    text = errorFormulario!!,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            Button(
                                onClick = {
                                    val monto = montoTexto.trim().toBigDecimalOrNull()
                                    val fecha = runCatching {
                                        LocalDate.parse(fechaTexto.trim())
                                    }.getOrNull()

                                    when {
                                        usuarioSeleccionado == null -> {
                                            errorFormulario = "Selecciona un usuario."
                                        }
                                        empresaSeleccionada == null -> {
                                            errorFormulario = "Selecciona una empresa."
                                        }
                                        nombre.isBlank() -> {
                                            errorFormulario = "Ingresa el nombre del compromiso."
                                        }
                                        monto == null || monto <= BigDecimal.ZERO -> {
                                            errorFormulario = "Ingresa un monto válido."
                                        }
                                        fecha == null -> {
                                            errorFormulario = "Ingresa una fecha válida (AAAA-MM-DD)."
                                        }
                                        else -> {
                                            errorFormulario = null
                                            val idEdicion = idCompromisoEnEdicion
                                            if (idEdicion != null) {
                                                viewModel.actualizarCompromiso(
                                                    idCompromiso = idEdicion,
                                                    idUsuario = usuarioSeleccionado.idUsuario,
                                                    idEmpresa = empresaSeleccionada.idEmpresa,
                                                    nombre = nombre.trim(),
                                                    monto = monto,
                                                    fechaVencimiento = fecha.toString()
                                                )
                                            } else {
                                                viewModel.asignarCompromiso(
                                                    idUsuario = usuarioSeleccionado.idUsuario,
                                                    idEmpresa = empresaSeleccionada.idEmpresa,
                                                    nombre = nombre.trim(),
                                                    monto = monto,
                                                    fechaVencimiento = fecha.toString()
                                                )
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !cargando && !guardando
                            ) {
                                Text(
                                    text = when {
                                        guardando -> "Guardando..."
                                        idCompromisoEnEdicion != null -> "Guardar cambios"
                                        else -> "Asignar compromiso"
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }

                            if (idCompromisoEnEdicion != null) {
                                TextButton(
                                    onClick = { limpiarFormulario() },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !guardando
                                ) {
                                    Text("Cancelar edición")
                                }
                            }
                        }
                    }
                } else if (seccion == "consultar") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            EncabezadoSeccionAdministrador(
                                etiqueta = "SEGUIMIENTO",
                                titulo = "Consultar compromisos",
                                descripcion = "Busca un usuario para consultar, editar o anular sus compromisos."
                            )

                            if (cargando) {
                                CircularProgressIndicator()
                                Text("Cargando usuarios...")
                            } else {
                                OutlinedTextField(
                                    value = busquedaUsuarioConsulta,
                                    onValueChange = {
                                        busquedaUsuarioConsulta = it
                                        idUsuarioConsulta = null
                                    },
                                    label = { Text("Buscar usuario por nombre o RUT") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                ListaDesplegable(
                                    etiqueta = "Usuario para consultar",

                                    opciones = if (busquedaUsuarioConsulta.trim().length < 2) {
                                        listOfNotNull(
                                            usuariosActivos.firstOrNull {
                                                it.idUsuario == idUsuarioConsulta
                                            }
                                        )
                                    } else {
                                        val texto = busquedaUsuarioConsulta.trim()

                                        usuariosActivos.filter { usuario ->
                                            usuario.nombres.contains(texto, ignoreCase = true) ||
                                                    usuario.apellidos.contains(texto, ignoreCase = true) ||
                                                    usuario.rut.contains(texto, ignoreCase = true) ||
                                                    usuario.correo.contains(texto, ignoreCase = true) ||
                                                    "${usuario.nombres} ${usuario.apellidos}"
                                                        .contains(texto, ignoreCase = true)
                                        }.take(20)
                                    },

                                    seleccionada = usuariosActivos.firstOrNull {
                                        it.idUsuario == idUsuarioConsulta
                                    },
                                    textoDe = { "${it.nombres} ${it.apellidos} - ${it.rut}" },
                                    onSeleccionar = {
                                        idUsuarioConsulta = it.idUsuario
                                    }
                                )
                            }


                            if (idUsuarioConsulta == null) {
                                Text("Selecciona un usuario para consultar sus compromisos.")
                            } else {
                                Text("Compromisos activos: ${compromisosActivos.size}")

                                if (cargandoCompromisos) {
                                    CircularProgressIndicator()
                                    Text("Cargando compromisos...")
                                } else if (compromisosActivos.isEmpty()) {
                                    Text("Este usuario no tiene compromisos activos.")
                                } else {
                                    compromisosActivos
                                        .sortedByDescending { it.idCompromiso }
                                        .forEach { compromiso ->
                                            val usuario = usuarios.firstOrNull {
                                                it.idUsuario == compromiso.idUsuario
                                            }
                                            val empresa = empresas.firstOrNull {
                                                it.idEmpresa == compromiso.idEmpresa
                                            }

                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(20.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                                                ),
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(16.dp),
                                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Text(
                                                        text = compromiso.nombre,
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                    Text(
                                                        "Usuario: " + (
                                                                usuario?.let {
                                                                    "${it.nombres} ${it.apellidos}"
                                                                } ?: "ID ${compromiso.idUsuario}"
                                                                )
                                                    )
                                                    Text(
                                                        "Empresa: " +
                                                                (empresa?.nombre ?: "ID ${compromiso.idEmpresa}")
                                                    )
                                                    Text(
                                                        text = "${compromiso.monto} CLP",
                                                        style = MaterialTheme.typography.headlineSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    Text("Vencimiento: ${compromiso.fechaVencimiento}")
                                                    Text(
                                                        text = "Estado: ${compromiso.estado}",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        color = MaterialTheme.colorScheme.secondary
                                                    )

                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        TextButton(
                                                            onClick = {
                                                                idCompromisoEnEdicion = compromiso.idCompromiso
                                                                idUsuario = compromiso.idUsuario
                                                                busquedaUsuarioAsignar = ""
                                                                idEmpresa = compromiso.idEmpresa
                                                                busquedaEmpresaAsignar = ""
                                                                nombre = compromiso.nombre
                                                                montoTexto = compromiso.monto.toPlainString()
                                                                fechaTexto = compromiso.fechaVencimiento
                                                                errorFormulario = null
                                                                viewModel.limpiarMensaje()
                                                                seccion = "asignar"
                                                            },
                                                            enabled = !guardando
                                                        ) {
                                                            Text("Editar")
                                                        }

                                                        TextButton(
                                                            onClick = {
                                                                idCompromisoParaAnular = compromiso.idCompromiso
                                                            },
                                                            enabled = !guardando
                                                        ) {
                                                            Text(
                                                                "Anular",
                                                                color = MaterialTheme.colorScheme.error
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                }
                            }
                        }
                    }
                } else if (seccion == "usuarios") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            EncabezadoSeccionAdministrador(
                                etiqueta = "PERSONAS",
                                titulo = "Gestión de usuarios",
                                descripcion = "Registra nuevas cuentas o actualiza los datos de usuarios existentes."
                            )

                            Button(
                                onClick = {
                                    mostrarRegistroUsuario = !mostrarRegistroUsuario
                                    errorRegistroUsuario = null
                                    viewModel.limpiarMensaje()
                                    if (!mostrarRegistroUsuario) {
                                        contrasenaRegistro = ""
                                        repetirContrasenaRegistro = ""
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !guardando && !cargando
                            ) {
                                Text(
                                    text = if (mostrarRegistroUsuario) {
                                        "Volver a gestionar usuarios"
                                    } else {
                                        "Registrar nuevo usuario"
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }

                            if (mostrarRegistroUsuario) {
                                Text(
                                    text = "Registrar usuario",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = "La cuenta nueva tendrá el rol Usuario final.",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                OutlinedTextField(
                                    value = nombresRegistro,
                                    onValueChange = {
                                        nombresRegistro = it.take(40)
                                        errorRegistroUsuario = null
                                        viewModel.limpiarMensaje()
                                    },
                                    label = { Text("Nombres") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    enabled = !guardando
                                )

                                OutlinedTextField(
                                    value = apellidosRegistro,
                                    onValueChange = {
                                        apellidosRegistro = it.take(40)
                                        errorRegistroUsuario = null
                                        viewModel.limpiarMensaje()
                                    },
                                    label = { Text("Apellidos") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    enabled = !guardando
                                )

                                OutlinedTextField(
                                    value = rutNumeroRegistro,
                                    onValueChange = {
                                        rutNumeroRegistro = it.filter { c -> c.isDigit() }.take(8)
                                        errorRegistroUsuario = null
                                        viewModel.limpiarMensaje()
                                    },
                                    label = { Text("RUT sin puntos ni guion") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    enabled = !guardando
                                )

                                OutlinedTextField(
                                    value = rutDvRegistro,
                                    onValueChange = {
                                        rutDvRegistro = it.filter { c ->
                                            c.isDigit() || c.uppercaseChar() == 'K'
                                        }.take(1).uppercase()
                                        errorRegistroUsuario = null
                                        viewModel.limpiarMensaje()
                                    },
                                    label = { Text("Dígito verificador (DV)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    enabled = !guardando
                                )

                                OutlinedTextField(
                                    value = correoRegistro,
                                    onValueChange = {
                                        correoRegistro = it.trim().take(60)
                                        errorRegistroUsuario = null
                                        viewModel.limpiarMensaje()
                                    },
                                    label = { Text("Correo electrónico") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    singleLine = true,
                                    enabled = !guardando
                                )

                                ListaDesplegable(
                                    etiqueta = "Comuna",
                                    opciones = comunasRegistro,
                                    seleccionada = comunaRegistro.ifBlank { null },
                                    textoDe = { it },
                                    onSeleccionar = {
                                        comunaRegistro = it
                                        errorRegistroUsuario = null
                                        viewModel.limpiarMensaje()
                                    }
                                )

                                CampoContrasena(
                                    valor = contrasenaRegistro,
                                    onValorCambia = {
                                        contrasenaRegistro = it.take(30)
                                        errorRegistroUsuario = null
                                        viewModel.limpiarMensaje()
                                    },
                                    etiqueta = "Contraseña",
                                    visible = contrasenaRegistroVisible,
                                    onAlternarVisible = {
                                        contrasenaRegistroVisible = !contrasenaRegistroVisible
                                    }
                                )

                                RequisitosContrasena(
                                    requisitos = Validaciones.requisitosContrasena(
                                        contrasena = contrasenaRegistro,
                                        correo = correoRegistro,
                                        nombres = "$nombresRegistro $apellidosRegistro"
                                    )
                                )

                                CampoContrasena(
                                    valor = repetirContrasenaRegistro,
                                    onValorCambia = {
                                        repetirContrasenaRegistro = it.take(30)
                                        errorRegistroUsuario = null
                                        viewModel.limpiarMensaje()
                                    },
                                    etiqueta = "Repetir contraseña",
                                    visible = repetirContrasenaRegistroVisible,
                                    onAlternarVisible = {
                                        repetirContrasenaRegistroVisible = !repetirContrasenaRegistroVisible
                                    }
                                )

                                if (errorRegistroUsuario != null) {
                                    Text(
                                        text = errorRegistroUsuario!!,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }

                                Button(
                                    onClick = {
                                        errorRegistroUsuario = listOf(
                                            Validaciones.validarNombre(nombresRegistro, "nombres"),
                                            Validaciones.validarNombre(apellidosRegistro, "apellidos"),
                                            Validaciones.validarRut(rutNumeroRegistro, rutDvRegistro),
                                            Validaciones.validarCorreo(correoRegistro),
                                            Validaciones.validarComuna(comunaRegistro),
                                            Validaciones.validarContrasena(
                                                contrasena = contrasenaRegistro,
                                                correo = correoRegistro,
                                                nombres = "$nombresRegistro $apellidosRegistro"
                                            ),
                                            Validaciones.validarRepeticion(
                                                contrasenaRegistro,
                                                repetirContrasenaRegistro
                                            )
                                        ).firstOrNull { it != null }

                                        if (errorRegistroUsuario == null) {
                                            viewModel.registrarUsuario(
                                                rut = Validaciones.formatearRut(
                                                    rutNumeroRegistro,
                                                    rutDvRegistro
                                                ),
                                                nombres = Validaciones.normalizarNombre(nombresRegistro),
                                                apellidos = Validaciones.normalizarNombre(apellidosRegistro),
                                                correo = correoRegistro.trim(),
                                                contrasena = contrasenaRegistro,
                                                comuna = comunaRegistro
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !guardando && !cargando
                                ) {
                                    Text(
                                        text = if (guardando) "Registrando..." else "Registrar usuario",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                Text(
                                    text = "Busca una cuenta y modifica sus datos de contacto. " +
                                            "El RUT, el rol y la contraseña no se cambian aquí.",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                if (cargando) {
                                    CircularProgressIndicator()
                                    Text("Cargando usuarios...")
                                } else {
                                    OutlinedTextField(
                                        value = busquedaUsuarioGestion,
                                        onValueChange = {
                                            busquedaUsuarioGestion = it
                                            idUsuarioGestion = null
                                            errorUsuario = null
                                        },
                                        label = { Text("Buscar por nombre, RUT o correo") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    if (textoBusquedaGestion.length < 2) {
                                        Text("Escribe al menos 2 caracteres para buscar.")
                                    }

                                    ListaDesplegable(
                                        etiqueta = "Seleccionar usuario",
                                        opciones = usuariosFiltradosGestion,
                                        seleccionada = usuarioGestion,
                                        textoDe = { "${it.nombres} ${it.apellidos} - ${it.rut}" },
                                        onSeleccionar = { usuario ->
                                            idUsuarioGestion = usuario.idUsuario
                                            nombresUsuario = usuario.nombres
                                            apellidosUsuario = usuario.apellidos
                                            correoUsuario = usuario.correo
                                            comunaUsuario = usuario.comuna
                                            errorUsuario = null
                                            viewModel.limpiarMensaje()
                                        }
                                    )
                                }

                                if (usuarioGestion == null) {
                                    Text("Selecciona un usuario para editar sus datos.")
                                } else {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                                        ),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Text(
                                                "Editar datos del usuario",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Text("RUT: ${usuarioGestion.rut}")
                                            Text("Rol: ${usuarioGestion.rol}")

                                            OutlinedTextField(
                                                value = nombresUsuario,
                                                onValueChange = {
                                                    nombresUsuario = it
                                                    errorUsuario = null
                                                },
                                                label = { Text("Nombres") },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                            )
                                            OutlinedTextField(
                                                value = apellidosUsuario,
                                                onValueChange = {
                                                    apellidosUsuario = it
                                                    errorUsuario = null
                                                },
                                                label = { Text("Apellidos") },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                            )
                                            OutlinedTextField(
                                                value = correoUsuario,
                                                onValueChange = {
                                                    correoUsuario = it
                                                    errorUsuario = null
                                                },
                                                label = { Text("Correo electrónico") },
                                                modifier = Modifier.fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Email
                                                ),
                                                singleLine = true
                                            )
                                            OutlinedTextField(
                                                value = comunaUsuario,
                                                onValueChange = {
                                                    comunaUsuario = it
                                                    errorUsuario = null
                                                },
                                                label = { Text("Comuna") },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                            )

                                            if (errorUsuario != null) {
                                                Text(
                                                    text = errorUsuario!!,
                                                    color = MaterialTheme.colorScheme.error
                                                )
                                            }

                                            Button(
                                                onClick = {
                                                    errorUsuario = when {
                                                        nombresUsuario.isBlank() -> "Ingresa los nombres."
                                                        apellidosUsuario.isBlank() -> "Ingresa los apellidos."
                                                        correoUsuario.isBlank() ||
                                                                !correoUsuario.contains("@") ->
                                                            "Ingresa un correo válido."
                                                        comunaUsuario.isBlank() -> "Ingresa la comuna."
                                                        else -> null
                                                    }
                                                    if (errorUsuario == null) {
                                                        confirmarActualizacionUsuario = true
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                enabled = !guardando && !cargando
                                            ) {
                                                Text(
                                                    text = if (guardando) "Guardando..." else "Guardar cambios",
                                                    modifier = Modifier.fillMaxWidth(),
                                                    textAlign = TextAlign.Center
                                                )
                                            }

                                            if (usuarioGestion.idUsuario != idUsuarioActual) {
                                                TextButton(
                                                    onClick = {
                                                        idUsuarioParaDesactivar = usuarioGestion.idUsuario
                                                    },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    enabled = !guardando && !cargando
                                                ) {
                                                    Text(
                                                        text = "Desactivar usuario",
                                                        color = MaterialTheme.colorScheme.error
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (seccion == "empresas") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            EncabezadoSeccionAdministrador(
                                etiqueta = "COMERCIOS",
                                titulo = "Registrar empresa",
                                descripcion = "Ingresa el nombre y la categoría de la nueva empresa."
                            )

                            OutlinedTextField(
                                value = nombreEmpresaNueva,
                                onValueChange = {
                                    nombreEmpresaNueva = it
                                    errorEmpresaNueva = null
                                    viewModel.limpiarMensaje()
                                },
                                label = { Text("Nombre de la empresa") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = categoriaEmpresaNueva,
                                onValueChange = {
                                    categoriaEmpresaNueva = it
                                    errorEmpresaNueva = null
                                    viewModel.limpiarMensaje()
                                },
                                label = { Text("Categoría") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            if (errorEmpresaNueva != null) {
                                Text(
                                    text = errorEmpresaNueva!!,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            Button(
                                onClick = {
                                    errorEmpresaNueva = when {
                                        nombreEmpresaNueva.isBlank() -> "Ingresa el nombre de la empresa."
                                        categoriaEmpresaNueva.isBlank() -> "Ingresa la categoría."
                                        nombreEmpresaNueva.trim().length > 100 ->
                                            "El nombre no puede superar 100 caracteres."
                                        categoriaEmpresaNueva.trim().length > 100 ->
                                            "La categoría no puede superar 100 caracteres."
                                        else -> null
                                    }

                                    if (errorEmpresaNueva == null) {
                                        viewModel.crearEmpresa(
                                            nombre = nombreEmpresaNueva.trim(),
                                            categoria = categoriaEmpresaNueva.trim()
                                        )
                                    }
                                },
                                enabled = !guardando && !cargando,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (guardando) "Guardando..." else "Registrar empresa",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Text(
                                text = "Editar empresa",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Busca una empresa existente para modificar su nombre o categoría.",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            OutlinedTextField(
                                value = busquedaEmpresaGestion,
                                onValueChange = {
                                    busquedaEmpresaGestion = it
                                    idEmpresaGestion = null
                                    errorEmpresaEditar = null
                                    viewModel.limpiarMensaje()
                                },
                                label = { Text("Buscar empresa por nombre o categoría") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            if (textoBusquedaEmpresas.length < 2) {
                                Text("Escribe al menos 2 caracteres para buscar.")
                            }

                            ListaDesplegable(
                                etiqueta = "Seleccionar empresa",
                                opciones = empresasFiltradas,
                                seleccionada = empresaGestion,
                                textoDe = { "${it.nombre} - ${it.categoria.orEmpty()}" },
                                onSeleccionar = { empresa ->
                                    idEmpresaGestion = empresa.idEmpresa
                                    nombreEmpresaEditar = empresa.nombre
                                    categoriaEmpresaEditar = empresa.categoria.orEmpty()
                                    errorEmpresaEditar = null
                                    viewModel.limpiarMensaje()
                                }
                            )

                            if (empresaGestion == null) {
                                Text("Selecciona una empresa para editarla.")
                            } else {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = "Datos de la empresa",
                                            style = MaterialTheme.typography.titleMedium
                                        )

                                        OutlinedTextField(
                                            value = nombreEmpresaEditar,
                                            onValueChange = {
                                                nombreEmpresaEditar = it
                                                errorEmpresaEditar = null
                                            },
                                            label = { Text("Nombre") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = categoriaEmpresaEditar,
                                            onValueChange = {
                                                categoriaEmpresaEditar = it
                                                errorEmpresaEditar = null
                                            },
                                            label = { Text("Categoría") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )

                                        if (errorEmpresaEditar != null) {
                                            Text(
                                                text = errorEmpresaEditar!!,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                errorEmpresaEditar = when {
                                                    nombreEmpresaEditar.isBlank() ->
                                                        "Ingresa el nombre de la empresa."
                                                    categoriaEmpresaEditar.isBlank() ->
                                                        "Ingresa la categoría."
                                                    nombreEmpresaEditar.trim().length > 100 ->
                                                        "El nombre no puede superar 100 caracteres."
                                                    categoriaEmpresaEditar.trim().length > 100 ->
                                                        "La categoría no puede superar 100 caracteres."
                                                    else -> null
                                                }
                                                if (errorEmpresaEditar == null) {
                                                    confirmarActualizacionEmpresa = true
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            enabled = !guardando && !cargando
                                        ) {
                                            Text(
                                                text = if (guardando) "Guardando..." else "Guardar cambios",
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                        TextButton(
                                            onClick = {
                                                idEmpresaParaAnular = empresaGestion.idEmpresa
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            enabled = !guardando && !cargando
                                        ) {
                                            Text(
                                                text = "Anular empresa",
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricaAdministrador(
    cantidad: Int,
    etiqueta: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = cantidad.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFFE5EAFF)
        )
    }
}

@Composable
private fun EncabezadoSeccionAdministrador(
    etiqueta: String,
    titulo: String,
    descripcion: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = descripcion,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
