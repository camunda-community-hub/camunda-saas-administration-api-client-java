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
 * type to replace it; as the external properties are still bound and validated, also set {@code
 * enabled=false} if you do not configure the credentials externally. Startup fails if {@code
 * client-id} or {@code client-secret} is blank.
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
  public CamundaSaasAdministrationApiClient camundaSaasAdministrationApiClient(
      CamundaSaasAdministrationApiClientSpringProperties properties,
      CamundaOAuthSpringProperties oAuthProperties) {
    return CamundaSaasAdministrationApiClient.create(
        toClientProperties(properties, oAuthProperties));
  }

  static CamundaSaasAdministrationApiClientProperties toClientProperties(
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
