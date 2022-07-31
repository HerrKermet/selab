<?php

require_once "session.php";

function datef(string $field): string {
    return "DATE_FORMAT($field, '%Y-%m-%dT%TZ') as $field";
}

$df = 'datef';

try {
    header('Content-type: application/json; charset=utf-8');

    $user_password = random_str();
    $user_password_hash = password_hash($user_password, PASSWORD_DEFAULT);

    $dbh = require "connect.php";
    $dbh->beginTransaction();
    $dbh->exec('set time_zone = "+00:00";');

    $sql = "insert into users (password) values (?);";
    $sth = $dbh->prepare($sql);
    $sth->execute([$user_password_hash]);
    $user_id = $dbh->lastInsertId();

    $sth = $dbh->prepare("select id, {$df('creation')} from users where id = ?");
    $sth->execute([$user_id]);
    $user_row = $sth->fetch(PDO::FETCH_ASSOC);
    if ($user_row === false) {
        throw new RuntimeException("Cannot get inserted user row");
    }

    $user_id = $user_row['id'];

    $response = [
        "id" => $user_id,
        "password" => $user_password,
        "creation" => $user_row['creation'],
        "session" => (string) Session::create($dbh, $user_id)
    ];

    $json = json_encode($response, JSON_THROW_ON_ERROR);

    $dbh->commit();
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
