CREATE TABLE driver (
    id             BIGSERIAL PRIMARY KEY,
    full_name      VARCHAR(120) NOT NULL,
    license_number VARCHAR(30)  NOT NULL UNIQUE
);

CREATE TABLE claim (
    id                BIGSERIAL PRIMARY KEY,
    claim_number      VARCHAR(30)  NOT NULL UNIQUE,
    policy_number     VARCHAR(30)  NOT NULL,
    incident_date     DATE         NOT NULL,
    location          VARCHAR(160) NOT NULL,
    status            VARCHAR(20)  NOT NULL CHECK (status IN ('OPEN', 'IN_REVIEW', 'APPROVED', 'DENIED', 'CLOSED')),
    description       TEXT         NOT NULL,
    insured_driver_id BIGINT       NOT NULL REFERENCES driver (id)
);

-- Other drivers involved in the incident (not the policy holder).
CREATE TABLE claim_other_driver (
    claim_id  BIGINT NOT NULL REFERENCES claim (id) ON DELETE CASCADE,
    driver_id BIGINT NOT NULL REFERENCES driver (id),
    PRIMARY KEY (claim_id, driver_id)
);

CREATE INDEX idx_claim_insured_driver ON claim (insured_driver_id);
CREATE INDEX idx_claim_other_driver_driver ON claim_other_driver (driver_id);
