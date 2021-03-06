package com.lightbend.aspubsub.users.domain;

import com.akkaserverless.javasdk.eventsourcedentity.EventSourcedEntityContext;
import com.google.protobuf.Empty;
import com.lightbend.aspubsub.users.UsersApi;
import org.apache.commons.codec.digest.DigestUtils;

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
public class Users extends AbstractUsers {

  @SuppressWarnings("unused")
  private final String entityId;

  protected static UsersApi.User domainToApiUser(UsersDomain.User domainUser) {
    return UsersApi.User.newBuilder()
            .setUserId(domainUser.getUserId())
            .setName(domainUser.getName())
            .setEmail(domainUser.getEmail())
            .setStreet(domainUser.getStreet())
            .setCity(domainUser.getCity())
            .setState(domainUser.getState())
            .setCountry(domainUser.getCountry())
            .setZip(domainUser.getZip())
            .build();
  }

  protected static UsersApi.Users domainToApiUsers(UsersDomain.Users domainUsers) {
    return UsersApi.Users.newBuilder()
            .addAllUsers(domainUsers.getUsersList().stream().map(
                    user -> UsersApi.User.newBuilder()
                            .setUserId(user.getUserId())
                            .setEmail(user.getEmail())
                            .setName(user.getName())
                            .setStreet(user.getStreet())
                            .setCity(user.getCity())
                            .setState(user.getState())
                            .setCountry(user.getCountry())
                            .setZip(user.getZip())
                            .build())
                    .collect(Collectors.toList()))
            .build();
  }

  protected static String getOrComputeUserId(UsersDomain.User user) {
    return user.getUserId().isEmpty()
            ? DigestUtils.sha256Hex(user.getEmail())
            : user.getUserId();
  }

  public Users(EventSourcedEntityContext context) {
    this.entityId = context.entityId();
  }

  @Override
  public UsersDomain.Users emptyState() {
    return UsersDomain.Users.getDefaultInstance();
  }

  @Override
  public Effect<Empty> addUser(UsersDomain.Users currentState, UsersApi.AddUserMsg addUserMsg) {
    if (addUserMsg.getEmail().isEmpty()) {
      return effects().error("You must supply an email to add a new user.");
    }

    UsersDomain.UserAdded event = UsersDomain.UserAdded.newBuilder()
            .setUser(UsersDomain.User.newBuilder()
                    .setEmail(addUserMsg.getEmail())
                    .setName(addUserMsg.getName())
                    .setStreet(addUserMsg.getStreet())
                    .setCity(addUserMsg.getCity())
                    .setState(addUserMsg.getState())
                    .setCountry(addUserMsg.getCountry())
                    .setZip(addUserMsg.getZip())
                    .build())
            .build();

    return effects().emitEvent(event)
            .thenReply(newState -> Empty.getDefaultInstance());
  }

  @Override
  public Effect<Empty> deleteUser(UsersDomain.Users currentState, UsersApi.DeleteUserMsg deleteUserMsg) {
    if (deleteUserMsg.getUserId().isEmpty()) {
      return effects().error("You must supply a user ID to delete a user.");
    }

    var event = UsersDomain.UserDeleted.newBuilder()
            .setUserId(deleteUserMsg.getUserId())
            .build();

    return effects().emitEvent(event)
            .thenReply(newState -> Empty.getDefaultInstance());
  }

  @Override
  public Effect<UsersApi.User> getUser(UsersDomain.Users currentState, UsersApi.GetUserMsg getUserMsg) {
    var user = findUserByUserId(currentState, getUserMsg.getUserId());
    if (user.isPresent()) {
      return effects().reply(domainToApiUser(user.get()));
    }
    return effects().error("No user found for given user ID " + getUserMsg.getUserId());
  }

  @Override
  public Effect<UsersApi.Users> getUsers(UsersDomain.Users currentState, UsersApi.GetUsersMsg getUsersMsg) {
    return effects().reply(domainToApiUsers(currentState));
  }

  @Override
  public UsersDomain.Users userAdded(UsersDomain.Users currentState, UsersDomain.UserAdded userAdded) {
    UsersDomain.User userToAdd = updateUserIfExists(userAdded.getUser(), currentState);

    List<UsersDomain.User> users = removeUserByUserIdIfExists(currentState, userToAdd.getUserId());
    users.add(userToAdd);
    users.sort(Comparator.comparing(UsersDomain.User::getName));

    return UsersDomain.Users.newBuilder().addAllUsers(users).build();
  }

  private UsersDomain.User updateUserIfExists(UsersDomain.User user, UsersDomain.Users users) {
    var userId = getOrComputeUserId(user);
    return findUserByUserId(users, userId).map(
            u -> u.toBuilder()
                    .setName(user.getName())
                    .setStreet(user.getStreet())
                    .setCity(user.getCity())
                    .setState(user.getState())
                    .setCountry(user.getCountry())
                    .setZip(user.getZip())
                    .build()
    ).orElse(user.toBuilder().setUserId(userId).build());
  }

  private Optional<UsersDomain.User> findUserByUserId(UsersDomain.Users users, String userId) {
    return users.getUsersList().stream()
            .filter(user -> user.getUserId().equals(userId))
            .findFirst();
  }

  private List<UsersDomain.User> removeUserByUserIdIfExists(UsersDomain.Users users, String userId) {
    return users.getUsersList().stream()
            .filter(user -> !user.getUserId().equals(userId))
            .collect(Collectors.toList());
  }

  @Override
  public UsersDomain.Users userDeleted(UsersDomain.Users currentState, UsersDomain.UserDeleted userDeleted) {
    List<UsersDomain.User> users = removeUserByUserIdIfExists(currentState, userDeleted.getUserId());
    users.sort(Comparator.comparing(UsersDomain.User::getName));

    return UsersDomain.Users.newBuilder().addAllUsers(users).build();
  }

}
