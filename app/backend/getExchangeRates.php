<?php

class ExchangeRate {
    public $symbol = "";
    public $rate = "";
}

require_once("database.php");

$mysqli = mysqli();

$result = $mysqli->query("SELECT symbol,rate FROM ExchangeRate") or die($mysqli->error);

if ($result->num_rows > 0) {
    $exchangeRates = [];
    while ($row = $result->fetch_assoc()) {
        $exchangeRate = new ExchangeRate();
        $exchangeRate->symbol = $row['symbol'];
        $exchangeRate->rate = $row['rate'];
        $exchangeRates[] = $exchangeRate;
    }
    echo json_encode($exchangeRates);
} else {
    echo json_encode("no data");
}
$mysqli->close();