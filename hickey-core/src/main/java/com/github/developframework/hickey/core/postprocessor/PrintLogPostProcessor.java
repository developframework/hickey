package com.github.developframework.hickey.core.postprocessor;

import com.github.developframework.hickey.core.HickeyPostProcessor;
import com.github.developframework.hickey.core.structs.RequestWrapper;
import com.github.developframework.hickey.core.structs.ResponseWrapper;

import java.util.List;
import java.util.Map;

/**
 * @author qiushui on 2022-01-06.
 */
public final class PrintLogPostProcessor implements HickeyPostProcessor {

    @Override
    public void process(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
        debugPrintLog(requestWrapper, responseWrapper);
    }

    private void debugPrintLog(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
        StringBuilder sb = new StringBuilder("\n=========================================================================================================\n");
        sb
                .append("\nlabel: ").append(requestWrapper.getLabel())
                .append("\ntimestamp: ").append(requestWrapper.getBeginTimestamp())
                .append("\nrequest:\n  method: ").append(requestWrapper.getMethod()).append("\n  url: ")
                .append(requestWrapper.getFullUrl()).append("\n  headers:\n");
        requestWrapper
                .getHeaders()
                .forEach((k, v) -> sb.append("    ").append(k).append(": ").append(v).append("\n"));
        sb.append("  body: ").append(requestWrapper.getRequestBody().pretty()).append("\n").append("\nresponse:\n");
        if (responseWrapper.isConnectError()) {
            sb.append("  (connect error)");
        } else if (responseWrapper.isConnectTimeout()) {
            sb.append("  (connect timeout ").append(requestWrapper.getConnectionTimeout().getSeconds()).append("s)");
        } else if (responseWrapper.isReadTimeout()) {
            sb.append("  (read timeout ").append(requestWrapper.getReadTimeout().getSeconds()).append("s)");
        } else if (responseWrapper.getErrorMessage() != null) {
            sb.append("  (io error ").append(responseWrapper.getErrorMessage()).append(")");
        } else if (responseWrapper.getHeaders() != null) {
            sb.append("  status: ").append(responseWrapper.getStatus()).append("\n  headers:\n");
            for (Map.Entry<String, List<String>> entry : responseWrapper.getHeaders().entrySet()) {
                sb.append("    ").append(entry.getKey()).append(": ").append(String.join(";", entry.getValue())).append("\n");
            }
            sb.append("  cost: ").append(responseWrapper.getCost()).append("ms\n");
//            sb.append("  body: ").append(bodyToString(receiver.getBody()));
        }
        sb.append("\n\n=========================================================================================================\n");
        System.out.println(sb);
    }
}
