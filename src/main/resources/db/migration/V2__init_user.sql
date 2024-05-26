DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user`(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role TINYINT NOT NULL,
    fk_university_id BIGINT NOT NULL,
    domain VARCHAR(255),
    created_at BIGINT NOT NULL,
    updated_at BIGINT,
    deleted_at BIGINT,
    is_active BOOLEAN NOT NULL
) ENGINE = INNODB;
CREATE INDEX `index_user` ON `user`(email, fk_university_id, is_active);