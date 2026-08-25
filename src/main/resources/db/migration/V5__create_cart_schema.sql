CREATE TABLE cart_line_item (
    id         BIGSERIAL   PRIMARY KEY,
    session_id VARCHAR(64) NOT NULL,
    sku        VARCHAR(64) NOT NULL,
    quantity   INT         NOT NULL CHECK (quantity > 0),
    CONSTRAINT uq_cart_line_item_session_sku UNIQUE (session_id, sku)
);

CREATE INDEX idx_cart_line_item_session_id ON cart_line_item (session_id);
