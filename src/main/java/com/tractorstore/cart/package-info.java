/**
 * Módulo Cart: carrito de compra por sesión anónima.
 *
 * <p>Expone {@code domain} ({@code Cart}, {@code CartLineItem}) y {@code application} ({@code
 * CartService}) al resto de módulos. Persistencia y web son internos. Escucha {@code
 * shared.events.OrderPlaced} (publicado por el módulo order al confirmar un pedido) para vaciar el
 * carrito de la sesión, en vez de que order llame a {@code CartService.clear} directamente.
 */
package com.tractorstore.cart;
