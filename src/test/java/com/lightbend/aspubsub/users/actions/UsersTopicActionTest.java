package com.lightbend.aspubsub.users.actions;

import com.akkaserverless.javasdk.testkit.ActionResult;
import com.lightbend.aspubsub.users.domain.UsersDomain;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class UsersTopicActionTest {

  @Test
  public void deleteUserTest() {
    // when the service emits a UserDeleted event
    UsersTopicActionTestKit testKit = UsersTopicActionTestKit.of(UsersTopicAction::new);
    var expectedUserID = "test user ID";
    ActionResult<UsersTopicApi.Deleted> result = testKit.deleteUser(
            UsersDomain.UserDeleted.newBuilder()
                    .setUserId(expectedUserID)
                    .build());

    // then a Deleted message should be published to the topic (returned to the testKit)
    var actualResponse = result.getReply();
    assertThat(actualResponse, instanceOf(UsersTopicApi.Deleted.class));
    assertThat(actualResponse.getUserId(), is(expectedUserID));
  }

}
