package io.camunda.saas.administration.api.client.examples;

import io.camunda.saas.administration.api.client.api.CamundaSaasAdministrationApiClient;
import io.camunda.saas.administration.api.client.api.model.ClusterResponse;
import java.util.List;
import org.springframework.stereotype.Service;

// docs:start spring-boot-usage
@Service
public class SpringBootExample {

  private final CamundaSaasAdministrationApiClient client;

  public SpringBootExample(CamundaSaasAdministrationApiClient client) {
    this.client = client;
  }

  public List<ClusterResponse> clusters() {
    return client.clusters().get();
  }
}
// docs:end spring-boot-usage
