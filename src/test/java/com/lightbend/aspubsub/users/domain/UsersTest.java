package com.lightbend.aspubsub.users.domain;

import com.akkaserverless.javasdk.eventsourcedentity.EventSourcedEntity;
import com.akkaserverless.javasdk.eventsourcedentity.EventSourcedEntityContext;
import com.akkaserverless.javasdk.testkit.EventSourcedResult;
import com.google.protobuf.Empty;
import com.lightbend.aspubsub.users.UsersApi;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class UsersTest {

  static final String TEST_EMAIL = "test@email.net";
  static final String TEST_NAME = "Test User";
  static final String TEST_STREET = "123 Main St";
  static final String TEST_CITY = "Testville";
  static final String TEST_STATE = "TST";
  static final String TEST_COUNTRY = "Testland";
  static final String TEST_ZIP = "98765";

  static final UsersDomain.User TEST_USER = UsersDomain.User.newBuilder()
          .setEmail(TEST_EMAIL)
          .setName(TEST_NAME)
          .setStreet(TEST_STREET)
          .setCity(TEST_CITY)
          .setState(TEST_STATE)
          .setCountry(TEST_COUNTRY)
          .setZip(TEST_ZIP)
          .build();

  static final String TEST_USER_ID = Users.getOrComputeUserId(TEST_USER);

  @Test
  public void addUserMissingEmailTest() {
    UsersTestKit testKit = UsersTestKit.of(Users::new);
    EventSourcedResult<Empty> result = testKit.addUser(
            UsersApi.AddUserMsg.newBuilder()
                    .setName(TEST_NAME)
                    .setStreet(TEST_STREET)
                    .setCity(TEST_CITY)
                    .setState(TEST_STATE)
                    .setCountry(TEST_COUNTRY)
                    .setZip(TEST_ZIP)
                    .build());

    assertThat(result.isError(), is(true));
  }

  @Test
  public void addUserTest() {
    UsersTestKit testKit = UsersTestKit.of(Users::new);

    // use the testkit to execute a command
    EventSourcedResult<Empty> result = testKit.addUser(
            UsersApi.AddUserMsg.newBuilder()
                    .setEmail(TEST_EMAIL)
                    .setName(TEST_NAME)
                    .setStreet(TEST_STREET)
                    .setCity(TEST_CITY)
                    .setState(TEST_STATE)
                    .setCountry(TEST_COUNTRY)
                    .setZip(TEST_ZIP)
                    .build());

    // verify the response
    assertThat(result.getReply(), is(Empty.getDefaultInstance()));

    // verify the emitted events
    var expectedEvent = UsersDomain.UserAdded.newBuilder()
            .setUser(TEST_USER)
            .build();
    var actualEvent = result.getNextEventOfType(UsersDomain.UserAdded.class);
    assertThat(actualEvent, is(expectedEvent));

    // verify the final state after applying the events
    assertThat(testKit.getState(), is(
            UsersDomain.Users.newBuilder()
                    .addUsers(TEST_USER.toBuilder()
                            .setUserId(TEST_USER_ID)
                            .build())
                    .build()));
  }


  @Test
  public void deleteUserMissingUserIdTest() {
    UsersTestKit testKit = UsersTestKit.of(Users::new);
    EventSourcedResult<Empty> result = testKit.deleteUser(
            UsersApi.DeleteUserMsg.newBuilder().build()
    );

    assertThat(result.isError(), is(true));
  }


  @Test
  public void deleteUserTest() {
    UsersTestKit testKit = UsersTestKit.of(Users::new);

    // use the testkit to execute a command
    EventSourcedResult<Empty> result = testKit.deleteUser(
            UsersApi.DeleteUserMsg.newBuilder()
                    .setUserId(TEST_USER_ID)
                    .build());

    // verify the response
    assertThat(result.getReply(), is(Empty.getDefaultInstance()));

    // verify the emitted events
    var expectedEvent = UsersDomain.UserDeleted.newBuilder()
            .setUserId(TEST_USER_ID);
    var actualEvent = result.getNextEventOfType(UsersDomain.UserDeleted.class);
    assertThat(actualEvent, is(expectedEvent));

    // verify the final state after applying the events
    assertThat(testKit.getState(), is(
            UsersDomain.Users.newBuilder().build()
    ));
  }


  @Test
  public void getUserTest() {
    UsersTestKit testKit = UsersTestKit.of(Users::new);
    EventSourcedResult<UsersApi.User> result = testKit.getUser(UsersApi.GetUserMsg.newBuilder().build());

    assertThat(result.isError(), is(false));
    assertThat(result.isNoReply(), is(true));
  }


  @Test
  public void getUsersTest() {
    UsersTestKit testKit = UsersTestKit.of(Users::new);
    EventSourcedResult<UsersApi.Users> result = testKit.getUsers(UsersApi.GetUsersMsg.newBuilder().build());

    assertThat(result.isError(), is(false));
    assertThat(result.isNoReply(), is(true));
  }

}
