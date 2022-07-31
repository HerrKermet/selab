<?php

$sql =
"CREATE TABLE IF NOT EXISTS `users` (
 `id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
 `creation` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 `password` varchar(255) NOT NULL,
 `next_sync_sqn` bigint(20) unsigned NOT NULL DEFAULT '0',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `activities` (
 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
 `user_id` bigint(20) unsigned NOT NULL,
 `last_sync_sqn` bigint(20) unsigned NOT NULL,
 `last_modification` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 `start` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 `end` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 `duration` bigint(20) unsigned DEFAULT NULL, -- duration in seconds
 `activity_type` varchar(255) DEFAULT NULL, -- is a string from a clearly defined enum
 `type` varchar(255) DEFAULT NULL, -- can be written by the user of the app
 `is_automatically_detected` boolean NOT NULL DEFAULT 0,
 PRIMARY KEY (`id`),
 FOREIGN KEY (`user_id`) REFERENCES users(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `moods` (
 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
 `user_id` bigint(20) unsigned NOT NULL,
 `last_sync_sqn` bigint(20) unsigned NOT NULL,
 `last_modification` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 `assessment` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 `satisfaction` tinyint(4) DEFAULT NULL,
 `calmness` tinyint(4) DEFAULT NULL,
 `comfort` tinyint(4) DEFAULT NULL,
 `relaxation` tinyint(4) DEFAULT NULL,
 `energy` tinyint(4) DEFAULT NULL,
 `wakefulness` tinyint(4) DEFAULT NULL,
 `event_negative_intensity` tinyint(4) DEFAULT NULL,
 `event_positive_intensity` tinyint(4) DEFAULT NULL,
 `alone` boolean DEFAULT NULL,
 `surrounding_people_liking` tinyint(4) DEFAULT NULL,
 `surrounding_people_type` varchar(255) DEFAULT NULL,
 `location` varchar(255) DEFAULT NULL,
 `satisfied_with_yourself` tinyint(4) DEFAULT NULL,
 `consider_yourself_failure` tinyint(4) DEFAULT NULL,
 `acted_impulsively` tinyint(4) DEFAULT NULL,
 `acted_aggressively` tinyint(4) DEFAULT NULL,
 `notes` varchar(255) DEFAULT NULL,
 PRIMARY KEY (`id`),
 FOREIGN KEY (`user_id`) REFERENCES users(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `accelerometer_data` (
 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
 `user_id` bigint(20) unsigned NOT NULL,
 `last_sync_sqn` bigint(20) unsigned NOT NULL,
 `time` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 `x` float NOT NULL,
 `y` float NOT NULL,
 `z` float NOT NULL,
 PRIMARY KEY (`id`),
 FOREIGN KEY (`user_id`) REFERENCES users(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `login_sessions` (
 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
 `key` varchar(255) NOT NULL,
 `user_id` bigint(20) unsigned NOT NULL,
 `creation` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 `last_access` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
 PRIMARY KEY (`id`),
 FOREIGN KEY (`user_id`) REFERENCES users(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
";

try {
    header('Content-type: application/json; charset=utf-8');

    $dbh = require 'connect.php';
    $dbh->exec($sql);

    $json = "{}";
}
catch (Exception $e) {
    http_response_code(500);
    $json = json_encode(['error' => [
        'msg' => $e->getMessage(),
        'code' => $e->getCode()
    ]]);
}

echo "$json\n";

?>
