CREATE TABLE catalog_product (
    id          VARCHAR(64)   PRIMARY KEY,
    name        VARCHAR(120)  NOT NULL,
    description VARCHAR(500)  NOT NULL,
    category    VARCHAR(20)   NOT NULL,
    price       NUMERIC(12,2) NOT NULL CHECK (price >= 0)
);

CREATE TABLE catalog_product_highlight (
    product_id VARCHAR(64)  NOT NULL REFERENCES catalog_product (id) ON DELETE CASCADE,
    position   INT          NOT NULL,
    highlight  VARCHAR(200) NOT NULL,
    PRIMARY KEY (product_id, position)
);

CREATE TABLE catalog_variant (
    sku        VARCHAR(64) PRIMARY KEY,
    product_id VARCHAR(64) NOT NULL REFERENCES catalog_product (id) ON DELETE CASCADE,
    position   INT         NOT NULL,
    color_name VARCHAR(60) NOT NULL,
    color_hex  VARCHAR(7)  NOT NULL,
    image_url  VARCHAR(300) NOT NULL
);

CREATE INDEX idx_catalog_variant_product_id ON catalog_variant (product_id);

CREATE TABLE catalog_store (
    id           VARCHAR(64)  PRIMARY KEY,
    name         VARCHAR(120) NOT NULL,
    address_line VARCHAR(200) NOT NULL,
    city         VARCHAR(120) NOT NULL
);
