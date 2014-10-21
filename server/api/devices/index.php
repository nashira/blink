<?php
  include '../db.php';
	header("Content-Type: application/json");


  $db = get_db();
  $query = <<<SQL
SELECT masterDevice.deviceId AS id,
       masterDevice.userName AS name,
       masterDevice.interconnect,
       zigbeeDevice.deviceType AS deviceTypeId
FROM masterDevice, zigbeeDevice
WHERE zigbeeDevice.masterId = masterDevice.deviceId;
SQL;

  $result = $db->query($query);
  if (!$result) {
          echo json_encode(["error" => "Cannot execute query."]);
          return;
  }

        $data = array();
        while($row = $result->fetchArray(SQLITE3_ASSOC)){
                $attr_query = <<<SQL
SELECT zigbeeDeviceState.attributeId AS attributeTypeId,
       zigbeeDeviceState.value_get AS value
FROM zigbeeDevice, zigbeeDeviceState
WHERE zigbeeDevice.globalId = zigbeeDeviceState.globalId
      AND zigbeeDevice.masterId = '{$row['id']}';
SQL;

                $attr_result = $db->query($attr_query);

                $attrs = array();
                while($attr = $attr_result->fetchArray(SQLITE3_ASSOC)) {
                  $attrs[] = $attr;
                }
                $row['attributes'] = $attrs;
                $data[] = $row;
        }
        echo json_encode($data);
        $db->close()
?>
