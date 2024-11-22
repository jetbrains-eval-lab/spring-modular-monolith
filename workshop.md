# Spring Modulith Workshop

## Prerequisites
* JDK 21
* Docker and Docker Compose
* Your favourite IDE (Recommended: [IntelliJ IDEA](https://www.jetbrains.com/idea/))

## Project Local Setup

```shell
$ git clone https://github.com/sivaprasadreddy/spring-modular-monolith.git
$ git chechout workshop
$ ./mvnw clean verify
```

## Exercises

1. Make sure the following `spring-modulith` dependencies are added to `pom.xml`.

```xml
<dependency>
    <groupId>org.springframework.modulith</groupId>
    <artifactId>spring-modulith-starter-core</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.modulith</groupId>
    <artifactId>spring-modulith-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.modulith</groupId>
    <artifactId>spring-modulith-events-amqp</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.springframework.modulith</groupId>
    <artifactId>spring-modulith-actuator</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.springframework.modulith</groupId>
    <artifactId>spring-modulith-observability</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.springframework.modulith</groupId>
    <artifactId>spring-modulith-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

2. Create a test that verifies modularity.

```java
package com.sivalabs.bookstore;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {
    static ApplicationModules modules = ApplicationModules.of(BookStoreApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }
}
```

Now you should see the failure and in the console you can see all the violations of modular structure.

Let's fix them.

3. Make `common` module `OPEN` type module.

Add `package-info.java` in `com.sivalabs.bookstore.common` package with the following content:

```java
@ApplicationModule(type = ApplicationModule.Type.OPEN)
package com.sivalabs.bookstore.common;

import org.springframework.modulith.ApplicationModule;
```

4. Expose `com.sivalabs.bookstore.orders.domain.models` and `com.sivalabs.bookstore.orders.domain.events` packages as named-interfaces.

Add `package-info.java` in `com.sivalabs.bookstore.orders.domain.models` package with the following content:

```java
@NamedInterface("order-models")
package com.sivalabs.bookstore.orders.domain.models;

import org.springframework.modulith.NamedInterface;
```

Add `package-info.java` in `com.sivalabs.bookstore.orders.domain.events` package with the following content:

```java
@NamedInterface("order-events")
package com.sivalabs.bookstore.orders.domain.events;

import org.springframework.modulith.NamedInterface;
```

5. Explicitly specify/restrict module dependencies.

Add `package-info.java` in `com.sivalabs.bookstore.orders` package with the following content:

```java
@ApplicationModule(allowedDependencies = {"catalog"})
package com.sivalabs.bookstore.orders;

import org.springframework.modulith.ApplicationModule;
```

6. Try to access other module's internal component.

Autowire `ProductRepository` in `OrderServiceImpl`.

Run `ModularityTests` and the test should fail.

7. Try to create circular-dependency between two modules.

Make `InventoryService` as a `public` class and autowire in `OrderServiceImpl`.

Run `ModularityTests` and the test should fail.

8. Testing modules independently using `@ApplicationModuleTest`

9. Verify event published or not.

In `OrderRestControllerTests`, update `shouldCreateOrderSuccessfully()` test as follows:

```java
@Test
void shouldCreateOrderSuccessfully(AssertablePublishedEvents events) throws Exception {
    mockMvc.perform(
        post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(
            """
             ...
             ...
           """))
        .andExpect(status().isCreated());

    assertThat(events)
            .contains(OrderCreatedEvent.class)
            .matching(e -> e.customer().email(), "siva123@gmail.com")
            .matching(OrderCreatedEvent::productCode, "P100");
}
```

10. Publish event and verify the expected behaviour.

In `InventoryIntegrationTests`, update `handleOrderCreatedEvent()` test as follows:

```java
@Test
void handleOrderCreatedEvent(Scenario scenario) {
    var customer = new Customer("Siva", "siva@gmail.com", "9987654");
    String productCode = "P114";
    var event = new OrderCreatedEvent(UUID.randomUUID().toString(), productCode, 2, customer);
    scenario.publish(event).andWaitForStateChange(() -> inventoryService.getStockLevel(productCode) == 598);
}
```

11. Create C4 Model Documentation

In `ModularityTests.java` add the `createModuleDocumentation()` test.

```java
package com.sivalabs.bookstore;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTests {
    static ApplicationModules modules = ApplicationModules.of(BookStoreApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }

    @Test
    void createModuleDocumentation() {
        new Documenter(modules).writeDocumentation();
    }
}
```
