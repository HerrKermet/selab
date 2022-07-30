<?php

require_once "http_exceptions.php";
require_once "session.php";

// Check if the user credentials are valid.
// Returns true if valid, false otherwise.
function authenticate_login(PDO $dbh, int $user_id, string $password) : bool {
    $sql = "select user.password from users user where user.id = ?;";
    $sth = $dbh->prepare($sql);
    $sth->execute([$user_id]);

    $password_hash = $sth->fetchColumn();

    return $password_hash && password_verify($password, $password_hash);
}

try {
    header('Content-type: application/json; charset=utf-8');

    $login = require "get_input.php";

    $user_id = $login['id'] ?? null;
    $password = $login['password'] ?? null;

    if (!isset($user_id) || !isset($password)) {
        throw new BadRequest();
    }

    $dbh = require 'connect.php';

    $dbh->beginTransaction();
    $dbh->exec('set time_zone = "+00:00";');

    if (!authenticate_login($dbh, $user_id, $password)) {
        throw new Unauthorized();
    }

    $json = json_encode(['session' => (string) Session::create($dbh, $user_id)]);

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
