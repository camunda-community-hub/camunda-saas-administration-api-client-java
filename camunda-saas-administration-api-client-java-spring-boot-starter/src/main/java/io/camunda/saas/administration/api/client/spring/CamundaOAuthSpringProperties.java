package io.camunda.saas.administration.api.client.spring;

import static io.camunda.saas.administration.api.client.properties.PropertiesConstants.Default.CAMUNDA_SAAS_OAUTH_URL;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Spring Boot binding for {@code camunda.oauth-url}, which is shared with other Camunda clients.
 */
@ConfigurationProperties(prefix = "camunda")
public record CamundaOAuthSpringProperties(@DefaultValue(CAMUNDA_SAAS_OAUTH_URL) String oauthUrl) {}
