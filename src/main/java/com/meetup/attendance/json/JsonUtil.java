package com.meetup.attendance.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

public class JsonUtil {
    private static JsonFactory factory;

    public static synchronized JsonFactory getFactory() {
        if (factory == null) {
            ObjectMapper om = new ObjectMapper();
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            om.registerModule(new GuavaModule());
            om.registerModule(new AndroidModule());
            factory = new MappingJsonFactory(om);
        }
        return factory;
    }
}
