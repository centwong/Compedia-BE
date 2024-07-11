DROP TABLE IF EXISTS `competition_tracker`;
CREATE TABLE IF NOT EXISTS `competition_tracker`(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    competition_id BIGINT NOT NULL,
    unique_id VARCHAR(255) NOT NULL
) ENGINE = INNODB;

CREATE INDEX `competition_tracker_index` ON `competition_tracker`(unique_id);