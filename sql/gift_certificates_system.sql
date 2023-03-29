-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema gift_certificates_system
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `gift_certificates_system`;

-- -----------------------------------------------------
-- Schema gift_certificates_system
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `gift_certificates_system` DEFAULT CHARACTER SET utf8;
USE `gift_certificates_system`;

-- -----------------------------------------------------
-- Table `gift_certificates_system`.`tag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_certificates_system`.`tag`
(
    `id`   INT         NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(64) NOT NULL,
    PRIMARY KEY (`id`)
);


-- -----------------------------------------------------
-- Table `gift_certificates_system`.`gift_certificate`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_certificates_system`.`gift_certificate`
(
    `id`               INT            NOT NULL AUTO_INCREMENT,
    `name`             VARCHAR(64)    NOT NULL,
    `description`      VARCHAR(255)   NULL,
    `price`            DECIMAL(10, 2) NULL,
    `duration`         INT            NULL,
    `create_date`      DATETIME       NULL DEFAULT CURRENT_TIMESTAMP,
    `last_update_date` DATETIME       NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);


-- -----------------------------------------------------
-- Table `gift_certificates_system`.`gift_certificate_has_tag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_certificates_system`.`gift_certificate_has_tag`
(
    `gift_certificate_id` INT NOT NULL,
    `tag_id`              INT NOT NULL,
    PRIMARY KEY (`gift_certificate_id`, `tag_id`),
    INDEX `fk_gift_certificate_has_tag_tag1_idx` (`tag_id` ASC) VISIBLE,
    INDEX `fk_gift_certificate_has_tag_gift_certificate_idx` (`gift_certificate_id` ASC) VISIBLE,
    CONSTRAINT `fk_gift_certificate_has_tag_gift_certificate`
        FOREIGN KEY (`gift_certificate_id`)
            REFERENCES `gift_certificates_system`.`gift_certificate` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    CONSTRAINT `fk_gift_certificate_has_tag_tag1`
        FOREIGN KEY (`tag_id`)
            REFERENCES `gift_certificates_system`.`tag` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);

USE `gift_certificates_system`;

DELIMITER $$
USE `gift_certificates_system`$$
CREATE DEFINER = CURRENT_USER TRIGGER `gift_certificates_system`.`gift_certificate_BEFORE_UPDATE`
    BEFORE UPDATE
    ON `gift_certificate`
    FOR EACH ROW
BEGIN
    SET NEW.last_update_date = CURRENT_TIMESTAMP;
END$$


DELIMITER ;

SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;
