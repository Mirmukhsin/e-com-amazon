package org.ecomapp.paymentservice.security.http;

import org.ecomapp.paymentservice.security.config.InternalAuthentication;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class InternalTokenInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof InternalAuthentication internalAuth) {
            request.getHeaders()
                    .add("X-Internal-Auth", internalAuth.getToken());
        }

        return execution.execute(request, body);
    }
}
