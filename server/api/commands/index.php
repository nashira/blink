<?php
  // [
  //   {action: update, id: 1, updates: [{id: 1, value: 23}, {id: 2, value: ON},
  //   {action: add, type: [lutron, zwave, zigbee, kidde]}
  //   {action: remove, id: 2}
  //   {action: set_name, id: 2, name: "foo"}
  // ]

  $commands = json_decode(file_get_contents('php://input'));

  if (!isset($commands)) {
    http_response_code(400);
    return;
  }

  $responses = array();
  foreach ($commands as $command) {
    switch ($command->action) {
      case 'add':
        exec('aprontest -a -r ' . escapeshellarg($command->type) . ' &> /dev/null &');
        $responses[] = ['status' => 'ok'];
      break;

      case 'remove':
        exec('aprontest -d -m ' . escapeshellarg($command->id) . ' &> /dev/null &');
        $responses[] = ['status' => 'ok'];
      break;

      case 'update':
        foreach ($command->updates as $update) {
          $attr = ' -t ' . escapeshellarg($update->id) . ' -v ' . escapeshellarg($update->value);
          exec('aprontest -u -m ' . escapeshellarg($command->id) . $attr . ' &> /dev/null &');
        }
        $responses[] = ['status' => 'ok'];
      break;

      case 'set-name':
        exec('aprontest -m ' . escapeshellarg($command->id) . ' --set-name ' .  escapeshellarg($command->name) . ' &> /dev/null &');
        $responses[] = ['status' => 'ok'];
      break;

    }
  }

  echo json_encode($responses);
?>
