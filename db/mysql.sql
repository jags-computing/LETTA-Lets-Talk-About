DROP DATABASE IF EXISTS `letta`;
CREATE DATABASE `letta`;

CREATE TABLE `letta`.`people` (
	`id` int NOT NULL AUTO_INCREMENT,
	`name` varchar(50) NOT NULL,
	`surname` varchar(100) NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- TABLA USERS
CREATE TABLE `letta`.`users` (
    `user_id` INT AUTO_INCREMENT PRIMARY KEY,
	`username` varchar(100) UNIQUE NOT NULL,
	`password` varchar(64) NOT NULL,
    `email` varchar(100) UNIQUE NOT NULL,
	`role` varchar(10) NOT NULL,
    `profile_about` VARCHAR(512) DEFAULT NULL,
    `public_contact` VARCHAR(255) DEFAULT NULL,
    `nickname` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- TABLA GROUPS
CREATE TABLE `letta`.`groups` (
    `group_id` INT AUTO_INCREMENT NOT NULL,
    `group_name` varchar(50) UNIQUE NOT NULL,
    `description` TEXT NOT NULL,
    `owner_id` INT NOT NULL,
    PRIMARY KEY (`group_id`),
    FOREIGN KEY (`owner_id`) REFERENCES `users`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- TABLA MODERATIONS
CREATE TABLE `letta`.`moderations` (
    `group_id` INT NOT NULL, 
    `user_id` INT NOT NULL, 
    PRIMARY KEY (`group_id`, `user_id`),
    FOREIGN KEY (`group_id`) REFERENCES `groups`(`group_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- TABLA TOPICs
CREATE TABLE `letta`.`topics` (
    `topic_name` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`topic_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- TABLA group_topics
CREATE TABLE `letta`.`group_topics` (
    `topic_name` VARCHAR(50) NOT NULL,
    `group_id` INT NOT NULL,
    PRIMARY KEY (`topic_name`, `group_id`),
    FOREIGN KEY (`group_id`) REFERENCES `groups`(`group_id`),
    FOREIGN KEY (`topic_name`) REFERENCES `topics`(`topic_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- TABLA events
CREATE TABLE `letta`.`events` (
    `event_id` INT AUTO_INCREMENT NOT NULL,
    `event_name` VARCHAR(50) NOT NULL,
    `event_date` DATE NOT NULL,
    `group_id` INT NOT NULL,
    `event_description` TEXT NOT NULL,
    `media_file` VARCHAR(255) DEFAULT NULL,
    `owner_id` INT NOT NULL,
    PRIMARY KEY (`event_id`),
    UNIQUE KEY `unique_event_name_in_group` (`event_name`, `group_id`),
    FOREIGN KEY (`group_id`) REFERENCES `groups`(`group_id`),
    FOREIGN KEY (`owner_id`) REFERENCES `users`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- TABLA event_comments
CREATE TABLE `letta`.`event_comments` (
    `comment_id` INT AUTO_INCREMENT NOT NULL,
    `event_id` INT NOT NULL,
    `user_id` INT NOT NULL,
    `comment_text` TEXT NOT NULL,
    PRIMARY KEY (`comment_id`),
    FOREIGN KEY (`event_id`) REFERENCES `events`(`event_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- TABLA event_reports
CREATE TABLE `letta`.`event_reports` (
    `event_report_id` INT AUTO_INCREMENT NOT NULL,
    `event_id` INT NOT NULL,
    `report_reason` TEXT NOT NULL,
    `owner_id` INT NOT NULL,
    PRIMARY KEY (`event_report_id`),
    FOREIGN KEY (`event_id`) REFERENCES `events`(`event_id`),
    FOREIGN KEY (`owner_id`) REFERENCES `users`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- TABLA comment_reports
CREATE TABLE `letta`.`comment_reports` (
    `comment_report_id` INT AUTO_INCREMENT NOT NULL,
    `comment_id` INT NOT NULL,
    `user_id` INT NOT NULL, 
    `report_reason` TEXT NOT NULL,
    
    PRIMARY KEY (`comment_report_id`),
    FOREIGN KEY (`comment_id`) REFERENCES `event_comments`(`comment_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- relation many-to-many between user and group
CREATE TABLE `letta`.`user_groups` (
    `user_id` INT NOT NULL,
    `group_id` INT NOT NULL,
    PRIMARY KEY (`user_id`, `group_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`),
    FOREIGN KEY (`group_id`) REFERENCES `groups`(`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE USER IF NOT EXISTS 'letta'@'localhost' IDENTIFIED BY 'letta';
GRANT ALL ON `letta`.* TO 'letta'@'localhost';