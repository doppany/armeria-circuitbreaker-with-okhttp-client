/*
 * Copyright (c) 2021 LINE Corporation. All rights reserved.
 * LINE Corporation PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.example.armeriacircuitbreakerwithokhttpclient.config;

import static java.util.Objects.isNull;

import java.io.IOException;

import org.jetbrains.annotations.Nullable;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
public class OkHttpRetryInterceptor implements Interceptor {
    private final int retryCount;

    public OkHttpRetryInterceptor(int retryCount) {
        this.retryCount = retryCount;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = doRequest(chain, request);

        int tryCount = 0;
        while (isNull(response) && tryCount < retryCount) {
            tryCount++;
            Request newRequest = request.newBuilder().build();
            response = doRequest(chain, newRequest);
        }

        if (isNull(response)) {
            throw new IOException("Retry count exceed");
        }

        return response;
    }

    @Nullable
    private Response doRequest(Chain chain, Request request) throws IOException {
        Response response = null;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            log.warn("Failed request {}", request, e);
        }
        return response;
    }
}
