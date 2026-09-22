package com.minispring2.web.core;

import java.util.Map;

/**
 *
 * @since 2026-09-09 11:26:26
 **/
public class HttpRequest {
    public RequestMappingInfo requestMappingInfo;
    public Map<String, Object> requestParams;
    public String requestBody;
}
