package com.lightbend.aspubsub.inventory.domain;

import com.akkaserverless.javasdk.eventsourcedentity.EventSourcedEntity;
import com.akkaserverless.javasdk.eventsourcedentity.EventSourcedEntity.Effect;
import com.akkaserverless.javasdk.eventsourcedentity.EventSourcedEntityContext;
import com.google.protobuf.Empty;
import com.lightbend.aspubsub.inventory.InventoryApi;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

/** An event sourced entity. */
public class Inventory extends AbstractInventory {

  @SuppressWarnings("unused")
  private final String entityId;

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
    return effects().error("The command handler for `RemoveItem` is not implemented, yet");
  }

  @Override
  public Effect<InventoryApi.Inventory> getInventory(InventoryDomain.Inventory currentState, InventoryApi.GetInventoryMsg getInventoryMsg) {
    return effects().error("The command handler for `GetInventory` is not implemented, yet");
  }

  @Override
  public InventoryDomain.Inventory itemAdded(InventoryDomain.Inventory currentState, InventoryDomain.ItemAdded itemAdded) {
    throw new RuntimeException("The event handler for `ItemAdded` is not implemented, yet");
  }
  @Override
  public InventoryDomain.Inventory itemRemoved(InventoryDomain.Inventory currentState, InventoryDomain.ItemRemoved itemRemoved) {
    throw new RuntimeException("The event handler for `ItemRemoved` is not implemented, yet");
  }

}
