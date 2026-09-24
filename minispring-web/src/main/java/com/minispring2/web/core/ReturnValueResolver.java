package com.minispring2.web.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minispring2.web.common.HttpStatus;

/**
 *
 * @since 2026-09-24 09:56:39
 **/
public class ReturnValueResolver {

    public void returnValueResolver(HttpResponse httpResponse, Object result, Class returnValueClass) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        if(String.class.isAssignableFrom(returnValueClass)) {
            httpResponse.status = HttpStatus.OK.value();
            httpResponse.contentType = "text/plain;charset=UTF-8";
            if(result != null) {
                httpResponse.responseBody = result.toString();
            }
        } else {
            httpResponse.status = HttpStatus.OK.value();
            httpResponse.contentType = "application/json;charset=UTF-8";
            if(result != null) {
                httpResponse.responseBody = objectMapper.writeValueAsString(result);
            }
        }
    }

}
