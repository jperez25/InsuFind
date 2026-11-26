package com.isf.integrationtests.models;

import com.isf.integrationtests.constants.HealthStatus;
import java.util.List;

/** Body of GET /actuator/health. */
public record HealthResponse(List<String> groups, HealthStatus status) {
}
