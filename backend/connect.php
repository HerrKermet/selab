<?php

$db = require 'database.php';
$dsn = "mysql:host={$db['host']};dbname={$db['db']};charset=UTF8";
$options = [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION];
return new PDO($dsn, $db['user'], $db['password'], $options);

?>
