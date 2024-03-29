-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

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
    PRIMARY KEY (`id`),
    UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE
);


-- -----------------------------------------------------
-- Table `gift_certificates_system`.`gift_certificate`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_certificates_system`.`gift_certificate`
(
    `id`               INT            NOT NULL AUTO_INCREMENT,
    `name`             VARCHAR(64)    NOT NULL,
    `description`      VARCHAR(255)   NULL,
    `img`              VARCHAR(255)   NULL,
    `price`            DECIMAL(10, 2) NULL,
    `duration`         INT            NULL,
    `create_date`      DATETIME       NULL DEFAULT CURRENT_TIMESTAMP,
    `last_update_date` DATETIME       NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE
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
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT `fk_gift_certificate_has_tag_tag1`
        FOREIGN KEY (`tag_id`)
            REFERENCES `gift_certificates_system`.`tag` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE
);

-- -----------------------------------------------------
-- Table `gift_certificates_system`.`role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_certificates_system`.`role`
(
    `id`   INT         NOT NULL,
    `name` VARCHAR(45) NULL,
    PRIMARY KEY (`id`)
);


-- -----------------------------------------------------
-- Table `gift_certificates_system`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_certificates_system`.`user`
(
    `id`           INT         NOT NULL AUTO_INCREMENT,
    `first_name`   VARCHAR(45) NOT NULL,
    `last_name`    VARCHAR(45) NULL,
    `role_id`      INT         NOT NULL DEFAULT 1,
    `email`        VARCHAR(45) NULL,
    `auth0user_id` VARCHAR(45) NULL,
    `password`     VARCHAR(64) NULL,
    PRIMARY KEY (`id`, `role_id`),
    INDEX `fk_user_role1_idx` (`role_id` ASC) VISIBLE,
    CONSTRAINT `fk_user_role1`
        FOREIGN KEY (`role_id`)
            REFERENCES `gift_certificates_system`.`role` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);


-- -----------------------------------------------------
-- Table `gift_certificates_system`.`user_order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_certificates_system`.`user_order`
(
    `id`               INT            NOT NULL AUTO_INCREMENT,
    `user_id`          INT            NOT NULL,
    `sum`              DECIMAL(10, 2) NOT NULL,
    `create_date`      DATETIME       NULL DEFAULT CURRENT_TIMESTAMP,
    `last_update_date` DATETIME       NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `fk_order_user1_idx` (`user_id` ASC) VISIBLE,
    CONSTRAINT `fk_order_user1`
        FOREIGN KEY (`user_id`)
            REFERENCES `gift_certificates_system`.`user` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);


-- -----------------------------------------------------
-- Table `gift_certificates_system`.`order_has_gift_certificate`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_certificates_system`.`order_has_gift_certificate`
(
    `order_id`            INT NOT NULL,
    `gift_certificate_id` INT NOT NULL,
    `count`               INT NOT NULL DEFAULT 1,
    PRIMARY KEY (`order_id`, `gift_certificate_id`),
    INDEX `fk_order_has_gift_certificate_gift_certificate1_idx` (`gift_certificate_id` ASC) VISIBLE,
    INDEX `fk_order_has_gift_certificate_order1_idx` (`order_id` ASC) VISIBLE,
    CONSTRAINT `fk_order_has_gift_certificate_order1`
        FOREIGN KEY (`order_id`)
            REFERENCES `gift_certificates_system`.`user_order` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    CONSTRAINT `fk_order_has_gift_certificate_gift_certificate1`
        FOREIGN KEY (`gift_certificate_id`)
            REFERENCES `gift_certificates_system`.`gift_certificate` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);

CREATE TABLE refresh_tokens
(
    id          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id     INT          NOT NULL,
    token       VARCHAR(255) NOT NULL,
    expiry_date DATETIME     NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id)
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
