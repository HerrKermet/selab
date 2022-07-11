<?php

require_once "http_exceptions.php";

require_once "session.php";

try {
    header('Content-type: application/json; charset=utf-8');

    $request = require "get_input.php";

    $sess_str = $request['session'] ?? null;

    if (!isset($sess_str)) {
        throw new BadRequest();
    }

    $dbh = require "connect.php";
    $dbh->beginTransaction();
    $dbh->exec('set time_zone = "+00:00";');

    Session::fromString($sess_str)->delete($dbh);

    $dbh->commit();

    $json = "{}";
}
catch (HttpException $e) {
    http_response_code($e->getHttpCode());
    $json = json_encode(['error' => [
        'msg' => $e->getMessage(),
        'code' => $e->getCode()
    ]]);
}
catch (Throwable $e) {
    http_response_code(500);
    $json = json_encode(['error' => [
        'msg' => $e->getMessage(),
        'code' => $e->getCode()
    ]]);
}

echo "$json\n";

?>
