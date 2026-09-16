CREATE TABLE order_order (
    id         VARCHAR(64)  PRIMARY KEY,
    first_name VARCHAR(120) NOT NULL,
    last_name  VARCHAR(120) NOT NULL,
    store_id   VARCHAR(64)  NOT NULL,
    placed_at  TIMESTAMPTZ  NOT NULL
);

CREATE TABLE order_line (
    id         BIGSERIAL    PRIMARY KEY,
    order_id   VARCHAR(64)  NOT NULL REFERENCES order_order (id) ON DELETE CASCADE,
    position   INT          NOT NULL,
    sku        VARCHAR(64)  NOT NULL,
    quantity   INT          NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(12,2) NOT NULL CHECK (unit_price >= 0)
);

CREATE INDEX idx_order_line_order_id ON order_line (order_id);
