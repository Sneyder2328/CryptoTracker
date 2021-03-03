<?php
require_once("database.php");


$id = $_REQUEST['id'];
$firebaseTokenId = $_REQUEST['firebaseTokenId'];

if($id!=""){
    $mysqli = mysqli();
    $mysqli->query("REPLACE INTO LBCTrackerUser(id, firebaseTokenId) VALUES('$id', '$firebaseTokenId')") or die($mysqli->error);
    $mysqli->close();
    echo "true";
}
else {
    echo "false";
}