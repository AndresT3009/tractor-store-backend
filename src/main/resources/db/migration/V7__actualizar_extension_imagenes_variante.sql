-- Las imágenes reales entregadas para el catálogo son .jpg, no .png como asumía el seed original
-- (V2). No se editan las filas de V2 directamente (las migraciones ya aplicadas son inmutables);
-- se actualiza el valor con un UPDATE en una migración nueva.
UPDATE catalog_variant SET image_url = '/images/smartfarm-titan-copper.jpg' WHERE sku = 'SF-TITAN-COPPER';
UPDATE catalog_variant SET image_url = '/images/smartfarm-titan-sapphire.jpg' WHERE sku = 'SF-TITAN-SAPPHIRE';
UPDATE catalog_variant SET image_url = '/images/heritage-workhorse-green.jpg' WHERE sku = 'HERITAGE-GREEN';
UPDATE catalog_variant SET image_url = '/images/rapid-racer-blue.jpg' WHERE sku = 'RAPID-BLUE';
UPDATE catalog_variant SET image_url = '/images/fieldmaster-classic-pink.jpg' WHERE sku = 'FIELDMASTER-PINK';
UPDATE catalog_variant SET image_url = '/images/countryside-commander-teal.jpg' WHERE sku = 'COMMANDER-TEAL';
