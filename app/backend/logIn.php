<?php
require_once("utils.php");
require_once("tokenVerification.php");
require_once("database.php");
require_once("User.php");


$email = $_REQUEST['email'];
$typeLogin = $_REQUEST['typeLogin'];
$password = $_REQUEST['password'];

$verified = true;
if($typeLogin=="Facebook" || $typeLogin=="Google"){
    $accessToken = $_REQUEST['accessToken'];
    $verified = verifyTokenId($_REQUEST['userId'], $typeLogin, $accessToken);
}

if($email!="" && $verified==true){
    $mysqli = mysqli();

    $result = $mysqli->query("SELECT `password`, userId, displayName, firebaseTokenId FROM `User` WHERE email='$email' LIMIT 1") or die($mysqli->error);

    if($data = $result->fetch_array()){
        $hashedPassword = $data['password'];
        $userId = $data['userId'];
        if (password_verify($password, $hashedPassword)) {

            $sessionId = bin2hex(random_bytes(50));
            $day = 86400000; // milliseconds a day has
            $expiryDate = currentTimeInMillis() + ($day * 180); // currentTimeInMillis + 2 days
            $mysqli->query("INSERT INTO Session(sessionId, userId, expiryDate) VALUES('$sessionId', '$userId', $expiryDate) ON DUPLICATE KEY UPDATE expiryDate = '$expiryDate', sessionId = '$sessionId'") or die($mysqli->error);
            $mysqli->close();

            $user = new User();
            $user->userId = $userId;
            $user->displayName = $data['displayName'];
            $user->sessionId = $sessionId;
            $user->firebaseTokenId = $data['firebaseTokenId'];
            echo json_encode($user);
        }
        else {
            echo "wrong password";
        }
    }

}
else {
    echo "missing arguments or invalid auth token";
}