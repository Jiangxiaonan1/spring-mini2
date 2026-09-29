package com.minispring2.web.servlet;

import com.minispring2.web.core.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @since 2026-09-23 10:03:50
 **/
public class DispatcherServlet extends HttpServlet {

    private Dispatcher dispatcher;

    public DispatcherServlet(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * curl "http://localhost:8080/getUser?userId=12345"
     * @param req  the {@link HttpServletRequest} object that contains the request the client made of the servlet
     * @param resp the {@link HttpServletResponse} object that contains the response the servlet returns to the client
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpRequest httpRequest = new HttpRequest();
        RequestMappingInfo requestMappingInfo = new RequestMappingInfo();
        requestMappingInfo.path = req.getRequestURI();
        requestMappingInfo.method = RequestMethod.valueOf(req.getMethod());
        // requestParam
        Map<String,Object> params = new HashMap<>();
        Map<String, String[]> parameterMap = req.getParameterMap();
        for (String key : parameterMap.keySet()) {
            params.put(key, parameterMap.get(key)[0]);
        }
        httpRequest.requestParams = params;
        // requestBody
        StringBuilder stringBuilder = new StringBuilder();
        try(BufferedReader bufferedReader =  req.getReader()) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        httpRequest.requestBody = stringBuilder.toString();
        httpRequest.requestMappingInfo = requestMappingInfo;
        HttpResponse httpResponse = new HttpResponse();
        dispatcher.doService(httpRequest, httpResponse);
        if (httpResponse.status != null) {
            resp.setStatus(httpResponse.status);
        }
        if (httpResponse.contentType != null) {
            resp.setContentType(httpResponse.contentType);
        }
        if (httpResponse.responseBody != null) {
            resp.getWriter().write(httpResponse.responseBody);
        }
    }

}
