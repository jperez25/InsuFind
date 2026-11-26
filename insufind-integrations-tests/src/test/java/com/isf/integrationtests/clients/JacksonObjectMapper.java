package com.isf.integrationtests.clients;

import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import tools.jackson.databind.json.JsonMapper;

/** Plugs Jackson 3 into REST Assured (it only ships Jackson 2 support), so `response.as(Type.class)` works. */
class JacksonObjectMapper implements ObjectMapper {

  private final JsonMapper mapper = JsonMapper.builder().build();

  @Override
  public Object deserialize(ObjectMapperDeserializationContext context) {
    return mapper.readValue(context.getDataToDeserialize().asString(), mapper.constructType(context.getType()));
  }

  @Override
  public Object serialize(ObjectMapperSerializationContext context) {
    return mapper.writeValueAsString(context.getObjectToSerialize());
  }
}
