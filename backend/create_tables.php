<?php

$sql =
"CREATE TABLE IF NOT EXISTS `users` (
 `id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
 `creation` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
 `password` varchar(255) NOT NULL,
 `next_sync_sqn` bigint(20) unsigned NOT NULL DEFAULT '0',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `activities` (
 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
 `user_id` bigint(20) unsigned NOT NULL,
 `last_sync_sqn` bigint(20) unsigned NOT NULL,
 `last_modification` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
 `start` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
 `end` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
 `type` varchar(255) DEFAULT NULL,
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `moods` (
 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
 `user_id` bigint(20) unsigned NOT NULL,
 `last_sync_sqn` bigint(20) unsigned NOT NULL,
 `last_modification` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
 `assessment` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
 `satisfaction` tinyint(4) DEFAULT NULL,
 `calmness` tinyint(4) DEFAULT NULL,
 `comfort` tinyint(4) DEFAULT NULL,
 `relaxation` tinyint(4) DEFAULT NULL,
 `energy` tinyint(4) DEFAULT NULL,
 `wakefulness` tinyint(4) DEFAULT NULL,
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
";

try {
    header('Content-type: application/json; charset=utf-8');

    $dbh = require 'connect.php';
    $dbh->exec($sql);

    $json = "{}";
}
catch (Exception $e) {
    $json = json_encode(['error' => [
        'msg' => $e->getMessage(),
        'code' => $e->getCode()
    ]]);
}

echo "$json\n";

?>
