DROP TABLE IF EXISTS `competition`;
CREATE TABLE IF NOT EXISTS `competition`(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    image_id VARCHAR(255) NOT NULL,
    image VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    fk_interest_type_ids VARCHAR(255) NOT NULL,
    price BIGINT NOT NULL,
    start_time BIGINT NOT NULL,
    deadline BIGINT NOT NULL,
    fk_interest_time_id BIGINT NOT NULL,
    fk_university_id BIGINT NOT NULL,
    competition_fee BIGINT NOT NULL,
    competition_level TEXT NOT NULL,
    description TEXT NOT NULL,
    prize_pool BIGINT NOT NULL,
    link_guidebook TEXT NOT NULL,
    link_competition_registration TEXT NOT NULL,
    publisher_name TEXT NOT NULL,
    publisher_email TEXT NOT NULL,
    publisher_instagram TEXT,
    city_name TEXT,
    competition_paid_status BIGINT,
    created_at BIGINT,
    updated_at BIGINT,
    updated_by BIGINT,
    deleted_at BIGINT,
    deleted_by BIGINT,
    is_active BOOLEAN
) ENGINE = INNODB;
CREATE INDEX `competition_index` ON `competition`(fk_interest_type_ids, image_id, fk_interest_time_id, is_active);

CREATE TABLE IF NOT EXISTS `competition_interest_type`(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    fk_competition_id BIGINT NOT NULL,
    fk_interest_type_id BIGINT NOT NULL
) ENGINE = INNODB;
CREATE INDEX `competition_interest_type_index` ON `competition_interest_type`(fk_competition_id, fk_interest_type_id);