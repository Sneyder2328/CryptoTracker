<?php
$userId = $_REQUEST['userId'];
$sessionId = $_REQUEST['sessionId'];

class Favorite
{
    public $userId = "";
    public $currencyId = "";
}

require_once("sessionVerification.php");
require_once("database.php");

$sessionVerified = verifySession($userId, $sessionId);

if ($userId != "" && $sessionVerified) {
    $mysqli = mysqli();
    $result = $mysqli->query("SELECT currencyId FROM Favorite WHERE userId='$userId'") or die($mysqli->error);

    $favorites = [];
    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $favorite = new Favorite();
            $favorite->userId = $userId;
            $favorite->currencyId = $row['currencyId'];
            $favorites[] = $favorite;
        }
    }
    echo json_encode($favorites);
    $mysqli->close();
}