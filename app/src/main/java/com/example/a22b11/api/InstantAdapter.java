package com.example.a22b11.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.Instant;

/**
 * Serializer and deserializer for java.time.Instant timestamps
 * Instants will be represented in the ISO-8601 format in JSON.
 * For example: "2012-06-11T20:06:10Z"
 */
public class InstantAdapter implements JsonDeserializer<Instant>, JsonSerializer<Instant> {
    @Override
    public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Instant.parse(json.getAsJsonPrimitive().getAsString());
    }

    @Override
    public JsonElement serialize(Instant time, Type typeOfT, JsonSerializationContext context) {
        return new JsonPrimitive(time.toString());
    }
}
