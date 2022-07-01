<?php

try {
    if (!session_start()) {
        throw new RuntimeException("Cannot start session");
    }

    header('Content-type: application/json; charset=utf-8');

    $_SESSION = [];

    if (ini_get("session.use_cookies")) {
        $params = session_get_cookie_params();
        setcookie(session_name(), '', time() - 42000,
            $params["path"], $params["domain"],
            $params["secure"], $params["httponly"]
        );
    }

    if (!session_destroy()) {
        throw new RuntimeException("Cannot stop session");
    }

    $json = "{}";
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
