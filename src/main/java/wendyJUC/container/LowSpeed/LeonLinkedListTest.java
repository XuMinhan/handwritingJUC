package wendyJUC.container.LowSpeed;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.NoSuchElementException;

public class LeonLinkedListTest {
    private LeonLinkedList<Integer> list;

    @Before
    public void setUp() {
        list = new LeonLinkedList<>();
    }

    @Test
    public void testAddAndRemoveFirst() {
        list.addLast(1);
        list.addLast(2);
        assertEquals("Size should be 2 after adding elements.", 2, list.size());
        assertEquals("RemoveFirst should return the first element added.", Integer.valueOf(1), list.removeFirst());
        assertEquals("Size should be 1 after removing one element.", 1, list.size());
    }

    @Test(expected = NoSuchElementException.class)
    public void testRemoveFirstFromEmpty() {
        list.removeFirst();
    }

    @Test
    public void testIsEmpty() {
        assertTrue("List should be empty initially.", list.isEmpty());
        list.addLast(1);
        assertFalse("List should not be empty after adding an element.", list.isEmpty());
    }

    @Test
    public void testContains() {
        assertFalse("List should not contain element 1 initially.", list.contains(1));
        list.addLast(1);
        assertTrue("List should contain element 1 after it's added.", list.contains(1));
    }

    @Test
    public void testRemove() {
        list.addLast(1);
        list.addLast(2);
        assertTrue("List should successfully remove existing element.", list.remove(Integer.valueOf(1)));
        assertFalse("List should not contain element 1 after removal.", list.contains(1));
        assertEquals("Size should be 1 after removal.", 1, list.size());
        assertFalse("List should return false when removing non-existing element.", list.remove(Integer.valueOf(3)));
    }

    @Test
    public void testGet() {
        list.addLast(1);
        list.addLast(2);
        assertEquals("Get should return the correct element.", Integer.valueOf(1), list.get(0));
        assertEquals("Get should return the correct element.", Integer.valueOf(2), list.get(1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetOutOfBounds() {
        list.addLast(1);
        list.get(1); // This should throw an IndexOutOfBoundsException
    }

    @Test
    public void testClear() {
        list.addLast(1);
        list.addLast(2);
        list.clear();
        assertTrue("List should be empty after clear.", list.isEmpty());
    }

    // You may add more tests to cover edge cases and other functionalities.
}
