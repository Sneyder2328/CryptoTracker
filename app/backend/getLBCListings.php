<?php

class LBCListing
{
    public $url = "";
    public $listingName = "";
    public $amountSample = 0.0;
    public $active = false;
    public $amIBuying = false;
    public $myPrice = 0.0;
    public $keyword = "";
}

require_once("/var/www/html/CryptoTracker/database.php");
require_once("/var/www/html/CryptoTracker/trackView.php");
track("getLBCListings.php");

$mysqli = mysqli();
$code = $_POST['code'];
if($code != "yeahhhhhhh"){
    return;
}

$result = $mysqli->query("SELECT url,listingName,amountSample,active,myPrice,amIBuying,keyword FROM LBCListing") or die($mysqli->error);

if ($result->num_rows > 0) {
    $LBCListings = [];
    while ($row = $result->fetch_assoc()) {
        $LBCListing = new LBCListing();
        $LBCListing->url = $row['url'];
        $LBCListing->listingName = $row['listingName'];
        $LBCListing->amountSample = $row['amountSample'];
        $LBCListing->active = $row['active'] == 1;
        $LBCListing->myPrice = $row['myPrice'];
        $LBCListing->amIBuying = $row['amIBuying'] == 1;
        $LBCListing->keyword = $row['keyword'];
        $LBCListings[] = $LBCListing;
    }
    echo json_encode($LBCListings);
} else {
    echo json_encode("no data");
}
$mysqli->close();
