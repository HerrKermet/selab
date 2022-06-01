<?php

function random_str(
    $length = 8,
    $keyspace = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'
) {
    $str = '';
    $max = mb_strlen($keyspace, '8bit') - 1;
    if ($max < 1) {
        throw new Exception('$keyspace must be at least two characters long');
    }
    for ($i = 0; $i < $length; ++$i) {
        $str .= $keyspace[random_int(0, $max)];
    }
    return $str;
}

try {
    if (!session_start()) {
        throw new RuntimeException("Cannot start session");
    }

    header('Content-type: application/json; charset=utf-8');

    $user_password = random_str();
    $user_password_hash = password_hash($user_password, PASSWORD_DEFAULT);

    $dbh = require 'connect.php';

    $dbh->beginTransaction();

    $dbh->exec('set time_zone = "+00:00";');
    $sql = "insert into users (password) values (?);";
    $sth = $dbh->prepare($sql);
    $sth->execute([$user_password_hash]);
    $user_id = intval($dbh->lastInsertId());

    $_SESSION['user_id'] = $user_id;
    $_SESSION['last_access'] = time();
    $response = [
        "userId" => $user_id,
        "password" => $user_password
    ];

    $json = json_encode($response, JSON_THROW_ON_ERROR);

    $dbh->commit();
}
catch (Throwable $e) {
    $json = json_encode(['error' => [
        'msg' => $e->getMessage(),
        'code' => $e->getCode()
    ]]);
}

echo "$json\n";

?>
