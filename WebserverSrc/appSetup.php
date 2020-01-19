<?php
include("server.php");

// todo:
//   server needs to send some sort of json response

// This page will be accessed with the android app on the app's startup.
//   If the phone has already been registered, then just try to start a
//   socket connection. Otherwise, send back a msg to the app telling it
//   to get the user to enter their phone's number.

if(!isset($_GET["phoneid"])) {
    print("error: no phone id given");
    $conn->close();
    die();
}


// to see if this is the first time the phone is talking to the server
//   if this is so, add an empty row in the userinfo table and tell the
//   client to send another request with a phone number

$phoneId = $conn->real_escape_string($_GET["phoneid"]);
$result = $conn->query("SELECT * FROM userinfo WHERE userphoneid LIKE '" . $phoneId . "'");
$row = $result->fetch_array(MYSQLI_ASSOC);

if ($result->num_rows == 0) {
    echo "need phonenum";
    $result->free();

    $sql = (
        "INSERT INTO userinfo (userpass, userphoneid)
        VALUES ('npw', '" . $_GET["phoneid"] . "');"
    );
    
    if ($conn->query($sql) === false) {
        echo "ERROR: Could not execute $sql. " . $conn->error;
        $conn->close();
        $result->free_result();
        die();
    }

    $conn->close();
    die();
}

// updating the FCM token
if (isset(($_GET["fcmtoken"]))) {
    $fcmToken = $conn->real_escape_string($_GET["fcmtoken"]);
    $sql = ("UPDATE userinfo SET userfcmtoken = '" . $fcmToken . "' WHERE userphoneid = '" . $phoneId . "'");

    if ($conn->query($sql) === false) {
        echo "ERROR: Could not able to execute $sql. "
            . $conn->error;
        $conn->close();
        $result->free_result();
        die();
    }

    echo "FCM token updated successfully";
}

if ($row["userphonenum"] == "null" && !isset($_GET["newphonenumber"])) {
    echo "need phonenum";
    $result->free();
    $conn->close();
    die();
}

// now, set (or reset) the phone number

if(isset($_GET["newphonenumber"])) {
    $newPhoneNum = $conn->real_escape_string($_GET["newphonenumber"]);

    $sql = ("UPDATE userinfo SET userphonenum = '" . $newPhoneNum . "' WHERE userphoneid = '" . $phoneId . "'");
    
    if ($conn->query($sql) === false) {
        echo "ERROR: Could not able to execute $sql. "
            . $conn->error;
        $conn->close();
        $result->free_result();
        die();
    }

    echo "Phone number updated successfully\nGo to http://ec2-3-20-67-129.us-east-2.compute.amazonaws.com/register.php to
        start sending SMSs";
    $result->free();
    $conn->close();
    die();
}
?>