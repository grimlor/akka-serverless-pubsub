package com.lightbend.aspubsub.users.actions;

import akka.stream.javadsl.Source;
import com.akkaserverless.javasdk.testkit.ActionResult;
import com.lightbend.aspubsub.users.actions.UsersDeletedTopicApi;
import com.lightbend.aspubsub.users.actions.UsersTopicsAction;
import com.lightbend.aspubsub.users.actions.UsersTopicsActionTestKit;
import com.lightbend.aspubsub.users.domain.UsersDomain;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class UsersTopicsActionTest {

  @Test
  public void deleteUserTest() {
    var expectedUserID = "test user ID";
    // use the testkit to execute a command
    UsersTopicsActionTestKit testKit = UsersTopicsActionTestKit.of(UsersTopicsAction::new);
    ActionResult<UsersDeletedTopicApi.Deleted> result =
            testKit.deleteUser(UsersDomain.UserDeleted.newBuilder().setUserId(expectedUserID).build());

    // verify the response
    var actualResponse = result.getReply();
    assertThat(result.isError(), is(false));
    assertThat(expectedUserID, is(actualResponse.getUserId()));
  }

}
