Para compilar este proyecto es necesario usar la base de datos, por lo que debería instalar-se maria-db en 
este caso i crear una base de datos llamada dsajuego i crear tres tablas:

TABLA ITEM:--------------------------------------------------------------------------
CREATE TABLE `item` (
	`id` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
	`name` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
	`description` TEXT NULL DEFAULT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
	`price` INT(11) NOT NULL,
	`imageUrl` VARCHAR(2083) NULL DEFAULT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
	PRIMARY KEY (`id`) USING BTREE,
	CONSTRAINT `price` CHECK (`price` >= 0)
)
COLLATE='utf8mb4_uca1400_ai_ci'
ENGINE=InnoDB
;

TABLA USER:----------------------------------------------------------------------------------
CREATE TABLE `user` (
	`id` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
	`username` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
	`password` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
	`isAdmin` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
	`fullName` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
	`email` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
	`age` INT(11) NULL DEFAULT NULL,
	`profilePicture` VARCHAR(2083) NULL DEFAULT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
	`coins` INT(11) NULL DEFAULT '0',
	PRIMARY KEY (`id`) USING BTREE,
	UNIQUE INDEX `username` (`username`) USING BTREE,
	UNIQUE INDEX `email` (`email`) USING BTREE,
	CONSTRAINT `age` CHECK (`age` >= 0),
	CONSTRAINT `coins` CHECK (`coins` >= 0)
)
COLLATE='utf8mb4_uca1400_ai_ci'
ENGINE=InnoDB
;

TABLA USER_ITEM:--------------------------------------------------------------------------------
CREATE TABLE `user_item` (
	`id` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
	`user_id` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
	`item_id` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
	`quantity` INT(11) NOT NULL DEFAULT '1',
	`purchase_date` DATETIME NOT NULL DEFAULT current_timestamp(),
	PRIMARY KEY (`id`) USING BTREE,
	INDEX `user_id` (`user_id`) USING BTREE,
	INDEX `item_id` (`item_id`) USING BTREE,
	CONSTRAINT `user_item_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT `user_item_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT `quantity` CHECK (`quantity` > 0)
)
COLLATE='utf8mb4_uca1400_ai_ci'
ENGINE=InnoDB
;
