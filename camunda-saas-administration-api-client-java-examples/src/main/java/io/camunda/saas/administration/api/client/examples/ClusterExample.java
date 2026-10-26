package io.camunda.saas.administration.api.client.examples;

import io.camunda.saas.administration.api.client.api.CamundaSaasAdministrationApiClient;
import io.camunda.saas.administration.api.client.api.model.ClusterResponse;
import io.camunda.saas.administration.api.client.api.model.CreateClusterRequest;
import io.camunda.saas.administration.api.client.api.model.CreateClusterResponse;
import io.camunda.saas.administration.api.client.api.model.ParametersResponse;
import io.camunda.saas.administration.api.client.api.model.StageLabel;
import io.camunda.saas.administration.api.client.api.model.UpdateClusterRequest;
import java.util.List;

public class ClusterExample {

  List<ClusterResponse> list(CamundaSaasAdministrationApiClient client) {
    // docs:start cluster-list
    List<ClusterResponse> clusters = client.clusters().get();
    // docs:end cluster-list
    return clusters;
  }

  ParametersResponse parameters(CamundaSaasAdministrationApiClient client) {
    // docs:start cluster-parameters
    ParametersResponse parameters = client.clusters().parameters().get();
    // docs:end cluster-parameters
    return parameters;
  }

  String create(CamundaSaasAdministrationApiClient client, ParametersResponse parameters) {
    // docs:start cluster-create
    var channel = parameters.channel().get(0);
    var region = parameters.regions().get(0);
    CreateClusterResponse created =
        client
            .clusters()
            .post(
                new CreateClusterRequest(
                    "my-cluster",
                    "created via the administration API",
                    parameters.clusterPlanTypes().get(0).uuid(),
                    channel.uuid(),
                    channel.defaultGeneration().uuid(),
                    region.uuid(),
                    null,
                    null,
                    StageLabel.DEV,
                    null,
                    null,
                    null));
    String clusterId = created.clusterId();
    // docs:end cluster-create
    return clusterId;
  }

  ClusterResponse get(CamundaSaasAdministrationApiClient client, String clusterId) {
    // docs:start cluster-get
    ClusterResponse cluster = client.clusters(clusterId).get();
    // docs:end cluster-get
    return cluster;
  }

  void update(CamundaSaasAdministrationApiClient client, String clusterId) {
    // docs:start cluster-update
    client
        .clusters(clusterId)
        .patch(new UpdateClusterRequest("renamed-cluster", null, StageLabel.TEST, null));
    // docs:end cluster-update
  }

  void delete(CamundaSaasAdministrationApiClient client, String clusterId) {
    // docs:start cluster-delete
    client.clusters(clusterId).delete();
    // docs:end cluster-delete
  }
}
