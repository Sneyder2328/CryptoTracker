<?php

class GlobalData
{
    public $id = "";
    public $totalMarketCapUsd = "";
    public $total24HVolumeUsd = "";
    public $bitcoinPercentageOfMarketCap = "";
    public $activeCurrencies = "";
    public $activeMarkets = "";
    public $lastUpdated = "";
}

require_once("database.php");
require_once("/var/www/html/CryptoTracker/trackView.php");
track("getLBCAds.php");
$mysqli = mysqli();
$result = $mysqli->query("SELECT id,totalMarketCapUsd,total24HVolumeUsd,bitcoinPercentageOfMarketCap,activeCurrencies,activeMarkets,lastUpdated FROM GlobalData") or die($mysqli->error);

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $globalData = new GlobalData();
        $globalData->id = $row['id'];
        $globalData->totalMarketCapUsd = $row['totalMarketCapUsd'];
        $globalData->total24HVolumeUsd = $row['total24HVolumeUsd'];
        $globalData->bitcoinPercentageOfMarketCap = $row['bitcoinPercentageOfMarketCap'];
        $globalData->activeCurrencies = $row['activeCurrencies'];
        $globalData->activeMarkets = $row['activeMarkets'];
        $globalData->lastUpdated = $row['lastUpdated'];
        echo json_encode($globalData);
    }
} else {
    echo json_encode("no data");
}
$mysqli->close();