<?php

$userId = $_REQUEST['userId'];
$sessionId = $_REQUEST['sessionId'];
$currencyId = $_REQUEST['currencyId'];

require_once("sessionVerification.php");
require_once("database.php");
require_once("SyncResponse.php");

$sessionVerified = verifySession($userId, $sessionId);

$response = new SyncResponse();
$response->typeSync = "add";

if ($userId != "" && $currencyId != "" && $sessionVerified) {
    $mysqli = mysqli();
    $mysqli->query("INSERT INTO Favorite(userId, currencyId) VALUES('$userId', $currencyId)") or die($mysqli->error);
    $mysqli->close();

    $response->successful = true;
} else {
    $response->successful = false;
}
echo json_encode($response);
