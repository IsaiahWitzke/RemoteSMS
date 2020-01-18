<?php

$servername = $_SERVER['RDS_HOSTNAME'];
$username = $_SERVER['RDS_USERNAME'];
$password = $_SERVER['RDS_PASSWORD'];

// Create connection
$conn = new mysqli($servername, $username, $password, "remotesmsdb");

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
?>