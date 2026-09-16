INSERT INTO catalog_product (id, name, description, category, price) VALUES
    ('smartfarm-titan', 'SmartFarm Titan', 'Autonomous navigation across rough terrain', 'AUTONOMOUS', 4000.00),
    ('heritage-workhorse', 'Heritage Workhorse', 'A dependable classic built for daily field work', 'CLASSIC', 5700.00),
    ('rapid-racer', 'Rapid Racer', 'Fast and nimble for smaller plots', 'CLASSIC', 7500.00),
    ('fieldmaster-classic', 'Fieldmaster Classic', 'The all-rounder for mixed terrain', 'CLASSIC', 6200.00),
    ('countryside-commander', 'Countryside Commander', 'Autonomous power for large estates', 'AUTONOMOUS', 8900.00);

INSERT INTO catalog_product_highlight (product_id, position, highlight) VALUES
    ('smartfarm-titan', 0, 'Autonomous navigation across rough terrain'),
    ('smartfarm-titan', 1, 'Solar-assisted drivetrain for all-day work'),
    ('smartfarm-titan', 2, 'Modular tool bay for field-specific attachments'),
    ('heritage-workhorse', 0, 'Rugged cast-iron frame'),
    ('heritage-workhorse', 1, 'Simple mechanical controls'),
    ('rapid-racer', 0, 'Lightweight frame'),
    ('rapid-racer', 1, 'Tight turning radius'),
    ('fieldmaster-classic', 0, 'Adjustable wheelbase'),
    ('countryside-commander', 0, 'Full-day autonomous routes'),
    ('countryside-commander', 1, 'Obstacle detection');

INSERT INTO catalog_variant (sku, product_id, position, color_name, color_hex, image_url) VALUES
    ('SF-TITAN-COPPER', 'smartfarm-titan', 0, 'Sunset Copper', '#C24914', '/images/smartfarm-titan-copper.png'),
    ('SF-TITAN-SAPPHIRE', 'smartfarm-titan', 1, 'Cosmic Sapphire', '#1B3F91', '/images/smartfarm-titan-sapphire.png'),
    ('HERITAGE-GREEN', 'heritage-workhorse', 0, 'Heritage Green', '#4C7A2E', '/images/heritage-workhorse-green.png'),
    ('RAPID-BLUE', 'rapid-racer', 0, 'Racing Blue', '#1D4FA3', '/images/rapid-racer-blue.png'),
    ('FIELDMASTER-PINK', 'fieldmaster-classic', 0, 'Blossom Pink', '#D46A9C', '/images/fieldmaster-classic-pink.png'),
    ('COMMANDER-TEAL', 'countryside-commander', 0, 'Countryside Teal', '#1F7A6C', '/images/countryside-commander-teal.png');

INSERT INTO catalog_store (id, name, address_line, city) VALUES
    ('aurora-flagship', 'Aurora Flagship Store', 'Astronaut Way 1', 'Arlington'),
    ('big-micro-machines', 'Big Micro Machines', 'Broadway 2', 'Burlington'),
    ('central-mall', 'Central Mall', 'Clown Street 3', 'Cryo'),
    ('downtown-model', 'Downtown Model Store', 'Duck Street 4', 'Davenport');
