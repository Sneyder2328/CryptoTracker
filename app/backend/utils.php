<?php
function currentTimeInMillis(){
  return round(microtime(true) * 1000);
}