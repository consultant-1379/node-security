package com.ericsson.nms.security.nscs.handler.command.utility;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse.NscsCommandResponseType;

@RunWith(MockitoJUnitRunner.class)
public class NscsNameMultipleValueResponseBuilderTest {

    public static final String DUMMY_NAME_TITLE = "Name";
    public static final String DUMMY_STATE_TITLE = "State";
    public static final String DUMMY_ERROR_TITLE = "Error";
    private static Map<String, Integer> theDummyRow =
            new HashMap<String, Integer>();
    static {
        theDummyRow.put(DUMMY_NAME_TITLE, 0);
        theDummyRow.put(DUMMY_ERROR_TITLE, 1);
    }
    public static final int DUMMY_ROW_SIZE = theDummyRow.size();

    NscsNameMultipleValueResponseBuilder beanUnderTest;
    String[] aDummyRow = { "state", "error" };

    @Before
    public void setup() {

    }

    @Test
    public void testNscsNameMultipleValueResponseBuilderWithNumOfColumns() {
        for (int numOfColumns = 0; numOfColumns < 1000; numOfColumns++) {
            beanUnderTest =
                    new NscsNameMultipleValueResponseBuilder(numOfColumns);
            assertEquals(numOfColumns, beanUnderTest.getNumberOfColumns());
            assertEquals(NscsCommandResponseType.NAME_MULTIPLE_VALUE,
                    beanUnderTest.getResponse().getResponseType());
            assertEquals("", beanUnderTest.getResponse().getResponseTitle());
            assertEquals(numOfColumns, beanUnderTest.getResponse()
                    .getValueSize());
        }
    }

    @Test
    public void addToZeroColumns() {
        int numOfColumns = 0;
        beanUnderTest = new NscsNameMultipleValueResponseBuilder(numOfColumns);
        String[] header = beanUnderTest.formatHeader(theDummyRow);
        assertEquals(0, header.length);
        beanUnderTest.add(DUMMY_NAME_TITLE, header);
        beanUnderTest.add("Name1", aDummyRow);
        assertEquals("", beanUnderTest.getResponse().getResponseTitle());
        assertEquals(numOfColumns, beanUnderTest.getResponse().getValueSize());
    }

    @Test
    public void addToLessColumns() {
        int numOfColumns = 1;
        beanUnderTest = new NscsNameMultipleValueResponseBuilder(numOfColumns);
        String[] header = beanUnderTest.formatHeader(theDummyRow);
        for (String h : header) {
            System.out.println("[" + h + "]");
        }
        //		assertEquals(0, header.length);
        beanUnderTest.add(DUMMY_NAME_TITLE, header);
        beanUnderTest.add("Name1", aDummyRow);
        assertEquals("", beanUnderTest.getResponse().getResponseTitle());
        assertEquals(numOfColumns, beanUnderTest.getResponse().getValueSize());
    }

    @Test
    public void setAdditionalInformation() {
        int numOfColumns = 1;
        beanUnderTest = new NscsNameMultipleValueResponseBuilder(numOfColumns);
        beanUnderTest.setAdditionalInformation("this is the additional info");
        assertEquals("this is the additional info", beanUnderTest.getResponse().getAdditionalInformation());
        assertEquals(numOfColumns, beanUnderTest.getResponse().getValueSize());
    }
}
