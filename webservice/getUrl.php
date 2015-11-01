<?php
/**
	This method gets the url from the database
*/
	require_once('dbconn.php');

	$email = isset($_GET['email']) ? $_GET['email'] : '';

	$sql = "SELECT * FROM savedPage ";
    $sql .= " WHERE owner = '" . $email . "'";

        
    $q = $conn->prepare($sql);
    $q->execute();
    $result = $q->fetchAll(PDO::FETCH_ASSOC);
    
       
    //check results
    if ($result != false) {
        //on success, echo  the json
        $json = json_encode($results);
        echo $json;
    } else {
        echo '{"result": "fail", "error": "No registered user"}';
    }
?>