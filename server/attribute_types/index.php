<?php
  include '../../db.php';
  header("Content-Type: application/json");

  $query = "SELECT attributeId as id, description, dataType FROM zigbeeAttribute";
  $result = db_exec_query($query);
  if (!$result) {
    echo json_encode(["error" => "Cannot execute query."]);
    return;
  }

  $data = array();
  while($row = $result->fetchArray(SQLITE3_ASSOC)){
    $data[] = $row;
  }

  echo json_encode($data);
?>
