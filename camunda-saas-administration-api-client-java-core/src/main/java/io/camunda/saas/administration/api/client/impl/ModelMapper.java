package io.camunda.saas.administration.api.client.impl;

import io.camunda.saas.administration.api.client.api.model.ActivityResponse;
import io.camunda.saas.administration.api.client.api.model.ActivityResponse.AuditType;
import io.camunda.saas.administration.api.client.api.model.BackupResponse;
import io.camunda.saas.administration.api.client.api.model.BackupResponse.Status;
import io.camunda.saas.administration.api.client.api.model.ClusterClientDetailsResponse;
import io.camunda.saas.administration.api.client.api.model.ClusterClientResponse;
import io.camunda.saas.administration.api.client.api.model.ClusterResponse;
import io.camunda.saas.administration.api.client.api.model.ClusterResponse.BackupRegion;
import io.camunda.saas.administration.api.client.api.model.ClusterResponse.Channel;
import io.camunda.saas.administration.api.client.api.model.ClusterResponse.Generation;
import io.camunda.saas.administration.api.client.api.model.ClusterResponse.PlanType;
import io.camunda.saas.administration.api.client.api.model.ClusterResponse.Region;
import io.camunda.saas.administration.api.client.api.model.ClusterResponse.Status.ComponentStatus;
import io.camunda.saas.administration.api.client.api.model.ClusterUpgradeResponse;
import io.camunda.saas.administration.api.client.api.model.CreateClusterClientRequest;
import io.camunda.saas.administration.api.client.api.model.CreateClusterClientResponse;
import io.camunda.saas.administration.api.client.api.model.CreateClusterClientResponse.Links;
import io.camunda.saas.administration.api.client.api.model.CreateClusterRequest.Encryption;
import io.camunda.saas.administration.api.client.api.model.CreateClusterResponse;
import io.camunda.saas.administration.api.client.api.model.CreateSecretRequest;
import io.camunda.saas.administration.api.client.api.model.IpAllow;
import io.camunda.saas.administration.api.client.api.model.IpAllowListRequest;
import io.camunda.saas.administration.api.client.api.model.IpRangesResponse;
import io.camunda.saas.administration.api.client.api.model.MemberResponse;
import io.camunda.saas.administration.api.client.api.model.ParametersResponse;
import io.camunda.saas.administration.api.client.api.model.ParametersResponse.Region.Backup;
import io.camunda.saas.administration.api.client.api.model.Permission;
import io.camunda.saas.administration.api.client.api.model.PostMemberRequest;
import io.camunda.saas.administration.api.client.api.model.StageLabel;
import io.camunda.saas.administration.api.client.api.model.UpdateClusterRequest;
import io.camunda.saas.administration.api.client.gen.model.AssignableOrganizationRoleType;
import io.camunda.saas.administration.api.client.gen.model.AuditDto;
import io.camunda.saas.administration.api.client.gen.model.AuditDto.AuditTypeEnum;
import io.camunda.saas.administration.api.client.gen.model.BackupDto;
import io.camunda.saas.administration.api.client.gen.model.BackupStatus;
import io.camunda.saas.administration.api.client.gen.model.CamundaClusterStage;
import io.camunda.saas.administration.api.client.gen.model.Cluster;
import io.camunda.saas.administration.api.client.gen.model.ClusterBackupRegion;
import io.camunda.saas.administration.api.client.gen.model.ClusterChannel;
import io.camunda.saas.administration.api.client.gen.model.ClusterClient;
import io.camunda.saas.administration.api.client.gen.model.ClusterClient.PermissionsEnum;
import io.camunda.saas.administration.api.client.gen.model.ClusterClientConnectionDetails;
import io.camunda.saas.administration.api.client.gen.model.ClusterEncryptionKey;
import io.camunda.saas.administration.api.client.gen.model.ClusterGeneration;
import io.camunda.saas.administration.api.client.gen.model.ClusterIpwhitelistInner;
import io.camunda.saas.administration.api.client.gen.model.ClusterLinks;
import io.camunda.saas.administration.api.client.gen.model.ClusterPlanType;
import io.camunda.saas.administration.api.client.gen.model.ClusterRegion;
import io.camunda.saas.administration.api.client.gen.model.ClusterStatus;
import io.camunda.saas.administration.api.client.gen.model.CreateCluster200Response;
import io.camunda.saas.administration.api.client.gen.model.CreateClusterClientBody;
import io.camunda.saas.administration.api.client.gen.model.CreateClusterRequest;
import io.camunda.saas.administration.api.client.gen.model.CreateSecretBody;
import io.camunda.saas.administration.api.client.gen.model.CreatedClusterClient;
import io.camunda.saas.administration.api.client.gen.model.CreatedClusterClientLinks;
import io.camunda.saas.administration.api.client.gen.model.GenerationUpgradeForClusterDto;
import io.camunda.saas.administration.api.client.gen.model.GenerationUpgradeForClusterDtoCluster;
import io.camunda.saas.administration.api.client.gen.model.IpAllowListBody;
import io.camunda.saas.administration.api.client.gen.model.Member;
import io.camunda.saas.administration.api.client.gen.model.MetaDto;
import io.camunda.saas.administration.api.client.gen.model.OrganizationRole;
import io.camunda.saas.administration.api.client.gen.model.Parameters;
import io.camunda.saas.administration.api.client.gen.model.ParametersChannelsInner;
import io.camunda.saas.administration.api.client.gen.model.ParametersChannelsInnerAllowedGenerationsInner;
import io.camunda.saas.administration.api.client.gen.model.ParametersChannelsInnerDefaultGeneration;
import io.camunda.saas.administration.api.client.gen.model.ParametersRegionsInner;
import io.camunda.saas.administration.api.client.gen.model.ParametersRegionsInnerBackupsInner;
import io.camunda.saas.administration.api.client.gen.model.ParametersRegionsInnerBackupsInnerRegionsInner;
import io.camunda.saas.administration.api.client.gen.model.PostMemberBody;
import io.camunda.saas.administration.api.client.gen.model.UpdateClusterBody;
import java.util.List;

public class ModelMapper {

  public static List<BackupResponse> fromBackupList(List<BackupDto> backups) {
    if (backups == null) {
      return null;
    }
    return backups.stream().map(ModelMapper::from).toList();
  }

  public static BackupResponse from(BackupDto backupDto) {
    if (backupDto == null) {
      return null;
    }
    return new BackupResponse(
        backupDto.getUuid(),
        backupDto.getName(),
        backupDto.getCreated(),
        backupDto.getCompleted(),
        from(backupDto.getStatus()),
        from(backupDto.getZeebeStatus()),
        from(backupDto.getTasklistStatus()),
        from(backupDto.getOperateStatus()),
        from(backupDto.getOptimizeStatus()));
  }

  private static Status from(BackupStatus status) {
    if (status == null) {
      return null;
    }
    return switch (status) {
      case MINUS -> Status.MINUS;
      case FAILED -> Status.FAILED;
      case COMPLETE -> Status.COMPLETE;
      case IN_PROGRESS -> Status.IN_PROGRESS;
    };
  }

  public static ClusterClientDetailsResponse from(ClusterClientConnectionDetails client) {
    if (client == null) {
      return null;
    }
    return new ClusterClientDetailsResponse(
        client.getName(),
        client.getZEEBEADDRESS(),
        client.getZEEBECLIENTID(),
        client.getZEEBEAUTHORIZATIONSERVERURL());
  }

  public static List<ClusterClientResponse> fromClientList(List<ClusterClient> clients) {
    if (clients == null) {
      return null;
    }
    return clients.stream().map(ModelMapper::from).toList();
  }

  public static ClusterClientResponse from(ClusterClient clusterClient) {
    if (clusterClient == null) {
      return null;
    }
    return new ClusterClientResponse(
        clusterClient.getName(),
        clusterClient.getClientId(),
        fromPermissionsEnum(clusterClient.getPermissions()));
  }

  public static List<Permission> fromPermissionsEnum(List<PermissionsEnum> permissions) {
    if (permissions == null) {
      return null;
    }
    return permissions.stream().map(ModelMapper::from).toList();
  }

  public static Permission from(PermissionsEnum permissionsEnum) {
    if (permissionsEnum == null) {
      return null;
    }
    return switch (permissionsEnum) {
      case ZEEBE -> Permission.ZEEBE;
      case OPERATE -> Permission.OPERATE;
      case OPTIMIZE -> Permission.OPTIMIZE;
      case SECRETS -> Permission.SECRETS;
      case TASKLIST -> Permission.TASKLIST;
    };
  }

  public static CreateClusterClientBody from(CreateClusterClientRequest request) {
    if (request == null) {
      return null;
    }
    return new CreateClusterClientBody()
        .clientName(request.clientName())
        .permissions(fromPermissions(request.permissions()));
  }

  public static List<CreateClusterClientBody.PermissionsEnum> fromPermissions(
      List<Permission> permissions) {
    if (permissions == null) {
      return null;
    }
    return permissions.stream().map(ModelMapper::from).toList();
  }

  private static CreateClusterClientBody.PermissionsEnum from(Permission permission) {
    if (permission == null) {
      return null;
    }
    return switch (permission) {
      case ZEEBE -> CreateClusterClientBody.PermissionsEnum.ZEEBE;
      case OPERATE -> CreateClusterClientBody.PermissionsEnum.OPERATE;
      case OPTIMIZE -> CreateClusterClientBody.PermissionsEnum.OPTIMIZE;
      case SECRETS -> CreateClusterClientBody.PermissionsEnum.SECRETS;
      case TASKLIST -> CreateClusterClientBody.PermissionsEnum.TASKLIST;
    };
  }

  public static CreateClusterClientResponse from(CreatedClusterClient client) {
    if (client == null) {
      return null;
    }
    return new CreateClusterClientResponse(
        client.getName(),
        client.getUuid(),
        client.getClientId(),
        client.getClientSecret(),
        client.getPermissions(),
        from(client.getLinks()));
  }

  private static Links from(CreatedClusterClientLinks links) {
    if (links == null) {
      return null;
    }
    return new Links(
        links.getOauth(),
        links.getConnectors(),
        links.getConsole(),
        links.getOptimize(),
        links.getTasklist(),
        links.getOperate(),
        links.getZeebe());
  }

  public static ClusterResponse from(Cluster cluster) {
    if (cluster == null) {
      return null;
    }
    return new ClusterResponse(
        cluster.getUuid(),
        cluster.getName(),
        cluster.getDescription(),
        cluster.getOwnerId(),
        cluster.getCreated(),
        from(cluster.getPlanType()),
        from(cluster.getRegion()),
        from(cluster.getBackupRegion()),
        from(cluster.getGeneration()),
        from(cluster.getChannel()),
        cluster.getAutoUpdate(),
        fromIpWhiteList(cluster.getIpallowlist()),
        from(cluster.getStatus()),
        from(cluster.getLinks()),
        cluster.getLabels());
  }

  private static ClusterResponse.Links from(ClusterLinks links) {
    if (links == null) {
      return null;
    }
    return new ClusterResponse.Links(
        links.getOauth(),
        links.getConnectors(),
        links.getConsole(),
        links.getIdentity(),
        links.getOptimize(),
        links.getTasklist(),
        links.getOperate(),
        links.getZeebe());
  }

  private static ClusterResponse.Status from(
      io.camunda.saas.administration.api.client.gen.model.Status status) {
    if (status == null) {
      return null;
    }
    return new ClusterResponse.Status(
        from(status.getConnectorsStatus()),
        from(status.getOptimizeStatus()),
        from(status.getTasklistStatus()),
        from(status.getOperateStatus()),
        from(status.getZeebeStatus()),
        from(status.getReady()));
  }

  private static ComponentStatus from(ClusterStatus status) {
    if (status == null) {
      return null;
    }
    return switch (status) {
      case HEALTHY -> ComponentStatus.HEALTHY;
      case UNHEALTHY -> ComponentStatus.UNHEALTHY;
      case CREATING -> ComponentStatus.CREATING;
      case UPDATING -> ComponentStatus.UPDATING;
      case RESUMING -> ComponentStatus.RESUMING;
      case SUSPENDED -> ComponentStatus.SUSPENDED;
      case WAITING_FOR_INPUT -> ComponentStatus.WAITING_FOR_INPUT;
      case MAINTENANCE -> ComponentStatus.MAINTENANCE;
    };
  }

  private static List<IpAllow> fromIpWhiteList(List<ClusterIpwhitelistInner> ipallowlist) {
    if (ipallowlist == null) {
      return null;
    }
    return ipallowlist.stream().map(ModelMapper::from).toList();
  }

  private static IpAllow from(ClusterIpwhitelistInner clusterIpwhitelistInner) {
    if (clusterIpwhitelistInner == null) {
      return null;
    }
    return new IpAllow(clusterIpwhitelistInner.getDescription(), clusterIpwhitelistInner.getIp());
  }

  private static Channel from(ClusterChannel channel) {
    if (channel == null) {
      return null;
    }
    return new Channel(channel.getName(), channel.getUuid());
  }

  private static Generation from(ClusterGeneration generation) {
    if (generation == null) {
      return null;
    }
    return new Generation(generation.getName(), generation.getUuid());
  }

  private static BackupRegion from(ClusterBackupRegion backupRegion) {
    if (backupRegion == null) {
      return null;
    }
    return new BackupRegion(backupRegion.getName(), backupRegion.getUuid());
  }

  private static Region from(ClusterRegion region) {
    if (region == null) {
      return null;
    }
    return new Region(region.getName(), region.getUuid());
  }

  private static PlanType from(ClusterPlanType planType) {
    if (planType == null) {
      return null;
    }
    return new PlanType(planType.getName(), planType.getUuid());
  }

  public static UpdateClusterBody from(UpdateClusterRequest request) {
    if (request == null) {
      return null;
    }
    return new UpdateClusterBody()
        .name(request.name())
        .description(request.description())
        .stageLabel(from(request.stageLabel()))
        .numberOfAllocatedHwPackages(request.numberOfAllocatedHwPackages());
  }

  private static CamundaClusterStage from(StageLabel stageLabel) {
    if (stageLabel == null) {
      return null;
    }
    return switch (stageLabel) {
      case DEV -> CamundaClusterStage.DEV;
      case PROD -> CamundaClusterStage.PROD;
      case TEST -> CamundaClusterStage.TEST;
      case STAGE -> CamundaClusterStage.STAGE;
    };
  }

  public static List<ClusterResponse> fromClusterList(List<Cluster> clusters) {
    if (clusters == null) {
      return null;
    }
    return clusters.stream().map(ModelMapper::from).toList();
  }

  public static CreateClusterRequest from(
      io.camunda.saas.administration.api.client.api.model.CreateClusterRequest request) {
    if (request == null) {
      return null;
    }
    return new CreateClusterRequest()
        .name(request.name())
        .description(request.description())
        .planTypeId(request.planTypeId())
        .channelId(request.channelId())
        .generationId(request.generationId())
        .regionId(request.regionId())
        .backupRegionId(request.backupRegionId())
        .autoUpdate(request.autoUpdate())
        .stageLabel(from(request.stageLabel()))
        .encryption(from(request.encryption()))
        .hardwarePackages(request.hardwarePackages())
        .identityBackendChecksEnabled(request.identityBackendChecksEnabled());
  }

  private static ClusterEncryptionKey from(Encryption encryption) {
    if (encryption == null) {
      return null;
    }
    return switch (encryption) {
      case EXTERNAL -> ClusterEncryptionKey.EXTERNAL;
      case HARDWARE -> ClusterEncryptionKey.HARDWARE;
      case PROVIDER -> ClusterEncryptionKey.PROVIDER;
      case SOFTWARE -> ClusterEncryptionKey.SOFTWARE;
    };
  }

  public static CreateClusterResponse from(CreateCluster200Response cluster) {
    if (cluster == null) {
      return null;
    }
    return new CreateClusterResponse(cluster.getClusterId());
  }

  public static IpAllowListBody from(IpAllowListRequest request) {
    if (request == null) {
      return null;
    }
    return new IpAllowListBody().ipallowlist(fromIpAllowList(request.ipallowlist()));
  }

  private static List<ClusterIpwhitelistInner> fromIpAllowList(List<IpAllow> ipallowlist) {
    if (ipallowlist == null) {
      return null;
    }
    return ipallowlist.stream().map(ModelMapper::from).toList();
  }

  private static ClusterIpwhitelistInner from(IpAllow ipAllow) {
    if (ipAllow == null) {
      return null;
    }
    return new ClusterIpwhitelistInner().description(ipAllow.description()).ip(ipAllow.ip());
  }

  public static IpRangesResponse from(MetaDto meta) {
    if (meta == null) {
      return null;
    }
    return new IpRangesResponse(meta.getWebModeler());
  }

  public static List<ActivityResponse> fromAuditDtoList(List<AuditDto> json) {
    if (json == null) {
      return null;
    }
    return json.stream().map(ModelMapper::from).toList();
  }

  private static ActivityResponse from(AuditDto auditDto) {
    if (auditDto == null) {
      return null;
    }
    return new ActivityResponse(
        auditDto.getService(),
        auditDto.getOrgId(),
        auditDto.getTimestamp(),
        auditDto.getAudit(),
        from(auditDto.getAuditType()),
        auditDto.getEntity(),
        auditDto.getEntityId(),
        auditDto.getParentEntity(),
        auditDto.getParentEntityId(),
        auditDto.getUserId(),
        auditDto.getEntityAttribute(),
        auditDto.getEntityAttributeValueFrom(),
        auditDto.getEntityAttributeValueTo());
  }

  private static AuditType from(AuditTypeEnum auditType) {
    if (auditType == null) {
      return null;
    }
    return switch (auditType) {
      case C -> AuditType.C;
      case D -> AuditType.D;
      case R -> AuditType.R;
      case U -> AuditType.U;
    };
  }

  public static PostMemberBody from(PostMemberRequest request) {
    if (request == null) {
      return null;
    }
    return new PostMemberBody().orgRoles(fromRoles(request.orgRoles()));
  }

  private static List<AssignableOrganizationRoleType> fromRoles(
      List<PostMemberRequest.Role> roles) {
    if (roles == null) {
      return null;
    }
    return roles.stream().map(ModelMapper::from).toList();
  }

  private static AssignableOrganizationRoleType from(PostMemberRequest.Role role) {
    if (role == null) {
      return null;
    }
    return switch (role) {
      case ADMIN -> AssignableOrganizationRoleType.ADMIN;
      case ANALYST -> AssignableOrganizationRoleType.ANALYST;
      case MODELER -> AssignableOrganizationRoleType.MODELER;
      case VISITOR -> AssignableOrganizationRoleType.VISITOR;
      case TASKUSER -> AssignableOrganizationRoleType.TASKUSER;
      case DEVELOPER -> AssignableOrganizationRoleType.DEVELOPER;
      case OPERATIONSENGINEER -> AssignableOrganizationRoleType.OPERATIONSENGINEER;
    };
  }

  public static List<MemberResponse> fromMemberList(List<Member> members) {
    if (members == null) {
      return null;
    }
    return members.stream().map(ModelMapper::from).toList();
  }

  private static MemberResponse from(Member member) {
    if (member == null) {
      return null;
    }
    return new MemberResponse(
        member.getName(),
        member.getEmail(),
        fromOrgRoles(member.getRoles()),
        member.getInvitePending());
  }

  private static List<MemberResponse.Role> fromOrgRoles(List<OrganizationRole> roles) {
    if (roles == null) {
      return null;
    }
    return roles.stream().map(ModelMapper::from).toList();
  }

  private static MemberResponse.Role from(OrganizationRole organizationRole) {
    if (organizationRole == null) {
      return null;
    }
    return switch (organizationRole) {
      case ADMIN -> MemberResponse.Role.ADMIN;
      case ANALYST -> MemberResponse.Role.ANALYST;
      case MODELER -> MemberResponse.Role.MODELER;
      case VISITOR -> MemberResponse.Role.VISITOR;
      case TASKUSER -> MemberResponse.Role.TASKUSER;
      case DEVELOPER -> MemberResponse.Role.DEVELOPER;
      case OPERATIONSENGINEER -> MemberResponse.Role.OPERATIONSENGINEER;
      case OWNER -> MemberResponse.Role.OWNER;
      case MEMBER -> MemberResponse.Role.MEMBER;
      case SUPPORTAGENT -> MemberResponse.Role.SUPPORTAGENT;
    };
  }

  public static ParametersResponse from(Parameters parameters) {
    if (parameters == null) {
      return null;
    }
    return new ParametersResponse(
        fromChannelList(parameters.getChannels()),
        fromDefaultGenerationList(parameters.getClusterPlanTypes()),
        fromRegionList(parameters.getRegions()));
  }

  private static List<ParametersResponse.Region> fromRegionList(
      List<ParametersRegionsInner> regions) {
    if (regions == null) {
      return null;
    }
    return regions.stream().map(ModelMapper::from).toList();
  }

  private static ParametersResponse.Region from(ParametersRegionsInner parametersRegionsInner) {
    if (parametersRegionsInner == null) {
      return null;
    }
    return new ParametersResponse.Region(
        fromBackupParamList(parametersRegionsInner.getBackups()),
        parametersRegionsInner.getProvider(),
        parametersRegionsInner.getName(),
        parametersRegionsInner.getUuid());
  }

  private static List<Backup> fromBackupParamList(
      List<ParametersRegionsInnerBackupsInner> backups) {
    if (backups == null) {
      return null;
    }
    return backups.stream().map(ModelMapper::from).toList();
  }

  private static Backup from(
      ParametersRegionsInnerBackupsInner parametersRegionsInnerBackupsInner) {
    if (parametersRegionsInnerBackupsInner == null) {
      return null;
    }
    return new Backup(
        fromBackupRegionList(parametersRegionsInnerBackupsInner.getRegions()),
        parametersRegionsInnerBackupsInner.getUuid());
  }

  private static List<Backup.BackupRegion> fromBackupRegionList(
      List<ParametersRegionsInnerBackupsInnerRegionsInner> regions) {
    if (regions == null) {
      return null;
    }
    return regions.stream().map(ModelMapper::from).toList();
  }

  private static Backup.BackupRegion from(
      ParametersRegionsInnerBackupsInnerRegionsInner
          parametersRegionsInnerBackupsInnerRegionsInner) {
    if (parametersRegionsInnerBackupsInnerRegionsInner == null) {
      return null;
    }
    return new Backup.BackupRegion(
        parametersRegionsInnerBackupsInnerRegionsInner.getLabel(),
        parametersRegionsInnerBackupsInnerRegionsInner.getId());
  }

  private static List<ParametersResponse.ClusterPlanType> fromAllowedGenerationList(
      List<ParametersChannelsInnerAllowedGenerationsInner> allowedGenerations) {
    if (allowedGenerations == null) {
      return null;
    }
    return allowedGenerations.stream().map(ModelMapper::from).toList();
  }

  private static List<ParametersResponse.ClusterPlanType> fromDefaultGenerationList(
      List<ParametersChannelsInnerDefaultGeneration> clusterPlanTypes) {
    if (clusterPlanTypes == null) {
      return null;
    }
    return clusterPlanTypes.stream().map(ModelMapper::from).toList();
  }

  private static ParametersResponse.ClusterPlanType from(
      ParametersChannelsInnerDefaultGeneration defaultGeneration) {
    if (defaultGeneration == null) {
      return null;
    }
    return new ParametersResponse.ClusterPlanType(
        defaultGeneration.getName(), defaultGeneration.getUuid());
  }

  private static ParametersResponse.ClusterPlanType from(
      ParametersChannelsInnerAllowedGenerationsInner
          parametersChannelsInnerAllowedGenerationsInner) {
    if (parametersChannelsInnerAllowedGenerationsInner == null) {
      return null;
    }
    return new ParametersResponse.ClusterPlanType(
        parametersChannelsInnerAllowedGenerationsInner.getName(),
        parametersChannelsInnerAllowedGenerationsInner.getUuid());
  }

  private static List<ParametersResponse.Channel> fromChannelList(
      List<ParametersChannelsInner> channels) {
    if (channels == null) {
      return null;
    }
    return channels.stream().map(ModelMapper::from).toList();
  }

  private static ParametersResponse.Channel from(ParametersChannelsInner parametersChannelsInner) {
    if (parametersChannelsInner == null) {
      return null;
    }
    return new ParametersResponse.Channel(
        fromAllowedGenerationList(parametersChannelsInner.getAllowedGenerations()),
        from(parametersChannelsInner.getDefaultGeneration()),
        parametersChannelsInner.getName(),
        parametersChannelsInner.getUuid());
  }

  public static CreateSecretBody from(CreateSecretRequest request) {
    if (request == null) {
      return null;
    }
    return new CreateSecretBody()
        .secretName(request.secretName())
        .secretValue(request.secretValue());
  }

  public static ClusterUpgradeResponse from(
      GenerationUpgradeForClusterDto generationUpgradeForClusterDto) {
    if (generationUpgradeForClusterDto == null) {
      return null;
    }
    return new ClusterUpgradeResponse(
        from(generationUpgradeForClusterDto.getCluster()),
        from(generationUpgradeForClusterDto.getOldGeneration()),
        from(generationUpgradeForClusterDto.getNewGeneration()),
        generationUpgradeForClusterDto.getOrgId());
  }

  private static ClusterUpgradeResponse.Generation from(
      GenerationUpgradeForClusterDtoCluster generation) {
    if (generation == null) {
      return null;
    }
    return new ClusterUpgradeResponse.Generation(generation.getId(), generation.getName());
  }
}
