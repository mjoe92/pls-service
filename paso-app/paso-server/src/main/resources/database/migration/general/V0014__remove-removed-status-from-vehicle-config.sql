UPDATE vehicle_config SET pls_status = 'ERROR'
WHERE pls_status NOT IN ('INCOMPLETE', 'PENDING', 'READY', 'COMPLETE', 'ERROR');
