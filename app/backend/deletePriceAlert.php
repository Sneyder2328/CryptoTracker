<?php

require_once("sessionVerification.php");
require_once("database.php");
require_once("SyncResponse.php");

$id = $_POST['id'];
$userId = $_POST['userId'];
$sessionId = $_POST['sessionId'];

$sessionVerified = verifySession($userId, $sessionId);

$response = new SyncResponse();
$response->typeSync = "delete";

if ($id != "" && $sessionVerified) {
    $mysqli = mysqli();
    $request = $mysqli->query("DELETE FROM PriceAlert WHERE id='$id'");
    $mysqli->close();

    $response->successful = true;
} else {
    $response->successful = false;
}
echo json_encode($response);