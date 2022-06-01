<?php

require_once "camel_to_snake.php";

// Accept data either in JSON or URLENCODE format
$content_type = $_SERVER['CONTENT_TYPE'] ?? null;
if ($content_type == "application/json") {
    $arr = json_decode(file_get_contents('php://input'), true, 5, JSON_THROW_ON_ERROR);
    arr_camel_to_snake($arr);
    return $arr;
}
return $_REQUEST;

?>
