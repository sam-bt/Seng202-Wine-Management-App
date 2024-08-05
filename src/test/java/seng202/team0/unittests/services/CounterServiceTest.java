package seng202.team0.unittests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team0.services.CounterService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Test Counter implementation
 * @author seng202 teaching team
 */
public class CounterServiceTest
{
    private CounterService testCounterService;
    private CounterService testCounterServiceMock;
    private int incrementCount = 0;

    /**
     * Setup before each test, we create two objects, one an actual
     * instance of our CounterService class, and another a mocked version
     * that has overridden methods.
     */
    @BeforeEach
    public void setupTest() {
        // Use CounterService directly
        testCounterService = new CounterService();

        // Mock CounterService and associated methods
        testCounterServiceMock = spy(new CounterService());                     // Mock CounterService class
        when(testCounterServiceMock.getCurrentCount()).thenCallRealMethod();    // Call real underlying method
        doAnswer(invocationOnMock -> {                                          // Mock "incrementCounter" method
            incrementCount--;   // Decrement instead of increment
            return null;
        }).when(testCounterServiceMock).incrementCounter();
    }

    /**
     * Test incrementing the counter by one, normal JUnit test
     */
    @Test
    public void testIncrement() {
        assertEquals(0, testCounterService.getCurrentCount());
        testCounterService.incrementCounter();
        assertEquals(1, testCounterService.getCurrentCount());
    }

    /**
     * Test incrementing counter, however mock the method call and replace it with
     * something else. This is more applicable for things like database retrieval, reading files,
     * where you might not want to actually call underlying methods involved.
     */
    @Test
    public void testMockIncrement() {
        assertEquals(0, incrementCount);
        testCounterServiceMock.incrementCounter();
        assertEquals(-1, incrementCount);

        assertEquals(0, testCounterServiceMock.getCurrentCount());
    }
}
