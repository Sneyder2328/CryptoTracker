<?php

require_once("NewsArticle.php");
require_once("database.php");

$mysqli = mysqli();

$result = $mysqli->query("SELECT title,link,pubDate,imgUrl FROM NewsArticle") or die($mysqli->error);

if ($result->num_rows > 0) {
    $newsArticles = [];
    while ($row = $result->fetch_assoc()) {
        $newsArticle = new NewsArticle();
        $newsArticle->title = $row['title'];
        $newsArticle->link = $row['link'];
        $newsArticle->pubDate = $row['pubDate'];
        $newsArticle->imgUrl = $row['imgUrl'];
        $newsArticles[] = $newsArticle;
    }
    echo json_encode($newsArticles);
} else {
    echo json_encode("no data");
}
$mysqli->close();