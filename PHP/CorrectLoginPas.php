<?php
$con = mysqli_connect("localhost", "id7932331_id7932331_vitya", "gfer02001") or die(mysqli_error($con));// подключение к серверу
$db = mysqli_select_db($con,"id7932331_client") or die(mysqli_error($con)); // выбираем БД
$OutsideLoginPass = $_REQUEST["OutsideLoginPass"]; // получаем логин/пароль
$r = mysqli_query($con,"SELECT * FROM LoginPasswordClient WHERE LoginPass = '{$OutsideLoginPass}'");
$count = mysqli_num_rows($r);
$isCorrect = false;
if($count>0){
$isCorrect = true;
while($row = mysqli_fetch_array($r)){
    $test['UserName']=$row['UserName'];
	$test['ID']=$row['ID'];
}
}
$test['is']=$isCorrect;
$test['rows']=$count;
echo (json_encode($test));
mysqli_close($con);
