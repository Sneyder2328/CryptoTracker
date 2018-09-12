<?php

require_once("sessionVerification.php");
require_once("database.php");
require_once("SyncResponse.php");

$id = $_POST['id'];
$sessionId = $_POST['sessionId'];
$tradingPair = $_POST['tradingPair'];
$priceBelow = $_POST['priceBelow'];
$priceAbove = $_POST['priceAbove'];
$userId = $_POST['userId'];

$sessionVerified = verifySession($userId, $sessionId);

$response = new SyncResponse();
$response->typeSync = "add";

if ($id != "" && $tradingPair != "" && $priceBelow != "" && $priceAbove != "" && $userId != "" && $sessionVerified) {
    $mysqli = mysqli();
    $request = $mysqli->query("INSERT INTO PriceAlert(id, tradingPair, priceBelow, priceAbove, userId) VALUES('$id', '$tradingPair', $priceBelow, $priceAbove, '$userId')");
    $mysqli->close();

    $response->successful = true;
} else {
    $response->successful = false;
}
echo json_encode($response);