DROP TABLE IF EXISTS `persona`;
CREATE TABLE IF NOT EXISTS `persona`(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fk_user_id BIGINT NOT NULL,
    persona VARCHAR(100) NOT NULL
) ENGINE = INNODB;
CREATE INDEX `persona_index` ON `persona`(fk_user_id);