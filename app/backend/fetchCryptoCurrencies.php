<?php
$currencies = $_REQUEST['currencies'];

require_once("database.php");
require_once("CryptoCurrency.php");

if ($currencies == "allCurrencies") {
    $mysqli = mysqli();
    $result = $mysqli->query("SELECT id,`name`,symbol,rank,priceUsd,volumeLast24H,marketCap,availableSupply,totalSupply,maxSupply,percentChangeLast1H,percentChangeLast24H,percentChangeLast7D,lastUpdated
FROM CryptoCurrency ORDER BY `rank` ASC") or die($mysqli->error);

    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $cryptoCurrency = new CryptoCurrency();
            $cryptoCurrency->id = $row['id'];
            $cryptoCurrency->name = $row['name'];
            $cryptoCurrency->symbol = $row['symbol'];
            $cryptoCurrency->rank = $row['rank'];
            $cryptoCurrency->price = $row['priceUsd'];
            $cryptoCurrency->volumeLast24H = $row['volumeLast24H'];
            $cryptoCurrency->marketCap = $row['marketCap'];
            $cryptoCurrency->availableSupply = $row['availableSupply'];
            $cryptoCurrency->totalSupply = $row['totalSupply'];
            $cryptoCurrency->maxSupply = $row['maxSupply'];
            $cryptoCurrency->percentChangeLast1H = $row['percentChangeLast1H'];
            $cryptoCurrency->percentChangeLast24H = $row['percentChangeLast24H'];
            $cryptoCurrency->percentChangeLast7D = $row['percentChangeLast7D'];
            $cryptoCurrency->lastUpdated = $row['lastUpdated'];
            $cryptoCurrencies[] = $cryptoCurrency;
        }
        echo json_encode($cryptoCurrencies);
    } else {
        echo json_encode("no data");
    }
    $mysqli->close();
} else if ($currencies != "") {
    $mysqli = mysqli();
    $result = $mysqli->query("SELECT `name`,symbol,rank,priceUsd,volumeLast24H,marketCap,availableSupply,totalSupply,maxSupply,percentChangeLast1H,percentChangeLast24H,percentChangeLast7D,lastUpdated
FROM CryptoCurrency WHERE id='$currencies'") or die($mysqli->error);

    if ($result->num_rows > 0) {
        if ($data = $result->fetch_array()) {
            $cryptoCurrency = new CryptoCurrency();
            $cryptoCurrency->id = $currencies;
            $cryptoCurrency->name = $data['name'];
            $cryptoCurrency->symbol = $data['symbol'];
            $cryptoCurrency->rank = $data['rank'];
            $cryptoCurrency->price = $data['priceUsd'];
            $cryptoCurrency->volumeLast24H = $data['volumeLast24H'];
            $cryptoCurrency->marketCap = $data['marketCap'];
            $cryptoCurrency->availableSupply = $data['availableSupply'];
            $cryptoCurrency->totalSupply = $data['totalSupply'];
            $cryptoCurrency->percentChangeLast1H = $data['percentChangeLast1H'];
            $cryptoCurrency->percentChangeLast24H = $data['percentChangeLast24H'];
            $cryptoCurrency->percentChangeLast7D = $data['percentChangeLast7D'];
            $cryptoCurrency->lastUpdated = $data['lastUpdated'];
            echo json_encode($cryptoCurrency);
        }
    }
}