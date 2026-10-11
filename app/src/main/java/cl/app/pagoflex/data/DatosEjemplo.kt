package cl.app.pagoflex.data

import cl.app.pagoflex.model.Compromiso
import cl.app.pagoflex.model.Empresa
import cl.app.pagoflex.model.EstadoCompromiso.ANULADO
import cl.app.pagoflex.model.EstadoCompromiso.EN_REVISION
import cl.app.pagoflex.model.EstadoCompromiso.PAGADO
import cl.app.pagoflex.model.EstadoCompromiso.PENDIENTE
import cl.app.pagoflex.model.EstadoCompromiso.VENCIDO
import cl.app.pagoflex.model.Pago
import cl.app.pagoflex.model.Rol
import cl.app.pagoflex.model.Usuario
import java.time.LocalDate
import java.time.LocalDateTime

// =====================================================================
//  DatosEjemplo.kt  —  "API SIMULADA" CON DATOS FICTICIOS
//  ---------------------------------------------------------------------

object DatosEjemplo {

    val FECHA_REFERENCIA: LocalDate = LocalDate.of(2026, 10, 6)

    val empresas = listOf(
        Empresa("EC-01", "Altos del Maipo", "Inmobiliaria Altos del Maipo SpA", "Inmobiliaria", 8500),
        Empresa("EC-02", "Colegio Bosque Nativo", "Corporación Educacional Bosque Nativo", "Educación escolar", 5000),
        Empresa("EC-03", "Crédito Andino", "Crédito Andino S.A.", "Crédito de consumo", 12000),
        Empresa("EC-04", "Horizonte Sur", "Seguros Horizonte Sur S.A.", "Seguros", 3000),
        Empresa("EC-05", "Manos Abiertas", "Fundación Manos Abiertas", "ONG", 0),
        Empresa("EC-06", "Club Los Halcones", "Club Deportivo Los Halcones", "Club deportivo", 2000),
        Empresa("EC-07", "Instituto Cordillera", "Instituto Técnico Cordillera SpA", "Educación superior", 6000),
        Empresa("EC-08", "Plaza Oriente", "Arriendos Plaza Oriente Ltda.", "Administración de arriendos", 7500)
    )

    val usuarios = listOf(
        // ----- Usuarios finales (R-01) -----
        Usuario("UF-01", "15.834.207-3", "Camila Andrea", "Rojas Peña", "crojas@correo.ejemplo", Rol.USUARIO_FINAL, comuna = "Maipú", diasAnticipacion = 3),
        Usuario("UF-02", "12.476.318-5", "Luis Alberto", "Muñoz Tapia", "lmunoz@correo.ejemplo", Rol.USUARIO_FINAL, comuna = "Puente Alto", diasAnticipacion = 1),
        Usuario("UF-03", "6.893.541-5", "Rosa Elena", "Contreras Díaz", "rcontreras@correo.ejemplo", Rol.USUARIO_FINAL, comuna = "San Miguel", diasAnticipacion = 5),
        Usuario("UF-04", "21.045.873-5", "Matías Ignacio", "Soto Vergara", "msoto@correo.ejemplo", Rol.USUARIO_FINAL, comuna = "La Florida", diasAnticipacion = null),
        Usuario("UF-05", "17.562.904-1", "Fernanda Paz", "Godoy Lagos", "fgodoy@correo.ejemplo", Rol.USUARIO_FINAL, comuna = "Ñuñoa", diasAnticipacion = 3),
        Usuario("UF-06", "10.318.265-4", "Héctor Manuel", "Pizarro Campos", "hpizarro@correo.ejemplo", Rol.USUARIO_FINAL, comuna = "Quilicura", diasAnticipacion = 3),
        Usuario("UF-07", "19.287.430-0", "Javiera Constanza", "Reyes Molina", "jreyes@correo.ejemplo", Rol.USUARIO_FINAL, comuna = "San Bernardo", diasAnticipacion = 1),
        Usuario("UF-08", "18.704.396-4", "Benjamín Esteban", "Araya Fuentes", "baraya@correo.ejemplo", Rol.USUARIO_FINAL, comuna = "Estación Central", diasAnticipacion = 3),
        // ----- Agentes PagoFlex (R-02) -----
        Usuario("AG-01", "16.248.317-K", "Andrés Felipe", "Lagos Meza", "alagos@pagoflex.ejemplo", Rol.AGENTE, area = "Soporte"),
        Usuario("AG-02", "18.352.091-1", "Daniela Sofía", "Ortiz Bravo", "dortiz@pagoflex.ejemplo", Rol.AGENTE, area = "Soporte"),
        Usuario("AG-03", "13.907.264-2", "Ricardo José", "Salinas Cáceres", "rsalinas@pagoflex.ejemplo", Rol.AGENTE, area = "Soporte", activo = false),
        Usuario("AG-04", "17.781.435-0", "Valentina Ignacia", "Paredes Silva", "vparedes@pagoflex.ejemplo", Rol.AGENTE, area = "Comercial"),
        Usuario("AG-05", "19.564.028-9", "Tomás Alejandro", "Vidal Rojas", "tvidal@pagoflex.ejemplo", Rol.AGENTE, area = "Comercial"),
        // ----- Ejecutivos de empresas clientes (R-03) -----
        Usuario("EJ-01", "14.629.853-2", "Paula Andrea", "Saavedra Núñez", "psaavedra@altosmaipo.ejemplo", Rol.EJECUTIVO, codigoEmpresa = "EC-01", cargo = "Ejecutiva de cobranza"),
        Usuario("EJ-02", "11.853.720-3", "Marco Antonio", "Fuentes Riquelme", "mfuentes@bosquenativo.ejemplo", Rol.EJECUTIVO, codigoEmpresa = "EC-02", cargo = "Encargado de finanzas"),
        Usuario("EJ-03", "15.370.946-7", "Claudia Beatriz", "Henríquez Soto", "chenriquez@creditoandino.ejemplo", Rol.EJECUTIVO, codigoEmpresa = "EC-03", cargo = "Jefa de cobranza"),
        Usuario("EJ-04", "17.095.482-3", "Felipe Ignacio", "Morales Tapia", "fmorales@creditoandino.ejemplo", Rol.EJECUTIVO, codigoEmpresa = "EC-03", cargo = "Ejecutivo de cobranza"),
        Usuario("EJ-05", "9.846.217-1", "Gloria Inés", "Carrasco Vega", "gcarrasco@loshalcones.ejemplo", Rol.EJECUTIVO, codigoEmpresa = "EC-06", cargo = "Tesorera"),
        Usuario("EJ-06", "18.930.571-0", "Sebastián Andrés", "Núñez Olivares", "snunez@itcordillera.ejemplo", Rol.EJECUTIVO, codigoEmpresa = "EC-07", cargo = "Analista de finanzas"),
        Usuario("EJ-07", "12.584.396-4", "Mónica Patricia", "Leiva Castro", "mleiva@plazaoriente.ejemplo", Rol.EJECUTIVO, codigoEmpresa = "EC-08", cargo = "Administradora de cobranza")
    )

    // Compromiso(folio, usuario, empresa, concepto, cuota, totalCuotas, monto, vencimiento, estado)
    val compromisos = listOf(
        Compromiso("CP-2026-0101", "UF-01", "EC-08", "Arriendo departamento octubre", 10, 12, 520000, LocalDate.of(2026, 10, 10), PENDIENTE),
        Compromiso("CP-2026-0102", "UF-01", "EC-06", "Cuota social octubre", 10, 12, 15000, LocalDate.of(2026, 10, 5), VENCIDO),
        Compromiso("CP-2026-0103", "UF-01", "EC-05", "Aporte mensual octubre", null, null, 10000, LocalDate.of(2026, 10, 15), PENDIENTE),
        Compromiso("CP-2026-0104", "UF-01", "EC-03", "Crédito de consumo", 7, 24, 86400, LocalDate.of(2026, 9, 25), PAGADO),
        Compromiso("CP-2026-0105", "UF-01", "EC-03", "Crédito de consumo", 8, 24, 86400, LocalDate.of(2026, 10, 25), PENDIENTE),
        Compromiso("CP-2026-0106", "UF-02", "EC-01", "Cuota pie departamento", 14, 36, 310000, LocalDate.of(2026, 9, 30), VENCIDO),
        Compromiso("CP-2026-0107", "UF-02", "EC-01", "Cuota pie departamento", 15, 36, 310000, LocalDate.of(2026, 10, 30), PENDIENTE),
        Compromiso("CP-2026-0108", "UF-03", "EC-04", "Seguro de hogar octubre", 10, 12, 18900, LocalDate.of(2026, 10, 1), PAGADO),
        Compromiso("CP-2026-0109", "UF-04", "EC-07", "Arancel octubre", 8, 10, 142000, LocalDate.of(2026, 10, 5), EN_REVISION),
        Compromiso("CP-2026-0110", "UF-05", "EC-02", "Mensualidad octubre", 8, 10, 185000, LocalDate.of(2026, 10, 10), PENDIENTE),
        Compromiso("CP-2026-0111", "UF-06", "EC-03", "Crédito de consumo", 19, 36, 54200, LocalDate.of(2026, 9, 20), ANULADO),
        Compromiso("CP-2026-0112", "UF-07", "EC-06", "Cuota social octubre", 10, 12, 15000, LocalDate.of(2026, 10, 5), PAGADO)
    )

    // Pago(comprobante, folio, usuario, empresa, concepto, fecha y hora, monto pagado, canal, ¿a tiempo?)
    val pagos = listOf(
        Pago("CPR-000377", "CP-2026-0072", "UF-02", "EC-01", "Cuota pie departamento 13/36", LocalDateTime.of(2026, 9, 2, 21, 14), 318500, "Agente conversacional", false),
        Pago("CPR-000398", "CP-2026-0089", "UF-01", "EC-08", "Arriendo departamento septiembre", LocalDateTime.of(2026, 9, 9, 13, 5), 520000, "Portal de pagos", true),
        Pago("CPR-000402", "CP-2026-0090", "UF-01", "EC-06", "Cuota social septiembre", LocalDateTime.of(2026, 9, 7, 18, 40), 17000, "Portal de pagos", false),
        Pago("CPR-000405", "CP-2026-0091", "UF-01", "EC-05", "Aporte mensual septiembre", LocalDateTime.of(2026, 9, 15, 8, 22), 10000, "Agente conversacional", true),
        Pago("CPR-000447", "CP-2026-0098", "UF-05", "EC-02", "Mensualidad septiembre", LocalDateTime.of(2026, 9, 10, 22, 31), 185000, "Portal de pagos", true),
        Pago("CPR-000451", "CP-2026-0104", "UF-01", "EC-03", "Crédito de consumo 7/24", LocalDateTime.of(2026, 9, 24, 10, 42), 86400, "Portal de pagos", true),
        Pago("CPR-000452", "CP-2026-0108", "UF-03", "EC-04", "Seguro de hogar octubre", LocalDateTime.of(2026, 9, 30, 19, 5), 18900, "Agente conversacional", true),
        Pago("CPR-000453", "CP-2026-0112", "UF-07", "EC-06", "Cuota social octubre", LocalDateTime.of(2026, 10, 4, 8, 15), 15000, "App", true)
    )
}
