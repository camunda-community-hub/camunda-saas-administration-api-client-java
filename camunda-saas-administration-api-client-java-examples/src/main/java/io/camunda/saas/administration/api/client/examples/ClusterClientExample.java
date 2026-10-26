package io.camunda.saas.administration.api.client.examples;

import io.camunda.saas.administration.api.client.api.CamundaSaasAdministrationApiClient;
import io.camunda.saas.administration.api.client.api.model.ClusterClientResponse;
import io.camunda.saas.administration.api.client.api.model.CreateClusterClientRequest;
import io.camunda.saas.administration.api.client.api.model.CreateClusterClientResponse;
import io.camunda.saas.administration.api.client.api.model.Permission;
import java.util.List;

public class ClusterClientExample {

  CreateClusterClientResponse create(CamundaSaasAdministrationApiClient client, String clusterId) {
    // docs:start client-create
    CreateClusterClientResponse created =
        client
            .clusters(clusterId)
            .clients()
            .post(
                new CreateClusterClientRequest(
                    "my-client", List.of(Permission.ZEEBE, Permission.OPERATE)));
    // the secret is only returned once
    String clientSecret = created.clientSecret();
    // docs:end client-create
    return created;
  }

  List<ClusterClientResponse> list(CamundaSaasAdministrationApiClient client, String clusterId) {
    // docs:start client-list
    List<ClusterClientResponse> clients = client.clusters(clusterId).clients().get();
    // docs:end client-list
    return clients;
  }

  void delete(CamundaSaasAdministrationApiClient client, String clusterId, String clientId) {
    // docs:start client-delete
    client.clusters(clusterId).clients(clientId).delete();
    // docs:end client-delete
  }
}
