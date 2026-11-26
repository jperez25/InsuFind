-- Demo data. All people, license numbers and policy numbers are fictional.
INSERT INTO driver (id, full_name, license_number) VALUES
    (1, 'Maria Gonzalez', 'D1000001'),
    (2, 'John Smith',     'D1000002'),
    (3, 'Aiko Tanaka',    'D1000003'),
    (4, 'Liam O''Brien',  'D1000004'),
    (5, 'Priya Patel',    'D1000005'),
    (6, 'Carlos Mendes',  'D1000006'),
    (7, 'Sofia Rossi',    'D1000007'),
    (8, 'Noah Johnson',   'D1000008');
SELECT setval('driver_id_seq', 8);

INSERT INTO claim (id, claim_number, policy_number, incident_date, location, status, description, insured_driver_id) VALUES
    (1,  'CLM-2026-0001', 'POL-100001', '2026-01-12', 'Main St & 3rd Ave, Austin TX',  'OPEN',      'Rear-ended at a red light. Bumper and trunk damage.', 1),
    (2,  'CLM-2026-0002', 'POL-100001', '2026-03-02', 'I-35 exit 233, Austin TX',      'APPROVED',  'Side swipe while changing lanes. Driver door dented.', 1),
    (3,  'CLM-2026-0003', 'POL-100002', '2026-02-18', 'Elm Street, Dallas TX',         'IN_REVIEW', 'Parking lot collision, disputed fault.', 2),
    (4,  'CLM-2026-0004', 'POL-100003', '2026-04-07', 'Harbor Blvd, Houston TX',       'DENIED',    'Hail damage reported outside policy period.', 3),
    (5,  'CLM-2026-0005', 'POL-100004', '2026-05-21', 'Oak Ave & 9th St, Plano TX',    'CLOSED',    'Intersection collision. Both parties cited.', 4),
    (6,  'CLM-2026-0006', 'POL-100005', '2026-06-30', 'US-290, Austin TX',             'OPEN',      'Multi-vehicle pileup in heavy rain.', 5),
    (7,  'CLM-2026-0007', 'POL-100002', '2026-07-14', 'Congress Ave, Austin TX',       'IN_REVIEW', 'Hit and run while parked.', 2),
    (8,  'CLM-2026-0008', 'POL-100006', '2026-08-03', 'Riverside Dr, Austin TX',       'APPROVED',  'Windshield cracked by road debris.', 6),
    (9,  'CLM-2026-0009', 'POL-100007', '2026-09-09', 'Lamar Blvd, Austin TX',         'OPEN',      'Collision with cyclist, minor vehicle damage.', 7),
    (10, 'CLM-2026-0010', 'POL-100001', '2026-10-01', 'Burnet Rd, Austin TX',          'OPEN',      'Collision at a four-way stop.', 1),
    (11, 'CLM-2026-0011', 'POL-100008', '2026-10-15', 'Guadalupe St, Austin TX',       'IN_REVIEW', 'Rear-end collision, whiplash claimed.', 8),
    (12, 'CLM-2026-0012', 'POL-100005', '2026-11-02', 'S 1st St, Austin TX',           'OPEN',      'Front bumper damage after a low-speed crash.', 5);
SELECT setval('claim_id_seq', 12);

-- Other drivers involved
INSERT INTO claim_other_driver (claim_id, driver_id) VALUES
    (1, 2),
    (2, 3),
    (3, 4),
    (5, 1),
    (5, 6),
    (6, 2),
    (6, 7),
    (9, 8),
    (10, 4),
    (11, 1),
    (12, 3);
