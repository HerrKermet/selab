package com.example.a22b11.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class BooleanAdapter implements JsonDeserializer<Boolean> {
    public Boolean deserialize(JsonElement json, Type typeOfT,
                               JsonDeserializationContext context) throws JsonParseException {
        if (((JsonPrimitive) json).isBoolean()) {
            return json.getAsBoolean();
        }
        if (((JsonPrimitive) json).isString()) {
            String jsonValue = json.getAsString();
            if (jsonValue.equalsIgnoreCase("true")) {
                return true;
            }
            else if (jsonValue.equalsIgnoreCase("false")) {
                return false;
            }
            else {
                return null;
            }
        }

        int code = json.getAsInt();
        if (code == 0) {
            return false;
        }
        else if (code == 1) {
            return true;
        }
        else {
            return null;
        }
    }
}
