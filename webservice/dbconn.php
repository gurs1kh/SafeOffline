
<?PHP
	ini_set('display_errors', '1');
	error_reporting(E_ALL);

	// Connect to the Database
	$dsn = 'mysql:host=cssgate.insttech.washington.edu;dbname=singhm5';
    	$username = 'singhm5';
    	$password = 'uphbyu';

    	try {
        	$conn = new PDO($dsn, $username, $password);
		$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		
    	} catch (PDOException $e) {
        	$error_message = $e->getMessage();
        	echo 'There was an error connecting to the database.';
		echo $error_message;
        	exit();
    	}


?>