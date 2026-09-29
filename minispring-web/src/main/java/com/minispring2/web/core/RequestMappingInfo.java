package com.minispring2.web.core;

import java.util.Objects;

/**
 *
 * @since 2026-09-21 10:54:44
 **/
public class RequestMappingInfo {
    public String path;
    public String regexPath;
    public RequestMethod method;

    public RequestMappingInfo(String path, RequestMethod method) {
        this.path = path;
        this.method = method;
    }

    public RequestMappingInfo() {
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        RequestMappingInfo that = (RequestMappingInfo) object;
        return Objects.equals(path, that.path) && method == that.method;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, method);
    }
}
