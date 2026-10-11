package cl.app.pagoflex.model

// =====================================================================
//  MotivoNoPago.kt  —  POR QUÉ NO SE PUEDE PAGAR UN COMPROMISO
//  ---------------------------------------------------------------------
//  El ViewModel decide el motivo (la lógica) y la pantalla lo traduce a
//  un texto claro con strings.xml (la vista).
//  Reglas: RN-04 (orden de pago) y RN-09 (en revisión). No hay pago
//  anulado ni repetido.
// =====================================================================

enum class MotivoNoPago {
    YA_PAGADO,
    EN_REVISION,
    ANULADO,
    HAY_ANTERIOR      // hay otro compromiso de la misma empresa que vence antes
}
