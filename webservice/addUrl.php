<?PHP
        require_once('dbconn.php');
       
    	try {
			if (!isset($_GET['id'])) {
				echo '{"result": "fail", "error": "Please enter a valid id."}';
				die();
			} else if (!isset($_GET['title'])) {
				echo '{"result": "fail", "error": "Please enter a valid title."}';
				die();
			} else if (!isset($_GET['url'])) {
				echo '{"result": "fail", "error": "Please enter a valid url."}';
				die();
			}		
			
			//get input 
            $id = $_GET['id'];
            $title = $_GET['title'];
            $url = $_GET['url'];
			
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
