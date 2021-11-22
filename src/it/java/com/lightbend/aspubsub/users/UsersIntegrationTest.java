package com.lightbend.aspubsub.users;

import com.akkaserverless.javasdk.testkit.junit.AkkaServerlessTestKitResource;
import com.google.protobuf.Empty;
import com.lightbend.aspubsub.Main;
import com.lightbend.aspubsub.users.domain.UsersDomain;
import org.junit.ClassRule;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.*;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

// Example of an integration test calling our service via the Akka Serverless proxy
// Run all test classes ending with "IntegrationTest" using `mvn verify -Pit`
public class UsersIntegrationTest {

  /**
   * The test kit starts both the service container and the Akka Serverless proxy.
   */
  @ClassRule
  public static final AkkaServerlessTestKitResource testKit =
    new AkkaServerlessTestKitResource(Main.createAkkaServerless());

  /**
   * Use the generated gRPC client to call the service through the Akka Serverless proxy.
   */
  private final UsersService client;

  public UsersIntegrationTest() {
    client = testKit.getGrpcClient(UsersService.class);
  }

  @Test
  public void addUserOnNonExistingEntity() throws Exception {
    // TODO: set fields in command, and provide assertions to match replies
    // client.addUser(UsersApi.AddUserMsg.newBuilder().build())
    //         .toCompletableFuture().get(5, SECONDS);
  }

  @Test
  public void deleteUserOnNonExistingEntity() throws Exception {
    // TODO: set fields in command, and provide assertions to match replies
    // client.deleteUser(UsersApi.DeleteUserMsg.newBuilder().build())
    //         .toCompletableFuture().get(5, SECONDS);
  }

  @Test
  public void getUserOnNonExistingEntity() throws Exception {
    // TODO: set fields in command, and provide assertions to match replies
    // client.getUser(UsersApi.GetUserMsg.newBuilder().build())
    //         .toCompletableFuture().get(5, SECONDS);
  }

  @Test
  public void getUsersOnNonExistingEntity() throws Exception {
    // TODO: set fields in command, and provide assertions to match replies
    // client.getUsers(Empty.newBuilder().build())
    //         .toCompletableFuture().get(5, SECONDS);
  }
}
