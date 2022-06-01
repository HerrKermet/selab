<?php

// Converts a string from snake to camel case.
// If the argument is not a string, it is returned unchanged.
function snake_to_camel($snake) {
    if (is_string($snake)) {
        $snake = explode("_", $snake);
        $count = count($snake);
        for ($i = 1; $i < $count; $i++) {
            $snake[$i] = ucfirst($snake[$i]);
        }
        return implode("", $snake);
    }
    return $snake;
}

// Convert all array keys from snake to camel case.
function arr_snake_to_camel(array &$arr) {
    foreach ($arr as $key => $value) {
        if (is_array($value)) {
            arr_snake_to_camel($value);
        }
        unset($arr[$key]);
        $arr[snake_to_camel($key)] = $value;
    }
}

?>
