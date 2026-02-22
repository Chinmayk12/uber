package com.chinuthon.project.uber.uber.advices;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

// ResponseBodyAdvice Uses:
// Modify or enhance the response body globally or conditionally.
//Add metadata (e.g., timestamps, additional fields).
//Apply encryption, logging, or any transformations.
//Format or wrap the response body.

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice {
    @Override

    //To check if the advice applies to the given request or type.
    //This method determines whether the response body advice should be executed for a given request.
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    //To modify the response body before sending it to the client.
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        //Body of ResponseBody Received from controller
        if(body instanceof ApiResponse<?>) {
            return body;     // If it's already wrapped, don't wrap it again, return as is
        }
        // If it's not an ApiResponse instance, wrap the body inside an ApiResponse
        return new ApiResponse<>(body);
    }
}
