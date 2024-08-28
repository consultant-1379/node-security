package com.ericsson.nms.security.nscs.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.ericsson.nms.security.nscs.cpp.model.CppMOEnrollmentMode;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;

public class NSCSCppNodeUtilityTest {

	@Test
	public void testUpdateMoParamsUsingNodeModelValuesForNullEnrollmentData() {
		MoParams enrollmentDataParams = null;
		MoParams updatedMoParams = NSCSCppNodeUtility.updateMoParamsUsingNodeModelValues(enrollmentDataParams);
		assertNull(updatedMoParams);
	}

	@Test
	public void testUpdateMoParamsUsingNodeModelValuesForUnknownEnrollmentMode() {
		MoParams enrollmentDataParams = new MoParams();
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE, "UNKNOWN");
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH, "0");
		MoParams updatedMoParams = NSCSCppNodeUtility.updateMoParamsUsingNodeModelValues(enrollmentDataParams);
		assertNull(updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE).getParam());
		assertEquals(NSCSCppNodeUtility.CPP_KEY_LENGTH_1024, updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH).getParam());
	}

	@Test
	public void testUpdateMoParamsUsingNodeModelValuesForUnknownKeyLength() {
		MoParams enrollmentDataParams = new MoParams();
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE, "SCEP");
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH, "UNKNOWN");
		MoParams updatedMoParams = NSCSCppNodeUtility.updateMoParamsUsingNodeModelValues(enrollmentDataParams);
		assertEquals(CppMOEnrollmentMode.SCEP.name(), updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE).getParam());
		assertNull(updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH).getParam());
	}

	@Test
	public void testUpdateMoParamsUsingNodeModelValuesForScepRsa1024() {
		MoParams enrollmentDataParams = new MoParams();
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE, "SCEP");
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH, "0");
		MoParams updatedMoParams = NSCSCppNodeUtility.updateMoParamsUsingNodeModelValues(enrollmentDataParams);
		assertEquals(CppMOEnrollmentMode.SCEP.name(),updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE).getParam());
		assertEquals(NSCSCppNodeUtility.CPP_KEY_LENGTH_1024, updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH).getParam());
	}

	@Test
	public void testUpdateMoParamsUsingNodeModelValuesForScepRsa2048() {
		MoParams enrollmentDataParams = new MoParams();
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE, "SCEP");
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH, "1");
		MoParams updatedMoParams = NSCSCppNodeUtility.updateMoParamsUsingNodeModelValues(enrollmentDataParams);
		assertEquals(CppMOEnrollmentMode.SCEP.name(), updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE).getParam());
		assertEquals(NSCSCppNodeUtility.CPP_KEY_LENGTH_2048, updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH).getParam());
	}

	@Test
	public void testUpdateMoParamsUsingNodeModelValuesForCmpV2InitialRsa1024() {
		MoParams enrollmentDataParams = new MoParams();
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE, "CMPv2_INITIAL");
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH, "0");
		MoParams updatedMoParams = NSCSCppNodeUtility.updateMoParamsUsingNodeModelValues(enrollmentDataParams);
		assertEquals(CppMOEnrollmentMode.CMPV2_INITIAL.name(),updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE).getParam());
		assertEquals(NSCSCppNodeUtility.CPP_KEY_LENGTH_1024, updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH).getParam());
	}

	@Test
	public void testUpdateMoParamsUsingNodeModelValuesForCmpV2InitialRsa2048() {
		MoParams enrollmentDataParams = new MoParams();
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE, "CMPv2_INITIAL");
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH, "1");
		MoParams updatedMoParams = NSCSCppNodeUtility.updateMoParamsUsingNodeModelValues(enrollmentDataParams);
		assertEquals(CppMOEnrollmentMode.CMPV2_INITIAL.name(), updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE).getParam());
		assertEquals(NSCSCppNodeUtility.CPP_KEY_LENGTH_2048, updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH).getParam());
	}

	public void testUpdateMoParamsUsingNodeModelValuesForCmpV2VCRsa1024() {
		MoParams enrollmentDataParams = new MoParams();
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE, "CMPv2_VC");
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH, "0");
		MoParams updatedMoParams = NSCSCppNodeUtility.updateMoParamsUsingNodeModelValues(enrollmentDataParams);
		assertEquals(CppMOEnrollmentMode.CMPV2_VC.name(),updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE).getParam());
		assertEquals(NSCSCppNodeUtility.CPP_KEY_LENGTH_1024, updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH).getParam());
	}

	@Test
	public void testUpdateMoParamsUsingNodeModelValuesForCmpV2VCRsa2048() {
		MoParams enrollmentDataParams = new MoParams();
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE, "CMPv2_VC");
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH, "1");
		MoParams updatedMoParams = NSCSCppNodeUtility.updateMoParamsUsingNodeModelValues(enrollmentDataParams);
		assertEquals(CppMOEnrollmentMode.CMPV2_VC.name(), updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE).getParam());
		assertEquals(NSCSCppNodeUtility.CPP_KEY_LENGTH_2048, updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH).getParam());
	}

	public void testUpdateMoParamsUsingNodeModelValuesForCmpV2UpdateRsa1024() {
		MoParams enrollmentDataParams = new MoParams();
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE, "CMPv2_UPDATE");
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH, "0");
		MoParams updatedMoParams = NSCSCppNodeUtility.updateMoParamsUsingNodeModelValues(enrollmentDataParams);
		assertEquals(CppMOEnrollmentMode.CMPV2_UPDATE.name(),updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE).getParam());
		assertEquals(NSCSCppNodeUtility.CPP_KEY_LENGTH_1024, updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH).getParam());
	}

	@Test
	public void testUpdateMoParamsUsingNodeModelValuesForCmpV2UpdateRsa2048() {
		MoParams enrollmentDataParams = new MoParams();
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE, "CMPv2_UPDATE");
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH, "1");
		MoParams updatedMoParams = NSCSCppNodeUtility.updateMoParamsUsingNodeModelValues(enrollmentDataParams);
		assertEquals(CppMOEnrollmentMode.CMPV2_UPDATE.name(), updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE).getParam());
		assertEquals(NSCSCppNodeUtility.CPP_KEY_LENGTH_2048, updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH).getParam());
	}

	public void testUpdateMoParamsUsingNodeModelValuesForManualRsa1024() {
		MoParams enrollmentDataParams = new MoParams();
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE, "MANUAL");
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH, "0");
		MoParams updatedMoParams = NSCSCppNodeUtility.updateMoParamsUsingNodeModelValues(enrollmentDataParams);
		assertEquals(CppMOEnrollmentMode.MANUAL.name(),updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE).getParam());
		assertEquals(NSCSCppNodeUtility.CPP_KEY_LENGTH_1024, updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH).getParam());
	}

	@Test
	public void testUpdateMoParamsUsingNodeModelValuesForManualRsa2048() {
		MoParams enrollmentDataParams = new MoParams();
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE, "MANUAL");
		enrollmentDataParams.addParam(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH, "1");
		MoParams updatedMoParams = NSCSCppNodeUtility.updateMoParamsUsingNodeModelValues(enrollmentDataParams);
		assertEquals(CppMOEnrollmentMode.MANUAL.name(), updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_ENROLLMENTMODE).getParam());
		assertEquals(NSCSCppNodeUtility.CPP_KEY_LENGTH_2048, updatedMoParams.getParamMap().get(NSCSCppNodeUtility.MOPARAMS_KEY_KEYLENGTH).getParam());
	}
}
