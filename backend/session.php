<?php

require_once "http_exceptions.php";
require_once "random_str.php";

class Session {
    public $id;
    public $key;

    static function fromString($name) {
        $arr = explode("-", $name);
        if (count($arr) < 2) {
            throw new BadRequest();
        }
        return new Session($arr[0], $arr[1]);
    }

    function __construct(int $id = 0, string $key = "a") {
        $this->id = $id;
        $this->key = $key;
    }

    function __toString() : string {
        return "{$this->id}-{$this->key}";
    }

    // Create a new session for the user
    static function create(PDO $dbh, int $user_id) : Session {
        $sql = "insert into `login_sessions` (`key`, `user_id`) values (?, ?);";
        $sth = $dbh->prepare($sql);
        $key = random_str(32);
        $sth->execute([$key, $user_id]);
        $id = $dbh->lastInsertId();
        if ($id === false) {
            throw new RuntimeException("Cannot insert new session");
        }
        return new Session($id, $key);
    }

    // Check if the session is valid and return the id of the user.
    // As a side effect, this method updates the sessions last_access timestamp.
    // If it is invalid, return false.
    function authenticate(PDO $dbh) : int|false {
        $sql = "select `key`, `user_id` from `login_sessions` where `id` = ?;";
        $sth = $dbh->prepare($sql);
        $sth->execute([$this->id]);
        $row = $sth->fetch(PDO::FETCH_ASSOC);
        // Check if session is valid
        if ($row === false || $row['key'] != $this->key) {
            return false;
        }
        // Update timestamp
        $sql = "update `login_sessions` set `last_access` = CURRENT_TIMESTAMP where `id` = ?;";
        $dbh->prepare($sql)->execute([$this->id]);
        return $row['user_id'];
    }

    // Delete session if it is valid
    // Returns true if the session was deleted from the database, false otherwise.
    function delete(PDO $dbh) : bool {
        if ($this->authenticate($dbh) !== false) {
            $sql = "delete from `login_sessions` where `id` = ?;";
            $dbh->prepare($sql)->execute([$this->id]);
            return true;
        }
        return false;
    }
}

?>
