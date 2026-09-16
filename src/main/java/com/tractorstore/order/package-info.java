/**
 * Módulo Order: confirmación y recuperación de pedidos.
 *
 * <p>Depende de {@code catalog :: application} (precio al momento de compra) y de {@code cart ::
 * domain}/{@code cart :: application} (el carrito ya resuelto, ver {@code OrderController}). No
 * expone ninguna interfaz nombrada propia todavía. Al confirmar un pedido publica {@code
 * shared.events.OrderPlaced}, que escucha el módulo cart para vaciar el carrito.
 */
package com.tractorstore.order;
