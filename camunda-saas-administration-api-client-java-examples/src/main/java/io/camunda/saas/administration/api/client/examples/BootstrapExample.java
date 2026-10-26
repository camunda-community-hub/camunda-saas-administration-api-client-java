package io.camunda.saas.administration.api.client.examples;

import io.camunda.saas.administration.api.client.api.CamundaSaasAdministrationApiClient;
import io.camunda.saas.administration.api.client.properties.CamundaSaasAdministrationApiClientProperties;
import java.util.Properties;

public class BootstrapExample {

  CamundaSaasAdministrationApiClient fromProperties(
      CamundaSaasAdministrationApiClientProperties camundaSaasAdministrationApiClientProperties) {
    // docs:start bootstrap-properties
    var saasAdministrationApiClient =
        CamundaSaasAdministrationApiClient.create(camundaSaasAdministrationApiClientProperties);
    // docs:end bootstrap-properties
    return saasAdministrationApiClient;
  }

  CamundaSaasAdministrationApiClient fromEnv() {
    // docs:start bootstrap-env
    var saasAdministrationApiClient = CamundaSaasAdministrationApiClient.fromEnv();
    // docs:end bootstrap-env
    return saasAdministrationApiClient;
  }

  CamundaSaasAdministrationApiClient fromPropertiesFile(Properties properties) {
    // docs:start bootstrap-properties-file
    var saasAdministrationApiClient = CamundaSaasAdministrationApiClient.fromProperties(properties);
    // docs:end bootstrap-properties-file
    return saasAdministrationApiClient;
  }
}
