<?php

require_once("sessionVerification.php");
require_once("database.php");
require_once("SyncResponse.php");

$userId = $_POST['userId'];
$sessionId = $_POST['sessionId'];
$currencyId = $_POST['currencyId'];

$sessionVerified = verifySession($userId, $sessionId);

$response = new SyncResponse();
$response->typeSync = "delete";

if ($userId != "" && $currencyId != "" && $sessionVerified) {
    $mysqli = mysqli();
    $mysqli->query("DELETE FROM Favorite WHERE userId='$userId' AND currencyId=$currencyId") or die($mysqli->error);
    $mysqli->close();

    $response->successful = true;
} else {
    $response->successful = false;
}
echo json_encode($response);
