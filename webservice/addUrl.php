<?PHP
        require_once('dbconn.php');
       
    	try {
            //get input 
            $id = isset($_GET['id']) ? $_GET['id'] : '';
            $title = isset($_GET['title']) ? $_GET['title'] : '';
            $url = isset($_GET['url']) ? $_GET['url'] : '';

			//build query
			$sql = "INSERT INTO SavedPages (owner, title, url)";
			$sql .= " VALUES ('$id', '$title', '$url')";
			
			
			$q = $conn->prepare($sql);
			//attempts to add record
			if ($q->execute()) {
				echo '{"result": "success"}';
				$db = null;
            }   
        } catch(PDOException $e) {
			echo 'Error Number: ' . $e->getCode() . '<br>';
			echo '{"result": "fail", "error": "Unknown error (' . (((int)($e->getCode()) + 123) * 2) .')"}';
        }
?>
