package com.lightbend.aspubsub.inventory.domain;

import com.akkaserverless.javasdk.testkit.EventSourcedResult;
import com.google.protobuf.Empty;
import com.lightbend.aspubsub.inventory.InventoryApi;
import org.junit.Test;

import static com.lightbend.aspubsub.inventory.domain.Inventory.domainToApiItem;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class InventoryTest {

  public static final String TEST_ITEM_ID = "b097ad72-fefd-47a8-bc21-57f99f15637b";
  public static final String TEST_USER_ID = "test user ID";
  public static final String TEST_NAME = "test item name";
  public static final String TEST_DESCRIPTION = "test item description";
  public static final String TEST_CONDITION = "good";
  public static final int TEST_COUNT = 1;

  public static final InventoryDomain.Item TEST_ITEM = InventoryDomain.Item.newBuilder()
          .setItemId(TEST_ITEM_ID)
          .setName(TEST_NAME)
          .setDescription(TEST_DESCRIPTION)
          .setCondition(TEST_CONDITION)
          .setCount(TEST_COUNT)
          .build();

  public static final String TEST_COMPUTED_ITEM_ID = Inventory.getOrComputeItemId(TEST_ITEM);

  @Test
  public void addItemMissingItemIdTest() {
    InventoryTestKit testKit = InventoryTestKit.of(Inventory::new);
    EventSourcedResult<Empty> result = testKit.addItem(
            InventoryApi.AddItemMsg.newBuilder()
                    .setCount(TEST_COUNT)
                    .build());

    assertThat("Should be an error", result.isError());
  }

  @Test
  public void addItemMissingUserTest() {
    InventoryTestKit testKit = InventoryTestKit.of(Inventory::new);
    EventSourcedResult<Empty> result = testKit.addItem(
            InventoryApi.AddItemMsg.newBuilder()
                    .setItemId(TEST_ITEM_ID)
                    .setCount(TEST_COUNT)
                    .build());

    assertThat("Should be an error", result.isError());
  }

  @Test
  public void addItemTest() {
    InventoryTestKit testKit = InventoryTestKit.of(Inventory::new);
    EventSourcedResult<Empty> result = testKit.addItem(
            InventoryApi.AddItemMsg.newBuilder()
                    .setUserId(TEST_USER_ID)
                    .setItemId(TEST_ITEM_ID)
                    .setName(TEST_NAME)
                    .setDescription(TEST_DESCRIPTION)
                    .setCondition(TEST_CONDITION)
                    .setCount(TEST_COUNT)
                    .build());

    // then the response should be empty
    assertThat(result.getReply(), is(Empty.getDefaultInstance()));

    // and an ItemAdded event should be emitted
    var expectedEvent = InventoryDomain.ItemAdded.newBuilder()
            .setItem(TEST_ITEM)
            .build();
    var actualEvent = result.getNextEventOfType(InventoryDomain.ItemAdded.class);
    assertThat(actualEvent, is(expectedEvent));

    // and the item should be added to the state
    assertThat(testKit.getState(), is(
            InventoryDomain.Inventory.newBuilder()
                    .addItems(TEST_ITEM.toBuilder()
                            .setItemId(TEST_COMPUTED_ITEM_ID)
                            .build())
                    .build()));
  }


  @Test
  public void removeItemMissingItemIdTest() {
    InventoryTestKit testKit = InventoryTestKit.of(Inventory::new);
    EventSourcedResult<Empty> result = testKit.removeItem(
            InventoryApi.RemoveItemMsg.newBuilder()
                    .build());

    assertThat("Should be an error", result.isError());
  }


  @Test
  public void removeItemMissingUserIdTest() {
    InventoryTestKit testKit = InventoryTestKit.of(Inventory::new);
    EventSourcedResult<Empty> result = testKit.removeItem(
            InventoryApi.RemoveItemMsg.newBuilder()
                    .setItemId(TEST_ITEM_ID)
                    .build());

    assertThat("Should be an error", result.isError());
  }


  @Test
  public void removeItemTest() {
    // given a test item exists in the current state
    InventoryTestKit testKit = InventoryTestKit.of(Inventory::new);
    testKit.addItem(
            InventoryApi.AddItemMsg.newBuilder()
                    .setItemId(TEST_ITEM_ID)
                    .setUserId(TEST_USER_ID)
                    .setName(TEST_NAME)
                    .setDescription(TEST_DESCRIPTION)
                    .setCondition(TEST_CONDITION)
                    .setCount(TEST_COUNT)
                    .build());

    assertThat(testKit.getState(), is(
            InventoryDomain.Inventory.newBuilder()
                    .addItems(TEST_ITEM.toBuilder()
                            .setItemId(TEST_COMPUTED_ITEM_ID)
                            .build())
                    .build()));

    // when I remove an item
    EventSourcedResult<Empty> result = testKit.removeItem(
            InventoryApi.RemoveItemMsg.newBuilder()
                    .setUserId(TEST_USER_ID)
                    .setItemId(TEST_COMPUTED_ITEM_ID)
                    .build());

    // then the response should be empty
    assertThat(result.getReply(), is(Empty.getDefaultInstance()));

    // and an ItemRemoved event should be emitted
    var expectedEvent = InventoryDomain.ItemRemoved.newBuilder()
            .setItemId(TEST_COMPUTED_ITEM_ID)
            .build();
    var actualEvent = result.getNextEventOfType(InventoryDomain.ItemRemoved.class);
    assertThat(actualEvent, is(expectedEvent));

    // and the test item should no longer be in the state
    assertThat(testKit.getState(), is(InventoryDomain.Inventory.newBuilder().build()));
  }


  @Test
  public void getMissingInventoryTest() {
    // given that no inventory exists for the specified user
    InventoryTestKit testKit = InventoryTestKit.of(Inventory::new);

    // when I fetch that user's inventory
    EventSourcedResult<InventoryApi.Inventory> result = testKit.getInventory(
            InventoryApi.GetInventoryMsg.newBuilder()
                    .setUserId(TEST_USER_ID)
                    .build());

    // Then I should get back an empty Inventory
    var items = result.getReply().getItemsList();
    assertThat(items.isEmpty(), is(true));
  }


  @Test
  public void getInventoryTest() {
    // given a test item exists in the current state
    InventoryTestKit testKit = InventoryTestKit.of(Inventory::new);
    testKit.addItem(
            InventoryApi.AddItemMsg.newBuilder()
                    .setUserId(TEST_USER_ID)
                    .setItemId(TEST_ITEM_ID)
                    .setName(TEST_NAME)
                    .setDescription(TEST_DESCRIPTION)
                    .setCondition(TEST_CONDITION)
                    .setCount(TEST_COUNT)
                    .build());

    // when I fetch a user's inventory
    EventSourcedResult<InventoryApi.Inventory> result = testKit.getInventory(
            InventoryApi.GetInventoryMsg.newBuilder()
                    .setUserId(TEST_USER_ID)
                    .build());

    // then I should get back the user's inventory
    var items = result.getReply().getItemsList();
    assertThat(items, hasItem(domainToApiItem(TEST_ITEM).toBuilder()
            .setItemId(TEST_COMPUTED_ITEM_ID)
            .build()));
  }

}
