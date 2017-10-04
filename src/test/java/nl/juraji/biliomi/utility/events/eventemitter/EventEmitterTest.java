package nl.juraji.biliomi.utility.events.eventemitter;

import nl.juraji.biliomi.test.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Juraji on 24-4-2017.
 * Biliomi v3
 */
public class EventEmitterTest {

  private ExecutorService executor;

  // Use the latch as last runnable in the executor, to have jUnit wait for the executor to finish
  private CountDownLatch latch;

  @Before
  public void setUp() throws Exception {
    executor = Executors.newSingleThreadExecutor();
    latch = new CountDownLatch(1);

    // Replace the execitor within EventEmitter with the local one, so latches can be added to the end of the queue
    TestUtils.setStaticFinalField(EventEmitter.class, "EXECUTOR", executor);
  }

  @After
  public void tearDown() throws Exception {
    if (executor != null) {
      executor.shutdownNow();
    }
  }

  @Test
  public void subscribe() throws Exception {
    EventEmitter<TestObject> eventEmitter = new EventEmitter<>();
    TestObject source = new TestObject();
    TestObject target = new TestObject();

    source.setValue(5);

    // Subscribe to emitter without filter
    eventEmitter.subscribe(s -> target.setValue(s.getValue()));

    // Emit source to invoke subscriber
    eventEmitter.emit(source);

    // Submit the latch countdown consumer to the executor
    executor.submit(() -> latch.countDown());

    // Emit source to invoke subscriber
    eventEmitter.emit(source);

    // Await subscriber invocation
    latch.await();

    // target id should match source id
    assertEquals(source.getValue(), target.getValue());
  }

  @Test
  public void subscribeWithFilter() throws Exception {
    EventEmitter<TestObject> eventEmitter = new EventEmitter<>();
    TestObject source = new TestObject();
    TestObject otherSource = new OtherTestObject();
    TestObject target = new TestObject();

    source.setValue(5);
    otherSource.setValue(10);

    // Subscriber to emitter but filter on PointsSettings
    eventEmitter.subscribe(TestObject.class, s -> target.setValue(s.getValue()));

    // Emit some excluded objects
    eventEmitter.emit(otherSource);
    eventEmitter.emit(otherSource);

    // Emit source to invoke subscriber
    eventEmitter.emit(source);

    // Emit more excluded objects
    eventEmitter.emit(otherSource);

    // Submit the latch countdown consumer to the executor
    executor.submit(() -> latch.countDown());

    // Emit more excluded objects
    eventEmitter.emit(otherSource);

    // Await subscriber invocation
    latch.await();

    // target id should match source id
    assertEquals(source.getValue(), target.getValue());
  }

  @Test
  public void unsubscribe() throws Exception {
    EventEmitter<TestObject> eventEmitter = new EventEmitter<>();
    TestObject source = new TestObject();
    TestObject target = new TestObject();

    source.setValue(5);

    // Subscrib to emitter
    String subId = eventEmitter.subscribe(s -> target.setValue(s.getValue()));

    // Unsubscriber the first consumer
    eventEmitter.unsubscribe(subId);

    // Submit the latch countdown consumer to the executor
    executor.submit(() -> latch.countDown());

    // Emit source to invoke subscribers
    eventEmitter.emit(source);

    // Await subscriber invocation
    latch.await();

    // target id should be null, because it unsubscribed before emission
    assertEquals(0, target.getValue());
  }

  @Test
  public void emit() throws Exception {
    // Is tested in other methods, this test is only here for completeness
    assertTrue(true);
  }

  @Test
  public void emitToDeadSubscriber() throws Exception {
    // Emit should unsubscribe consumers that threw an exception
    EventEmitter<Void> eventEmitter = new EventEmitter<>();

    eventEmitter.subscribe(s -> {
      throw new RuntimeException("W00t");
    });

    //noinspection UnusedAssignment
    // Should cause a NPE within the subscriber
    eventEmitter.emit(null);

    // Submit the latch countdown consumer to the executor
    executor.submit(() -> latch.countDown());

    //noinspection UnusedAssignment
    // Should cause a NPE within the subscriber
    eventEmitter.emit(null);

    latch.await();

    assertEquals(0, eventEmitter.subscriptionsSize());
  }

  private class TestObject {
    private int value;

    public int getValue() {
      return value;
    }

    public void setValue(int value) {
      this.value = value;
    }
  }

  private class OtherTestObject extends TestObject {
  }
}