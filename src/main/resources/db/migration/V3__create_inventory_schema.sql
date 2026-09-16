CREATE TABLE inventory_stock (
    sku                 VARCHAR(64) PRIMARY KEY,
    quantity_available  INT         NOT NULL CHECK (quantity_available >= 0)
);
