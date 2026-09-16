/**
 * Utilidades transversales (CORS, resolución de sesión, reloj) usadas por los módulos de negocio.
 *
 * <p>Se marca como {@code OPEN} a propósito: no es un módulo de dominio con reglas de negocio que
 * proteger, sino infraestructura compartida. Un módulo de negocio (catalog, inventory, cart, order)
 * nunca debería marcarse así.
 */
@org.springframework.modulith.ApplicationModule(
    type = org.springframework.modulith.ApplicationModule.Type.OPEN)
package com.tractorstore.shared;
