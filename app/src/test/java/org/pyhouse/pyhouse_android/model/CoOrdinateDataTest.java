package org.pyhouse.pyhouse_android.model;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by briank on 5/29/17.
 */

public class CoOrdinateDataTest {

    String TEST_STRING_1 = "[1.72,3.4,5.6]";
    String TEST_STRING_2 = "[ 11.2, 13.4, 15.66 ]";

    @Test
    public void name() throws Exception {
    }

    @Test
    public void test_01_CoOrdinateCreationFromNumbers() {
        CoOrdinateData actual = new CoOrdinateData(2.3f, 4.2f, 2.5f);
        float expected = 2.3f;
        System.out.println("Test 01 " + actual.x +  ", " + actual.y + ", " + actual.z);
        assertEquals("Test_01 ", expected, actual.x, 0.001f);
    }

    @Test
    public void test_02_CreateFromArray() {
        CoOrdinateData actual = new CoOrdinateData("[1.2, 3.2, 3.3 ]");
        float expectedX = 1.2f;
        System.out.println("Test 02 " + actual.x + ", " + actual.y + ", " + actual.z);
        assertEquals("Test_02", expectedX, actual.x, 0.001);
    }

    @Test
    public void test_03_CreateFromString() {
        CoOrdinateData actual = new CoOrdinateData(TEST_STRING_1);
        float expectedX = 1.72f;
        System.out.println("Test 03 " + actual.x + ", " + actual.y + ", " + actual.z);
        assertEquals("Test_03", expectedX, actual.x, 0.001);
    }

    @Test
    public void test_04_CreateFromString() {
        CoOrdinateData actual = new CoOrdinateData(TEST_STRING_2);
        String expected = "[11.2,13.4,15.66]";
        System.out.println("test_04_ToString - Actual: " + actual.toString(actual) + "; Expected: " + expected);
        assertEquals("Test_04.", expected, actual.toString(actual));
    }

    @Test
    public void test_05_ToString() {
        CoOrdinateData actual = new CoOrdinateData("[1.2, 3.4, 5.66 ]");
        String expected = "[1.2,3.4,5.66]";
        System.out.println("test_05_ToString - Actual: " + actual.toString(actual) + "; Expected: " + expected);
        //assertEquals("Conversion from celsius to fahrenheit failed", expected, actual.toString(actual));
    }

    @Test
    public void test_06_ToString() {
        CoOrdinateData actual = new CoOrdinateData("[1.2, 3.4, 5.66 ]");
        String expected = "[1.2,3.4,5.66]";
        System.out.println("test_06_ToString - Actual: " + actual.toString(actual) + "; Expected: " + expected);
        //assertEquals("Conversion from celsius to fahrenheit failed", expected, actual.toString(actual));
    }


}
