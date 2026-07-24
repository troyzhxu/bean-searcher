DROP TABLE IF EXISTS `dept`;

CREATE TABLE `dept` (
  `id` int NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` int NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `age` int DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `entry_date` datetime DEFAULT NULL,
  `dept_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
);
