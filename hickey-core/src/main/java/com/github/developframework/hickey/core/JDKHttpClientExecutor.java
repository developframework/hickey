package com.github.developframework.hickey.core;

import com.github.developframework.hickey.core.structs.ContentType;
import com.github.developframework.hickey.core.structs.RequestBody;
import com.github.developframework.hickey.core.structs.RequestWrapper;
import com.github.developframework.hickey.core.structs.ResponseWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * 基于JDK的HttpClient实现Http执行器
 *
 * @author qiushui on 2021-12-30.
 */
public final class JDKHttpClientExecutor extends HickeyHttpExecutor {

    private final HttpClient httpClient;

    public JDKHttpClientExecutor(HickeyOptions options) {
        super(options);
        httpClient = HttpClient
                .newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(options.readOption(HickeyOptions.KEY_DEFAULT_CONNECTION_TIMEOUT, Duration.class))
                .build();
    }

    @Override
    public ResponseWrapper execute(RequestWrapper requestWrapper) {
        final RequestBody requestBody = requestWrapper.getRequestBody();
        final byte[] bodyBytes = requestBody == null ? null : requestBody.toByteArray(requestWrapper.getCharset());
        final HttpRequest.Builder builder = HttpRequest
                .newBuilder()
                .version(httpClient.version())
                .method(
                        requestWrapper.getMethod().name(),
                        bodyBytes == null ?
                                HttpRequest.BodyPublishers.noBody() :
                                HttpRequest.BodyPublishers.ofByteArray(bodyBytes)
                )
                .uri(URI.create(requestWrapper.getFullUrl()))
                .timeout(requestWrapper.getReadTimeout());
        requestWrapper.getHeaders().forEach(builder::header);
        final ResponseWrapper responseWrapper = new ResponseWrapper();
        try {
            final HttpResponse<InputStream> httpResponse = httpClient.send(
                    builder.build(),
                    HttpResponse.BodyHandlers.ofInputStream()
            );
            responseWrapper.setStatus(httpResponse.statusCode());
            responseWrapper.setHeaders(httpResponse.headers().map());
            responseWrapper.setResponseBody(httpResponse.body());
        } catch (ConnectException e) {
            responseWrapper.setConnectError(true);
        } catch (HttpConnectTimeoutException e) {
            responseWrapper.setConnectTimeout(true);
        } catch (HttpTimeoutException e) {
            responseWrapper.setReadTimeout(true);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            responseWrapper.setErrorMessage(e.getMessage());
        } finally {
            responseWrapper.setCost(requestWrapper.getBeginTimestamp().until(LocalDateTime.now(), ChronoUnit.MILLIS));
        }
        return responseWrapper;
    }

    public boolean isReadable(ResponseWrapper responseWrapper) {
        final Map<String, List<String>> headers = responseWrapper.getHeaders();
        final List<String> contentTypes = headers.get("Content-Type");
        String contentTypeStr = contentTypes.isEmpty() ? null : contentTypes.get(0);
        final ContentType contentType = ContentType.matches(contentTypeStr);
    }
}
