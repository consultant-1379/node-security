package com.ericsson.oss.services.nscs.workflow.serializer.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;

@RunWith(MockitoJUnitRunner.class)
public class NscsObjectSerializerTest {

    /*
     * In the scope of FOSS activities, the xerces.xercesImpl library has been removed from node-security EAR. In the NscsObjectSerializer class, the
     * org.apache.xerces.impl.dv.util.Base64 has been replaced with java.util.Base64 class. The following constants contains the serialized results
     * generated with the xerces Base64 and they are used to verify the backward-compatibility of the correspondent serialized results generated with
     * the java Base64.
     */
    private static final String XERCES_PLAIN_TEXT_SERIALIZED = "rO0ABXQADFBMQUlOX1NUUklORw==";
    private static final String XERCES_LIST_SERIALIZED = "rO0ABXNyABpqYXZhLnV0aWwuQXJyYXlzJEFycmF5TGlzdNmkPL7NiAbSAgABWwABYXQAE1tMamF2YS9sYW5nL09iamVjdDt4cHVyABNbTGphdmEubGFuZy5TdHJpbmc7rdJW5+kde0cCAAB4cAAAAAN0AAVJVEVNMXQABUlURU0ydAAFSVRFTTM=";
    private static final String XERCES_BOOLEAN_SERIALIZED = "rO0ABXQABHRydWU=";
    private static final String XERCES_WF_RESULT_SERIALIZED = "rO0ABXNyAFtjb20uZXJpY3Nzb24ub3NzLnNlcnZpY2VzLm5zY3Mud29ya2Zsb3cudGFza3MuYXBpLnJlcXVlc3QuYXR0cmlidXRlLldvcmtmbG93UXVlcnlUYXNrUmVzdWx0w+ZKEpWXsH8CAAJMAAxvdXRwdXRQYXJhbXN0AA9MamF2YS91dGlsL01hcDtMAAZyZXN1bHR0ABJMamF2YS9sYW5nL1N0cmluZzt4cHNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAACdAAFS0VZXzF0AAdWQUxVRV8xdAAFS0VZXzJ0AAdWQUxVRV8yeHQABlJFU1VMVA==";

    @InjectMocks
    NscsObjectSerializer beanUnderTest;

    @Test
    public void readEncodedString() {
        String object = "PLAIN_STRING";
        String serialized = null;
        try {
            serialized = NscsObjectSerializer.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(serialized);
        assertEquals(XERCES_PLAIN_TEXT_SERIALIZED, serialized);
        String deserialized = NscsObjectSerializer.readObject(serialized);
        assertEquals(object, deserialized);
    }

    @Test
    public void readEncodedListString() {
        List<String> objectList = Arrays.asList("ITEM1", "ITEM2", "ITEM3");
        String serialized = null;
        try {
            serialized = NscsObjectSerializer.writeObject(objectList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(serialized);
        assertEquals(XERCES_LIST_SERIALIZED, serialized);
        List<String> deserialized = NscsObjectSerializer.readObject(serialized);
        assertEquals(objectList, deserialized);
    }

    @Test
    public void readEncodedBoolean() {
        String object = Boolean.toString(true);
        String serialized = null;
        try {
            serialized = NscsObjectSerializer.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(serialized);
        assertEquals(XERCES_BOOLEAN_SERIALIZED, serialized);
        String deserialized = NscsObjectSerializer.readObject(serialized);
        assertEquals(object, deserialized);
        assertEquals(Boolean.toString(true), Boolean.toString(Boolean.parseBoolean(deserialized)));
    }

    @Test
    public void readEncodedWorkflowQueryTaskResult() {
        String result = "RESULT";
        Map<String, Serializable> outputParams = new HashMap<String, Serializable>();
        outputParams.put("KEY_1", "VALUE_1");
        outputParams.put("KEY_2", "VALUE_2");
        WorkflowQueryTaskResult object = new WorkflowQueryTaskResult(result, outputParams);
        String serialized = null;
        try {
            serialized = NscsObjectSerializer.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(serialized);
        assertEquals(XERCES_WF_RESULT_SERIALIZED, serialized);
        WorkflowQueryTaskResult deserialized = NscsObjectSerializer.readObject(serialized);
        assertEquals(object.getResult(), deserialized.getResult());
        assertEquals(object.getOutputParams(), deserialized.getOutputParams());
    }

    @Test
    public void readNotEncodedObject() {
        String object = "PLAIN_STRING";
        String deserialized = NscsObjectSerializer.readObject(object);
        assertNull(deserialized);
    }
}