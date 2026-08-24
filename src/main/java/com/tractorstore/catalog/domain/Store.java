package com.tractorstore.catalog.domain;

import java.util.Objects;

/**
 * Una tienda física donde recoger un pedido.
 *
 * @param id identificador único de la tienda, referenciado por Order sin acoplarse a esta clase
 * @param name nombre comercial de la tienda
 * @param addressLine dirección (calle y número)
 * @param city ciudad donde está ubicada
 */
public record Store(String id, String name, String addressLine, String city) {

  public Store {
    Objects.requireNonNull(id, "id no puede ser null");
    Objects.requireNonNull(name, "name no puede ser null");
    Objects.requireNonNull(addressLine, "addressLine no puede ser null");
    Objects.requireNonNull(city, "city no puede ser null");
    if (id.isBlank() || name.isBlank() || city.isBlank()) {
      throw new IllegalArgumentException("id, name y city no pueden estar vacíos");
    }
  }
}
