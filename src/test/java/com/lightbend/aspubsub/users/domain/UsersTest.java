package com.lightbend.aspubsub.users.domain;

import com.akkaserverless.javasdk.testkit.EventSourcedResult;
import com.google.protobuf.Empty;
import com.lightbend.aspubsub.users.UsersApi;
import org.junit.jupiter.api.Test;

import static com.lightbend.aspubsub.users.domain.Users.domainToApiUser;
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

    assertThat("Should be an error", result.isError());
  }


  @Test
  public void addUserTest() {
    // given a user service
    UsersTestKit testKit = UsersTestKit.of(Users::new);

    // when I add a user
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

    // then the response should be empty
    assertThat(result.getReply(), is(Empty.getDefaultInstance()));

    // and a UserAdded event should be emitted
    var expectedEvent = UsersDomain.UserAdded.newBuilder()
            .setUser(TEST_USER)
            .build();
    var actualEvent = result.getNextEventOfType(UsersDomain.UserAdded.class);
    assertThat(actualEvent, is(expectedEvent));

    // and the user should be added to the state
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

    assertThat("Should be an error", result.isError());
  }


  @Test
  public void deleteUserTest() {
    // given a test user exists in the current state
    UsersTestKit testKit = UsersTestKit.of(Users::new);
    testKit.addUser(
            UsersApi.AddUserMsg.newBuilder()
                    .setEmail(TEST_EMAIL)
                    .setName(TEST_NAME)
                    .setStreet(TEST_STREET)
                    .setCity(TEST_CITY)
                    .setState(TEST_STATE)
                    .setCountry(TEST_COUNTRY)
                    .setZip(TEST_ZIP)
                    .build());

    assertThat(testKit.getState(), is(
            UsersDomain.Users.newBuilder()
                    .addUsers(TEST_USER.toBuilder()
                            .setUserId(TEST_USER_ID)
                            .build())
                    .build()));

    // when I delete a user
    EventSourcedResult<Empty> result = testKit.deleteUser(
            UsersApi.DeleteUserMsg.newBuilder()
                    .setUserId(TEST_USER_ID)
                    .build());

    // then the response should be empty
    assertThat(result.getReply(), is(Empty.getDefaultInstance()));

    // and a UserDeleted event should be emitted
    var expectedEvent = UsersDomain.UserDeleted.newBuilder()
            .setUserId(TEST_USER_ID)
            .build();
    var actualEvent = result.getNextEventOfType(UsersDomain.UserDeleted.class);
    assertThat(actualEvent, is(expectedEvent));

    // and the test user should no longer be in the state
    assertThat(testKit.getState(), is(UsersDomain.Users.newBuilder().build()));
  }


  @Test
  public void getMissingUserTest() {
    // given an empty state
    UsersTestKit testKit = UsersTestKit.of(Users::new);

    // when I fetch a user with a user ID not found in the state
    EventSourcedResult<UsersApi.User> result = testKit.getUser(
            UsersApi.GetUserMsg.newBuilder()
                    .setUserId(TEST_USER_ID)
                    .build());

    // then I should get back an error
    assertThat(result.isError(), is(true));
    assertThat(result.getError(), containsString(TEST_USER_ID));
  }


  @Test
  public void getUserTest() {
    // given a test user exists in the current state
    UsersTestKit testKit = UsersTestKit.of(Users::new);
    testKit.addUser(
            UsersApi.AddUserMsg.newBuilder()
                    .setEmail(TEST_EMAIL)
                    .setName(TEST_NAME)
                    .setStreet(TEST_STREET)
                    .setCity(TEST_CITY)
                    .setState(TEST_STATE)
                    .setCountry(TEST_COUNTRY)
                    .setZip(TEST_ZIP)
                    .build());

    // when I fetch the user with its user ID
    EventSourcedResult<UsersApi.User> result = testKit.getUser(
            UsersApi.GetUserMsg.newBuilder()
                    .setUserId(TEST_USER_ID)
                    .build());

    // then I should get back the user from state
    assertThat(result.getReply(), is(
            UsersApi.User.newBuilder()
                    .setUserId(TEST_USER_ID)
                    .setEmail(TEST_EMAIL)
                    .setName(TEST_NAME)
                    .setStreet(TEST_STREET)
                    .setCity(TEST_CITY)
                    .setState(TEST_STATE)
                    .setCountry(TEST_COUNTRY)
                    .setZip(TEST_ZIP)
                    .build()));
  }


  @Test
  public void getUsersTest() {
    // given a test user exists in the current state
    UsersTestKit testKit = UsersTestKit.of(Users::new);
    testKit.addUser(
            UsersApi.AddUserMsg.newBuilder()
                    .setEmail(TEST_EMAIL)
                    .setName(TEST_NAME)
                    .setStreet(TEST_STREET)
                    .setCity(TEST_CITY)
                    .setState(TEST_STATE)
                    .setCountry(TEST_COUNTRY)
                    .setZip(TEST_ZIP)
                    .build());

    // when I fetch all users
    EventSourcedResult<UsersApi.Users> result = testKit.getUsers(
            UsersApi.GetUsersMsg.newBuilder()
                    .build());

    // then I should get back a list of users containing the test user
    var users = result.getReply().getUsersList();
    assertThat(users, hasItem(domainToApiUser(TEST_USER).toBuilder()
            .setUserId(TEST_USER_ID)
            .build()));
  }

}
