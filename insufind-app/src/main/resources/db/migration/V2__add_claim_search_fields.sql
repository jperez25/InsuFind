-- New claim attributes for the expanded search. Nullable (except claim_type) so existing rows stay valid.
ALTER TABLE claim
    ADD COLUMN claim_type    VARCHAR(20) NOT NULL DEFAULT 'COLLISION'
        CHECK (claim_type IN ('COLLISION', 'THEFT', 'GLASS', 'WEATHER', 'VANDALISM', 'OTHER')),
    ADD COLUMN reported_date DATE,
    ADD COLUMN vehicle_plate VARCHAR(15),
    ADD COLUMN vehicle_vin   VARCHAR(17),
    ADD COLUMN policy_state  VARCHAR(2),
    ADD COLUMN loss_state    VARCHAR(2);

CREATE INDEX idx_claim_incident_date ON claim (incident_date);
CREATE INDEX idx_claim_status ON claim (status);
CREATE INDEX idx_claim_vehicle_plate ON claim (vehicle_plate);
CREATE INDEX idx_claim_vehicle_vin ON claim (vehicle_vin);
