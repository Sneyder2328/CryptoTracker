<?php

require_once("/var/www/html/CryptoTracker/database.php");
require_once("/var/www/html/CryptoTracker/trackView.php");
$mysqli = mysqli();

$server_key = 'AAAAZSjhHYo:APA91bHi3F8Fct244JtCel2qhldhT268PjsOoBAkk7TQfoARDEoy_faSSvaNrZYshTSpM1OB2ZFNZb0F1ZUaMWQtTuXTGxmAiMml9N0GmWb2hg9zwrJGHxHfp0Jk1ZBBJeSJPb3T1vOk';
$headers = array('Content-Type:application/json', 'Authorization:key=' . $server_key);

function sendNotification($profile, $price, $min_amount, $max_amount, $bank_name, $url_ad){
    global $mysqli;
    $resultFirebaseTokenId = $mysqli->query("SELECT firebaseTokenId FROM LBCTrackerUser WHERE firebaseTokenId != ''") or die($mysqli->error);
    if ($resultFirebaseTokenId->num_rows > 0) {
        $data = array(
            "profile" => $profile,
            "price" => $price,
            "min_amount" => $min_amount,
            "max_amount" => $max_amount,
            "bank_name" => $bank_name,
            "url_ad" => $url_ad);

        $url = 'https://fcm.googleapis.com/fcm/send';
        $fields = array();
        $fields['data'] = $data;
        $fields['priority'] = "high";
        global $headers;
        while ($data = $resultFirebaseTokenId->fetch_assoc()) {
            $firebaseTokenId = $data['firebaseTokenId'];
            if($firebaseTokenId=="") return;
            $fields['to'] = $firebaseTokenId;
            $ch = curl_init();
            curl_setopt($ch, CURLOPT_URL, $url);
            curl_setopt($ch, CURLOPT_POST, true);
            curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
            curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));

            $curlResult = json_decode(curl_exec($ch), true);
            curl_close($ch);
            $success = $curlResult['success'];
            print_r($curlResult);
            echo "<br>Sending FCM $data to $firebaseTokenId result = $success<br>";
        }
    }
}

function hasAGoodRecord($trade_count, $feedback_score){
    return $trade_count > 10 && $feedback_score > 90;
}


function analiseListing($listing, $keyword, $amIBuying, $amount_sample, $my_price, $listing_name, $receive_notifications)
{
    foreach ($listing["data"]["ad_list"] as $ad) {
        $bank_name = $ad["data"]["bank_name"];
        $bank_name = str_replace('\'', '', $bank_name);
        $bank_name = str_replace('"', '', $bank_name);
        $url_ad = $ad["actions"]["public_view"];
        $price = $ad["data"]["temp_price"];
        $min_amount = $ad["data"]["min_amount"];
        if($min_amount==null){
            $min_amount = 0;
        }
        $max_amount = $ad["data"]["max_amount"];
        if($max_amount==null){
            $max_amount = 1000000000000;
        }
        $profile = $ad["data"]["profile"]["name"];
        $trade_count = $ad["data"]["profile"]["trade_count"];
        $feedback_score = $ad["data"]["profile"]["feedback_score"];
     /*   echo "<br>banknamme=".$bank_name."<br>";
        var_dump($price);
        echo "<br>";
        var_dump($profile);
        echo "<br>";
        var_dump($url_ad);
        echo "<br>";
        var_dump($min_amount);
        echo "<br>";
        var_dump($max_amount);
        echo "<br>";*/
        if(stripos($bank_name, "venezuela") !== false && stripos($bank_name, "ip venezuela") !== false){
            return;
        }
        if ((stripos($bank_name, $keyword) !== false || $keyword == "") && stripos($profile, "stas2328") === false) { // if bank_name contains $keyword and does not contain stas2328(my user)
            global $mysqli;
            // then save the ad into the database
            $mysqli->query("REPLACE INTO LBCAd(url,listingName,minAmount,maXAmount,price,`profile`,bankName) VALUES('$url_ad', '$listing_name', $min_amount, $max_amount, $price, '$profile', '$bank_name')") or die($mysqli->error);
            if ($amount_sample >= $min_amount && $amount_sample <= $max_amount && $receive_notifications) { // if amount sample fits in(i.e min<=sample<=max)
                if ($amIBuying == 1) {
                    if ($price >= $my_price && hasAGoodRecord($trade_count, $feedback_score)) {
                        sendNotification($profile, $price, $min_amount, $max_amount, $bank_name, $url_ad);
                        echo $profile . "<br>";
                    }
                } else {
                    if ($price <= $my_price && hasAGoodRecord($trade_count, $feedback_score)) {
                        sendNotification($profile, $price, $min_amount, $max_amount, $bank_name, $url_ad);
                        echo $profile . "<br>";
                    }
                }
            }
        }
    }
}

$mysqli->query("DELETE from LBCAd") or die($mysqli->error); // delete all current LBC ads
$result = $mysqli->query("SELECT keyword, url, amIBuying, amountSample, listingName, myPrice, receiveNotifications FROM LBCListing WHERE active=1") or die($mysqli->error);


if ($result->num_rows > 0) {
    while ($listing = $result->fetch_assoc()) {
        $keyword = $listing['keyword'];
        $url = $listing['url'];
        $amIBuying = $listing['amIBuying'];
        $receive_notifications = $listing['receiveNotifications'] == 1;
        $listing_name = $listing['listingName'];
        $amount_sample = $listing['amountSample'];
        $my_price = $listing['myPrice'];
        $repeat = true;
        while ($repeat) {
            $json = file_get_contents($url);
            $json_data = json_decode($json, true);
            echo "Actual listing ".$listing_name." - ".$url . "<br>";
            analiseListing($json_data, $keyword, $amIBuying, $amount_sample, $my_price, $listing_name, $receive_notifications);
            $next_link = $json_data["pagination"]["next"];
            $repeat = $next_link != "";
            $url = $next_link;
        }
    }
} else {
    echo "No data found on ListingUnderTracking";
}

track("checkLBC.php");

$mysqli->close();

