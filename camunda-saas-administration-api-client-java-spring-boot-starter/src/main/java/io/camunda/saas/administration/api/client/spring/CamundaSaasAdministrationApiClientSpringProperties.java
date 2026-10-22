package io.camunda.saas.administration.api.client.spring;

import static io.camunda.saas.administration.api.client.properties.PropertiesConstants.Default.*;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Spring Boot binding for the {@code camunda.saas.administration.api.*} properties. The OAuth URL
 * is bound separately from {@code camunda.oauth-url}, which is shared with other Camunda clients.
 */
@ConfigurationProperties(prefix = "camunda.saas.administration.api")
public record CamundaSaasAdministrationApiClientSpringProperties(
    String clientId,
    String clientSecret,
    @DefaultValue(CAMUNDA_SAAS_ADMINISTRATION_API_SAAS_BASE_URL) String baseUrl,
    @DefaultValue(CAMUNDA_SAAS_ADMINISTRATION_API_SAAS_OAUTH_AUDIENCE) String oauthAudience) {}
