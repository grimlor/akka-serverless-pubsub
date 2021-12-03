package com.lightbend.aspubsub.inventory.actions;

import com.akkaserverless.javasdk.action.ActionCreationContext;
import com.google.protobuf.Empty;
import com.lightbend.aspubsub.inventory.InventoryApi;
import com.lightbend.aspubsub.users.actions.UsersTopicApi;

import java.util.List;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

/** An action. */
public class UsersTopicSubscriptionAction extends AbstractUsersTopicSubscriptionAction {

  public UsersTopicSubscriptionAction(ActionCreationContext creationContext) {}

  /** Handler for "Delete". */
  @Override
  public Effect<Empty> delete(UsersTopicApi.Deleted deleted) {
    var userIdOpt = actionContext().eventSubject();
    if (userIdOpt.isEmpty()) {
      return effects().error("User ID is missing for this event");
    }

    components().inventory()
            .getInventory(
                    InventoryApi.GetInventoryMsg.newBuilder()
                            .setUserId(deleted.getUserId())
                            .build())
            .execute()
            .thenApply(inventory -> inventory.getItemsList().stream().map(item ->
                    components().inventory().removeItem(
                            InventoryApi.RemoveItemMsg.newBuilder()
                                    .setItemId(item.getItemId())
                                    .setUserId(userIdOpt.get())
                                    .build())));

    return effects().reply(Empty.getDefaultInstance());
  }

}
