package com.lightbend.aspubsub.inventory.domain;

import com.akkaserverless.javasdk.eventsourcedentity.EventSourcedEntityContext;
import com.google.protobuf.Empty;
import com.lightbend.aspubsub.inventory.InventoryApi;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

/** An event sourced entity. */
public class Inventory extends AbstractInventory {

  @SuppressWarnings("unused")
  private final String entityId;

  protected static InventoryApi.Item domainToApiItem(InventoryDomain.Item domainItem) {
    return InventoryApi.Item.newBuilder()
            .setItemId(domainItem.getItemId())
            .setName(domainItem.getName())
            .setDescription(domainItem.getDescription())
            .setCondition(domainItem.getCondition())
            .setCount(domainItem.getCount())
            .build();
  }

  protected static InventoryApi.Inventory domainToApiInventory(InventoryDomain.Inventory domainInventory) {
    return InventoryApi.Inventory.newBuilder()
            .addAllItems(domainInventory.getItemsList().stream().map(
                    item -> InventoryApi.Item.newBuilder()
                            .setItemId(item.getItemId())
                            .setName(item.getName())
                            .setDescription(item.getDescription())
                            .setCondition(item.getCondition())
                            .setCount(item.getCount())
                            .build())
                    .collect(Collectors.toList()))
            .build();
  }

  protected static String getOrComputeItemId(InventoryDomain.Item item) {
    var itemId = item.getItemId();
    if (!itemId.endsWith(item.getCondition())) {
      itemId = (itemId.isEmpty() ? UUID.randomUUID() : itemId) + "-" + item.getCondition();
    }

    return itemId;
  }

  public Inventory(EventSourcedEntityContext context) {
    this.entityId = context.entityId();
  }

  @Override
  public InventoryDomain.Inventory emptyState() {
    return InventoryDomain.Inventory.getDefaultInstance();
  }

  @Override
  public Effect<Empty> addItem(InventoryDomain.Inventory currentState, InventoryApi.AddItemMsg addItemMsg) {
    if (addItemMsg.getItemId().isEmpty() || addItemMsg.getUserId().isEmpty()) {
      return effects().error("You must supply an item ID and user ID to add an item.");
    }

    var event = InventoryDomain.ItemAdded.newBuilder()
            .setItem(InventoryDomain.Item.newBuilder()
                    .setItemId(addItemMsg.getItemId())
                    .setName(addItemMsg.getName())
                    .setDescription(addItemMsg.getDescription())
                    .setCondition(addItemMsg.getCondition())
                    .setCount(addItemMsg.getCount())
                    .build())
            .build();

    return effects().emitEvent(event)
            .thenReply(newState -> Empty.getDefaultInstance());
  }

  @Override
  public Effect<Empty> removeItem(InventoryDomain.Inventory currentState, InventoryApi.RemoveItemMsg removeItemMsg) {
    if (removeItemMsg.getItemId().isEmpty() || removeItemMsg.getUserId().isEmpty()) {
      return effects().error("You must supply both a user ID and item ID to remove an item from inventory");
    }

    var event = InventoryDomain.ItemRemoved.newBuilder()
            .setItemId(removeItemMsg.getItemId())
            .build();

    return effects().emitEvent(event)
            .thenReply(newState -> Empty.getDefaultInstance());
  }

  @Override
  public Effect<InventoryApi.Inventory> getInventory(InventoryDomain.Inventory currentState, InventoryApi.GetInventoryMsg getInventoryMsg) {
    return effects().reply(domainToApiInventory(currentState));
  }

  @Override
  public InventoryDomain.Inventory itemAdded(InventoryDomain.Inventory currentState, InventoryDomain.ItemAdded itemAdded) {
    InventoryDomain.Item itemToAdd = updateItemIfExists(itemAdded.getItem(), currentState);

    List<InventoryDomain.Item> items = removeItemByItemIdIfExists(currentState, itemToAdd.getItemId());
    items.add(itemToAdd);
    items.sort(Comparator.comparing(InventoryDomain.Item::getName));

    return InventoryDomain.Inventory.newBuilder().addAllItems(items).build();
  }

  private InventoryDomain.Item updateItemIfExists(InventoryDomain.Item item, InventoryDomain.Inventory inventory) {
    var itemId = getOrComputeItemId(item);
    return findItemByItemId(inventory, itemId).map(
            i -> i.toBuilder()
                    .setName(item.getName())
                    .setDescription(item.getDescription())
                    .setCondition(item.getCondition())
                    .setCount(item.getCount())
                    .build()
    ).orElse(item.toBuilder().setItemId(itemId).build());
  }

  private Optional<InventoryDomain.Item> findItemByItemId(InventoryDomain.Inventory inventory, String itemId) {
    return inventory.getItemsList().stream()
            .filter(item -> item.getItemId().equals(itemId))
            .findFirst();
  }

  private List<InventoryDomain.Item> removeItemByItemIdIfExists(InventoryDomain.Inventory inventory, String itemId) {
    return inventory.getItemsList().stream()
            .filter(item -> !item.getItemId().equals(itemId))
            .collect(Collectors.toList());
  }

  @Override
  public InventoryDomain.Inventory itemRemoved(InventoryDomain.Inventory currentState, InventoryDomain.ItemRemoved itemRemoved) {
    List<InventoryDomain.Item> items = removeItemByItemIdIfExists(currentState, itemRemoved.getItemId());
    items.sort(Comparator.comparing(InventoryDomain.Item::getName));

    return InventoryDomain.Inventory.newBuilder().addAllItems(items).build();
  }

}
