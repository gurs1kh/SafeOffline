<?php
/**
	This method gets the url from the database
*/
	require_once('dbconn.php');

	$id = isset($_GET['id']) ? $_GET['id'] : '';

	$sql = "SELECT * FROM SavedPages ";
    $sql .= " WHERE owner = '" . $id . "'";

        
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