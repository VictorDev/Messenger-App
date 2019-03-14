<?php

$con = mysqli_connect("localhost", "id7932331_id7932331_vitya", "gfer02001") or die(mysqli_error($con));// подключение к серверу
$db = mysqli_select_db($con,"id7932331_client") or die(mysqli_error($con)); // выбираем БД
$action = $_REQUEST['action'];


if($action=='select'){
    
    $last_id = null;
    if(isset($_REQUEST['ID'])){
    	$last_id = $_REQUEST['ID'];
    }
    $r = null;
    $output = null;
	if($last_id == null){
		$r = mysqli_query($con,"SELECT * FROM `mytable`");
	}else{
		$r = mysqli_query($con,"SELECT * FROM `mytable` WHERE ID>$last_id");
	}
	while($row = mysqli_fetch_assoc($r)){
		$output[]=$row;
	}
	print(json_encode($output));
}

if($action=='insert'){
    $text = $_REQUEST['message'];
    $id = $_REQUEST['idUser'];
    $Name = $_REQUEST['userName'];
	$r = mysqli_query($con,"INSERT INTO `mytable` (`ID`, `message`,`idUser`,`userName`) VALUES (NULL, '{$text}','{$id}','{$Name}')");
}
mysqli_close($con);

?>