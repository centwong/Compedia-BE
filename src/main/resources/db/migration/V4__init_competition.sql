DROP TABLE IF EXISTS `competition`;
CREATE TABLE IF NOT EXISTS `competition`(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    image_id VARCHAR(255) NOT NULL,
    image VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    fk_interest_type_ids VARCHAR(255) NOT NULL,
    fk_user_id BIGINT NOT NULL,
    price BIGINT NOT NULL,
    deadline BIGINT,
    fk_interest_time_id BIGINT,
    description TEXT,
    link_guidebook TEXT,
    created_at BIGINT,
    created_by BIGINT,
    updated_at BIGINT,
    updated_by BIGINT,
    deleted_at BIGINT,
    deleted_by BIGINT,
    is_active BOOLEAN
) ENGINE = INNODB;
CREATE INDEX `competition_index` ON `competition`(fk_interest_type_ids, image_id, fk_user_id, fk_interest_time_id, is_active);

CREATE TABLE IF NOT EXISTS `competition_interest_type`(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    fk_competition_id BIGINT NOT NULL,
    fk_interest_type_id BIGINT NOT NULL
) ENGINE = INNODB;
CREATE INDEX `competition_interest_type_index` ON `competition_interest_type`(fk_competition_id, fk_interest_type_id);