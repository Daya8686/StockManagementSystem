package com.stockmanagement.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.stockmanagement.entity.StorageType;
import com.stockmanagement.entity.UnitOfMeasurement;

public class StorageTypeDeserializer extends JsonDeserializer<StorageType> {

    @Override
    public StorageType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().trim();
        if (value.isEmpty()) {
            return null;  // You can return a default value like StorageType.UNKNOWN if needed
        }
        return StorageType.valueOf(value);  // Default deserialization logic
    }
}