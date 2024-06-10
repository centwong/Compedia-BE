DROP TABLE IF EXISTS `interest`;
CREATE TABLE IF NOT EXISTS `interest`(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_user_id BIGINT NOT NULL,
    fk_interest_type_ids VARCHAR(255),
    fk_interest_time_ids VARCHAR(255),
    fk_interest_range_price_ids VARCHAR(255)
) ENGINE = INNODB;
CREATE INDEX `interest_index` ON `interest`(fk_user_id, fk_interest_type_ids, fk_interest_time_ids, fk_interest_range_price_ids);

DROP TABLE IF EXISTS `interest_type`;
CREATE TABLE IF NOT EXISTS `interest_type`(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(255) NOT NULL
) ENGINE = INNODB;

DROP TABLE IF EXISTS `interest_time`;
CREATE TABLE IF NOT EXISTS `interest_time`(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    `time` VARCHAR(255) NOT NULL
) ENGINE = INNODB;

DROP TABLE IF EXISTS `interest_range_price`;
CREATE TABLE IF NOT EXISTS `interest_range_price`(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    range_price VARCHAR(255) NOT NULL,
    lower_offset BIGINT,
    upper_offset BIGINT
) ENGINE = INNODB;

INSERT INTO `interest_type`(type)
VALUES
('Bisnis'),
('Desain'),
('Foto/Film'),
('Hukum'),
('IT'),
('Karya Tulis'),
('Olahraga'),
('Olimpiade'),
('Seni'),
('Teknik'),
('Public Speak'),
('Lainnya');

INSERT INTO `interest_time`(`time`)
VALUES
('Full Online'),
('Hybrid(Online & Offline'),
('Full On-Site'),
('Bisa Mengikuti Ke-3nya');

INSERT INTO `interest_range_price`(range_price, lower_offset, upper_offset)
VALUES
('Gratis!', 0, 0),
('Rp1 - Rp50.000', 1, 49999),
('Rp50000 - Rp150.000', 50000, 149999),
('Diatas Rp150.000', 150000, NULL);