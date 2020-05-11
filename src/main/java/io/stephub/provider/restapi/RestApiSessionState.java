package io.stephub.provider.restapi;

import io.stephub.provider.util.LocalProviderAdapter;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import okhttp3.Request;

import java.util.HashMap;
import java.util.Map;

@Data
@SuperBuilder
public class RestApiSessionState extends LocalProviderAdapter.SessionState<RestApiOptions> {
    @Builder.Default
    private Map<String, Request> requests = new HashMap<>();
}
