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

--                                  INSERTS

INSERT INTO `letta`.`people` (`id`,`name`,`surname`) VALUES (0,'Antón','Pérez');
INSERT INTO `letta`.`people` (`id`,`name`,`surname`) VALUES (0,'Manuel','Martínez');
INSERT INTO `letta`.`people` (`id`,`name`,`surname`) VALUES (0,'Laura','Reboredo');
INSERT INTO `letta`.`people` (`id`,`name`,`surname`) VALUES (0,'Perico','Palotes');
INSERT INTO `letta`.`people` (`id`,`name`,`surname`) VALUES (0,'Ana','María');
INSERT INTO `letta`.`people` (`id`,`name`,`surname`) VALUES (0,'María','Nuevo');
INSERT INTO `letta`.`people` (`id`,`name`,`surname`) VALUES (0,'Alba','Fernández');
INSERT INTO `letta`.`people` (`id`,`name`,`surname`) VALUES (0,'Asunción','Jiménez');

-- The password for each user is its login suffixed with "pass". For example, user "admin" has the password "adminpass".
INSERT INTO `letta`.`users` (`username`,`password`,`email`, `role`)
VALUES ('admin', '713bfda78870bf9d1b261f565286f85e97ee614efe5f0faf7c34e7ca4f65baca', 'admin@admin.com', 'ADMIN');
INSERT INTO `letta`.`users` (`username`,`password`,`email`, `role`)
VALUES ('normal', '7bf24d6ca2242430343ab7e3efb89559a47784eea1123be989c1b2fb2ef66e83', 'user@user.com','USER');
-- no me daja entrar con moderator
INSERT INTO `letta`.`users` (`username`,`password`,`email`, `role`) 
VALUES ('moderator', 'ec9b477f0db4c04247a8205ea173e0ff74979509c3544485145494ed1f4baeb3', 'moderator@user.com','MODERATOR');

-- INSERTS GROUPS
INSERT INTO `letta`.`groups` (`group_name`, `description`, `owner_id`)
VALUES ('Grupo lejano oeste', 'Un grupo para amantes de la lectura donde discutimos y compartimos nuestros libros favoritos sobre el lejano oeste.', 1);

INSERT INTO `letta`.`groups` (`group_name`, `description`, `owner_id`)
VALUES ('Club de cocina', 'Un club dedicado a libros de recetas, daremos consejos y experiencias culinarias.', 2);

INSERT INTO `letta`.`groups` (`group_name`, `description`, `owner_id`)
VALUES ('Romances medievales', 'Un club dedicado a los amantes de las historias de romances medievales', 1);

-- INSERTS MODERATIONS
INSERT INTO `letta`.`moderations` (`group_id`, `user_id`)
VALUES (1, 3);
INSERT INTO `letta`.`moderations` (`group_id`, `user_id`)
VALUES (2, 3);

-- INSERTS TOPICS
INSERT INTO `letta`.`topics` (`topic_name`)
VALUES ('Literatura clásica');

INSERT INTO `letta`.`topics` (`topic_name`)
VALUES ('Lejano oeste');

INSERT INTO `letta`.`topics` (`topic_name`)
VALUES ('Cocina internacional');


-- INSERTS GROUPS_TOPICS
INSERT INTO `letta`.`group_topics` (`topic_name`, `group_id`)
VALUES ('Literatura clásica', 1);

INSERT INTO `letta`.`group_topics` (`topic_name`, `group_id`)
VALUES ('Literatura clásica', 3);

INSERT INTO `letta`.`group_topics` (`topic_name`, `group_id`)
VALUES ('Lejano oeste', 1);

INSERT INTO `letta`.`group_topics` (`topic_name`, `group_id`)
VALUES ('Cocina internacional', 2);


-- INSERTS EVENTS
INSERT INTO `letta`.`events` (`event_name`, `event_date`, `group_id`, `event_description`, `media_file`, `owner_id`)
VALUES ('Evento de Literatura Clásica', '2024-05-15', 1, 'Un evento para discutir y compartir nuestros libros favoritos de literatura clásica.', 'libro_clasico.jpg', 2);

INSERT INTO `letta`.`events` (`event_name`, `event_date`, `group_id`, `event_description`, `media_file`, `owner_id`)
VALUES ('Taller de Cocina Mexicana', '2024-06-20', 2, 'Un taller práctico donde aprenderemos a cocinar platos típicos de México.', 'taller_cocina_mexicana.mp4', 2);

INSERT INTO `letta`.`events` (`event_name`, `event_date`, `group_id`, `event_description`, `media_file`,`owner_id`)
VALUES ('Encuentro de Romances Medievales', '2024-07-10', 3, 'Un encuentro para discutir y explorar las historias de romances medievales.', NULL, 2);

-- INSERTS COMMENTS

-- INSERTS event_comments
INSERT INTO `letta`.`event_comments` (`event_id`, `user_id`, `comment_text`)
VALUES (1, 1, 'Este evento fue increíble, disfruté mucho de la discusión.');

INSERT INTO `letta`.`event_comments` (`event_id`, `user_id`, `comment_text`)
VALUES (1, 2, '¡Totalmente de acuerdo! Me encantaría asistir a más eventos como este.');

INSERT INTO `letta`.`event_comments` (`event_id`, `user_id`, `comment_text`)
VALUES (2, 2, 'Aprendí mucho en el taller de cocina, ¡gracias por organizarlo!');

INSERT INTO `letta`.`event_comments` (`event_id`, `user_id`, `comment_text`)
VALUES (3, 1, 'Las historias de romances medievales siempre me fascinan. ¡Qué evento tan interesante!');


-- INSERT EVENT_REPORTS
INSERT INTO `letta`.`event_reports` (`event_id`, `report_reason`, `owner_id`)
VALUES (1, 'En este evento se permite el consumo de alcohol a menores de edad, esto es un escándalo!', 2);

INSERT INTO `letta`.`event_reports` (`event_id`, `report_reason`, `owner_id`)
VALUES (1, 'Se presentaron problemas técnicos durante el evento de Literatura Clásica, interrumpiendo la participación de los miembros.', 2);

INSERT INTO `letta`.`event_reports` (`event_id`, `report_reason`, `owner_id`)
VALUES (2, 'En el taller de Cocina Mexicana se usaron productos caducados.', 2);

-- INSERT COMMENT_REPORTS
INSERT INTO `letta`.`comment_reports` (`comment_id`,`user_id`, `report_reason`)
VALUES (1, 2, 'El evento aún no se ha celebrado, está mintiendo!.');

INSERT INTO `letta`.`comment_reports` (`comment_id`,`user_id`, `report_reason`)
VALUES (3, 2, 'Mentira, no se ha producido ningún problema técnico.');

-- ADD USERS TO GROUPS
INSERT INTO `letta`.`user_groups` (`user_id`, `group_id`) VALUES (1, 1);
INSERT INTO `letta`.`user_groups` (`user_id`, `group_id`) VALUES (1, 3);
INSERT INTO `letta`.`user_groups` (`user_id`, `group_id`) VALUES (2, 1);

