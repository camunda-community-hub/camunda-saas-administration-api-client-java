package io.camunda.saas.administration.api.client.spring;

import io.camunda.saas.administration.api.client.api.CamundaSaasAdministrationApiClient;
import io.camunda.saas.administration.api.client.properties.CamundaSaasAdministrationApiClientProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers a {@link CamundaSaasAdministrationApiClient} bean unless {@code
 * camunda.saas.administration.api.enabled} is set to {@code false}. Define your own bean of that
 * type to replace it. Startup fails if {@code client-id} or {@code client-secret} is blank.
 */
@AutoConfiguration
@ConditionalOnProperty(
    prefix = "camunda.saas.administration.api",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class CamundaSaasAdministrationApiClientAutoConfiguration {

  /**
   * Binds and validates the external properties only when no {@link
   * CamundaSaasAdministrationApiClientProperties} bean is supplied, so overriding that bean does
   * not require the external credentials.
   */
  @Configuration(proxyBeanMethods = false)
  @ConditionalOnMissingBean(CamundaSaasAdministrationApiClientProperties.class)
  @EnableConfigurationProperties({
    CamundaSaasAdministrationApiClientSpringProperties.class,
    CamundaOAuthSpringProperties.class
  })
  static class DefaultPropertiesConfiguration {

    @Bean
    CamundaSaasAdministrationApiClientProperties camundaSaasAdministrationApiClientProperties(
        CamundaSaasAdministrationApiClientSpringProperties properties,
        CamundaOAuthSpringProperties oAuthProperties) {
      return new CamundaSaasAdministrationApiClientProperties(
          properties.baseUrl(),
          properties.clientId(),
          properties.clientSecret(),
          oAuthProperties.oauthUrl(),
          properties.oauthAudience());
    }
  }

  @Bean
  @ConditionalOnMissingBean
  public CamundaSaasAdministrationApiClient camundaSaasAdministrationApiClient(
      CamundaSaasAdministrationApiClientProperties properties) {
    return CamundaSaasAdministrationApiClient.create(properties);
  }
}
