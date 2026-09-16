package com.tractorstore.cart.infrastructure.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface CartLineItemJpaRepository extends JpaRepository<CartLineItemEntity, Long> {

  List<CartLineItemEntity> findBySessionId(String sessionId);

  void deleteBySessionId(String sessionId);
}
