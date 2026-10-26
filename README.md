# Camunda SaaS Administration API Client Java

## How to use

To use the Camunda SaaS administration api client, add the dependency to your project:

```xml
<dependency>
  <groupId>io.camunda</groupId>
  <artifactId>camunda-saas-administration-api-client-java-core</artifactId>
  <version>${version.camunda-saas-administration-api-client-java}</version>
</dependency>
```

Then, you can bootstrap the client:

* by providing `CamundaSaasAdministrationApiClientProperties`:

<!-- docs:snippet bootstrap-properties java -->
```java
var saasAdministrationApiClient =
    CamundaSaasAdministrationApiClient.create(camundaSaasAdministrationApiClientProperties);
```
<!-- docs:end -->

* by using env variables:

<!-- docs:snippet bootstrap-env java -->
```java
var saasAdministrationApiClient = CamundaSaasAdministrationApiClient.fromEnv();
```
<!-- docs:end -->

The environment variables are:

```
CAMUNDA_SAAS_ADMINISTRATION_API_CLIENT_ID # (mandatory)
CAMUNDA_SAAS_ADMINISTRATION_API_CLIENT_SECRET # (mandatory)
CAMUNDA_OAUTH_URL # (optional, default: 'https://login.cloud.camunda.io/oauth/token')
CAMUNDA_SAAS_ADMINISTRATION_API_BASE_URL # (optional, default: 'https://api.cloud.camunda.io')
CAMUNDA_SAAS_ADMINISTRATION_API_OAUTH_AUDIENCE # (optional, default: 'api.cloud.camunda.io')
```

* by using a properties file:

<!-- docs:snippet bootstrap-properties-file java -->
```java
var saasAdministrationApiClient = CamundaSaasAdministrationApiClient.fromProperties(properties);
```
<!-- docs:end -->

The properties are:

```
camunda.saas.administration.api.client-id (mandatory)
camunda.saas.administration.api.client-secret (mandatory)
camunda.oauth-url (default: 'https://login.cloud.camunda.io/oauth/token')
camunda.saas.administration.api.base-url (default: 'https://api.cloud.camunda.io')
camunda.saas.administration.api.oauth-audience (default: 'api.cloud.camunda.io')
```

## Usage

The client mirrors the REST paths of the Camunda SaaS administration API. Each snippet below is compiled as part of the `examples` module.

### Clusters

List the clusters of your organization:

<!-- docs:snippet cluster-list java -->
```java
List<ClusterResponse> clusters = client.clusters().get();
```
<!-- docs:end -->

Look up the parameters (plan types, channels, regions) needed to create a cluster, then create one:

<!-- docs:snippet cluster-parameters java -->
```java
ParametersResponse parameters = client.clusters().parameters().get();
```
<!-- docs:end -->

<!-- docs:snippet cluster-create java -->
```java
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
```
<!-- docs:end -->

Get, update and delete a cluster:

<!-- docs:snippet cluster-get java -->
```java
ClusterResponse cluster = client.clusters(clusterId).get();
```
<!-- docs:end -->

<!-- docs:snippet cluster-update java -->
```java
client
    .clusters(clusterId)
    .patch(new UpdateClusterRequest("renamed-cluster", null, StageLabel.TEST, null));
```
<!-- docs:end -->

<!-- docs:snippet cluster-delete java -->
```java
client.clusters(clusterId).delete();
```
<!-- docs:end -->

### API clients

<!-- docs:snippet client-create java -->
```java
CreateClusterClientResponse created =
    client
        .clusters(clusterId)
        .clients()
        .post(
            new CreateClusterClientRequest(
                "my-client", List.of(Permission.ZEEBE, Permission.OPERATE)));
// the secret is only returned once
String clientSecret = created.clientSecret();
```
<!-- docs:end -->

<!-- docs:snippet client-list java -->
```java
List<ClusterClientResponse> clients = client.clusters(clusterId).clients().get();
```
<!-- docs:end -->

<!-- docs:snippet client-delete java -->
```java
client.clusters(clusterId).clients(clientId).delete();
```
<!-- docs:end -->

### Secrets, backups, IP allow list and wake

<!-- docs:snippet secrets java -->
```java
client.clusters(clusterId).secrets().post(new CreateSecretRequest("MY_SECRET", "s3cr3t"));
Map<String, String> secrets = client.clusters(clusterId).secrets().get();
client.clusters(clusterId).secrets("MY_SECRET").delete();
```
<!-- docs:end -->

<!-- docs:snippet backups java -->
```java
BackupResponse backup = client.clusters(clusterId).backups().post();
List<BackupResponse> backups = client.clusters(clusterId).backups().get();
```
<!-- docs:end -->

<!-- docs:snippet ip-allow-list java -->
```java
client
    .clusters(clusterId)
    .ipallowlist()
    .put(new IpAllowListRequest(List.of(new IpAllow("office", "203.0.113.0/24"))));
```
<!-- docs:end -->

<!-- docs:snippet wake java -->
```java
client.clusters(clusterId).wake().put();
```
<!-- docs:end -->

### Members

<!-- docs:snippet members java -->
```java
List<MemberResponse> members = client.members().get();
client
    .members("jane.doe@example.com")
    .post(new PostMemberRequest(List.of(PostMemberRequest.Role.DEVELOPER)));
client.members("jane.doe@example.com").delete();
```
<!-- docs:end -->

### Error handling

Failed calls throw a `CamundaConsoleClientException`:

<!-- docs:snippet error-handling java -->
```java
try {
  client.clusters(clusterId).get();
} catch (CamundaConsoleClientException e) {
  // the cause holds the underlying HTTP or I/O failure
  Throwable cause = e.getCause();
}
```
<!-- docs:end -->

## Spring Boot starter

For Spring Boot applications, use the starter instead of bootstrapping the client manually:

```xml
<dependency>
  <groupId>io.camunda</groupId>
  <artifactId>camunda-saas-administration-api-client-java-spring-boot-starter</artifactId>
  <version>${version.camunda-saas-administration-api-client-java}</version>
</dependency>
```

A `CamundaSaasAdministrationApiClient` bean is registered by default. Set `camunda.saas.administration.api.enabled` to `false` to disable it, or define your own bean of that type to replace it (the external properties are still validated, so set `enabled` to `false` if you do not configure the credentials externally). To have missing credentials fail at startup, add `spring-boot-starter-validation` to your application; the starter does not bring a validator itself. Inject the client like any other bean:

<!-- docs:snippet spring-boot-usage java -->
```java
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
```
<!-- docs:end -->

The properties are the same as above:

```yaml
camunda:
  oauth-url: https://login.cloud.camunda.io/oauth/token # optional
  saas:
    administration:
      api:
        enabled: true # optional
        client-id: ... # mandatory
        client-secret: ... # mandatory
        base-url: https://api.cloud.camunda.io # optional
        oauth-audience: api.cloud.camunda.io # optional
```

## Development

The generated client sources are committed in `camunda-saas-administration-api-client-java-generated/src/main/java`, so regular builds do not need the OpenAPI spec. To regenerate them from the current spec, run:

```
mvn verify -PgenerateClient
```

This also runs as a pre-commit hook.

### Documentation snippets

Code snippets in this README are not written by hand. They live in the `camunda-saas-administration-api-client-java-examples` module, delimited by `docs:start <name>` / `docs:end <name>` comments, so the compiler fails the build when the API drifts. Every build copies them into the `<!-- docs:snippet <name> <lang> -->` blocks of this file (see `SyncDocs` in the `util` module). Do not edit the content of those blocks; edit the example instead and commit the updated README.
