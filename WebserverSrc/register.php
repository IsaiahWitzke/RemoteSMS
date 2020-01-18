<!DOCTYPE html>
<html>

<head>
    <link rel="stylesheet" type="text/css" href="styles.css">
</head>

<body>

    <div class="inputBox">
        <h1 class="registerHeader">Register</h1>
        <form action="process.php" method="POST">
            <p>
                <label>Phone Number:</label>
                <input type="text" id="phoneNum" name="phoneNum" placeholder="ex: 5296960998" />
            </p>
            <p>
                <label>Password:</label>
                <input type="password" id="password" name="password" />
            </p>
            <p>
                <label>Confirm Password:</label>
                <input type="password" id="passwordConf" name="passwordConf" />
            </p>
            <input type="submit" name="register" id="btn" value="Register" />

        </form>
        Already registered? <a href="login.php">Login</a>
    </div>
</body>

</html>