<?php

require_once("/var/www/html/CryptoTracker/database.php");
$mysqli = mysqli();

$currency = "TTT";
$rate = 12.5;
$mysqli->query("REPLACE INTO ExchangeRate(symbol, rate) VALUES('$currency', $rate)") or die("Error in replace ExchangeRate currency=".$currency."  rate=".$rate." Error=".$mysqli->error);
