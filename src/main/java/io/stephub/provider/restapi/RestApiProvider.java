package io.stephub.provider.restapi;

import io.stephub.provider.api.ProviderException;
import io.stephub.provider.api.model.ProviderOptions;
import io.stephub.provider.api.model.spec.PatternType;
import io.stephub.provider.util.StepFailedException;
import io.stephub.provider.util.controller.LocalSpringBeanProviderAdapter;
import io.stephub.provider.util.spring.annotation.StepArgument;
import io.stephub.provider.util.spring.annotation.StepMethod;
import lombok.Builder;
import lombok.Getter;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isNotBlank;


@Service
public class RestApiProvider extends LocalSpringBeanProviderAdapter<RestApiSessionState, RestApiOptions> {
    @Override
    protected String getName() {
        return "rest-api";
    }

    @Override
    protected Class<? extends RestApiOptions> getOptionsSchemaClass() {
        return RestApiOptions.class;
    }

    @Override
    protected RestApiSessionState startState(final String s, final ProviderOptions<RestApiOptions> providerOptions) {
        return RestApiSessionState.builder().build();
    }

    @Override
    protected void stopState(final RestApiSessionState restApiSessionState) {

    }

    @StepMethod(pattern = "a GET request {requestName} to {url}", patternType = PatternType.SIMPLE)
    public void aRequest(final RestApiSessionState state,
                         @StepArgument(name = "requestName") final String requestName,
                         @StepArgument(name = "url") final String url) {
        state.getRequests().put(requestName, new Request.Builder().get().url(this.getUrl(state, url)).build());
    }

    @Builder
    @Getter
    public static class ApiResponse {
        private final int status;
    }

    @StepMethod(pattern = "I execute request {requestName} getting response under {responseName}", patternType = PatternType.SIMPLE)
    public ApiResponse callRequest(final RestApiSessionState state,
                                   @StepArgument(name = "requestName") final String requestName,
                                   @StepArgument(name = "responseName") final String responseName) {
        final Request httpRequest = this.getSafeRequest(state, requestName);
        try {
            final Response httpResponse = this.buildClient(state).newCall(httpRequest).execute();
            return ApiResponse.builder().
                    status(httpResponse.code())
                    .build();
        } catch (final IOException e) {
            throw new StepFailedException(e);
        }

    }

    private HttpUrl getUrl(final RestApiSessionState state, final String givenUrl) {
        if (isNotBlank(state.getProviderOptions().getOptions().getBaseUrl())) {
            return HttpUrl.parse(state.getProviderOptions().getOptions().getBaseUrl()).resolve(givenUrl);
        }
        return HttpUrl.parse(givenUrl);
    }

    private OkHttpClient buildClient(final RestApiSessionState state) {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        return builder.build();
    }

    private Request getSafeRequest(final RestApiSessionState state, final String requestName) {
        if (state.getRequests().containsKey(requestName)) {
            return state.getRequests().get(requestName);
        }
        throw new ProviderException("Request with name '" + requestName + "' not found");
    }
}
