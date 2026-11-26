-- Bulk demo data: claims 13..1000, reusing the 8 fictional drivers. Deterministic (no random()) so UI tests can rely on it.
-- Plates and VINs carry a TST prefix. Dates run 2025-01-01 .. 2026-11-11, so none are in the future.
INSERT INTO claim (id, claim_number, policy_number, incident_date, location, status, description, insured_driver_id,
                   claim_type, reported_date, vehicle_plate, vehicle_vin, policy_state, loss_state)
SELECT g,
       'CLM-' || extract(year FROM x.d)::int || '-' || lpad(g::text, 4, '0'),
       'POL-' || (100000 + x.ins),
       x.d,
       (ARRAY['Main St', 'Congress Ave', 'Oak Ave', 'Elm Street', 'Harbor Blvd', 'Lamar Blvd'])[g % 6 + 1]
           || ', ' || x.city || ' ' || x.st,
       (ARRAY['OPEN', 'IN_REVIEW', 'APPROVED', 'DENIED', 'CLOSED'])[g % 5 + 1],
       CASE x.ctype
           WHEN 'COLLISION'  THEN (ARRAY['Rear-ended at a red light.', 'Side swipe while changing lanes.',
                                         'Parking lot collision, fault disputed.', 'Intersection collision.'])[g % 4 + 1]
           WHEN 'GLASS'      THEN 'Windshield cracked by road debris.'
           WHEN 'WEATHER'    THEN 'Hail damage to roof and hood.'
           WHEN 'THEFT'      THEN 'Vehicle reported stolen overnight.'
           ELSE                   'Vandalism: keyed paint and broken mirror.'
       END,
       x.ins,
       x.ctype,
       x.d + (g % 5),
       'TST-' || lpad(g::text, 4, '0'),
       'TSTVIN' || lpad(g::text, 11, '0'),
       CASE WHEN g % 11 = 0 THEN 'OK' ELSE 'TX' END,
       x.st
FROM generate_series(13, 1000) AS g
CROSS JOIN LATERAL (
    SELECT DATE '2025-01-01' + (g * 7 % 680)                                           AS d,
           g % 8 + 1                                                                    AS ins,
           (ARRAY['COLLISION', 'COLLISION', 'COLLISION', 'COLLISION', 'COLLISION', 'COLLISION',
                  'GLASS', 'WEATHER', 'THEFT', 'VANDALISM'])[g % 10 + 1]                AS ctype,
           (ARRAY['Austin', 'Dallas', 'Houston', 'Plano', 'San Antonio', 'Tulsa', 'Shreveport', 'Albuquerque'])[g % 8 + 1] AS city,
           (ARRAY['TX', 'TX', 'TX', 'TX', 'TX', 'OK', 'LA', 'NM'])[g % 8 + 1]            AS st
) AS x;
SELECT setval('claim_id_seq', 1000);

-- One other driver on two of three claims, a second one on every seventh. Never the insured driver.
INSERT INTO claim_other_driver (claim_id, driver_id)
SELECT g, (g + 2) % 8 + 1 FROM generate_series(13, 1000) AS g WHERE g % 3 <> 0;
INSERT INTO claim_other_driver (claim_id, driver_id)
SELECT g, (g + 5) % 8 + 1 FROM generate_series(13, 1000) AS g WHERE g % 7 = 0;
