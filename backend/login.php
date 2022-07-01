<?php

try {
    if (!session_start()) {
        throw new RuntimeException("Cannot start session");
    }

    header('Content-type: application/json; charset=utf-8');

    $login = require "get_input.php";

    $user_id = $login['id'] ?? null;
    $password = $login['password'] ?? null;

    if (!isset($user_id) || !isset($password)) {
        throw new RuntimeException("UserID and password must be specified");
    }

    /*
    if (is_numeric($user_id)) {
        $user_id = intval($user_id);
    }
    else {
        throw new RuntimeException("UserID is not a number");
    }
    */
    
    $dbh = require 'connect.php';
    
    $dbh->beginTransaction();
    $sql = "select user.password from users user where user.id = ?;";
    $sth = $dbh->prepare($sql);
    $sth->execute([$user_id]);

    $password_hash = $sth->fetchColumn();

    $auth_success = false;

    if ($password_hash) {
        if (password_verify($password, $password_hash)) {
            $_SESSION['user_id'] = $user_id;
            $_SESSION['last_access'] = time();
            $auth_success = true;
            $json = "{}";
        }
    }

    if (!$auth_success) {
        throw new RuntimeException("UserID or password incorrect");
    }
    
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
