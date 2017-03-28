# Base tables
CREATE TABLE `project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `identifier` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `releases` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `released_at` datetime NOT NULL,
  `url` varchar(255) NOT NULL,
  `version` varchar(255) NOT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkc96tfkfbo7h16txn8y7hn87` (`project_id`),
  CONSTRAINT `FKkc96tfkfbo7h16txn8y7hn87` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `subscription` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `identifier` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `project_subscriptions` (
  `project_id` bigint(20) NOT NULL,
  `subscriptions_id` bigint(20) NOT NULL,
  PRIMARY KEY (`project_id`,`subscriptions_id`),
  KEY `FKk11tlb7u4idjxawbiuq6hacjk` (`subscriptions_id`),
  CONSTRAINT `FKimadi3cn36jwb6i8xfg49v0se` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FKk11tlb7u4idjxawbiuq6hacjk` FOREIGN KEY (`subscriptions_id`) REFERENCES `subscription` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# Vendor tables
CREATE TABLE `project_github` (
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKcxopdmx2c654hotr912p3n3np` FOREIGN KEY (`id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `project_npm` (
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKrgqf8x9grcsffer5e7c6bgh2p` FOREIGN KEY (`id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `subscription_slack` (
  `channel` varchar(255) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKkuhfwhbsxxe3cm0r555a3t4bl` FOREIGN KEY (`id`) REFERENCES `subscription` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
