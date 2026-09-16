package com.tractorstore.shared.events;

/**
 * Evento de dominio publicado por el módulo order al confirmar un pedido.
 *
 * <p>Vive en {@code shared} (módulo abierto) y no en {@code order.domain} para no crear una
 * dependencia cíclica: order ya depende de cart (lee el carrito para armar el pedido), así que si
 * el tipo de evento viviera en order, cart tendría que depender de vuelta de order para escucharlo.
 */
public record OrderPlaced(String orderId, String sessionId) {}
