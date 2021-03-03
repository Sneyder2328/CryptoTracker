<?php
require_once("database.php");

$keyword = $_POST['keyword'];
$url = $_POST['url'];
$amIBuying = $_POST['amIBuying'];
$amountSample = $_POST['amountSample'];
$active = $_POST['active'];
$listingName = $_POST['listingName'];
$myPrice = $_POST['myPrice'];

if ($url != "" && $amIBuying != "" && $amountSample != "" && $active != "" && $listingName != "" && $myPrice!="") {
    $mysqli = mysqli();
    $mysqli->query("REPLACE INTO LBCListing(url,keyword,amIBuying,amountSample,active,listingName,myPrice) VALUES('$url','$keyword',$amIBuying,$amountSample,$active,'$listingName',$myPrice)") or die($mysqli->error);
    $mysqli->close();
    echo "true";
} else {
    echo "false";
}