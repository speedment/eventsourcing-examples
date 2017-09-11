CREATE DATABASE booking_demo;
USE booking_demo;

CREATE TABLE `booking` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `booking` BINARY(16) NOT NULL,
    `type` ENUM('BOOK', 'UPDATE', 'CANCEL') NOT NULL,
    `version` TINYINT NOT NULL DEFAULT 1,
    `userId` INT,
    `resource` VARCHAR(32),
    `bookFrom` DATETIME,
    `bookTo` DATETIME
);