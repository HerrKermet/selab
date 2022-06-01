<?php

// Converts a string from camel to snake case.
// If the argument is not a string, it is returned unchanged.
function camel_to_snake($camel) {
    if (is_string($camel)) {
        return strtolower(preg_replace('/([a-z])([A-Z])/', '$1_$2', $camel));
    }
    return $camel;
}

// Convert all array keys from camel to snake case.
function arr_camel_to_snake(array &$arr) {
    foreach ($arr as $key => $value) {
        if (is_array($value)) {
            arr_camel_to_snake($value);
        }
        unset($arr[$key]);
        $arr[camel_to_snake($key)] = $value;
    }
}

?>
