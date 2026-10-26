package io.camunda.saas.administration.api.client.examples;

import io.camunda.saas.administration.api.client.api.CamundaSaasAdministrationApiClient;
import io.camunda.saas.administration.api.client.api.model.MemberResponse;
import io.camunda.saas.administration.api.client.api.model.PostMemberRequest;
import java.util.List;

public class MembersExample {

  void members(CamundaSaasAdministrationApiClient client) {
    // docs:start members
    List<MemberResponse> members = client.members().get();
    client
        .members("jane.doe@example.com")
        .post(new PostMemberRequest(List.of(PostMemberRequest.Role.DEVELOPER)));
    client.members("jane.doe@example.com").delete();
    // docs:end members
  }
}
