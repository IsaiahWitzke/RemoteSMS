<?php
$usedPorts = fgetcsv("UsedPorts.csv", 1000, ",");
$length = count($usedPorts);

for ($c=0; $c < $length; $c++) {
    echo $data[$c] . "<br />\n";
}

print_r();
?>