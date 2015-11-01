<?php
/**
	This method gets the url from the database
*/
	require_once('dbconn.php');

	$email = isset($_GET['email']) ? $_GET['email'] : '';
    $url = isset($_GET['url']) ? $_GET['url'] : '';

	$sql = "DELETE FROM savedPage ";
    $sql .= " WHERE owner = '" . $email . "' AND url = '" . $url . "'";

        
    $q = $conn->prepare($sql);
    $q->execute();
    $result = $q->fetch(PDO::FETCH_ASSOC);
    
       
    //check results
    if ($result != false) {
        //on success, echo  the json
        echo '{"result": "success", "error": "Record deleted succesfully"}';
    } else {
        echo '{"result": "fail", "error": "No registered user"}';
    }
?>