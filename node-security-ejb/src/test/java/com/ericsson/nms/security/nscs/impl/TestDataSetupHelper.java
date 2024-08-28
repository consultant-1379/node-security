package com.ericsson.nms.security.nscs.impl;

import java.util.*;

import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

public class TestDataSetupHelper {
    public static CmResponse createSampleData() {

        CmObject cmObject1;
        CmObject cmObject2;
        CmObject cmObject3;
        CmResponse cmResponse;
        List<CmObject> cmObjectList;
        final Map<String, Object> attributesMap1 = new HashMap<>();
        final Map<String, Object> attributesMap2 = new HashMap<>();
        final Map<String, Object> attributesMap3 = new HashMap<>();

        final String ATTRIBUTE_KEY = "OperationalSecurityLevel";
        final String ATTRIBUTE_VALUE_1 = "SecurityLevel 1";
        final String ATTRIBUTE_VALUE_2 = "SecurityLevel 2";
        final String ATTRIBUTE_VALUE_3 = "SecurityLevel 3";

        final String FDN1 = "fdn123";
        final String FDN2 = "fdn123456";
        final String FDN3 = "fdn123456789";

        final int STATUS_CODE = 1;
        final String STATUS_MESSAGE = "success";

        attributesMap1.put(ATTRIBUTE_KEY, ATTRIBUTE_VALUE_1);
        attributesMap2.put(ATTRIBUTE_KEY, ATTRIBUTE_VALUE_2);
        attributesMap3.put(ATTRIBUTE_KEY, ATTRIBUTE_VALUE_3);

        cmObject1 = createCmObject(attributesMap1, FDN1);

        cmObject2 = createCmObject(attributesMap2, FDN2);

        cmObject3 = createCmObject(attributesMap3, FDN3);

        cmResponse = new CmResponse();
        cmObjectList = new ArrayList<>();
        cmObjectList.add(cmObject1);
        cmObjectList.add(cmObject2);
        cmObjectList.add(cmObject3);

        cmResponse.setStatusCode(STATUS_CODE);
        cmResponse.setStatusMessage(STATUS_MESSAGE);
        cmResponse.setTargetedCmObjects(cmObjectList);

        return cmResponse;
    }

    private static CmObject createCmObject(final Map<String, Object> attributesMap1, final String FDN1) {
        CmObject cmObject1;
        cmObject1 = new CmObject();
        cmObject1.setFdn(FDN1);
        cmObject1.setAttributes(attributesMap1);
        return cmObject1;
    }

}
