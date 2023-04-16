package org.example;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.Issue;

import static org.junit.jupiter.api.Assertions.*;

class TestExample {
    @Test
    @Issue("HHH-16417")
    void testSum() {
        assertEquals(2, 1 + 1);
    }

    @Test
    @Issue("HHH-10000")
    void testSub() {
        assertEquals(5, 11 - 6);
    }
}