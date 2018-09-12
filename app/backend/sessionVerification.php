<?php
require_once("utils.php");
require_once("database.php");

function verifySession($userId, $sessionId)
{
    if ($userId != "" && $sessionId != "") {
        $mysqli = mysqli();
        $currentTimeInMillis = currentTimeInMillis();
        $reg = $mysqli->query("SELECT * FROM Session WHERE userId='$userId' AND sessionId='$sessionId' AND expiryDate >= $currentTimeInMillis") or die($mysqli->error);
        if ($data = $reg->fetch_array()) {
            $mysqli->close();
            return true;
        } else {
            $mysqli->close();
            return false;
        }
    } else {
        return false;
    }
}
