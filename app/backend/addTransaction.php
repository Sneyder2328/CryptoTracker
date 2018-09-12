<?php

$id = $_POST['id'];
$sessionId = $_POST['sessionId'];
$userId = $_POST['userId'];
$tradingPair = $_POST['tradingPair'];
$quantity = $_POST['quantity'];
$price = $_POST['price'];
$date = $_POST['date'];
$fee = $_POST['fee'];

require_once("sessionVerification.php");
require_once("database.php");
require_once("SyncResponse.php");

$sessionVerified = verifySession($userId, $sessionId);

$response = new SyncResponse();
$response->typeSync = "add";

if ($id != "" && $userId != "" && $tradingPair != "" && $quantity != "" && $price != "" && $date != "" && $sessionVerified) {
    if ($fee == "") $fee = 0;
    $mysqli = mysqli();
    $mysqli->query("INSERT INTO TransactionTable(id,userId,tradingPair,quantity,price,`date`,fee) VALUES('$id', '$userId', '$tradingPair', $quantity, $price, $date, $fee)") or die($mysqli->error);
    $mysqli->close();

    $response->successful = true;
} else {
    $response->successful = false;
}
echo json_encode($response);
