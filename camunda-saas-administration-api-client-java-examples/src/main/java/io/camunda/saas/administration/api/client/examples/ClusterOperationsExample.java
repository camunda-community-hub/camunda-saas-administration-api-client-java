package io.camunda.saas.administration.api.client.examples;

import io.camunda.saas.administration.api.client.api.CamundaSaasAdministrationApiClient;
import io.camunda.saas.administration.api.client.api.model.BackupResponse;
import io.camunda.saas.administration.api.client.api.model.CreateSecretRequest;
import io.camunda.saas.administration.api.client.api.model.IpAllow;
import io.camunda.saas.administration.api.client.api.model.IpAllowListRequest;
import java.util.List;
import java.util.Map;

public class ClusterOperationsExample {

  void secrets(CamundaSaasAdministrationApiClient client, String clusterId) {
    // docs:start secrets
    client.clusters(clusterId).secrets().post(new CreateSecretRequest("MY_SECRET", "s3cr3t"));
    Map<String, String> secrets = client.clusters(clusterId).secrets().get();
    client.clusters(clusterId).secrets("MY_SECRET").delete();
    // docs:end secrets
  }

  void backups(CamundaSaasAdministrationApiClient client, String clusterId) {
    // docs:start backups
    BackupResponse backup = client.clusters(clusterId).backups().post();
    List<BackupResponse> backups = client.clusters(clusterId).backups().get();
    // docs:end backups
  }

  void ipAllowList(CamundaSaasAdministrationApiClient client, String clusterId) {
    // docs:start ip-allow-list
    client
        .clusters(clusterId)
        .ipallowlist()
        .put(new IpAllowListRequest(List.of(new IpAllow("office", "203.0.113.0/24"))));
    // docs:end ip-allow-list
  }

  void wake(CamundaSaasAdministrationApiClient client, String clusterId) {
    // docs:start wake
    client.clusters(clusterId).wake().put();
    // docs:end wake
  }
}
