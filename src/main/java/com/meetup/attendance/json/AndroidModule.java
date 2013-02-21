package com.meetup.attendance.json;

import android.os.Bundle;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class AndroidModule extends SimpleModule {
    public AndroidModule() {
        super("AndroidModule");
        addDeserializer(Bundle.class, new BundleDeserializer());
        addSerializer(Bundle.class, new BundleSerializer());
    }

    private class BundleDeserializer extends JsonDeserializer<Bundle> {
        @Override
        public Bundle deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
                jp.nextToken();
                return deserializeBundle(jp, ctxt);
            } else if (jp.getCurrentToken() == JsonToken.FIELD_NAME) {
                return deserializeBundle(jp, ctxt);
            }
            throw ctxt.mappingException(Bundle.class);
        }

        protected Bundle deserializeBundle(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            Bundle b = new Bundle();
            JsonToken t = jp.getCurrentToken();
            if (t == JsonToken.START_OBJECT) {
                t = jp.nextToken();
            }
            for (; t == JsonToken.FIELD_NAME; t = jp.nextToken()) {
                String fieldName = jp.getCurrentName();
                switch (jp.nextToken()) {
                case START_OBJECT:
                    b.putParcelable(fieldName, deserializeBundle(jp, ctxt));
                    break;
                case VALUE_STRING:
                    b.putString(fieldName, jp.getText());
                    break;
                case VALUE_NUMBER_INT:
                    JsonParser.NumberType nt = jp.getNumberType();
                    if (nt == JsonParser.NumberType.INT)
                        b.putInt(fieldName, jp.getIntValue());
                    else
                        b.putLong(fieldName, jp.getLongValue());
                    break;
                case VALUE_NUMBER_FLOAT:
                    b.putDouble(fieldName, jp.getDoubleValue());
                    break;
                case VALUE_TRUE:
                    b.putBoolean(fieldName, true);
                    break;
                case VALUE_FALSE:
                    b.putBoolean(fieldName, false);
                    break;
                case VALUE_NULL:
                    b.putParcelable(fieldName, null);
                    break;
                //case START_ARRAY:
                //case VALUE_EMBEDDED_OBJECT:
                default:
                    throw ctxt.mappingException(Bundle.class);
                }
            }
            return b;
        }
    }
    private class BundleSerializer extends StdSerializer<Bundle> {
        public BundleSerializer() {
            super(Bundle.class);
        }

        @Override
        public void serialize(Bundle b, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeStartObject();
            if (!b.isEmpty()) {
                for (String key : b.keySet()) {
                    Object val = b.get(key);
                    provider.defaultSerializeField(key, val, jgen);
                }
            }
            jgen.writeEndObject();
        }
    }
}