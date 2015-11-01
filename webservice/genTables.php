<?php
/**
	I am assuming 'users' table is already created
*/
	require_once('dbconn.php');

	//creates the table where URL are being stored.
    $sql = "CREATE TABLE savedPage(
    	id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    	title VARCHAR(30) NOT NULL,
    	url TEXT NOT NULL,
    	owner VARCHAR(30) NOT NULL,
    	reg_date TIMESTAMP
    	)";

        
    $q = $conn->prepare($sql);
    $q->execute();

    $conn = null;
?>