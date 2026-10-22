package io.camunda.saas.administration.api.client.spring;

import static org.assertj.core.api.Assertions.assertThat;

import io.camunda.saas.administration.api.client.api.CamundaSaasAdministrationApiClient;
import io.camunda.saas.administration.api.client.properties.CamundaSaasAdministrationApiClientProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

public class CamundaSaasAdministrationApiClientAutoConfigurationTest {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(CamundaSaasAdministrationApiClientAutoConfiguration.class));

  @Test
  void shouldNotCreateClientWhenDisabled() {
    runner
        .withPropertyValues(
            "camunda.saas.administration.api.enabled=false",
            "camunda.saas.administration.api.client-id=id",
            "camunda.saas.administration.api.client-secret=secret")
        .run(
            context ->
                assertThat(context).doesNotHaveBean(CamundaSaasAdministrationApiClient.class));
  }

  @Test
  void shouldFailWithoutCredentials() {
    runner.run(
        context ->
            assertThat(context)
                .hasFailed()
                .getFailure()
                .hasStackTraceContaining("clientId")
                .hasStackTraceContaining("clientSecret"));
  }

  @Test
  void shouldFailWithoutClientSecret() {
    runner
        .withPropertyValues("camunda.saas.administration.api.client-id=id")
        .run(
            context ->
                assertThat(context)
                    .hasFailed()
                    .getFailure()
                    .hasStackTraceContaining("clientSecret"));
  }

  @Test
  void shouldCreateClientWithDefaults() {
    runner
        .withPropertyValues(
            "camunda.saas.administration.api.client-id=id",
            "camunda.saas.administration.api.client-secret=secret")
        .run(
            context -> {
              assertThat(context).hasSingleBean(CamundaSaasAdministrationApiClient.class);
              assertThat(context.getBean(CamundaSaasAdministrationApiClientProperties.class))
                  .isEqualTo(
                      new CamundaSaasAdministrationApiClientProperties(
                          "https://api.cloud.camunda.io",
                          "id",
                          "secret",
                          "https://login.cloud.camunda.io/oauth/token",
                          "api.cloud.camunda.io"));
            });
  }

  @Test
  void shouldBindConfiguredProperties() {
    runner
        .withPropertyValues(
            "camunda.saas.administration.api.client-id=id",
            "camunda.saas.administration.api.client-secret=secret",
            "camunda.saas.administration.api.base-url=http://localhost:8080/api",
            "camunda.saas.administration.api.oauth-audience=audience",
            "camunda.oauth-url=http://localhost:8080/token")
        .run(
            context ->
                assertThat(context.getBean(CamundaSaasAdministrationApiClientProperties.class))
                    .isEqualTo(
                        new CamundaSaasAdministrationApiClientProperties(
                            "http://localhost:8080/api",
                            "id",
                            "secret",
                            "http://localhost:8080/token",
                            "audience")));
  }

  @Test
  void shouldBackOffWhenClientBeanIsDefined() {
    CamundaSaasAdministrationApiClient custom =
        CamundaSaasAdministrationApiClient.create(
            new CamundaSaasAdministrationApiClientProperties(
                "http://localhost/api", "id", "secret", "http://localhost/token", "audience"));
    runner
        .withBean(CamundaSaasAdministrationApiClient.class, () -> custom)
        .withPropertyValues(
            "camunda.saas.administration.api.client-id=id",
            "camunda.saas.administration.api.client-secret=secret")
        .run(
            context ->
                assertThat(context.getBean(CamundaSaasAdministrationApiClient.class))
                    .isSameAs(custom));
  }
}
