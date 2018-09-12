<?php
require_once("sessionVerification.php");
require_once("database.php");
require_once("SyncResponse.php");

$id = $_POST['id'];
$priceBelow = $_POST['priceBelow'];
$priceAbove = $_POST['priceAbove'];
$userId = $_POST['userId'];
$sessionId = $_POST['sessionId'];

$response = new SyncResponse();
$response->typeSync = "update";

$sessionVerified = verifySession($userId, $sessionId);

if ($id != "" && $priceBelow != "" && $priceAbove != "" && $userId!="" && $sessionVerified) {
    $mysqli = mysqli();
    $request = $mysqli->query("UPDATE PriceAlert SET priceBelow=$priceBelow, priceAbove=$priceAbove, userId='$userId' WHERE id='$id'");
    $mysqli->close();

    $response->successful = true;
} else {
    $response->successful = false;
}
echo json_encode($response);