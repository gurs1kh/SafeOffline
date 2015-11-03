<?php
/**
	This method gets the url from the database
*/
	require_once('dbconn.php');

	$id = isset($_GET['id']) ? $_GET['id'] : '';
    $url = isset($_GET['url']) ? $_GET['url'] : '';
	
	$sql = "DELETE FROM SavedPages ";
    $sql .= " WHERE owner = '" . $id . "' AND url = '" . $url . "'";

        
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