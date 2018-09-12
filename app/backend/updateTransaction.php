<?php
require_once("sessionVerification.php");
require_once("database.php");
require_once("SyncResponse.php");

$id = $_POST['id'];
$userId = $_POST['userId'];
$sessionId = $_POST['sessionId'];
$price = $_POST['price'];
$quantity = $_POST['quantity'];
$tradingPair = $_POST['tradingPair'];
$date = $_POST['date'];
$fee = $_POST['fee'];

$response = new SyncResponse();
$response->typeSync = "update";

$sessionVerified = verifySession($userId, $sessionId);

if ($id != "" && $userId != "" && $price != "" && $quantity != "" && $tradingPair != "" && $date != "" && $sessionVerified) {
    if ($fee == "") $fee = 0;
    $mysqli = mysqli();
    $request = $mysqli->query("UPDATE TransactionTable SET id='$id', price=$price, userId='$userId', quantity=$quantity, tradingPair='$tradingPair', `date`=`date`, fee=$fee WHERE id='$id'");
    $mysqli->close();

    $response->successful = true;
} else {
    $response->successful = false;
}
echo json_encode($response);