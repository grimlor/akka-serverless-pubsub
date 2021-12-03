package com.lightbend.aspubsub.inventory.actions;

import com.akkaserverless.javasdk.testkit.ActionResult;
import com.google.protobuf.Empty;
import com.lightbend.aspubsub.inventory.InventoryApi;
import com.lightbend.aspubsub.inventory.domain.Inventory;
import com.lightbend.aspubsub.inventory.domain.InventoryDomain;
import com.lightbend.aspubsub.inventory.domain.InventoryTestKit;
import com.lightbend.aspubsub.users.actions.UsersTopicApi;
import org.junit.Test;

import static com.lightbend.aspubsub.inventory.domain.InventoryTest.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class UsersTopicSubscriptionActionTest {

  @Test
  public void deleteTest() {
    // given that a User has Inventory
    InventoryTestKit serviceTestKit = InventoryTestKit.of(Inventory::new);
    serviceTestKit.addItem(
            InventoryApi.AddItemMsg.newBuilder()
                    .setItemId(TEST_ITEM_ID)
                    .setUserId(TEST_USER_ID)
                    .setName(TEST_NAME)
                    .setDescription(TEST_DESCRIPTION)
                    .setCondition(TEST_CONDITION)
                    .setCount(TEST_COUNT)
                    .build());
    assertThat(serviceTestKit.getState(), is(
            InventoryDomain.Inventory.newBuilder()
                    .addItems(TEST_ITEM.toBuilder()
                            .setItemId(TEST_COMPUTED_ITEM_ID)
                            .build())
                    .build()));

    // when the service receives a Deleted message (from the Users topic)
    UsersTopicSubscriptionActionTestKit subTestKit = UsersTopicSubscriptionActionTestKit.of(UsersTopicSubscriptionAction::new);
    ActionResult<Empty> actionResult = subTestKit.delete(
            UsersTopicApi.Deleted.newBuilder()
                    .setUserId(TEST_USER_ID)
                    .build());

    // then the response should be empty
    assertThat(actionResult.getReply(), is(Empty.getDefaultInstance()));

    // and the user's inventory should be deleted
    assertThat(serviceTestKit.getState().getItemsList().isEmpty(), is(true));
  }

}
