package edu.wvup.cs460.util;


import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "I code not because I have a problem to solve, but because there is
 * code within me, crying to get out."
 */
public class TupleTest {

    @Test
    public void testTupleBase() {
        Tuple<String, Integer> tuple = new Tuple<String, Integer>("foo", 42);
        assertEquals(42, (int) tuple.getValue());
        assertEquals("foo", tuple.getKey());
    }

    @Test
    public void testTupleMutate() {
        Tuple<String, Integer> tuple = new Tuple<String, Integer>("foo", 42);
        assertEquals(42, (int) tuple.getValue());

        tuple.setValue(47);

        assertEquals(47, (int) tuple.getValue());
    }

    @Test
    public void testTupleEqualsAndHashcode() {
        Tuple<String, Integer> tuple = new Tuple<String, Integer>("foo", 42);

        tuple.setValue(47);

        Tuple<String, Integer> tuple2 = new Tuple<String, Integer>("foo", 47);

        assertTrue(tuple2.equals(tuple));

        assertTrue(tuple2.equals(tuple2));

        assertEquals(tuple.hashCode(), tuple2.hashCode());


        assertFalse(tuple2.equals(null));

        assertFalse(tuple2.equals("foobar"));

        tuple2.setValue(42);

        assertFalse(tuple2.equals(tuple));

        Tuple<String, Integer> tuple3 = new Tuple<String, Integer>("bar", 47);
        assertFalse(tuple3.equals(tuple));



    }

}
