<?php
$userId = $_REQUEST['userId'];
$sessionId = $_REQUEST['sessionId'];

require_once("sessionVerification.php");
require_once("Transaction.php");
require_once("database.php");

$sessionVerified = verifySession($userId, $sessionId);

if ($userId != "" && $sessionVerified) {
    $mysqli = mysqli();
    $result = $mysqli->query("SELECT id,tradingPair,quantity,price,`date`,fee FROM TransactionTable WHERE userId='$userId'") or die($mysqli->error);

    $transactions = [];
    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $transaction = new Transaction();
            $transaction->userId = $userId;
            $transaction->id = $row['id'];
            $transaction->tradingPair = $row['tradingPair'];
            $transaction->quantity = $row['quantity'];
            $transaction->price = $row['price'];
            $transaction->date = $row['date'];
            $transaction->fee = $row['fee'];
            $transactions[] = $transaction;
        }
    }
    echo json_encode($transactions);
    $mysqli->close();
} else {
    echo json_encode("userId no provided");
}