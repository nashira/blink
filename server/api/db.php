<?php
function get_db()
{
  $db_name = '/database/apron.db';
  $db = new SQLite3($db_name);
  if (!$db) {
    echo "error";
    echo ($error);
    return;
  }
  return $db;
}
?>
