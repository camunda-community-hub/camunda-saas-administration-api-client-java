package io.camunda.saas.administration.api.client.api.model;

import java.time.OffsetDateTime;
import java.util.List;

public record ClusterResponse(
    String uuid,
    String name,
    String description,
    String ownerId,
    OffsetDateTime created,
    PlanType planType,
    Region region,
    BackupRegion backupRegion,
    Generation generation,
    Channel channel,
    Boolean autoUpdate,
    List<IpAllow> ipAllowList,
    Status status,
    Links links,
    List<String> labels) {
  public record PlanType(String name, String uuid) {}

  public record Region(String name, String uuid) {}

  public record BackupRegion(String name, String uuid) {}

  public record Channel(String name, String uuid) {}

  public record Generation(String name, String uuid) {}

  public record Status(
      ComponentStatus connectorsStatus,
      ComponentStatus optimizeStatus,
      ComponentStatus tasklistStatus,
      ComponentStatus operateStatus,
      ComponentStatus zeebeStatus,
      ComponentStatus ready) {
    public enum ComponentStatus {
      CREATING,
      HEALTHY,
      UNHEALTHY,
      UPDATING,
      RESUMING,
      SUSPENDED,
      WAITING_FOR_INPUT,
      MAINTENANCE
    }
  }

  public record Links(
      String oauth,
      String connectors,
      String console,
      String identity,
      String optimize,
      String tasklist,
      String operate,
      String zeebe) {}
}
