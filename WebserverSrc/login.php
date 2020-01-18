<!DOCTYPE html>
<html>

<head>
    <link rel="stylesheet" type="text/css" href="styles.css">
</head>

<body>
    <div class="inputBox">
        <h1 class="loginHeader">Login</h1>
        <form action="process.php" method="POST">
            <p>
                <label>Phone Number:</label>
                <input type="text" id="phoneNum" name="phoneNum" placeholder="ex: 5296960998" />
            </p>
            <p>
                <label>Password:</label>
                <input type="password" id="password" name="password" />
            </p>
            <input type="submit" name="login" id="btn" value="Login" />
        </form>
        First time here? <a href="register.php">Register</a>
    </div>
</body>

</html>