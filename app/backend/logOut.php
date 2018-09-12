<?php
$userId = $_POST['userId'];
$sessionId = $_POST['sessionId'];

require_once("sessionVerification.php");
$sessionVerified = verifySession($userId, $sessionId);

if ($userId != "" && $sessionId != "" && $sessionVerified) {
    require_once("database.php");
    $mysqli = mysqli();

    $mysqli->query("DELETE FROM `Session` WHERE userId = '$userId'") or die($mysqli->error);
    $mysqli->query("UPDATE `User` SET firebaseTokenId = '' WHERE userId = '$userId'") or die($mysqli->error);
    $mysqli->close();
    echo "true";
} else {
    echo "false";
}