package com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;

@RunWith(MockitoJUnitRunner.class)
public class WorkflowQueryTaskResultTest {

	private static final String WF_RESULT = "SUPPORTED";
	private static final String PARAM_1_KEY = "PARAM_1_KEY";
	private static final String PARAM_1_VALUE = "PARAM_1_VALUE";
	private static final String PARAM_2_KEY = "PARAM_2_KEY";
	private static final String PARAM_2_VALUE = "PARAM_2_VALUE";
	private static Map<String, Serializable> theParams = new HashMap<String, Serializable>();
	static {
		theParams.put(PARAM_1_KEY, PARAM_1_VALUE);
		theParams.put(PARAM_2_KEY, PARAM_2_VALUE);
	}

	@InjectMocks
	NscsObjectSerializer objectSerializer;

	@Test
	public void testReadWriteObject() {
		WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(
				WF_RESULT, theParams);
		String encoded = null;
		try {
			encoded = objectSerializer.writeObject(wfQueryTaskResult);
		} catch (IOException e) {
		}
		assertNotNull(encoded);
		WorkflowQueryTaskResult decoded = objectSerializer.readObject(encoded);
		assertNotNull(decoded);
		assertEquals(wfQueryTaskResult.getResult(), decoded.getResult());
		assertEquals(wfQueryTaskResult.getOutputParams(), decoded.getOutputParams());
	}

	@Test
	public void testReadString() {
		String notEncoded = WF_RESULT;
		WorkflowQueryTaskResult decoded = objectSerializer.readObject(notEncoded);
		assertNull(decoded);
	}
}
