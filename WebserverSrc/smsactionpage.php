<!DOCTYPE html>
<html>

<head>
    <link rel="stylesheet" type="text/css" href="styles.css">
</head>

<body>

    <div class="inputBox">
        <h1 class="smsActionHeader">Send SMS</h1>
        <form action="smsaction.php" method="POST">
            <p>
                <label>Phone Number (Recipient):</label>
                <input type="text" id="recPhoneNum" name="recPhoneNum" placeholder="ex: 5296960998" />
            </p>
            <p>
                <label>Message:</label>
                <input type="text" id="msg" name="msg" />
            </p>
            <input type="submit" name="sendMsg" id="btn" value="Send Message" />
        </form>
    </div>
</body>

</html>
