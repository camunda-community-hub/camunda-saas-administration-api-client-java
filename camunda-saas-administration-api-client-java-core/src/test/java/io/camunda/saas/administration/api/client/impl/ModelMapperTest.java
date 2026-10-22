package io.camunda.saas.administration.api.client.impl;

import static org.assertj.core.api.Assertions.assertThat;

import io.camunda.saas.administration.api.client.api.model.ClusterResponse.Status.ComponentStatus;
import io.camunda.saas.administration.api.client.api.model.MemberResponse;
import io.camunda.saas.administration.api.client.api.model.ParametersResponse;
import io.camunda.saas.administration.api.client.gen.model.Cluster;
import io.camunda.saas.administration.api.client.gen.model.ClusterStatus;
import io.camunda.saas.administration.api.client.gen.model.Member;
import io.camunda.saas.administration.api.client.gen.model.OrganizationRole;
import io.camunda.saas.administration.api.client.gen.model.Parameters;
import io.camunda.saas.administration.api.client.gen.model.ParametersChannelsInner;
import io.camunda.saas.administration.api.client.gen.model.ParametersChannelsInnerAllowedGenerationsInner;
import io.camunda.saas.administration.api.client.gen.model.ParametersChannelsInnerDefaultGeneration;
import io.camunda.saas.administration.api.client.gen.model.Status;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class ModelMapperTest {

  @ParameterizedTest
  @EnumSource(ClusterStatus.class)
  void shouldMapEveryClusterStatus(ClusterStatus status) {
    Cluster cluster = new Cluster().status(new Status().ready(status));

    ComponentStatus mapped = ModelMapper.from(cluster).status().ready();

    assertThat(mapped).isNotNull();
    assertThat(mapped.name()).isEqualTo(status.name());
  }

  @ParameterizedTest
  @EnumSource(OrganizationRole.class)
  void shouldMapEveryOrganizationRole(OrganizationRole role) {
    Member member = new Member().roles(List.of(role));

    List<MemberResponse> mapped = ModelMapper.fromMemberList(List.of(member));

    assertThat(mapped).hasSize(1);
    assertThat(mapped.get(0).roles()).extracting(Enum::name).containsExactly(role.name());
  }

  @Test
  void shouldMapParameters() {
    Parameters parameters =
        new Parameters()
            .channels(
                List.of(
                    new ParametersChannelsInner()
                        .name("Stable")
                        .uuid("channel-uuid")
                        .defaultGeneration(
                            new ParametersChannelsInnerDefaultGeneration()
                                .name("Camunda 8.7")
                                .uuid("default-uuid"))
                        .allowedGenerations(
                            List.of(
                                new ParametersChannelsInnerAllowedGenerationsInner()
                                    .name("Camunda 8.6")
                                    .uuid("allowed-uuid")))))
            .clusterPlanTypes(
                List.of(
                    new ParametersChannelsInnerDefaultGeneration()
                        .name("Production")
                        .uuid("plan-uuid")));

    ParametersResponse mapped = ModelMapper.from(parameters);

    assertThat(mapped.clusterPlanTypes())
        .containsExactly(new ParametersResponse.ClusterPlanType("Production", "plan-uuid"));
    assertThat(mapped.channel()).hasSize(1);
    ParametersResponse.Channel channel = mapped.channel().get(0);
    assertThat(channel.name()).isEqualTo("Stable");
    assertThat(channel.uuid()).isEqualTo("channel-uuid");
    assertThat(channel.defaultGeneration())
        .isEqualTo(new ParametersResponse.ClusterPlanType("Camunda 8.7", "default-uuid"));
    assertThat(channel.allowedGenerations())
        .containsExactly(new ParametersResponse.ClusterPlanType("Camunda 8.6", "allowed-uuid"));
  }

  @Test
  void shouldMapNullParametersToNull() {
    assertThat(ModelMapper.from((Parameters) null)).isNull();
  }
}
