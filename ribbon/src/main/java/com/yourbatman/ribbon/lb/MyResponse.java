package com.yourbatman.ribbon.lb;

import com.netflix.client.ClientException;
import com.netflix.client.IResponse;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class MyResponse implements IResponse {

    private URI requestUri;
    public void setRequestUri(URI requestUri) {
        this.requestUri = requestUri;
    }

    @Override
    public Object getPayload() throws ClientException {
        return "ResponseBody";
    }

    @Override
    public boolean hasPayload() {
        return true;
    }

    // 永远成功
    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public URI getRequestedURI() {
        return requestUri;
    }

    @Override
    public Map<String, ?> getHeaders() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
