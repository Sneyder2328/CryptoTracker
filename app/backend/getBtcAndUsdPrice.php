<?php

require 'vendor/autoload.php';
require 'simple_html_dom.php';

use Goutte\Client;

class BTCPrice
{
    public $BTC_USD;
    public $blackMarket;
    public $lbc;
    public $USD_VES_BCV;
}

class BlackMarketRate
{
    public $USD_VES;
    public $updatedAt;
}

class LBC
{
    public $avg_BTC_VES;
    public $wavg_BTC_VES;
    public $avg_USDBTC_VES;
    public $wavg_USDBTC_VES;
}

function getLbc(){
    try {
        $trades = file_get_contents("https://localbitcoins.com/bitcoincharts/VES/trades.json");

        $tradesDecoded = json_decode($trades, true);
        $countTrades = count($tradesDecoded);
        $sumPrice = 0;
        $weightedSumPrice = 0;
        $sumAmount = 0;
        foreach ($tradesDecoded as $trade) {
            $price = $trade['price'];
            $amount = $trade['amount'];
            $sumPrice += $price;
            $weightedSumPrice += $price * $amount;
            $sumAmount += $amount;
        }
        $lbc = new LBC();
        $lbc->avg_BTC_VES = $sumPrice / $countTrades;
        $lbc->wavg_BTC_VES = $weightedSumPrice / $sumAmount;
        return $lbc;
    } catch (Exception $e){
        echo "Error in using localbitcoins api " . $e->getMessage();
        return null;
    }
}

function getBtcUsdLbc()
{
    try {
        $btcPriceHtml = file_get_contents("https://api.coindesk.com/v1/bpi/currentprice/BTC.json");
        $btcPriceDecoded = json_decode($btcPriceHtml, true);
        return $btcPriceDecoded['bpi']['USD']['rate_float'];
    } catch (Exception $e) {
        echo "Error in using coindesk api " . $e->getMessage();
        return null;
    }
}

function getBlackMarketRate()
{
    try {
        $data = file_get_contents("https://monitordolarvenezuela.com/");
        $html = new simple_html_dom();
        $html->load($data);

        $rateBox = $html->find("#space1 > div > div > div.col-12.col-lg-3.order-1.order-sm-1.order-md-1.order-lg-0 > .box-calcmd > p", 0)->innertext;
        $priceStr = substr($rateBox, strpos($rateBox, ":") + 5, strpos($rateBox, "B") - 25);
        $priceCleaned = str_replace(" ", "", str_replace(".", "", $priceStr));

        $dateUpdate = $html->find("#space1 > div > div > div.col-12.col-lg-6.order-0.order-sm-0.order-md-0.order-lg-1 > div.head-price > div.back-white-tabla > h4", 0)->innertext;

        $rate = new BlackMarketRate();
        $rate->updatedAt = $dateUpdate;
        $rate->USD_VES = (int)$priceCleaned;

        return $rate;
    } catch (Exception $e) {
        echo "Error in crawling twitter " . $e->getMessage();
        return null;
    }
}

function getBcvRate()
{
    try {
        $client = new Client();
        $crawler = $client->request('GET', 'http://www.bcv.org.ve/');
        $bcvStr = $crawler->filter('#dolar .field-content > .recuadrotsmc > div > strong')->text();
        return (int)str_replace(".", "", substr($bcvStr, 0, strpos($bcvStr, ",")));
    } catch (Exception $e) {
        echo "Error in crawling bcv " . $e->getMessage();
        return null;
    }
}

$btcPrice = new BTCPrice();
$btcPrice->BTC_USD = getBtcUsdLbc();
$btcPrice->USD_VES_BCV = getBcvRate();
$btcPrice->blackMarket = getBlackMarketRate();
$lbc = getLbc();
$lbc->avg_USDBTC_VES = $lbc->avg_BTC_VES / $btcPrice->BTC_USD;
$lbc->wavg_USDBTC_VES = $lbc->wavg_BTC_VES / $btcPrice->BTC_USD;
$lbc->avg_BTC_VES = round($lbc->avg_BTC_VES);
$lbc->wavg_BTC_VES = round($lbc->wavg_BTC_VES);
$lbc->avg_USDBTC_VES = round($lbc->avg_USDBTC_VES);
$lbc->wavg_USDBTC_VES = round($lbc->wavg_USDBTC_VES);
$btcPrice->lbc = $lbc;
$btcPrice->BTC_USD = round($btcPrice->BTC_USD);
echo json_encode($btcPrice);