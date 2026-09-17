ALTER TABLE catalog_store ADD COLUMN image_url VARCHAR(300);

UPDATE catalog_store SET image_url = '/images/stores/aurora-flagship.jpg' WHERE id = 'aurora-flagship';
UPDATE catalog_store SET image_url = '/images/stores/big-micro-machines.jpg' WHERE id = 'big-micro-machines';
UPDATE catalog_store SET image_url = '/images/stores/central-mall.jpg' WHERE id = 'central-mall';
UPDATE catalog_store SET image_url = '/images/stores/downtown-model.jpg' WHERE id = 'downtown-model';

ALTER TABLE catalog_store ALTER COLUMN image_url SET NOT NULL;
