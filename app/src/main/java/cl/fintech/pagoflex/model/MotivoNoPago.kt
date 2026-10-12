package cl.fintech.pagoflex.model

// =====================================================================
//  MotivoNoPago.kt  —  POR QUÉ NO SE PUEDE PAGAR UN COMPROMISO
//  ---------------------------------------------------------------------
//  El ViewModel decide el motivo y la pantalla lo traduce a
//  un texto con strings.xml.
//  Orden de pago y En revisión
// =====================================================================

enum class MotivoNoPago {
    YA_PAGADO,
    EN_REVISION,
    ANULADO,
    HAY_ANTERIOR      // hay otro compromiso de la misma empresa que vence antes
}
