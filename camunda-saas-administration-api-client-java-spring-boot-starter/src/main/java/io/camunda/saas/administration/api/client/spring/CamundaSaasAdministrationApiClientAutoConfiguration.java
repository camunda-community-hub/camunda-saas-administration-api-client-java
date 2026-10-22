package io.camunda.saas.administration.api.client.spring;

import static io.camunda.saas.administration.api.client.properties.PropertiesConstants.Default.CAMUNDA_SAAS_OAUTH_URL;

import io.camunda.saas.administration.api.client.api.CamundaSaasAdministrationApiClient;
import io.camunda.saas.administration.api.client.properties.CamundaSaasAdministrationApiClientProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Registers a {@link CamundaSaasAdministrationApiClient} bean unless {@code
 * camunda.saas.administration.api.enabled} is set to {@code false}. Define your own bean of that
 * type to replace it.
 */
@AutoConfiguration
@ConditionalOnProperty(
    prefix = "camunda.saas.administration.api",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@EnableConfigurationProperties(CamundaSaasAdministrationApiClientSpringProperties.class)
public class CamundaSaasAdministrationApiClientAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public CamundaSaasAdministrationApiClientProperties camundaSaasAdministrationApiClientProperties(
      CamundaSaasAdministrationApiClientSpringProperties properties,
      @Value("${camunda.oauth-url:" + CAMUNDA_SAAS_OAUTH_URL + "}") String oAuthUrl) {
    return new CamundaSaasAdministrationApiClientProperties(
        properties.baseUrl(),
        properties.clientId(),
        properties.clientSecret(),
        oAuthUrl,
        properties.oauthAudience());
  }

  @Bean
  @ConditionalOnMissingBean
  public CamundaSaasAdministrationApiClient camundaSaasAdministrationApiClient(
      CamundaSaasAdministrationApiClientProperties properties) {
    return CamundaSaasAdministrationApiClient.create(properties);
  }
}
