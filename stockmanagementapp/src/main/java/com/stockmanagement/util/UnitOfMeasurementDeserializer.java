package com.stockmanagement.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.stockmanagement.entity.UnitOfMeasurement;

public class UnitOfMeasurementDeserializer extends JsonDeserializer<UnitOfMeasurement> {

    @Override
    public UnitOfMeasurement deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().trim();
        if (value.isEmpty()) {
            return null;  // You can return a default value like UnitOfMeasurement.UNKNOWN if needed
        }
        return UnitOfMeasurement.valueOf(value);  // Default deserialization logic
    }
}