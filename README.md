# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2Z0YKAE9VuImgDmMAAwA6AJyZMdqBACu2AMQALADMABwATK4gMP7IdgAWYDoIPoYASih2SKrmckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9TsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6CwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YpmeqBRzBYbjObqYCMhbLCNQbx1Y1BkPy+Rq9BQ0ycTB2vnqH1VL0oepoHwIBAJin3Rt01S1ECq9Fu9kDHnc7T25v3Yy1BQcDia6XaXve-szptDkfB9EKHx6jHAI-xKcN7eDwULpcrw96j2Izf3GFPUvYtF4tRdrBvuEtn6pavIaSpfCCp56u0EDVmgXxXEBLYphg9ThI4jhZhMoGfJCEFntBsHwXWHAeF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhTIcwTrUP6zRtF0vQGOo+RoJhio4fsIK-P8gL7Ah1SUIK-7+iBUxgbh6z6H8OxEcp8K+s67YwAgbHihirHsQSRJgKSz6GFuNI7gyTLjuJXKXo517zsKMBihKboynKBpqXMmAqsGGpujAaAQMwABmvgStAMDSTsMDiiA0AouAHm8l53FUEi0VdggMDJZs3j2ClWkyRu9kFf6zLTGe0BIAAXigHBRjGcaFIhSaVJxaaOAAjJhOaqHm8z1GMRYlvUPjNXqrUdbsVzoCRA4OgNraFS6q7uuudmClt9IwHucgoA+8QnmeF6nQK3n1HeAZ3Udhktnp9QWeKGSqL+mB6YBCnAVhIXTZJyyQfEBE1pC8k8fCybIKmMBoRhoxg+86mQ2M0Ow3B8PEaR3h+P4XgoOgMRxIkFNUxZvhYJxgpAfUDTSBGzERu0EbdD0QmqCJwz4zB6AJopr6PHC9Qi7BgNSxLBltvUJl2Iz5lsYzVlqDZdUnVe-LOWA123VBotoNOnnbZUC6+eK95vfIsryrLYvheqr1m7B0WxTACWLZtBtzgVRWdt2es7azkpLfEK2dd1KCxiJ4tI4NKMoTA6ZjZjE1TQWs3FtAC0x3Ha3Ew9wPK5757vW2+tW2dHAoNwB5nqbMPm5beXW0K9QZDMEA0NXT4fTtX0sZrR5-QDQORyDLwI4myMlGAqHoVm631p4pMBCiK7+Ng4oasxaIwAA4kqGjMw1pYNGf3N83YSrC-h5sp0pCulq7hSz0re1GcgHIF8czmTRMAtQ2sSQRypEHIcMBGTGzbt-Lus5Ho2x8n5B2j5tDO2NK-OW7sNTXQJj7eKiVA4N0eiHfaYcezHTnojV0JcoDtXjtGROvV347SGpnUa41+R5xmnNIuCpmGsLLhtXKqDK7-2eo7YA0CZCwJVmAy+GIUE7hvD5M+TIyylXAR6CuDDZHnyVDyBonQI6S1hP6E+QDL7TwQH+T+MiVJjCfjmAsDRnA+M6IvCWy9Ubo0wh4tQXifHOD8cTbe5F-AcAAOyuEcCgRwMQIzBDgHRAAbPAUchhwFFHTlxP+vFWgdEfs-GOBMQlKgAHI40uFwyo49v41LmPUiSjTf67SKhddE4CMRwDyeAyBut6EwMoUbE2yCpGaKenbCU11Aou3wW7VURDVloFIX7chsz8p-1DiVKxJTSxNWhqXBOSd4z9UCRnLO-Dcz5iEYXUsi1zksNWrWSRRjqFGSWeuQh8C6lKnSgKWhZVYAzGyILewxzdr+gAEIhlGZczhNy04r2GhjbMAinmFhefUPQK4USEh1l8+s4ylGTPOsMpUGJQn3VgVo56y5TFzEMUy4xRVQkAElpBwvHkM-cKARk-icfLGxqd4Wg15dIAsI1wjBECP4qVPDgmY1lfKxVyrolkTJhYZuJlNjUyQAkMABruwQGNQAKQgOKNlhh-DJFAGqQpK8Wbz0aE0ZkAkeihJfl7dAmFsAIGAAaqAcAIAmSgOBZYmr6gKqVSqj+kqZabODaG8Nkbo2xvcUqPlWqk0SvfB6quAArO1aABm2vFKK0lUDKUPSmUgzZGivLoPqJg4eOCgrfzCus6uJCYpkIDns7avyOxHPoTfJh7zxGouTui+ARThrZxxY86a+L5qiLnZ8zeFDu7BwOftf58h+0RSBXMPloLirdghSlTNlBs3JWyOlNAVATRIBItOk59QkUcBRewq5fUQZIRXbw7FYxc54oLtuolMASXWXJd+0eEzD1wIQQM2Vbae62y7QYnt8pZXno9uA7Z-t6w-OPUZWhcKo7-sAz1RdoHuHgfTJB6Dm7YMiPg4hsl+6x1Hp6ftAjZ7AWjM6udKNyUTRmkrOGEDddjH+kDHJsMsEF3XJY7c1evDMw51xVx4RX9yzmhgFWGsAnG3KIVNgLQ-S6UMu0DhucHbJR2cug6mUOo9TBWxqFH9DxU0wBrVWpUjjnGStccBZNrHMVo3XqMATMSyZeDDSas1aX5SIGDLABQ2AQ2EDyPGa+v7Ggcy5jzPmxgmlBffPUDEMVZQQHNKSbpdl6ggG4HgdRiim3nW61AXrRi3PSGbkyQwJpSoBXXCN3uMhxvoj0d2s9c3bZjZbpNu9omFGBcFYNiLxaALKZi1wnTa9sX7qAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
