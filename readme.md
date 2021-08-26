# Spring Application Event 퀵 가이드

- Kafka등의 message broker가 아닌 Spring Framework 자체에서 오래전 부터 Event를 발행/처리하는 기능을 제공하고 있다.
- 이 데모에서는 간단하게 해당 기능을 살펴 본다.

## Dependency

- 본예제에서는 'org.springframework.boot:spring-boot-starter-web'를 사용하지만 실제 관련 내용은 spring-context에 있으므로 꼭 web이 아니라도 관계없다.
    ```groovy
    implementation 'org.springframework.boot:spring-boot-starter-web'
    ```

## Event의 정의

- Spring 4.2 이후에서는 Pojo 기반의 Event를 사용할 수 있다. 이전에는 ApplicationEvent(?)였던가 그것을 상속받아 사용했어야 한다.
    ```java
    @Getter
    @ToString
    public class TestEvent {
        private final String message;
    
        public TestEvent(String message) {
            this.message = message;
        }
    }
    ```

## Event 발행

- ApplicationEventPublisher를 주입받아 publishEvent를 사용하면 됨
    ```java
    @Slf4j
    @RequiredArgsConstructor
    @Component
    public class Publisher {
        private final ApplicationEventPublisher syncPublisher;
    
        public void pub(TestEvent event){
            log.info("Publishing {}", event.toString());
            syncPublisher.publishEvent(event);
        }
    }
    ```

## Event Handling

- 이 또한 많은 개선이 이루어져 현재는 @EventListener Annotation을 이용하여 쉽게 구현이 가능하다.
    ```java
    @Slf4j
    @Component
    public class TestEventHandler {
    
        @Async
        @EventListener
        public void handle(TestEvent event) throws InterruptedException {
            Thread.sleep(1000);
            log.info("handle : {}", event.getMessage());
        }
    
        @EventListener(condition = "#event.message.startsWith('messageType02')")
        public void handleFilter(TestEvent event) throws InterruptedException {
            Thread.sleep(1000);
            log.info("handle only messageType02 : {}", event.getMessage());
        }
    
        @EventListener
        public void handle(Object event) {
            log.info("ANY EVENTS, {}", event.toString());
        }
    }
    ```

## 그럼 그냥 순정(?)으로 사용하면 되나?

- 결론은 땡기는 데로 하면 되는데, ApplicationEventPublisher는 기본적으로 Sync 방식으로 동작한다. 쉽게말해 TestEvent를 처리하는 Handler가 3개가 있고 각 처리시간이 1초라면 3초동안 publisher가 block되어 처리하고 반환한다. ?? 머? 라고 생각 할 수 있지만, async 하게 설정할 수 있는 방법은 제공한다.

## Async Publisher 만들기 - 01

- 고대로 부터 사용되던 방식 : 발행되는 모든 Event가 별도의 thread에서 async 하게 수행된다.
- 장점 : 한방
- 단점 : 모든 event가 async하게 수행된다.(?)
    ```java
    @Slf4j
    @RequiredArgsConstructor
    @Component
    public class Publisher {
        private final ApplicationEventPublisher syncPublisher;
    
        @Bean(name = "applicationEventMulticaster")
        public ApplicationEventMulticaster asyncEventPublisher() {
            var asyncPublisher = new SimpleApplicationEventMulticaster();
            asyncPublisher.setTaskExecutor(new SimpleAsyncTaskExecutor());
            return asyncPublisher;
        }
    
        public void pub(TestEvent event){
            log.info("Publishing {}", event.toString());
            syncPublisher.publishEvent(event);
        }
    }
    ```

## Async Publisher 만들기 - 02

- 근대에 사용되는 방식(요새는 이게 대세던데...)
- 장점 : 필요한 EventListener 마다 async하게 동작시킬 수 있다. EventListener별로 ordering 할수 있다.(하나의 event를 처리하는 다수의 handler를 구성할 수 있으니)
- 단점 : 귀찮다.(한땀한땀 @Async 사용해야한다.)
    ```java
    @Async
    @EventListener
    public void handle(TestEvent event) throws InterruptedException {
        Thread.sleep(1000);
        log.info("handle : {}", event.getMessage());
    }
    ```

## Async Publisher 만들기 - 절충안

- MSA 할 것이면, 그냥 kafka 같은 message broker 써라.
- Monolithic 이면 가급적 01 번, 비즈니스가 특이해서 event 처리순서를 보장해야한다면, 1) 기획자랑 싸워본다, 2) 안되면 02번 고려... 에효...