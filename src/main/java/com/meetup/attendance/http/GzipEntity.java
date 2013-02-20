package com.meetup.attendance.http;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class GzipEntity extends HttpEntityWrapper {
    public GzipEntity(HttpEntity entity) {
        super(entity);
    }

    @Override
    public long getContentLength() {
        return -1;
    }

    @Override
    public InputStream getContent() throws IOException {
        return new GZIPInputStream(wrappedEntity.getContent());
    }
}
