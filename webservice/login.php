<?php
/* 
 * This file provides a web service to authenticate a user. Given "email" and "password",
 *  attempts to authenticate the user. If successful, returns the user ID. Else, returns
 *  an error message.
 */
 
 //establish database connection
require_once('dbconn.php');
    

//get input
$email = isset($_GET['email']) ? $_GET['email'] : '';
$password = isset($_GET['password']) ? $_GET['password'] : '';
    
//validate input
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo '{"result": "fail", "error": "Please enter a valid email."}';
} else if (strlen($password) < 6) {
    echo '{"result": "fail", "error": "Please enter a valid password (longer than five characters)."}';
} else {    
    //build query
    $sql = "SELECT uid, password FROM Users ";
    $sql .= " WHERE email = '" . $email . "'";

        
    $q = $conn->prepare($sql);
    $q->execute();
    $result = $q->fetch(PDO::FETCH_ASSOC);
    
       
    //check results
    if ($result != false) {
        //on success, return the user id
        if (strcmp($password, $result['password']) == 0)
        	echo '{"result": "success", "userid": "' . $result['uid'] . '"}';
		else 
			echo '{"result": "fail", "error": "Incorrect password."}';
    } else {
        echo '{"result": "fail", "error": "Incorrect email."}';
    }
}

//close database connection
$conn = null;
?>