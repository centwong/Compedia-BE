DROP TABLE IF EXISTS `mentor_data`;
CREATE TABLE IF NOT EXISTS `mentor_data`(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fk_user_id BIGINT NOT NULL,
    achievement TEXT NOT NULL,
    description TEXT NOT NULL,
    linkedin_url TEXT NOT NULL,
    instagram_url TEXT NOT NULL,
    approval_status BIGINT,
    created_at BIGINT,
    created_by BIGINT,
    updated_at BIGINT,
    updated_by BIGINT,
    deleted_at BIGINT,
    deleted_by BIGINT
) ENGINE = INNODB;
CREATE INDEX `mentor_data_index` ON `mentor_data`(fk_user_id);

DROP TABLE IF EXISTS `mentor_interest_type`;
CREATE TABLE IF NOT EXISTS `mentor_interest_type`(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fk_user_id BIGINT NOT NULL,
    fk_interest_type_id BIGINT NOT NULL
) ENGINE = INNODB;
CREATE INDEX `mentor_interest_type` ON `mentor_interest_type`(fk_user_id, fk_interest_type_id);