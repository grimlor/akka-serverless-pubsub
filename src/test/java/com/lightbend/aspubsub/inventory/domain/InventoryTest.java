package com.lightbend.aspubsub.inventory.domain;

import com.akkaserverless.javasdk.eventsourcedentity.EventSourcedEntity;
import com.akkaserverless.javasdk.eventsourcedentity.EventSourcedEntityContext;
import com.akkaserverless.javasdk.testkit.EventSourcedResult;
import com.google.protobuf.Empty;
import com.lightbend.aspubsub.inventory.InventoryApi;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class InventoryTest {

  static final String TEST_ITEM_ID = "b097ad72-fefd-47a8-bc21-57f99f15637b";
  static final String TEST_NAME = "test item";
  static final String TEST_DESCRIPTION = "test item";
  static final String TEST_CONDITION = "good";
  static final int TEST_COUNT = 1;

  @Test
  public void addItemMissingItemIdTest() {
    InventoryTestKit testKit = InventoryTestKit.of(Inventory::new);
    EventSourcedResult<Empty> result = testKit.addItem(
            InventoryApi.AddItemMsg.newBuilder()
                    .setCount(TEST_COUNT)
                    .build());

    assertThat(result.isError(), is(true));
  }

  @Test
  public void addItemMissingUserTest() {
    InventoryTestKit testKit = InventoryTestKit.of(Inventory::new);
    EventSourcedResult<Empty> result = testKit.addItem(
            InventoryApi.AddItemMsg.newBuilder()
                    .setItemId(TEST_ITEM_ID)
                    .setCount(TEST_COUNT)
                    .build());

    assertThat(result.isError(), is(true));
  }

  @Test
  public void addItemTest() {
    InventoryTestKit testKit = InventoryTestKit.of(Inventory::new);
    EventSourcedResult<Empty> result = testKit.addItem(
      InventoryApi.AddItemMsg.newBuilder()
        .setUserId("test_user")
        .setItemId("test_product")
        .setCount(1)
        .build());

    assertThat(result.isError(), is(false));
    assertThat(result.isNoReply(), is(true));
  }


  @Test
  public void removeItemTest() {
    InventoryTestKit testKit = InventoryTestKit.of(Inventory::new);
     EventSourcedResult<Empty> result = testKit.removeItem(InventoryApi.RemoveItemMsg.newBuilder().build());

    assertThat(result.isError(), is(false));
    assertThat(result.isNoReply(), is(true));
  }


  @Test
  public void getInventoryTest() {
    InventoryTestKit testKit = InventoryTestKit.of(Inventory::new);
     EventSourcedResult<InventoryApi.Inventory> result =
             testKit.getInventory(InventoryApi.GetInventoryMsg.newBuilder().build());

    assertThat(result.isError(), is(false));
    assertThat(result.isNoReply(), is(true));
  }

}
