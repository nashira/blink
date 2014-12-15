<?php
  include '../db.php';
  header("Content-Type: application/json");


  $db = get_db();
  $query = <<<SQL
SELECT zigbeeGroup.groupId AS id,
       zigbeeGroup.groupName AS name
FROM zigbeeGroup;
SQL;

  $result = $db->query($query);
  if (!$result) {
          echo json_encode(["error" => "Cannot execute query."]);
          return;
  }

        $data = array();
        while($row = $result->fetchArray(SQLITE3_ASSOC)){
                $group_device_query = <<<SQL
SELECT zigbeeGroupMembers.groupId,
       zigbeeGroupMembers.nodeId AS deviceId
FROM zigbeeGroup, zigbeeGroupMembers
WHERE zigbeeGroup.groupId = zigbeeGroupMembers.groupId
      AND zigbeeGroup.groupId = '{$row['id']}';
SQL;

                $group_device_result = $db->query($group_device_query);

                $group_devices = array();
                while($group_device = $group_device_result->fetchArray(SQLITE3_ASSOC)) {
                  $group_devices[] = $group_device;
                }
                $row['groupDeviceList'] = $group_devices;

                $attr_query = <<<SQL
SELECT zigbeeGroupState.attributeId AS attributeTypeId,
       zigbeeGroupState.value_get AS value
FROM zigbeeGroup, zigbeeGroupState
WHERE zigbeeGroup.groupId = zigbeeGroupState.groupId
      AND zigbeeGroup.groupId = '{$row['id']}';
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
        $db->close();
?>
