package com.lightbend.aspubsub.users.actions;

import com.akkaserverless.javasdk.action.ActionCreationContext;
import com.lightbend.aspubsub.users.domain.UsersDomain;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

/** An action. */
public class UsersTopicAction extends AbstractUsersTopicAction {

  public UsersTopicAction(ActionCreationContext creationContext) {}

  /** Handler for "DeleteUser". */
  @Override
  public Effect<UsersTopicApi.Deleted> deleteUser(UsersDomain.UserDeleted userDeleted) {
    var deleted = UsersTopicApi.Deleted.newBuilder()
            .setUserId(userDeleted.getUserId())
            .build();
    return effects().reply(deleted);
  }
}
