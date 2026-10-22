package io.camunda.saas.administration.api.client.spring;

import io.camunda.saas.administration.api.client.api.CamundaSaasAdministrationApiClient;
import io.camunda.saas.administration.api.client.properties.CamundaSaasAdministrationApiClientProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Registers a {@link CamundaSaasAdministrationApiClient} bean unless {@code
 * camunda.saas.administration.api.enabled} is set to {@code false}. Define your own bean of that
 * type to replace it. Startup fails if {@code client-id} or {@code client-secret} is missing.
 */
@AutoConfiguration
@ConditionalOnProperty(
    prefix = "camunda.saas.administration.api",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@EnableConfigurationProperties({
  CamundaSaasAdministrationApiClientSpringProperties.class,
  CamundaOAuthSpringProperties.class
})
public class CamundaSaasAdministrationApiClientAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public CamundaSaasAdministrationApiClientProperties camundaSaasAdministrationApiClientProperties(
      CamundaSaasAdministrationApiClientSpringProperties properties,
      CamundaOAuthSpringProperties oAuthProperties) {
    requireText(properties.clientId(), "client-id");
    requireText(properties.clientSecret(), "client-secret");
    return new CamundaSaasAdministrationApiClientProperties(
        properties.baseUrl(),
        properties.clientId(),
        properties.clientSecret(),
        oAuthProperties.oauthUrl(),
        properties.oauthAudience());
  }

  @Bean
  @ConditionalOnMissingBean
  public CamundaSaasAdministrationApiClient camundaSaasAdministrationApiClient(
      CamundaSaasAdministrationApiClientProperties properties) {
    return CamundaSaasAdministrationApiClient.create(properties);
  }

  private static void requireText(String value, String name) {
    if (value == null || value.isBlank()) {
      throw new IllegalStateException(
          "Property 'camunda.saas.administration.api."
              + name
              + "' must be set, or disable the client with"
              + " 'camunda.saas.administration.api.enabled=false'");
    }
  }
}
