<?php
// [
//   {action: update, device_id: 1, updates: [{attr_id: 1, value: 23}, {attr_id: 2, value: ON},
//   {action: add, type: [lutron, zwave, zigbee, kidde]}
//   {action: remove, device_id: 2}
// ]

$commands = json_decode(file_get_contents('php://input'));

if (!isset($commands)) {
  http_response_code(400);
  return;
}

foreach ($commands as $command) {
  switch ($command->action) {
    case 'add':
      echo 'add command';
    break;

    case 'remove':
      echo 'remove command';
    break;

    case 'update':
      echo 'update command';
    break;

  }
    echo json_encode($command);
}
//
// $nodeId = $_POST['dev'];
// $attrId = $_POST['attr'];
// $v = $_POST['val'];
//
// if (isset($nodeId) && isset($attrId) && $v) {
//
// 	//echo "nodeId=" .$nodeId . " attrId=" . $attrId . " value=" . $v;
// 	//$cmd = 'sudo ' . dirname(__FILE__) . '/php2apron set_value ' . $nodeId . " " . $attrId . " " . $v;
//
// 	$cmd = 'aprontest -u -m ' . $nodeId . ' -t ' . $attrId . ' -v ' . $v;
//
// 	//echo $cmd . " ";
//
// 	passthru($cmd, $retval);
// 	echo "ret_code=" . $retval;
//
// } else {
// 	http_response_code(400);
// }

?>
