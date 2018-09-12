<?php

require 'simple_html_dom.php';
require_once("NewsArticle.php");
require_once("database.php");

$newsArticles = array();

scrapNewsbtc();
scrapccn();
scrapNulltx();
scrapcoindesk();
scrapCryptoglobe();
scrapCointelegraph();
scrapNewsBitcoin();
scrapBitcoinist();

$mysqli = mysqli();
foreach ($newsArticles as $newsArticle) {
    try {
        $title = str_replace("'", "''", $newsArticle->title);
        $date = intval($newsArticle->pubDate)*1000;
        $mysqli->query("REPLACE INTO NewsArticle(title,link,pubDate,imgUrl) VALUES('$title', '$newsArticle->link', '$date', '$newsArticle->imgUrl')") or die($mysqli->error);
    } catch (Exception $e) {
        echo "Error: " . $e;
    }
}
$millisecondsIn40Hours = 144000000;
$limitDate = (time()*1000) - $millisecondsIn40Hours;
$mysqli->query("DELETE FROM NewsArticle WHERE pubDate<=$limitDate") or die($mysqli->error);
$mysqli->close();
echo json_encode("done");


function scrapCryptoglobe()
{
    $content = file_get_contents("https://www.cryptoglobe.com/latest/feed/");
    $rss = new SimpleXmlElement($content);

    foreach($rss->channel->item as $item){
        $newsArticle = new NewsArticle();
        $newsArticle->title = html_entity_decode($item->title, ENT_COMPAT, 'utf-8');
        $newsArticle->link = $item->link;
        $newsArticle->pubDate = strtotime($item->pubDate);
        $newsArticle->imgUrl = str_replace("https://static.cryptoglobe.com/https://static.cryptoglobe.com/", "https://static.cryptoglobe.com/", $item->image);
        if ($newsArticle->imgUrl != "" && $newsArticle->title != "" && $newsArticle->link != "" && $newsArticle->pubDate != "" && strpos($newsArticle->link, 'cryptoglobe') !== false) {
            global $newsArticles;
            $newsArticles[] = $newsArticle;
        }
    }

}

function scrapNewsBitcoin()
{
    $content = file_get_contents("https://news.bitcoin.com/feed/");
    $rss = new SimpleXmlElement($content);

    foreach($rss->channel->item as $item){
        $newsArticle = new NewsArticle();
        $newsArticle->title = html_entity_decode($item->title, ENT_COMPAT, 'utf-8');
        $newsArticle->link = $item->link;
        $newsArticle->pubDate = strtotime($item->pubDate);
        $dom = str_get_html($item->description);
        $newsArticle->imgUrl = $dom->find("img", 0)->attr['src'];
        if ($newsArticle->imgUrl != "" && $newsArticle->title != "" && $newsArticle->link != "" && $newsArticle->pubDate != "" && strpos($newsArticle->link, 'news.bitcoin') !== false) {
            global $newsArticles;
            $newsArticles[] = $newsArticle;
        }
    }

}

function scrapBitcoinist()
{
    $content = file_get_contents("http://bitcoinist.com/feed/");
    $rss = new SimpleXmlElement($content);

    foreach($rss->channel->item as $item){
        $newsArticle = new NewsArticle();
        $newsArticle->title = html_entity_decode($item->title, ENT_COMPAT, 'utf-8');
        $newsArticle->link = $item->link;
        $newsArticle->pubDate = strtotime($item->pubDate);
        $dom = str_get_html($item->description);
        $newsArticle->imgUrl = $dom->find("img", 0)->attr['src'];
        if ($newsArticle->imgUrl != "" && $newsArticle->title != "" && $newsArticle->link != "" && $newsArticle->pubDate != "" && strpos($newsArticle->link, 'bitcoinist') !== false) {
            global $newsArticles;
            $newsArticles[] = $newsArticle;
        }
    }

}


function file_get_contents_curl($url) {
    $curl = curl_init();
    curl_setopt($curl, CURLOPT_URL, $url);
    curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($curl, CURLOPT_USERAGENT, 'Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.0.3705; .NET CLR 1.1.4322)');
    $data = curl_exec($curl);
    curl_close($curl);
    return $data;
}


function scrapCointelegraph()
{
    $content = file_get_contents_curl("https://cointelegraph.com/rss");
    $rss = new SimpleXmlElement($content);
    $namespaces = $rss->getNamespaces(true);
    foreach($rss->channel->item as $item){
        $media = $item->children($namespaces["media"]);
        $newsArticle = new NewsArticle();
        $newsArticle->title = html_entity_decode($item->title, ENT_COMPAT, 'utf-8');
        $newsArticle->link = $item->link;
        $newsArticle->pubDate = strtotime($item->pubDate);
        $newsArticle->imgUrl = $media->content->attributes()["url"];
        if ($newsArticle->imgUrl != "" && $newsArticle->title != "" && $newsArticle->link != "" && $newsArticle->pubDate != "" && strpos($newsArticle->link, 'cointelegraph') !== false) {
            global $newsArticles;
            $newsArticles[] = $newsArticle;
        }
    }

}

function scrapNewsbtc()
{
    try {
        $html = file_get_html("https://www.newsbtc.com/");

        $scraper = $html->find('.type-post');

        foreach ($scraper as $article) {
            $newsArticle = new NewsArticle();
            $newsArticle->imgUrl = (string)$article->find('a figure img', 0)->attr['data-cfsrc'];
            $newsArticle->title = html_entity_decode((string)$article->find('div a h3', 0)->plaintext, ENT_COMPAT, 'utf-8');
            $newsArticle->link = (string)$article->find('.featured-image', 0)->attr['href'];
            $date = (string)$article->find('.date', 0)->plaintext;
            $newsArticle->pubDate = strtotime(trim($date));
            if ($newsArticle->imgUrl != "" && $newsArticle->title != "" && $newsArticle->link != "" && $newsArticle->pubDate != "" && strpos($newsArticle->link, 'newsbtc') !== false) {
                global $newsArticles;
                $newsArticles[] = $newsArticle;
            }
        }
    } catch (Exception $e) {
        echo "Error: " . $e;
    }
}

function scrapccn()
{
    try {
        $html = file_get_html("https://www.ccn.com/");

        $scraper = $html->find('.type-post');

        foreach ($scraper as $article) {
            $newsArticle = new NewsArticle();
            $newsArticle->imgUrl = (string)$article->find('div a img', 0)->attr['data-cfsrc'];
            $newsArticle->title = html_entity_decode((string)$article->find('header h4 a', 0)->plaintext, ENT_COMPAT, 'utf-8');
            $newsArticle->link = (string)$article->find('header h4 a', 0)->attr['href'];
            $newsArticle->pubDate = strtotime((string)$article->find('header .entry-meta time', 0)->attr['datetime']);
            if ($newsArticle->imgUrl != "" && $newsArticle->title != "" && $newsArticle->link != "" && $newsArticle->pubDate != "" && strpos($newsArticle->link, 'ccn') !== false) {
                global $newsArticles;
                $newsArticles[] = $newsArticle;
            }
        }
    } catch (Exception $e) {
        echo "Error: " . $e;
    }
}


function scrapNulltx()
{
    try {
        $html = file_get_html("https://nulltx.com/");

        $scraper = $html->find('.td_module_1');

        foreach ($scraper as $article) {
            $newsArticle = new NewsArticle();
            $newsArticle->imgUrl = (string)$article->find('.td-module-thumb a img', 0)->attr['src'];
            $newsArticle->title = html_entity_decode((string)$article->find('.td-module-thumb a', 0)->attr['title'], ENT_COMPAT, 'utf-8');
            $newsArticle->link = (string)$article->find('.td-module-thumb a', 0)->attr['href'];
            $newsArticle->pubDate = strtotime((string)$article->find('.td-module-meta-info .td-post-date time', 0)->attr['datetime']);
            if ($newsArticle->imgUrl != "" && $newsArticle->title != "" && $newsArticle->link != "" && $newsArticle->pubDate != "" && strpos($newsArticle->link, 'nulltx') !== false) {
                global $newsArticles;
                $newsArticles[] = $newsArticle;
            }
        }
    } catch (Exception $e) {
        echo "Error: " . $e;
    }
}


function scrapcoindesk()
{
    try {
        $html = file_get_html("https://www.coindesk.com/category/news/");

        $scraper = $html->find('.type-post');

        foreach ($scraper as $article) {
            $newsArticle = new NewsArticle();
            $newsArticle->imgUrl = (string)$article->find('.picture a img', 0)->attr['data-cfsrc'];
            $newsArticle->title = html_entity_decode((string)$article->find('.post-info h3 a', 0)->plaintext, ENT_COMPAT, 'utf-8');
            $newsArticle->link = (string)$article->find('.post-info h3 a', 0)->attr['href'];
            $newsArticle->pubDate = strtotime((string)$article->find('.post-info .timeauthor time', 0)->attr['datetime']);
            if ($newsArticle->imgUrl != "" && $newsArticle->title != "" && $newsArticle->link != "" && $newsArticle->pubDate != "" && strpos($newsArticle->link, 'coindesk') !== false) {
                global $newsArticles;
                $newsArticles[] = $newsArticle;
            }
        }
    } catch (Exception $e) {
        echo "Error: " . $e;
    }
}