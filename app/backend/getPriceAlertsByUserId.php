<?php
$userId = $_REQUEST['userId'];
$sessionId = $_REQUEST['sessionId'];

require_once("sessionVerification.php");
require_once("database.php");
require_once("PriceAlert.php");

$sessionVerified = verifySession($userId, $sessionId);

if ($userId != "" && $sessionVerified) {
    $mysqli = mysqli();
    $result = $mysqli->query("SELECT id,tradingPair,priceAbove,priceBelow FROM PriceAlert WHERE userId='$userId'") or die($mysqli->error);

    $priceAlerts = [];
    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $priceAlert = new PriceAlert();
            $priceAlert->id = $row['id'];
            $priceAlert->tradingPair = $row['tradingPair'];
            $priceAlert->priceAbove = $row['priceAbove'];
            $priceAlert->priceBelow = $row['priceBelow'];
            $priceAlert->userId = $userId;
            $priceAlerts[] = $priceAlert;
        }
    }
    echo json_encode($priceAlerts);
    $mysqli->close();
}