
<?php
session_start();

function sendPushNotification($fields = array())
{
    $API_ACCESS_KEY = "AAAA8nsVLYc:APA91bH0Mw8hn_TaXDcGQrLw4dPpkJd1HQXYguG6RApzmGJk86bascF0ECb9dueSmISPfRePFfjOB2Nrvu_oC1Vz3mwp24idP5zWWM5mgcLFX_jplumcKF2O7Fj4z8zpFXqLHKZO4vbb";
    $headers = array(
        'Authorization: key=' . $API_ACCESS_KEY,
        'Content-Type: application/json'
    );
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send');
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
    $result = curl_exec($ch);
    curl_close($ch);
    return $result;
}

if (!isset($_POST['recPhoneNum']) || !isset($_POST['msg'])) {
    echo "please enter all the info to send msg";
    $conn->close();
    header("Location: http://ec2-3-20-67-129.us-east-2.compute.amazonaws.com/smsactionpage.php");
    die();
}

if (!isset($_SESSION['login'])) {
    echo "please login first!";
    $conn->close();
    die();
}

$title = 'Whatever';
$message = 'Lorem ipsum';
$fields = array(
    'registration_ids'  => [$_SESSION['fcmtoken']],
    'data' => array('phoneNum' => "'" . $_POST['recPhoneNum'] . "'", 'msg' => "'" . $_POST['msg'] . "'"),
    'priority' => 'high',
    'notification' => array(
        'body' => $message,
        'title' => $title,
        'sound' => 'default',
        'icon' => 'icon'
    )
);

$responseObj = json_decode(sendPushNotification($fields));

if ($responseObj->success == 1) {
    echo "<h1>Message sent successfully</h1>";
} else {
    echo "<h1>Error</h1>";
}
// echo "<h1>" . sendPushNotification($fields) . "</h1>";

?>