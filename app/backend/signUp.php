<?php
require_once("utils.php");
require_once("database.php");
require_once("tokenVerification.php");
require_once("User.php");

$userId = $_POST['userId'];
$displayName = $_POST['displayName'];
$email = $_POST['email'];
$passwordPlain = $_POST['password'];
$typeLogin = $_POST['typeLogin'];


$firebaseTokenId = isset($_REQUEST['firebaseTokenId']) ? $_REQUEST['firebaseTokenId'] : "";

if ($passwordPlain != "") {
    $password = password_hash($passwordPlain, PASSWORD_DEFAULT, ['cost' => 8]);
} else {
    $password = "";
}
$verified = true;
if ($typeLogin == "Facebook" || $typeLogin == "Google") {
    $accessToken = $_REQUEST['accessToken'];
    $verified = verifyTokenId($userId, $typeLogin, $accessToken);
}
if ($userId != "" && $email != "" && $displayName != "" && $typeLogin != "" && $verified) {
    $mysqli = mysqli();

    $mysqli->query("INSERT INTO `User`(userId, displayName, email, `password`, typeLogin, firebaseTokenId) VALUES('$userId', '$displayName', '$email', '$password', '$typeLogin', '$firebaseTokenId')") or die($mysqli->error);

    $sessionId = bin2hex(random_bytes(50));
    $day = 86400000; // milliseconds a day has
    $expiryDate = currentTimeInMillis() + ($day * 180); // currentTimeInMillis + 2 days
    $mysqli->query("INSERT INTO `Session`(sessionId, userId, expiryDate) VALUES('$sessionId', '$userId', $expiryDate)") or die($mysqli->error);

    $mysqli->close();
    $user = new User();
    $user->userId = $userId;
    $user->displayName = $displayName;
    $user->sessionId = $sessionId;
    $user->firebaseTokenId = $firebaseTokenId;
    echo json_encode($user);
} else {
    echo json_encode("missing arguments or failed verification");
}