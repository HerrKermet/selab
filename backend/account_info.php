<?php

function datef(string $field): string {
    return "DATE_FORMAT($field, '%Y-%m-%dT%TZ') as $field";
}

$df = 'datef';

require_once "http_exceptions.php";
require_once "session.php";

try {
    header('Content-type: application/json; charset=utf-8');

    $request = require 'get_input.php';

    $sess_str = $request['session'] ?? null;

    if (!isset($sess_str)) {
        throw new BadRequest();
    }

    $sess = Session::fromString($sess_str);

    $dbh = require "connect.php";
    $dbh->beginTransaction();
    $dbh->exec('set time_zone = "+00:00";');

    $user_id = $sess->authenticate($dbh);

    if ($user_id === false) {
        throw new Unauthorized();
    }

    $dbh->exec('set time_zone = "+00:00";');
    
    $sth = $dbh->prepare("select {$df('creation')} from users where id = ?;");
    $sth->execute([$user_id]);
    $creation = $sth->fetchColumn();
    
    if ($creation === false) {
        throw new Exception("Cannot get creation date");
    }

    $response = [
        "id" => $user_id,
        "creation" => $creation
    ];
    
    $json = json_encode($response, JSON_THROW_ON_ERROR);

    $dbh->commit();
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
