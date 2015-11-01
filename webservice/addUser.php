<?PHP
        require_once('dbconn.php');
       
    	try {
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
                $sql = "INSERT INTO User";
                $sql .= " VALUES ('$email', '$password')";
             
                //attempts to add record
                if ($db->query($sql)) {
                    echo '{"result": "success"}';
                    $db = null;
                } 
            }   
        } catch(PDOException $e) {
                if ((int)($e->getCode()) == 23000) {
                    echo '{"result": "fail", "error": "That email address has already been registered."}';
                } else {
                    echo 'Error Number: ' . $e->getCode() . '<br>';
                    echo '{"result": "fail", "error": "Unknown error (' . (((int)($e->getCode()) + 123) * 2) .')"}';
                }
        }
?>
