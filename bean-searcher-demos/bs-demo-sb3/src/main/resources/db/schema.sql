DROP TABLE IF EXISTS `department`;

CREATE TABLE `department` (
  `id` int NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `attrs` json DEFAULT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `employee`;

CREATE TABLE `employee` (
  `id` int NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `age` int DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `entry_date` datetime DEFAULT NULL,
  `department_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
);
