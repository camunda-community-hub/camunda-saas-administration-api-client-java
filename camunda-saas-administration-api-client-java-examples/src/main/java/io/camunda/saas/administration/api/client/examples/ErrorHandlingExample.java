package io.camunda.saas.administration.api.client.examples;

import io.camunda.saas.administration.api.client.api.CamundaSaasAdministrationApiClient;
import io.camunda.saas.administration.api.client.exception.CamundaConsoleClientException;

public class ErrorHandlingExample {

  void errorHandling(CamundaSaasAdministrationApiClient client, String clusterId) {
    // docs:start error-handling
    try {
      client.clusters(clusterId).get();
    } catch (CamundaConsoleClientException e) {
      // the cause holds the underlying HTTP or I/O failure
      Throwable cause = e.getCause();
    }
    // docs:end error-handling
  }
}
