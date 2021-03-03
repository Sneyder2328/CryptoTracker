<?php

require_once("/var/www/html/CryptoTracker/database.php");
require_once("/var/www/html/CryptoTracker/utils.php");


function get_ip_address(){
    foreach (array('HTTP_CLIENT_IP', 'HTTP_X_FORWARDED_FOR', 'HTTP_X_FORWARDED', 'HTTP_X_CLUSTER_CLIENT_IP', 'HTTP_FORWARDED_FOR', 'HTTP_FORWARDED', 'REMOTE_ADDR') as $key){
        if (array_key_exists($key, $_SERVER) === true){
            foreach (explode(',', $_SERVER[$key]) as $ip){
                $ip = trim($ip); // just to be safe

                if (filter_var($ip, FILTER_VALIDATE_IP, FILTER_FLAG_NO_PRIV_RANGE | FILTER_FLAG_NO_RES_RANGE) !== false){
                    return $ip;
                }
            }
        }
    }
}

function track($section){
    $currentTimeInMillis = currentTimeInMillis();

    $mysqli = mysqli();
    $ip_address = get_ip_address();
    $mysqli->query("INSERT INTO `View`(section, ip_address, `date`) VALUES('$section', '$ip_address', $currentTimeInMillis)") or die($mysqli->error);
    $mysqli->close();
}