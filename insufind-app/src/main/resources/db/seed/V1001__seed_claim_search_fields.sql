-- Demo values for the V2 columns. Plates and VINs are obviously fictional (TST prefix).
UPDATE claim SET
    reported_date = incident_date + 1,
    vehicle_plate = 'TST-' || lpad(id::text, 4, '0'),
    vehicle_vin   = 'TSTVIN' || lpad(id::text, 11, '0'),
    policy_state  = 'TX',
    loss_state    = 'TX';

UPDATE claim SET claim_type = 'WEATHER' WHERE id = 4;
UPDATE claim SET claim_type = 'GLASS' WHERE id = 8;
UPDATE claim SET claim_type = 'VANDALISM' WHERE id = 7;
-- Out-of-state policy, loss in Texas.
UPDATE claim SET policy_state = 'OK' WHERE id = 3;
