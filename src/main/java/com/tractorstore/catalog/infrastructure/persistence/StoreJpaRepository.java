package com.tractorstore.catalog.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface StoreJpaRepository extends JpaRepository<StoreEntity, String> {}
