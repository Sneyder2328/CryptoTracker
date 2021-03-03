<?php

class LBCAd
{
    public $url = "";
    public $listingName = "";
    public $minAmount = "";
    public $maxAmount = "";
    public $profile = "";
    public $price = "";
    public $bankName = "";
}

require_once("database.php");
require_once("/var/www/html/CryptoTracker/trackView.php");
track("getLBCAds.php");

$mysqli = mysqli();
$code = $_POST['code'];
if($code != "siuuuuuuuuuu"){
    return;
}

$result = $mysqli->query("SELECT url,listingName,minAmount,maxAmount,`profile`,price,bankName FROM LBCAd") or die($mysqli->error);

if ($result->num_rows > 0) {
    $LBCAds = [];
    while ($row = $result->fetch_assoc()) {
        $LBCAd = new LBCAd();
        $LBCAd->url = $row['url'];
        $LBCAd->listingName = $row['listingName'];
        $LBCAd->minAmount = $row['minAmount'];
        $LBCAd->maxAmount = $row['maxAmount'];
        $LBCAd->profile = $row['profile'];
        $LBCAd->price = $row['price'];
        $LBCAd->bankName = $row['bankName'];
        $LBCAds[] = $LBCAd;
    }
    echo json_encode($LBCAds);
} else {
    echo json_encode("no data");
}
$mysqli->close();