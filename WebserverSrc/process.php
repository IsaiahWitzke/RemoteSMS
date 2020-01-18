<?php
include("server.php");
session_start();

// The client can get to this page from login.php or register.php
//   If they came from register.php then do some logic and change
//   their password to their desired password ($_POST['password'])
//   (their row in userinfo has already been created...
//   see *phoneInit.php or something*)
//
//   If they came from login.php, test to see if their password
//   matches the one in the database, then set .

// NOTE TO SELF: If trying to make a half decent database sometime in
//    the future, be sure to hash users' passwords before putting
//    them into the datbase



function kill($errormsg, $redirectPage, $conn) {
    echo "<h3>".$errormsg."</h3>";
    echo file_get_contents($redirectPage);
    $conn->close();
    die();
}

if (isset($_POST['register'])) {
    $phoneNum = $conn->real_escape_string($_POST['phoneNum']);
    $password = $conn->real_escape_string($_POST['password']);
    $passwordConf = $conn->real_escape_string($_POST['passwordConf']);

    if ($passwordConf != $password) {
        kill("Passwords do not match. Please try again", "register.php", $conn);
    }

    if (strlen($password) <= 4) {
        kill("Passwords Must be at least 5 characters long. Please try again", "register.php", $conn);
    }

    $result = $conn->query("SELECT * FROM userinfo WHERE userphonenum LIKE '" . $phoneNum . "'");

    if ($result->num_rows == 0) {
        kill("No phone with phone number " . $_POST['phoneNum'] . " found with remote sms app.
            Please ensure that the phone that you are attempting to register has the remote 
            sms app installed", "register.php", $conn);
        }

    $row = $result->fetch_array(MYSQLI_ASSOC);

    // when a user downloads the remote sms app onto their phone, the phone 
    //   will tell the server to create a row in userinfo with their phone
    //   number and "npw" as the password

    if ($row["userpass"] != "npw") {
        kill("Phone number " . $_POST['phoneNum'] . " has already been registered. Try logging in:", "login.php", $conn);
    }

    // $result->free_result();
    $sql = ("UPDATE userinfo SET userpass = '" . $password . "' WHERE userphonenum = '" . $phoneNum . "'");
    
    if ($conn->query($sql) === false) {
        echo "ERROR: Could not able to execute $sql. "
            . $conn->error;
        $conn->close();
        $result->free_result();
        die();
    }

    kill("Success! Phone number registered", "login.php", $conn);

} else if (isset($_POST['login'])) {

    $phoneNum = $conn->real_escape_string($_POST['phoneNum']);
    $password = $conn->real_escape_string($_POST['password']);

    // when a user downloads the remote sms app onto their phone, the phone 
    //   will tell the server to create a row in userinfo with their phone
    //   number and "npw" as the password. The following 5 lines are a bit
    //   of protection...

    // no password can be less than 5 chars
    if (strlen($password) <= 4) {
        kill("Incorrect password. Please try again", "login.php", $conn);
    }

    $result = $conn->query("SELECT * FROM userinfo WHERE userphonenum LIKE '" . $phoneNum . "'");

    if ($result === false) {
        echo "ERROR: Could not able to execute $sql. "
            . $conn->error;
        $conn->close();
        $result->free_result();
        die();
    }

    if ($result->num_rows == 0) {
        kill("No account with phone number " . $_POST['phoneNum'] . " found. Please try again.", "login.php", $conn);
    }

    $row = $result->fetch_array(MYSQLI_ASSOC);

    if ($row["userpass"] == $password) {
        $_SESSION['login'] = "putsomesecretstringhere";
        $_SESSION['fcmtoken'] = $row['userfcmtoken'];
        $result->free_result();
        $conn->close();
        header("Location: http://ec2-3-20-67-129.us-east-2.compute.amazonaws.com/smsactionpage.php");
        die();
    } else {
        kill("Incorrect password. Please try again", "login.php", $conn);
    }
} else {
    echo "error: No login/registration data";
}

$conn->close();
