package com.example.armeriacircuitbreakerwithokhttpclient.config;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import com.linecorp.armeria.client.circuitbreaker.CircuitBreaker;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Slf4j
public class OkHttpCircuitBreakerRetryInterceptor implements Interceptor {
    private final int retryCount;
    private final CircuitBreaker circuitBreaker;

    public OkHttpCircuitBreakerRetryInterceptor(CircuitBreaker circuitBreaker, int retryCount) {
        this.circuitBreaker = circuitBreaker;
        this.retryCount = retryCount;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        if (circuitBreaker.canRequest()) {
            Request request = chain.request();
            Response response = doRequest(chain, request);

            int tryCount = 0;
            while (isNull(response) && tryCount < retryCount) {
                circuitBreaker.onFailure();
                log.info("tryCount : {}, retryCount: {}, onFailure", tryCount, retryCount);
                tryCount++;
                Request newRequest = request.newBuilder().build();
                response = doRequest(chain, newRequest);
            }

            if (isNull(response)) {
                throw new IOException("Retry count exceed");
            }
            return response;
        } else {
            Request request = chain.request();
            log.info("Not accessible: {}", request.url());
            return new Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_2)
                    .code(500)
                    .message("Fail to request externally by Circuit Breaker")
                    .body(ResponseBody.create("{\"message\":\"Connection Internally Denied by Circuit Breaker\"}", MediaType.get(APPLICATION_JSON_VALUE)))
                    .build();
        }
    }

    @Nullable
    private Response doRequest(Chain chain, Request request) throws IOException {
        Response response = null;
        try {
            response = chain.proceed(request);
            if (response.isSuccessful() || response.isRedirect()) {
                circuitBreaker.onSuccess();
            } else {
                circuitBreaker.onFailure();
            }
        } catch (Exception e) {
            log.warn("Failed request {}", request, e);
        }
        return response;
    }
}
