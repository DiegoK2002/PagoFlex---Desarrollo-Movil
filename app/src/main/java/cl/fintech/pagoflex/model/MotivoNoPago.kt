package cl.fintech.pagoflex.model

/**
 * Razon por la que un compromiso no se puede pagar en este momento.
 * La logica decide el motivo y la pantalla lo traduce a un mensaje para la persona.
 */
enum class MotivoNoPago {
    YA_PAGADO,
    EN_REVISION,
    ANULADO,
    HAY_ANTERIOR   // existe otro compromiso de la misma empresa que vence antes y sigue sin pagar
}
