<?php

function datef(string $field): string {
    return "DATE_FORMAT($field, '%Y-%m-%dT%TZ') as $field";
}

$df = 'datef';

try {
    if (!session_start()) {
        throw new Exception("Cannot start session");
    }

    header('Content-type: application/json; charset=utf-8');

    if (!isset($_SESSION['user_id'])) {
        throw new Exception("Not logged in");
    }
    
    $_SESSION['last_access'] = time();

    $dbh = require 'connect.php';

    $dbh->beginTransaction();
    
    $dbh->exec('set time_zone = "+00:00";');
    
    $sth = $dbh->prepare("select {$df('creation')} from users where id = ?;");
    $sth->execute([$_SESSION['user_id']]);
    $creation = $sth->fetchColumn();
    
    if ($creation === false) {
        throw new Exception("Cannot get creation date");
    }

    $response = [
        "id" => $_SESSION['user_id'],
        "creation" => $creation
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
