CREATE DATABASE booking_demo;
USE booking_demo;

CREATE TABLE `booking` (
    `seqNo` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `version` TINYINT NOT NULL DEFAULT 1,
    `type` ENUM('CREATE_BOOKING', 'UPDATE_BOOKING', 'CANCEL_BOOKING') NOT NULL,
    `booking_id` BINARY(16) NOT NULL,
    `user_id` BINARY(16) NOT NULL,
    `resource_id` BINARY(16) NOT NULL,
    `bookFrom` DATETIME,
    `bookTo` DATETIME
);