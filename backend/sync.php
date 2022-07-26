<?php

require_once "http_exceptions.php";
require_once "session.php";
require_once "snake_to_camel.php";

function datef(string $field): string {
    return "DATE_FORMAT($field, '%Y-%m-%dT%TZ') as $field";
}

$df = "datef";

function to_bool($val) {
    return filter_var($val, FILTER_VALIDATE_BOOLEAN);
}

function assert_is_array($array, $name) {
    if (!is_array($array)) {
        throw new BadRequest("$name must be an array");
    }
}

function assert_is_numeric($num, $name) {
    if (!is_numeric($num)) {
        throw new BadRequest("$name must be numeric");
    }
}

$sql_get_sync_sqn = <<<'EOD'
select (next_sync_sqn) from users where id = ?;
EOD;

function get_sync_sqn(PDO $dbh, int $user_id) {
    global $sql_get_sync_sqn;
    $sth = $dbh->prepare($sql_get_sync_sqn);
    $sth->execute([$user_id]);
    return $sth->fetchColumn();
}

$sql_update_sync_sqn = <<<'EOD'
update users set
next_sync_sqn = next_sync_sqn + 1
where id = ?
EOD;

function update_sync_sqn(PDO $dbh, int $user_id) {
    global $sql_update_sync_sqn;
    $sth = $dbh->prepare($sql_update_sync_sqn);
    $sth->execute([$user_id]);
}

$sql_sync_activities_insert = <<<'EOD'
insert into activities (user_id, last_sync_sqn, last_modification, start, end, type)
values (:user_id, :sync_sqn, :last_modification, :start, :end, :type);
EOD;

$sql_sync_activities_update = <<<'EOD'
update activities set
last_sync_sqn = :sync_sqn,
last_modification = :last_modification,
start = :start,
end = :end,
type = :type
where user_id = :user_id and id = :activity_id and last_modification < :last_modification;
EOD;

function sync_activities(PDO $dbh, int $user_id, int $sync_sqn, array $client_acts): array {
    global $sql_sync_activities_insert, $sql_sync_activities_update;

    $act_id_map = [];

    $client_acts_created = $client_acts['created'] ?? null;
    if (isset($client_acts_created)) {
        assert_is_array($client_acts_created, "activities.created");
        $sth = $dbh->prepare($sql_sync_activities_insert);
        foreach ($client_acts_created as $created_act) {
            assert_is_array($created_act, "activities.created[x]");
            $sth->execute([
                "user_id" => $user_id,
                "sync_sqn" => $sync_sqn,
                "last_modification" => $created_act['lastModification'] ?? null,
                "start" => $created_act['start'] ?? null,
                "end" => $created_act['end'] ?? null,
                "type" => $created_act['type'] ?? null
            ]);
            $act_id_map[] = [
                "id" => $dbh->lastInsertId()
            ];
        }
    }

    $client_acts_mod = $client_acts['modified'] ?? null;
    if (isset($client_acts_mod)) {
        assert_is_array($client_acts_mod, "activities.modified");
        $sth = $dbh->prepare($sql_sync_activities_update);
        foreach ($client_acts_mod as $mod_act) {
            assert_is_array($mod_act, "activities.modified[x]");
            assert_is_numeric($mod_act['id'], "activities.modified[x]['id']");
            // $mod_act['id'] = intval($mod_act['id']);
            $sth->execute([
                "user_id" => $user_id,
                "activity_id" => $mod_act['id'],
                "sync_sqn" => $sync_sqn,
                "last_modification" => $mod_act['lastModification'] ?? null,
                "start" => $mod_act['start'] ?? null,
                "end" => $mod_act['end'] ?? null,
                "type" => $mod_act['type'] ?? null
            ]);
        }
    }

    return $act_id_map;
}

// Note it is missing a semicolon at the end.
// An additional condition can be appended.
$sql_get_updated_activities = <<<EOD
select id, {$df('last_modification')}, {$df('start')}, {$df('end')}, type
from activities
where user_id = :user_id
EOD;

function get_updated_activities(PDO $dbh, int $user_id, ?int $last_sync_sqn): array {
    global $sql_get_updated_activities;

    $sql = $sql_get_updated_activities;

    $params = [
        "user_id" => $user_id
    ];

    if (isset($last_sync_sqn)) {
        $sql .= " and last_sync_sqn > :last_sync_sqn";
        $params["last_sync_sqn"] = $last_sync_sqn;
    }
    $sql .= ";";

    $sth = $dbh->prepare($sql);
    $sth->execute($params);
    return $sth->fetchAll(PDO::FETCH_ASSOC);
}

$sql_sync_moods_insert = <<<'EOD'
insert into moods (user_id, last_sync_sqn, last_modification,
assessment, satisfaction, calmness, comfort, relaxation, energy, wakefulness,
event_negative_intensity, event_positive_intensity, alone, surrounding_people_liking,
surrounding_people_type, location, satisfied_with_yourself, consider_yourself_failure,
acted_impulsively, acted_aggressively)

values (:user_id, :sync_sqn, :last_modification,
:assessment, :satisfaction, :calmness, :comfort, :relaxation, :energy, :wakefulness,
:event_negative_intensity, :event_positive_intensity, :alone, :surrounding_people_liking,
:surrounding_people_type, :location, :satisfied_with_yourself, :consider_yourself_failure, :acted_impulsively, :acted_aggressively);
EOD;

$sql_sync_moods_update = <<<'EOD'
update moods set

last_sync_sqn = :sync_sqn,
last_modification = :last_modification,
assessment = :assessment,
satisfaction = :satisfaction,
calmness = :calmness,
comfort = :comfort,
relaxation = :relaxation,
energy = :energy,
wakefulness = :wakefulness,
event_negative_intensity = :event_negative_intensity,
event_positive_intensity = :event_positive_intensity,
alone = :alone,
surrounding_people_liking = :surrounding_people_liking,
surrounding_people_type = :surrounding_people_type,
location = :location,
satisfied_with_yourself = :satisfied_with_yourself,
consider_yourself_failure = :consider_yourself_failure,
acted_impulsively = :acted_impulsively,
acted_aggressively = :acted_aggressively

where user_id = :user_id and id = :id and last_modification < :last_modification;
EOD;

function sync_moods(PDO $dbh, int $user_id, int $sync_sqn, array $client_moods): array {
    global $sql_sync_moods_insert, $sql_sync_moods_update;

    $mood_id_map = [];

    $client_moods_created = $client_moods['created'] ?? null;
    if (isset($client_moods_created)) {
        assert_is_array($client_moods_created, "moods.created");
        $sth = $dbh->prepare($sql_sync_moods_insert);
        foreach ($client_moods_created as $created_mood) {
            assert_is_array($created_mood, "moods.created[x]");
            $subst_arr = $created_mood;
            arr_snake_to_camel($subst_arr);
            $subst_arr['user_id'] = $user_id;
            $subst_arr['sync_sqn'] = $sync_sqn;
            $sth->execute([
                "user_id" => $user_id,
                "sync_sqn" => $sync_sqn,
                "last_modification" => $created_mood['lastModification'] ?? null,
                "assessment" => $created_mood['assessment'] ?? null,
                "satisfaction" => $created_mood['satisfaction'] ?? null,
                "calmness" => $created_mood['calmness'] ?? null,
                "comfort" => $created_mood['comfort'] ?? null,
                "relaxation" => $created_mood['relaxation'] ?? null,
                "energy" => $created_mood['energy'] ?? null,
                "wakefulness" => $created_mood['wakefulness'] ?? null,
                "event_negative_intensity" => $created_mood['event_negative_intensity'] ?? null,
                "event_positive_intensity" => $created_mood['event_positive_intensity'] ?? null,
                "alone" => $created_mood['alone'] ?? null,
                "surrounding_people_liking" => $created_mood['surrounding_people_liking'] ?? null,
                "surrounding_people_type" => $created_mood['surrounding_people_type'] ?? null,
                "location" => $created_mood['location'] ?? null,
                "satisfied_with_yourself" => $created_mood['satisfied_with_yourself'] ?? null,
                "consider_yourself_failure" => $created_mood['consider_yourself_failure'] ?? null,
                "acted_impulsively" => $created_mood['acted_impulsively'] ?? null,
                "acted_aggressively" => $created_mood['acted_aggressively'] ?? null
            ]);
            $mood_id_map[] = [
                "id" => $dbh->lastInsertId()
            ];
        }
    }

    $client_moods_mod = $client_moods['modified'] ?? null;
    if (isset($client_moods_mod)) {
        assert_is_array($client_moods_mod, "moods.modified");
        $sth = $dbh->prepare($sql_sync_moods_update);
        foreach ($client_moods_mod as $mod_mood) {
            assert_is_array($mod_mood, "moods.modified[x]");
            assert_is_numeric($mod_mood['id'], "moods.modified[x]['id']");
            // $mod_mood['id'] = intval($mod_mood['id']);
            $sth->execute([
                "id" => $mod_mood['id'],
                "user_id" => $user_id,
                "sync_sqn" => $sync_sqn,
                "last_modification" => $created_mood['lastModification'] ?? null,
                "assessment" => $created_mood['assessment'] ?? null,
                "satisfaction" => $created_mood['satisfaction'] ?? null,
                "calmness" => $created_mood['calmness'] ?? null,
                "comfort" => $created_mood['comfort'] ?? null,
                "relaxation" => $created_mood['relaxation'] ?? null,
                "energy" => $created_mood['energy'] ?? null,
                "wakefulness" => $created_mood['wakefulness'] ?? null,
                "event_negative_intensity" => $created_mood['event_negative_intensity'] ?? null,
                "event_positive_intensity" => $created_mood['event_positive_intensity'] ?? null,
                "alone" => $created_mood['alone'] ?? null,
                "surrounding_people_liking" => $created_mood['surrounding_people_liking'] ?? null,
                "surrounding_people_type" => $created_mood['surrounding_people_type'] ?? null,
                "location" => $created_mood['location'] ?? null,
                "satisfied_with_yourself" => $created_mood['satisfied_with_yourself'] ?? null,
                "consider_yourself_failure" => $created_mood['consider_yourself_failure'] ?? null,
                "acted_impulsively" => $created_mood['acted_impulsively'] ?? null,
                "acted_aggressively" => $created_mood['acted_aggressively'] ?? null
            ]);
        }
    }

    return $mood_id_map;
}

// Note it is missing a semicolon at the end.
// An additional condition can be appended.
$sql_get_updated_moods = <<<EOD
select id, {$df('last_modification')},
{$df('assessment')}, satisfaction, calmness, comfort, relaxation, energy, wakefulness

from moods
where user_id = :user_id
EOD;

function get_updated_moods(PDO $dbh, int $user_id, ?int $last_sync_sqn): array {
    global $sql_get_updated_moods;

    $sql = $sql_get_updated_moods;

    $params = [
        "user_id" => $user_id
    ];

    if (isset($last_sync_sqn)) {
        $sql .= " and last_sync_sqn > :last_sync_sqn";
        $params["last_sync_sqn"] = $last_sync_sqn;
    }
    $sql .= ";";

    $sth = $dbh->prepare($sql);
    $sth->execute($params);
    return $sth->fetchAll(PDO::FETCH_ASSOC);
}

$sql_insert_accelerometer_data = <<<'EOD'
insert into accelerometer_data (user_id, last_sync_sqn, time, x, y, z)
values (:user_id, :sync_sqn, :time, :x, :y, :z)
ON DUPLICATE KEY UPDATE;
EOD;

function insert_accelerometer_data(PDO $dbh, int $user_id, int $sync_sqn, ?array $client_accels): array {
    global $sql_insert_accelerometer_data;

    if (isset($client_accels)) {
        $sth = $dbh->prepare($sql_insert_accelerometer_data);
        foreach ($client_accels as $accel) {
            $sth->execute([
                "user_id" => $user_id,
                "sync_sqn" => $sync_sqn,
                "time" => $accel['time'] ?? null,
                "x" => $accel['y'] ?? null,
                "y" => $accel['y'] ?? null,
                "z" => $accel['y'] ?? null
            ]);
        }
    }
}

// Note it is missing a semicolon at the end.
// An additional condition can be appended.
$sql_get_accelerometer_data = <<<EOD
select {$df('time')}, x, y, z
from accelerometer_data
where user_id = :user_id
EOD;

function get_accelerometer_data(PDO $dbh, int $user_id, ?int $last_sync_sqn): array {
    global $sql_get_accelerometer_data;

    $sql = $sql_get_accelerometer_data;

    $params = [
        "user_id" => $user_id
    ];

    if (isset($last_sync_sqn)) {
        $sql .= " and last_sync_sqn > :last_sync_sqn";
        $params["last_sync_sqn"] = $last_sync_sqn;
    }
    $sql .= ";";

    $sth = $dbh->prepare($sql);
    $sth->execute($params);
    return $sth->fetchAll(PDO::FETCH_ASSOC);
}

try {
    header('Content-type: application/json; charset=utf-8');

    $request = require "get_input.php";
    $json_pretty = $request['json_pretty'] ?? null;
    $json_pretty = to_bool($json_pretty);

    $sess_str = $request['session'] ?? null;

    if (!isset($sess_str)) {
        throw new BadRequest();
    }

    $dbh = require 'connect.php';
    $dbh->beginTransaction();
    $dbh->exec('set time_zone = "+00:00"; SET SESSION sql_mode="ALLOW_INVALID_DATES";');

    $sess = Session::fromString($sess_str);

    $user_id = $sess->authenticate($dbh);

    if ($user_id === false) {
        throw new Unauthorized();
    }

    $sync_sqn = get_sync_sqn($dbh, $user_id);

    if (!isset($sync_sqn)) {
        throw new RuntimeException("Cannot get sync sequence number");
    }

    $last_sync_sqn = $request['last_sync_sqn'] ?? null;
    if (isset($last_sync_sqn)) {
        assert_is_numeric($last_sync_sqn, "last_sync_sqn");
        // $last_sync_sqn = intval($last_sync_sqn);
    }

    $response = [
        "last_sync_sqn" => $sync_sqn,
        "activities" => [
            "created" => [],
            "modified" => []
        ],
        "moods" => [
            "created" => [],
            "modified" => []
        ],
        "accelerometer_data" => []
    ];

    $client_acts = $request['activities'] ?? null;
    if (isset($client_acts)) {
        $response['activities']['created'] = sync_activities($dbh, $user_id, $sync_sqn, $client_acts);
    }
    $response['activities']['modified'] = get_updated_activities($dbh, $user_id, $last_sync_sqn);

    $client_moods = $request['moods'] ?? null;
    if (isset($client_moods)) {
        $response['moods']['created'] = sync_moods($dbh, $user_id, $sync_sqn, $client_moods);
    }
    $response['moods']['modified'] = get_updated_moods($dbh, $user_id, $last_sync_sqn);

    $client_accels = $request['accelerometer_data'] ?? null;
    insert_accelerometer_data($dbh, $user_id, $sync_sqn, $client_accels);
    $response['accelerometer_data'] = get_accelerometer_data($dbh, $user_id, $last_sync_sqn);

    update_sync_sqn($dbh, $user_id);

    arr_snake_to_camel($response);

    $json_flags = JSON_THROW_ON_ERROR;
    if ($json_pretty) {
        $json_flags |= JSON_PRETTY_PRINT;
    }

    $json = json_encode($response, $json_flags);

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
