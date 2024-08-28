package com.ericsson.nms.security.nscs.capabilitymodel.service;

import static com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand.NORMAL_USER_NAME_PROPERTY;
import static com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand.NORMAL_USER_PASSWORD_PROPERTY;
import static com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand.NWIEA_SECURE_PASSWORD_PROPERTY;
import static com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand.NWIEA_SECURE_USER_NAME_PROPERTY;
import static com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand.NWIEB_SECURE_PASSWORD_PROPERTY;
import static com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand.NWIEB_SECURE_USER_NAME_PROPERTY;
import static com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand.ROOT_USER_NAME_PROPERTY;
import static com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand.ROOT_USER_PASSWORD_PROPERTY;
import static com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand.SECURE_USER_NAME_PROPERTY;
import static com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand.SECURE_USER_PASSWORD_PROPERTY;
import static com.ericsson.nms.security.nscs.ldap.utility.LdapConstants.COMECIM_DEFAULT_OTP_VALIDITY_PERIOD_IN_MINUTES;
import static com.ericsson.nms.security.nscs.ldap.utility.LdapConstants.COMECIM_LDAP_MO;
import static com.ericsson.nms.security.nscs.ldap.utility.LdapConstants.VDU_DEFAULT_ENROLL_CA_TRUST_CATEGORY_ID;
import static com.ericsson.nms.security.nscs.ldap.utility.LdapConstants.VDU_DEFAULT_OTP_VALIDITY_PERIOD_IN_MINUTES;
import static com.ericsson.nms.security.nscs.ldap.utility.LdapConstants.VDU_LDAP_MO;
import static com.ericsson.oss.services.nscs.workflow.utils.WorkflowNames.WORKFLOW_CBPOI_CONFIGURE_LDAP;
import static com.ericsson.oss.services.nscs.workflow.utils.WorkflowNames.WORKFLOW_COMECIM_CONFIGURE_LDAP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.command.impl.MockUtils;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;

/**
 * Test the NscsCapabilityModelService to provide access to information stored in Capability Model.
 *
 * @see NscsCapabilityModelService
 * @author emaborz
 */
@RunWith(MockitoJUnitRunner.class)
public class NscsCapabilityModelServiceTest {

    private final static String CSCF = "CSCF";
    private final static String ERBS = "ERBS";
    private final static String SGSN_MME = "SGSN-MME";
    private final static String RadioNode = "RadioNode";
    private final static String MSRBS_V1 = "MSRBS_V1";
    private final static String SAPC = "SAPC";
    private final static String EPG = "EPG";
    private final static String Router6672 = "Router6672";
    private final static String Router6675 = "Router6675";
    private final static String Router6x71 = "Router6x71";
    private final static String Router6274 = "Router6274";
    private final static String MINI_LINK_Indoor = "MINI-LINK-Indoor";
    private final static String MINI_LINK_CN210 = "MINI-LINK-CN210";
    private final static String MINI_LINK_CN510R1 = "MINI-LINK-CN510R1";
    private final static String MINI_LINK_CN510R2 = "MINI-LINK-CN510R2";
    private final static String MINI_LINK_CN810R1 = "MINI-LINK-CN810R1";
    private final static String MINI_LINK_CN810R2 = "MINI-LINK-CN810R2";
    private final static String MINI_LINK_665x = "MINI-LINK-665x";
    private final static String MINI_LINK_669x = "MINI-LINK-669x";
    private final static String MINI_LINK_MW2 = "MINI-LINK-MW2";
    private final static String MINI_LINK_6352 = "MINI-LINK-6352";
    private final static String MINI_LINK_6351 = "MINI-LINK-6351";
    private final static String MINI_LINK_6366 = "MINI-LINK-6366";
    private final static String MINI_LINK_PT2020 = "MINI-LINK-PT2020";
    private final static String Switch_6391 = "Switch-6391";
    private final static String MGW = "MGW";
    private final static String MTAS = "MTAS";
    private final static String RNC = "RNC";
    private final static String RBS = "RBS";
    private final static String RadioTNode = "RadioTNode";
    private final static String SBG = "SBG";
    private final static String CISCO_ASR9000 = "CISCO-ASR9000";
    private final static String CISCO_ASR900 = "CISCO-ASR900";
    private final static String JUNIPER_MX = "JUNIPER-MX";
    private final static String FRONTHAUL_6080 = "FRONTHAUL-6080";
    private final static String FRONTHAUL_6020 = "FRONTHAUL-6020";
    private final static String Fronthaul_6392 = "Fronthaul-6392";
    private final static String VEPG = "VEPG";
    private final static String BSC = "BSC";
    private final static String MSC = "MSC";
    private final static String HLR = "HLR";
    private final static String RnNode = "RnNode";
    private final static String vPP = "vPP";
    private final static String vRC = "vRC";
    private final static String vEME = "vEME";
    private final static String vWCG = "vWCG";
    private final static String HSS_FE = "HSS-FE";
    private final static String VHSS_FE = "vHSS-FE";
    private final static String vIPWorks = "vIPWorks";
    private final static String vUPG = "vUPG";
    private final static String BSP = "BSP";
    private final static String vBGF = "vBGF";
    private final static String vMRF = "vMRF";
    private final static String FIVEGRadioNode = "5GRadioNode";
    private final static String vMTAS = "vMTAS";
    private final static String vSBG = "vSBG";
    private final static String vCSCF = "vCSCF";
    private final static String VTFRadioNode = "VTFRadioNode";
    private final static String RVNFM = "RVNFM";
    private final static String HLR_FE = "HLR-FE";
    private final static String vHLR_FE = "vHLR-FE";
    private final static String HLR_FE_BSP = "HLR-FE-BSP";
    private final static String HLR_FE_IS = "HLR-FE-IS";
    private final static String vRSM = "vRSM";
    private final static String ECM = "ECM";
    private final static String vRM = "vRM";
    private final static String vSD = "vSD";
    private final static String GenericESA = "GenericESA";
    private final static String vECE = "vECE";
    private final static String VDU = "vDU";
    private final static String UNKNOWN = "UNKNOWN";

    private static final List<String> erbsTargetModelIdentities = Arrays.asList("6824-690-779", "4613-704-163", "17B-H.1.300", "1777-370-163",
            "4280-987-331", "17Q3-J.1.40", "5981-462-912", "17B-H.1.340", "2754-962-591", "17A-H.1.190", "17B-H.1.240", "17.Q3-J.1.82",
            "17.Q3-J.1.81", "3958-644-341", "16B-G.1.281", "4322-436-393", "17A-H.1.60", "1826-077-154", "18.Q2-J.1.240", "18.Q1-J.1.200",
            "1116-673-956", "17.Q4-J.1.140", "16A-G.1.143", "16A-G.1.142", "17B-H.1.263", "17A-H.1.140", "17B-H.1.260", "18.Q2-J.1.280", "17A-H.1.20",
            "6385-946-582", "2042-630-876", "17.Q2-H.1.362", "3520-829-806", "17B-H.1.320", "17Q3-J.1.22", "17B-H.1.361", "16B-G.1.260",
            "17B-H.1.220", "17.Q4-J.1.180", "1998-184-092", "17.Q4-J.1.185", "4322-940-032", "17Q2-H.1.351", "17.Q4-J.1.120", "17A-H.1.80",
            "18.Q2-J.1.261", "17.Q4-J.1.160", "18.Q1-J.1.220", "6607-651-025", "17A-H.1.120", "16B-G.1.308", "17A-H.1.40", "16B-G.1.301",
            "1147-458-334", "17A-H.1.160", "17B-H.1.281", "17Q3-J.1.80", "18.Q3-J.2.50");
    private static final List<String> erbsTargetModelIdentitiesLesserThan17B = Arrays.asList("6824-690-779", "4322-436-393", "1998-184-092",
            "4322-940-032", "1777-370-163", "4280-987-331", "3958-644-341", "3520-829-806", "6607-651-025", "4613-704-163", "5981-462-912",
            "2754-962-591", "1826-077-154", "16A-G.1.143", "16A-G.1.142", "2042-630-876", "1147-458-334", "16B-G.1.281", "1116-673-956",
            "6385-946-582", "16B-G.1.260", "16B-G.1.308", "16B-G.1.301", "17A-H.1.190", "17A-H.1.60", "17A-H.1.140", "17A-H.1.20", "17A-H.1.80",
            "17A-H.1.120", "17A-H.1.40", "17A-H.1.160");
    private static final List<String> rncTargetModelIdentities = Arrays.asList("15B-V.5.4658-G4", "17B-V.9.1240", "18.Q1-V.12.290", "16B-V.7.1659",
            "15B-V.5.4658", "17.Q4-V.11.338", "16A-V.6.940-J2", "17.Q4-V.11.243", "18.Q2-V.13.461", "17.Q4-V.11.127", "18.Q2-V.13.27", "17A-V.8.1349",
            "16B-V.7.1659-M9", "18.Q1-V.12.40", "18.Q2-V.13.316", "17B-V.10.304", "16A-V.6.940", "18.Q2-V.13.601");
    private static final List<String> rncTargetModelIdentitiesLesserThan17B = Arrays.asList("15B-V.5.4658", "15B-V.5.4658-G4", "16A-V.6.940",
            "16A-V.6.940-J2", "16B-V.7.1659", "16B-V.7.1659-M9", "17A-V.8.1349");
    private static final List<String> rbsTargetModelIdentities = Arrays.asList("17B-U.4.570", "15B-U.4.91", "13B-S2.1.100",
            "13B-S.1.100", "16B-U.4.340", "16A-U.4.210", "18.Q1-U.4.800", "17.Q4-U.4.680", "17A-U.4.460", "17.Q4-U.4.720",
            "18.Q2-U.4.830", "17B-U.4.630", "18.Q1-U.4.750", "18.Q2-U.4.910");
    private static final List<String> rbsTargetModelIdentitiesLesserThan17B = Arrays.asList("13B-S2.1.100", "13B-S.1.100", "15B-U.4.91",
            "16A-U.4.210", "16B-U.4.340", "17A-U.4.460");
    private static final List<String> rbsTargetModelIdentitiesLesserThan14A = Arrays.asList("13B-S2.1.100", "13B-S.1.100");
    private static final List<String> mgwTargetModelIdentities = Arrays.asList("17A-C.1.267", "17B-C.1.278", "17B-C.1.288", "17A-C.1.257",
            "16A-C.1.214", "16A-C.1.203", "1484-383-806", "14B-C.1.141", "16B-C.1.243", "6.9.2.0");
    private static final List<String> mgwTargetModelIdentitiesLesserThan17B = Arrays.asList("14B-C.1.141", "1484-383-806", "16A-C.1.214",
            "16A-C.1.203", "16B-C.1.243", "17A-C.1.267", "17A-C.1.257");
    private static final List<String> sgsnTargetModelIdentities = Arrays.asList("15A-CP01", "1.11-R72B17", "16A-CP12", "16A-CP01", "16A-CP00",
            "15B-CP01", "15B-CP00", "16A-CP02", "14B-CP11", "16A-CP09", "16A-CP06", "1.13-R74B15", "1.15", "1.18");
    private static final List<String> sbgTargetModelIdentities = Arrays.asList("SBG-1.4", "SBG-1.1");
    private static final List<String> vsbgTargetModelIdentities = Arrays.asList("vSBG-1.1", "vSBG-1.6", "vSBG-1.4");
    private static final List<String> cscfTargetModelIdentities = Arrays.asList("CSCF-7.4", "CSCF-7.2", "CSCF-1.2");
    private static final List<String> vcscfTargetModelIdentities = Arrays.asList("vCSCF-7.2", "vCSCF-1.2", "vCSCF-7.4", "vCSCF-1.3");
    private static final List<String> mtasTargetModelIdentities = Arrays.asList("MTAS-1.5", "MTAS-1.0", "MTAS-1.2");
    private static final List<String> vmtasTargetModelIdentities = Arrays.asList("vMTAS-1.0", "vMTAS-1.2", "vMTAS-1.5", "vMTAS-1.7");
    private static final List<String> msrbsv1TargetModelIdentities = Arrays.asList("18.Q2-R4G", "16A-R9F", "16A-R4B", "17B-R4C", "18.Q1-R4E",
            "17B-R4B", "18.Q3-R4H");
    private static final List<String> msrbsv1TargetModelIdentitiesLesserThan16B = Arrays.asList("16A-R9F", "16A-R4B");
    private static final List<String> sapcTargetModelIdentities = Arrays.asList("16B-R1A", "17A-R1C", "1.1-R2A", "SAPC-1.0", "1.2-R2E");
    private static final List<String> epgTargetModelIdentities = Arrays.asList("1.6", "16B-R13C", "1.3", "17B-1.0", "16A-CP00");
    private static final List<String> radioNodeTargetModelIdentities = Arrays.asList("18.Q1-R36A20", "16B-R2CJ", "17A-R2GB",
            "15B-R12EC", "17B-R2A14", "17Q4-R28A28", "17A-R2YX", "18.Q2-R39A12_P1", "17.Q3-R25C05", "16B-R2HH",
            "17B-R5A52", "16B-R28GY", "18.Q1-R33A54", "17B-R2A89", "17A-R2E", "16A-R28CJ", "17B-R2BJB", "18.Q2-R39A12", "17A-R2SJ",
            "16A-R22AC", "17B-R7A42", "18.Q2-R43A23", "17Q4-R26A41", "17.Q3-R25D04", "17A-R2CX", "17B-R16A20", "17.Q4-R32B07",
            "17.Q2-R12G10", "16B-R2ZV", "17B-R5B02", "17B-R10A41", "17B-R12B12", "16B-R28DS", "18.Q1-R36A20_P1", "17B-R2ANJ",
            "16A-R29AJ");
    private static final List<String> radioNodeTargetModelIdentitiesLesserThan16B = Arrays.asList("15B-R12EC", "16A-R28CJ", "16A-R22AC", "16A-R29AJ");
    private static final List<String> radioNodeTargetModelIdentitiesLesserThan17A = Arrays.asList("15B-R12EC", "16A-R28CJ", "16A-R22AC", "16A-R29AJ",
            "16B-R2CJ", "16B-R2HH", "16B-R28GY", "16B-R2ZV", "16B-R28DS");
    private static final List<String> er6672TargetModelIdentities = Arrays.asList("R18A-GA", "R17B-GA", "R17A-GA");
    private static final List<String> er6672TargetModelIdentitiesLesserThan17B = Arrays.asList("R17A-GA");
    private static final List<String> er6672TargetModelIdentitiesLesserThan18A = Arrays.asList("R17B-GA", "R17A-GA");
    private static final List<String> er6675TargetModelIdentities = Arrays.asList("R18A-PRA2", "R18A-GA");
    private static final List<String> er6675TargetModelIdentitiesLesserThan18A = new ArrayList<String>();
    private static final List<String> er6x71TargetModelIdentities = Arrays.asList("R18A-GA");
    private static final List<String> er6x71TargetModelIdentitiesLesserThan18A = new ArrayList<String>();
    private static final List<String> er6274TargetModelIdentities = Arrays.asList("R18Q2-PRA");
    private static final List<String> er6274TargetModelIdentitiesLesserThan18B = new ArrayList<String>();
    private static final List<String> miniLinkIndoorTargetModelIdentities = Arrays.asList("M15A-TN-5.3FP-LH-1.5FP", "M16A-TN-5.4FP-LH-1.6FP",
            "M17.Q4-TN-6.1-LH-2.1", "M11B-TN-4.4FP", "M17A-TN-6.0-LH-2.0");
    private static final List<String> miniLinkCn210TargetModelIdentities = Arrays.asList("M12A-CN210-1.2");
    private static final List<String> miniLinkCn510R1TargetModelIdentities = Arrays.asList("M12A-CN510R1-1.2");
    private static final List<String> miniLinkCn510R2TargetModelIdentities = Arrays.asList("M16A-CN510R2-2.4FP");
    private static final List<String> miniLinkCn810R1TargetModelIdentities = Arrays.asList("M13B-CN810R1-1.0");
    private static final List<String> miniLinkCn810R2TargetModelIdentities = Arrays.asList("M16A-CN810R2-2.4FP");
    private static final List<String> miniLink665xTargetModelIdentities = Arrays.asList("M18.Q1-665x-1.4");
    private static final List<String> miniLinkMW2TargetModelIdentities = Arrays.asList("M22.Q1-MW2-1.19");  
    private static final List<String> miniLink669xTargetModelIdentities = Arrays.asList("M18.Q1-669x-1.4");
    private static final List<String> miniLink6352TargetModelIdentities = Arrays.asList("M17.Q4-6352-2.9", "M17B-6352-2.8", "M17A-6352-2.7");
    private static final List<String> miniLink6351TargetModelIdentities = Arrays.asList("M17B-6351-2.8", "M17.Q4-6351-2.9");
    private static final List<String> miniLinkPt2020TargetModelIdentities = Arrays.asList("M17B-PT2020-2.8", "M17.Q4-PT2020-2.9");
    private static final List<String> switch6391TargetModelIdentities = Arrays.asList("M17B-6391-2.8", "M17.Q4-6391-2.9");
    private static final List<String> ciscoAsr9000TargetModelIdentities = Arrays.asList("17A");
    private static final List<String> ciscoAsr900TargetModelIdentities = Arrays.asList("17A");
    private static final List<String> juniperMxTargetModelIdentities = Arrays.asList("17A");
    private static final List<String> radioTNodeTargetModelIdentities = Arrays.asList("17.Q4-R22A192", "17.Q4-R13A41", "17.Q4-R17A193", "17B-R2A12",
            "17.Q4-R17A185", "17.Q4-R16A42", "17.Q4-R19C07", "16B-R2JH", "17A-R1ASL", "17.Q4-R21A10", "17.Q4-R14A215", "17.Q4-R11A316", "17B-R9A57",
            "17B-R4A147", "17B-R4A61", "17B-R2A122", "17B-R6A65", "17.Q2-R12B21");
    private static final List<String> radioTNodeTargetModelIdentitiesLesserThan17A = Arrays.asList("16B-R2JH");
    private static final List<String> bspTargetModelIdentities = Arrays.asList("R7-R1A", "R10-R10D", "BSP-R11", "R13");
    private static final List<String> vemeTargetModelIdentities = Arrays.asList("16A-100", "vEME-1.1", "17A-100");
    private static final List<String> vwcgTargetModelIdentities = Arrays.asList("15B-6220", "vWCG-1.2");
    private static final List<String> vipworksTargetModelIdentities = Arrays.asList("vIPWorks-1.3", "17A-8", "vIPWorks-1.9");
    private static final List<String> vbgfTargetModelIdentities = Arrays.asList("16A-100", "vBGF-1.9", "vBGF-1.7");
    private static final List<String> vmrfTargetModelIdentities = Arrays.asList("17A-R29C", "vMRF-1.1", "vMRF-1.2");
    private static final List<String> vupgTargetModelIdentities = Arrays.asList("15A-R1A", "vUPG-1.0");
    private static final List<String> vppTargetModelIdentities = Arrays.asList("18Q1-R1A55", "18Q2-R5A30", "18Q1-R2A10", "18Q1-R1A14", "17Q3-R1A05",
            "18.Q2-R7A04", "18.Q2-R6A15");
    private static final List<String> fronthaul6080TargetModelIdentities = Arrays.asList("17.Q4", "17.Q3");
    private static final List<String> fronthaul6020TargetModelIdentities = Arrays.asList("18.Q2");
    private static final List<String> fronthaul6392TargetModelIdentities = Arrays.asList("M17.Q4-6392-2.9");
    private static final List<String> bscTargetModelIdentities = Arrays.asList("BSC-G18.Q1-R1F-APG43L-3.4.3-R5E", "BSC-G18.Q2-R1H-APG43L-3.4.4-R5F",
            "BSC-G17.Q4-R1D-APG43L-3.4.0-R5A", "BSC-G17.Q4-R1C-APG43L-3.4.0-R5A", "BSC-G18.Q1-R1E-APG43L-3.4.3-R5E",
            "BSC-G18.Q2-R1G-APG43L-3.4.4-R5F", "BSC-G18.Q2-R1J-APG43L-3.4.4-R5F");
    private static final List<String> hlrfeTargetModelIdentities = Arrays.asList("1.9");
    private static final List<String> vhlrfeTargetModelIdentities = Arrays.asList("1.9");
    private static final List<String> hlrfebspTargetModelIdentities = Arrays.asList("1.9");
    private static final List<String> hlrfeisTargetModelIdentities = Arrays.asList("1.9");
    private static final List<String> vepgTargetModelIdentities = Arrays.asList("1.6", "16B-R13C", "16A-CP06", "1.3", "17B-1.0");
    private static final List<String> vtfRadioNodeTargetModelIdentities = Arrays.asList("18Q1-R1A318", "18.Q2-R3A03", "18Q2-R1A418");
    private static final List<String> vsdTargetModelIdentities = Arrays.asList("18A-R2A30", "18A-R3A11", "18A-R2A86", "18A-R1A02");
    private static final List<String> rvnfmTargetModelIdentities = Arrays.asList("18A-R2A02", "18A-R2A03", "18A-R3A02", "18A-R1A02");

    private static final String UNKNOWN_NODE_NAME = "unknown123";

    private static final NodeModelInformation invalidNodeModelInfo = new NodeModelInformation("ANY", ModelIdentifierType.OSS_IDENTIFIER, UNKNOWN);

    private static final String ERBS_NODE_NAME = "ERBS123";
    private static final String ERBS_16B_TARGET_MODEL_IDENTITY = "16B-G.1.260";
    private static final NodeModelInformation erbsNodeModelInfo = new NodeModelInformation(ERBS_16B_TARGET_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, ERBS);
    private static final String RNC_NODE_NAME = "RNC145";
    private static final String RNC_TARGET_MODEL_IDENTITY = "5509-862-855";
    private static final NodeModelInformation rncNodeModelInfo = new NodeModelInformation(RNC_TARGET_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, RNC);

    private static final String RBS_NODE_NAME = "RBS145";
    private static final String RBS_TARGET_MODEL_IDENTITY = "15B-U.4.90";
    private static final NodeModelInformation rbsNodeModelInfo = new NodeModelInformation(RBS_TARGET_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, RBS);
    private static final String MGW_NODE_NAME = "MGW123";
    private static final String MGW_TARGET_MODEL_IDENTITY = "1484-383-806";
    private static final NodeModelInformation mgwNodeModelInfo = new NodeModelInformation(MGW_TARGET_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, MGW);

    private static final String SGSN_NODE_NAME = "SGSN123";
    private static final String SGSN_OSS_MODEL_IDENTITY = "397-5538-244";
    private static final NodeModelInformation sgsnNodeModelInfo = new NodeModelInformation(SGSN_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, SGSN_MME);

    private static final String SBG_NODE_NAME = "SBG123";
    private static final String SBG_OSS_MODEL_IDENTITY = "101-1001-101";
    private static final NodeModelInformation sbgNodeModelInfo = new NodeModelInformation(SBG_OSS_MODEL_IDENTITY, ModelIdentifierType.OSS_IDENTIFIER,
            SBG);

    private static final String VSBG_NODE_NAME = "VSBG123";
    private static final String VSBG_OSS_MODEL_IDENTITY = "101-1001-101";
    private static final NodeModelInformation vsbgNodeModelInfo = new NodeModelInformation(VSBG_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, vSBG);

    private static final String CSCF_NODE_NAME = "CSCF123";
    private static final String CSCF_OSS_MODEL_IDENTITY = "202-2002-202";
    private static final NodeModelInformation cscfNodeModelInfo = new NodeModelInformation(CSCF_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, CSCF);

    private static final String VCSCF_NODE_NAME = "VCSCF123";
    private static final String VCSCF_OSS_MODEL_IDENTITY = "202-2002-202";
    private static final NodeModelInformation vcscfNodeModelInfo = new NodeModelInformation(VCSCF_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, vCSCF);

    private static final String MTAS_NODE_NAME = "MTAS123";
    private static final String MTAS_OSS_MODEL_IDENTITY = "303-3003-303";
    private static final NodeModelInformation mtasNodeModelInfo = new NodeModelInformation(MTAS_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, MTAS);

    private static final String VMTAS_NODE_NAME = "VMTAS123";
    private static final String VMTAS_OSS_MODEL_IDENTITY = "303-3003-303";
    private static final NodeModelInformation vmtasNodeModelInfo = new NodeModelInformation(VMTAS_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, vMTAS);

    private static final String MSRBSV1_NODE_NAME = "MSRBSV1123";
    private static final String MSRBSV1_OSS_MODEL_IDENTITY = "397-5538-555";
    private static final NodeModelInformation msrbsv1NodeModelInfo = new NodeModelInformation(MSRBSV1_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, MSRBS_V1);

    private static final String SAPC_NODE_NAME = "SAPC123";
    private static final String SAPC_OSS_MODEL_IDENTITY = "16A-R1C";
    private static final NodeModelInformation sapcNodeModelInfo = new NodeModelInformation(SAPC_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, SAPC);

    private static final String RNNODE_NODE_NAME = "RNNODE123";
    private static final String RNNODE_OSS_MODEL_IDENTITY = "17A-R1X";
    private static final NodeModelInformation rnNodeNodeModelInfo = new NodeModelInformation(RNNODE_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, RnNode);

    private static final String FIVE_G_RADIONODE_NAME = "5GRADIONODE123";
    private static final String FIVE_G_RADIONODE_OSS_MODEL_IDENTITY = "17B-R1A26";
    private static final NodeModelInformation fiveGRadioNodeNodeModelInfo = new NodeModelInformation(FIVE_G_RADIONODE_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, FIVEGRadioNode);

    private static final String VTF_RADIONODE_NAME = "VTFRADIONODE123";
    private static final String VTF_RADIONODE_OSS_MODEL_IDENTITY = "17B-R1A26";
    private static final NodeModelInformation vtfRadioNodeNodeModelInfo = new NodeModelInformation(VTF_RADIONODE_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, VTFRadioNode);

    private static final String VSD_NODE_NAME = "VSDNODE123";
    private static final String VSD_NODE_OSS_MODEL_IDENTITY = "18A-R1A02";
    private static final NodeModelInformation vsdNodeModelInfo = new NodeModelInformation(VSD_NODE_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, "vSD");

    private static final String RVNFM_NODE_NAME = "RVNFMNODE123";
    private static final String RVNFM_NODE_OSS_MODEL_IDENTITY = "18A-R1A02";
    private static final NodeModelInformation rVNFMNodeModelInfo = new NodeModelInformation(RVNFM_NODE_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, RVNFM);

    private static final String VPP_NODE_NAME = "VPP123";
    private static final String VPP_OSS_MODEL_IDENTITY = "17A-R1AA";
    private static final NodeModelInformation vppNodeModelInfo = new NodeModelInformation(VPP_OSS_MODEL_IDENTITY, ModelIdentifierType.OSS_IDENTIFIER,
            vPP);

    private static final String VRSM_NODE_NAME = "VRSM123";
    private static final String VRSM_OSS_MODEL_IDENTITY = "17A-R1AA";
    private static final NodeModelInformation vrsmNodeModelInfo = new NodeModelInformation(VRSM_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, vRSM);

    private static final String VRC_NODE_NAME = "VRC123";
    private static final String VRC_OSS_MODEL_IDENTITY = "17A-R1AA";
    private static final NodeModelInformation vrcNodeModelInfo = new NodeModelInformation(VRC_OSS_MODEL_IDENTITY, ModelIdentifierType.OSS_IDENTIFIER,
            vRC);

    private static final String VEME_NODE_NAME = "VEME123";
    private static final String VEME_OSS_MODEL_IDENTITY = "17A-R1X";
    private static final NodeModelInformation vEMENodeModelInfo = new NodeModelInformation(VEME_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, vEME);

    private static final String VWCG_NODE_NAME = "VWCG123";
    private static final String VWCG_OSS_MODEL_IDENTITY = "17A-R1X";
    private static final NodeModelInformation vWCGNodeModelInfo = new NodeModelInformation(VWCG_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, vWCG);

    private static final String HSSFE_NODE_NAME = "HSSFE123";
    private static final String HSSFE_OSS_MODEL_IDENTITY = "17A-R1X";
    private static final NodeModelInformation HSSFENodeModelInfo = new NodeModelInformation(HSSFE_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, HSS_FE);

    private static final String VHSSFE_NODE_NAME = "VHSSFE123";
    private static final String VHSSFE_OSS_MODEL_IDENTITY = "17A-R1X";
    private static final NodeModelInformation vHSSFENodeModelInfo = new NodeModelInformation(VHSSFE_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, VHSS_FE);

    private static final String VIPWORKS_NODE_NAME = "VIPWORKS123";
    private static final String VIPWORKS_OSS_MODEL_IDENTITY = "17A-R1X";
    private static final NodeModelInformation vIPWorksNodeModelInfo = new NodeModelInformation(RNNODE_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, vIPWorks);

    private static final String VUPG_NODE_NAME = "VUPG123";
    private static final String VUPG_OSS_MODEL_IDENTITY = "17A-R1X";
    private static final NodeModelInformation vUPGNodeModelInfo = new NodeModelInformation(VUPG_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, vUPG);

    private static final String BSP_NODE_NAME = "BSP123";
    private static final String BSP_OSS_MODEL_IDENTITY = "17A-R1X";
    private static final NodeModelInformation BSPNodeModelInfo = new NodeModelInformation(BSP_OSS_MODEL_IDENTITY, ModelIdentifierType.OSS_IDENTIFIER,
            BSP);

    private static final String VBGF_NODE_NAME = "VBGF123";
    private static final String VBGF_OSS_MODEL_IDENTITY = "17A-R1X";
    private static final NodeModelInformation vBGFNodeModelInfo = new NodeModelInformation(VBGF_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, vBGF);

    private static final String VMRF_NODE_NAME = "VMRF123";
    private static final String VMRF_OSS_MODEL_IDENTITY = "17A-R1X";
    private static final NodeModelInformation vMRFNodeModelInfo = new NodeModelInformation(VMRF_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, vMRF);

    private static final String EPG_NODE_NAME = "EPG123";
    private static final String EPG_OSS_MODEL_IDENTITY = "16B-R13C";
    private static final NodeModelInformation epgNodeModelInfo = new NodeModelInformation(EPG_OSS_MODEL_IDENTITY, ModelIdentifierType.OSS_IDENTIFIER,
            EPG);

    private static final String VEPG_NODE_NAME = "VEPG123";
    private static final String VEPG_OSS_MODEL_IDENTITY = "16B-R13C";
    private static final NodeModelInformation vepgNodeModelInfo = new NodeModelInformation(VEPG_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, VEPG);

    private static final String RADIOTNODE_NODE_NAME = "RADIOTNODE123";
    private static final String RADIOTNODE_OSS_MODEL_IDENTITY = "16A-R11AC";
    private static final NodeModelInformation radioTNodeModelInfo = new NodeModelInformation(RADIOTNODE_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, RadioTNode);

    private static final String RADIO_NODE_NAME = "DUG2123";
    private static final String RADIO_OSS_MODEL_IDENTITY = "397-5538-366";
    private static final NodeModelInformation radioNodeModelInfo = new NodeModelInformation(RADIO_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, RadioNode);

    private static final String ER6000_NODE_NAME = "ER6000-123";
    private static final String ER6000_OSS_MODEL_IDENTITY = "R16A-GA";
    private static final NodeModelInformation er6000NodeModelInfo = new NodeModelInformation(ER6000_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, Router6672);

    private static final String MINI_LINK_INDOOR_NODE_NAME = "MINI-LINK-Indoor123";
    private static final String MINI_LINK_INDOOR_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation miniLinkIndoorNodeModelInfo = new NodeModelInformation(MINI_LINK_INDOOR_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, MINI_LINK_Indoor);

    private static final String MINI_LINK_CN210_NODE_NAME = "MINI-LINK-CN210123";
    private static final String MINI_LINK_CN210_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation miniLinkcN210NodeModelInfo = new NodeModelInformation(MINI_LINK_CN210_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, MINI_LINK_CN210);

    private static final String MINI_LINK_CN510R1_NODE_NAME = "MINI-LINK-CN510R1123";
    private static final String MINI_LINK_CN510R1_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation miniLinkcn510r1NodeModelInfo = new NodeModelInformation(MINI_LINK_CN510R1_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, MINI_LINK_CN510R1);

    private static final String MINI_LINK_CN510R2_NODE_NAME = "MINI-LINK-CN510R2123";
    private static final String MINI_LINK_CN510R2_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation miniLinkcn510r2NodeModelInfo = new NodeModelInformation(MINI_LINK_CN510R2_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, MINI_LINK_CN510R2);

    private static final String MINI_LINK_CN810R1_NODE_NAME = "MINI-LINK-CN810R1123";
    private static final String MINI_LINK_CN810R1_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation miniLinkcn810r1NodeModelInfo = new NodeModelInformation(MINI_LINK_CN810R1_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, MINI_LINK_CN810R1);

    private static final String MINI_LINK_CN810R2_NODE_NAME = "MINI-LINK-CN810R2123";
    private static final String MINI_LINK_CN810R2_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation miniLinkcn810r2NodeModelInfo = new NodeModelInformation(MINI_LINK_CN810R2_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, MINI_LINK_CN810R2);

    private static final String MINI_LINK_665x_NODE_NAME = "MINI-LINK-665x";
    private static final String MINI_LINK_665x_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation miniLink665xNodeModelInfo = new NodeModelInformation(MINI_LINK_665x_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, MINI_LINK_665x);

    private static final String MINI_LINK_669x_NODE_NAME = "MINI-LINK-669x";
    private static final String MINI_LINK_669x_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation miniLink669xNodeModelInfo = new NodeModelInformation(MINI_LINK_669x_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, MINI_LINK_669x);

    private static final String MINI_LINK_MW2_NODE_NAME = "MINI-LINK-MW2";
    private static final String MINI_LINK_MW2_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation miniLinkMW2NodeModelInfo = new NodeModelInformation(MINI_LINK_MW2_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, MINI_LINK_MW2);

    private static final String MINI_LINK_6352_NODE_NAME = "MINI-LINK-6352123";
    private static final String MINI_LINK_6352_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation miniLink6352NodeModelInfo = new NodeModelInformation(MINI_LINK_6352_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, MINI_LINK_6352);

    private static final String MINI_LINK_6351_NODE_NAME = "MINI-LINK-6351123";
    private static final String MINI_LINK_6351_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation miniLink6351NodeModelInfo = new NodeModelInformation(MINI_LINK_6351_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, MINI_LINK_6351);

    private static final String MINI_LINK_6366_NODE_NAME = "MINI-LINK-6366";
    private static final String MINI_LINK_6366_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation miniLink6366NodeModelInfo = new NodeModelInformation(MINI_LINK_6366_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, MINI_LINK_6366);

    private static final String MINI_LINK_PT2020_NODE_NAME = "MINI-LINK-PT2020123";
    private static final String MINI_LINK_PT2020_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation miniLinkPT2020NodeModelInfo = new NodeModelInformation(MINI_LINK_PT2020_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, MINI_LINK_PT2020);

    private static final String SWITCH_6391_NODE_NAME = "Switch-6391123";
    private static final String SWITCH_6391_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation switch6391NodeModelInfo = new NodeModelInformation(SWITCH_6391_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, Switch_6391);

    private static final String FRONTHAUL_6392_NODE_NAME = "FRONTHAUL-6392123";
    private static final String FRONTHAUL_6392_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation fronthaul6392NodeModelInfo = new NodeModelInformation(FRONTHAUL_6392_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, Fronthaul_6392);

    private static final String CISCO_ASR9000_NODE_NAME = "CISCO-ASR9000-123";
    private static final String CISCO_ASR9000_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation ciscoAsr9000NodeModelInfo = new NodeModelInformation(CISCO_ASR9000_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, CISCO_ASR9000);

    private static final String CISCO_ASR900_NODE_NAME = "CISCO-ASR900-123";
    private static final String CISCO_ASR900_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation ciscoAsr900NodeModelInfo = new NodeModelInformation(CISCO_ASR900_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, CISCO_ASR900);

    private static final String FRONTHAUL_NODE_NAME = "FRONTHAUL-6080-001";
    private static final String FRONTHAUL_OSS_MODEL_IDENTITY = "";
    private static final NodeModelInformation fronthaulNodeModelInfo = new NodeModelInformation(FRONTHAUL_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, FRONTHAUL_6080);

    private static final String FRONTHAUL_6020_NODE_NAME = "FRONTHAUL-6020-001";
    private static final NodeModelInformation fronthaul6020NodeModelInfo = new NodeModelInformation(FRONTHAUL_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, FRONTHAUL_6020);

    private static final String JUNIPER_MX_NODE_NAME = "JUNIPER-MX-123";
    private static final String JUNIPER_MX_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation juniperMxNodeModelInfo = new NodeModelInformation(JUNIPER_MX_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, JUNIPER_MX);

    private static final String BSC_NODE_NAME = "BSC-123";
    private static final String BSC_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation bscNodeModelInfo = new NodeModelInformation(BSC_OSS_MODEL_IDENTITY, ModelIdentifierType.OSS_IDENTIFIER,
            BSC);

    private static final String HLR_FE_NODE_NAME = "HLR_FE-123";
    private static final String HLR_FE_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation hlrfeNodeModelInfo = new NodeModelInformation(HLR_FE_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, HLR_FE);

    private static final String vHLR_FE_NODE_NAME = "vHLR_FE-123";
    private static final String vHLR_FE_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation vhlrfeNodeModelInfo = new NodeModelInformation(vHLR_FE_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, vHLR_FE);

    private static final String HLR_FE_BSP_NODE_NAME = "HLR_FE_BSP-123";
    private static final String HLR_FE_BSP_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation hlrfebspNodeModelInfo = new NodeModelInformation(HLR_FE_BSP_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, HLR_FE_BSP);

    private static final String HLR_FE_IS_NODE_NAME = "HLR_FE_IS-123";
    private static final String HLR_FE_IS_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation hlrfeisNodeModelInfo = new NodeModelInformation(HLR_FE_IS_OSS_MODEL_IDENTITY,
            ModelIdentifierType.OSS_IDENTIFIER, HLR_FE_IS);

    private static final String MSC_NODE_NAME = "MSC-123";
    private static final String MSC_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation mscNodeModelInfo = new NodeModelInformation(MSC_OSS_MODEL_IDENTITY, ModelIdentifierType.OSS_IDENTIFIER,
            MSC);

    private static final String HLR_NODE_NAME = "HLR-123";
    private static final String HLR_OSS_MODEL_IDENTITY = "123-456-987";
    private static final NodeModelInformation hlrNodeModelInfo = new NodeModelInformation(HLR_OSS_MODEL_IDENTITY, ModelIdentifierType.OSS_IDENTIFIER,
            HLR);

    private static final String VDU_OSS_MODEL_IDENTITY = "123-456-987";
    private static final String VDU_NODE_NAME = "VDU-123";
    private static final NodeModelInformation vDUNodeModelInfo = new NodeModelInformation(VDU_OSS_MODEL_IDENTITY, ModelIdentifierType.OSS_IDENTIFIER, VDU);

    private final static String CREDENTIALS_COMMAND = "credentials";
    private final static String SSHKEY_COMMAND = "sshkey";
    private final static String ENROLLMENT_COMMAND = "enrollment";
    private final static String CERTIFICATE_COMMAND = "certificate";
    private final static String TRUST_COMMAND = "trust";
    private final static String SNMP_COMMAND = "snmp";
    private final static String LDAP_COMMAND = "ldap";
    private final static String IPSEC_COMMAND = "ipsec";
    private final static String SECURITYLEVEL_COMMAND = "securitylevel";
    private final static String CRLCHECK_COMMAND = "crlcheck";
    private final static String CRLDOWNLOAD_COMMAND = "crldownload";
    private final static String CIPHERS_COMMAND = "ciphers";
    private final static String HTTPS_COMMAND = "https";
    private final static String FTPES_COMMAND = "ftpes";
    private final static String RTSEL_COMMAND = "rtsel";

    private static final String ECIM_MOM = "ECIM";
    private static final String CPP_MOM = "CPP";

    @Spy
    private final Logger logger = LoggerFactory.getLogger(NscsCapabilityModelService.class);

    @InjectMocks
    NscsCapabilityModelService beanUnderTest;

    @Mock
    private BeanManager beanManager;

    @Mock
    private Bean<?> bean;

    @Mock
    private CreationalContext creationalContext;

    @InjectMocks
    private NscsCapabilityModelMock capabilityModel;

    @Mock
    private NscsCMReaderService reader;

    @Mock
    private NscsModelServiceImpl nscsModelServiceImpl;

    private final NodeReference unknownNodeRef = new NodeRef(UNKNOWN_NODE_NAME);
    private final NodeReference erbsNodeRef = new NodeRef(ERBS_NODE_NAME);
    private final NodeReference rncNodeRef = new NodeRef(RNC_NODE_NAME);
    private final NodeReference rbsNodeRef = new NodeRef(RBS_NODE_NAME);
    private final NodeReference mgwNodeRef = new NodeRef(MGW_NODE_NAME);
    private final NodeReference sgsnNodeRef = new NodeRef(SGSN_NODE_NAME);
    private final NodeReference sbgNodeRef = new NodeRef(SBG_NODE_NAME);
    private final NodeReference vsbgNodeRef = new NodeRef(VSBG_NODE_NAME);
    private final NodeReference cscfNodeRef = new NodeRef(CSCF_NODE_NAME);
    private final NodeReference vcscfNodeRef = new NodeRef(VCSCF_NODE_NAME);
    private final NodeReference mtasNodeRef = new NodeRef(MTAS_NODE_NAME);
    private final NodeReference vmtasNodeRef = new NodeRef(VMTAS_NODE_NAME);
    private final NodeReference msrbsv1NodeRef = new NodeRef(MSRBSV1_NODE_NAME);
    private final NodeReference sapcNodeRef = new NodeRef(SAPC_NODE_NAME);
    private final NodeReference epgNodeRef = new NodeRef(EPG_NODE_NAME);
    private final NodeReference vepgNodeRef = new NodeRef(VEPG_NODE_NAME);
    private final NodeReference radioNodeRef = new NodeRef(RADIO_NODE_NAME);
    private final NodeReference radioTNodeRef = new NodeRef(RADIOTNODE_NODE_NAME);
    private final NodeReference er6000NodeRef = new NodeRef(ER6000_NODE_NAME);
    private final NodeReference miniLinkIndoorNodeRef = new NodeRef(MINI_LINK_INDOOR_NODE_NAME);
    private final NodeReference miniLinkcn210NodeRef = new NodeRef(MINI_LINK_CN210_NODE_NAME);
    private final NodeReference miniLinkcn510r1NodeRef = new NodeRef(MINI_LINK_CN510R1_NODE_NAME);
    private final NodeReference miniLinkcn510r2NodeRef = new NodeRef(MINI_LINK_CN510R2_NODE_NAME);
    private final NodeReference miniLinkcn810r1NodeRef = new NodeRef(MINI_LINK_CN810R1_NODE_NAME);
    private final NodeReference miniLinkcn810r2NodeRef = new NodeRef(MINI_LINK_CN810R2_NODE_NAME);
    private final NodeReference miniLink665xNodeRef = new NodeRef(MINI_LINK_665x_NODE_NAME);
    private final NodeReference miniLink669xNodeRef = new NodeRef(MINI_LINK_669x_NODE_NAME);
    private final NodeReference miniLinkMW2NodeRef = new NodeRef(MINI_LINK_MW2_NODE_NAME);
    private final NodeReference miniLink6352NodeRef = new NodeRef(MINI_LINK_6352_NODE_NAME);
    private final NodeReference miniLink6351NodeRef = new NodeRef(MINI_LINK_6351_NODE_NAME);
    private final NodeReference miniLink6366NodeRef = new NodeRef(MINI_LINK_6366_NODE_NAME);
    private final NodeReference miniLinkPT2020NodeRef = new NodeRef(MINI_LINK_PT2020_NODE_NAME);
    private final NodeReference switch6391NodeRef = new NodeRef(SWITCH_6391_NODE_NAME);
    private final NodeReference fronthaul6392NodeRef = new NodeRef(FRONTHAUL_6392_NODE_NAME);
    private final NodeReference ciscoAsr9000NodeRef = new NodeRef(CISCO_ASR9000_NODE_NAME);
    private final NodeReference ciscoAsr900NodeRef = new NodeRef(CISCO_ASR900_NODE_NAME);
    private final NodeReference juniperMxNodeRef = new NodeRef(JUNIPER_MX_NODE_NAME);
    private final NodeReference rnNodeNodeRef = new NodeRef(RNNODE_NODE_NAME);
    private final NodeReference vppNodeRef = new NodeRef(VPP_NODE_NAME);
    private final NodeReference vrsmNodeRef = new NodeRef(VRSM_NODE_NAME);
    private final NodeReference vrcNodeRef = new NodeRef(VRC_NODE_NAME);
    private final NodeReference vEMENodeRef = new NodeRef(VEME_NODE_NAME);
    private final NodeReference vWCGNodeRef = new NodeRef(VWCG_NODE_NAME);
    private final NodeReference hSSFENodeRef = new NodeRef(HSSFE_NODE_NAME);
    private final NodeReference vhSSFENodeRef = new NodeRef(VHSSFE_NODE_NAME);
    private final NodeReference vIPWorksNodeRef = new NodeRef(VIPWORKS_NODE_NAME);
    private final NodeReference vUPGNodeRef = new NodeRef(VUPG_NODE_NAME);
    private final NodeReference bSPNodeRef = new NodeRef(BSP_NODE_NAME);
    private final NodeReference vBGFNodeRef = new NodeRef(VBGF_NODE_NAME);
    private final NodeReference vMRFNodeRef = new NodeRef(VMRF_NODE_NAME);
    private final NodeReference fronthaulNodeRef = new NodeRef(FRONTHAUL_NODE_NAME);
    private final NodeReference fronthaul6020NodeRef = new NodeRef(FRONTHAUL_6020_NODE_NAME);
    private final NodeReference bscNodeRef = new NodeRef(BSC_NODE_NAME);
    private final NodeReference mscNodeRef = new NodeRef(MSC_NODE_NAME);
    private final NodeReference hlrNodeRef = new NodeRef(HLR_NODE_NAME);
    private final NodeReference fiveGRadioNodeNodeRef = new NodeRef(FIVE_G_RADIONODE_NAME);
    private final NodeReference vtfRadioNodeNodeRef = new NodeRef(VTF_RADIONODE_NAME);
    private final NodeReference vsdNodeRef = new NodeRef(VSD_NODE_NAME);
    private final NodeReference rVNFMNodeRef = new NodeRef(RVNFM_NODE_NAME);
    private final NodeReference hlrfeNodeRef = new NodeRef(HLR_FE_NODE_NAME);
    private final NodeReference vhlrfeNodeRef = new NodeRef(vHLR_FE_NODE_NAME);
    private final NodeReference hlrfebspNodeRef = new NodeRef(HLR_FE_BSP_NODE_NAME);
    private final NodeReference hlrfeisNodeRef = new NodeRef(HLR_FE_IS_NODE_NAME);
    private final NodeReference vDuNodeRef = new NodeRef(VDU_NODE_NAME);

    private NormalizableNodeReference unknownNormNodeRef;
    private NormalizableNodeReference nullNeTypeNormNodeRef;
    private NormalizableNodeReference erbsNormNodeRef;
    private NormalizableNodeReference rncNormNodeRef;
    private NormalizableNodeReference rbsNormNodeRef;
    private NormalizableNodeReference mgwNormNodeRef;
    private NormalizableNodeReference sgsnNormNodeRef;
    private NormalizableNodeReference sbgNormNodeRef;
    private NormalizableNodeReference vsbgNormNodeRef;
    private NormalizableNodeReference cscfNormNodeRef;
    private NormalizableNodeReference vcscfNormNodeRef;
    private NormalizableNodeReference mtasNormNodeRef;
    private NormalizableNodeReference vmtasNormNodeRef;
    private NormalizableNodeReference msrbsv1NormNodeRef;
    private NormalizableNodeReference sapcNormNodeRef;
    private NormalizableNodeReference epgNormNodeRef;
    private NormalizableNodeReference vepgNormNodeRef;
    private NormalizableNodeReference radioNormNodeRef;
    private NormalizableNodeReference radioTNodeNormNodeRef;
    private NormalizableNodeReference er6672NormNodeRef;
    private NormalizableNodeReference er6675NormNodeRef;
    private NormalizableNodeReference er6x71NormNodeRef;
    private NormalizableNodeReference er6274NormNodeRef;
    private NormalizableNodeReference miniLinkIndoorNormNodeRef;
    private NormalizableNodeReference miniLinkcn210NormNodeRef;
    private NormalizableNodeReference miniLinkcn510r1NormNodeRef;
    private NormalizableNodeReference miniLinkcn510r2NormNodeRef;
    private NormalizableNodeReference miniLinkcn810r1NormNodeRef;
    private NormalizableNodeReference miniLinkcn810r2NormNodeRef;
    private NormalizableNodeReference miniLink6352NormNodeRef;
    private NormalizableNodeReference miniLink6351NormNodeRef;
    private NormalizableNodeReference miniLink6366NormNodeRef;
    private NormalizableNodeReference miniLinkPT2020NormNodeRef;
    private NormalizableNodeReference switch6391NormNodeRef;
    private NormalizableNodeReference fronthaul6392NormNodeRef;
    private NormalizableNodeReference ciscoAsr9000NormNodeRef;
    private NormalizableNodeReference rnNodeNormNodeRef;
    private NormalizableNodeReference ciscoAsr900NormNodeRef;
    private NormalizableNodeReference vppNormNodeRef;
    private NormalizableNodeReference vrsmNormNodeRef;
    private NormalizableNodeReference juniperMxNormNodeRef;
    private NormalizableNodeReference vrcNormNodeRef;
    private NormalizableNodeReference fronthaul6080NormNodeRef;
    private NormalizableNodeReference fronthaul6020NormNodeRef;
    private NormalizableNodeReference bscNormNodeRef;
    private NormalizableNodeReference mscNormNodeRef;
    private NormalizableNodeReference hlrNormNodeRef;
    private NormalizableNodeReference vEMENormNodeRef;
    private NormalizableNodeReference vWCGNormNodeRef;
    private NormalizableNodeReference hSSFENormNodeRef;
    private NormalizableNodeReference vhSSFENormNodeRef;
    private NormalizableNodeReference vIPWorksNormNodeRef;
    private NormalizableNodeReference vUPGNormNodeRef;
    private NormalizableNodeReference bSPNormNodeRef;
    private NormalizableNodeReference vBGFNormNodeRef;
    private NormalizableNodeReference vMRFNormNodeRef;
    private NormalizableNodeReference fiveGRadioNodeNormNodeRef;
    private NormalizableNodeReference vtfRadioNodeNormNodeRef;
    private NormalizableNodeReference vsdNormNodeRef;
    private NormalizableNodeReference rVNFMNodeNormNodeRef;
    private NormalizableNodeReference miniLink665xNormNodeRef;
    private NormalizableNodeReference miniLink669xNormNodeRef;
    private NormalizableNodeReference miniLinkMW2NormNodeRef;
    private NormalizableNodeReference hlrfeNormNodeRef;
    private NormalizableNodeReference vhlrfeNormNodeRef;
    private NormalizableNodeReference hlrfebspNormNodeRef;
    private NormalizableNodeReference hlrfeisNormNodeRef;
    private NormalizableNodeReference vDuNormNodeRef;
    private NormalizableNodeReference nullNeTypeVDuNormNodeRef;

    private Map<String, String> enrollmentCAAuthorizationModes = null;

    @Before
    public void setup() {
        unknownNormNodeRef = MockUtils.createNormNodeRefWithMeContext(UNKNOWN_NODE_NAME, UNKNOWN, null, unknownNodeRef, reader);
        nullNeTypeNormNodeRef = MockUtils.createNormNodeRefWithMeContext(ERBS_NODE_NAME, null, null, erbsNodeRef, reader);
        erbsNormNodeRef = MockUtils.createNormNodeRefWithMeContext(ERBS_NODE_NAME, ERBS, ERBS_16B_TARGET_MODEL_IDENTITY, erbsNodeRef, reader);
        rncNormNodeRef = MockUtils.createNormNodeRefWithMeContext(RNC_NODE_NAME, RNC, RNC_TARGET_MODEL_IDENTITY, rncNodeRef, reader);
        rbsNormNodeRef = MockUtils.createNormNodeRefWithMeContext(RBS_NODE_NAME, RBS, RBS_TARGET_MODEL_IDENTITY, rbsNodeRef, reader);
        mgwNormNodeRef = MockUtils.createNormNodeRefWithMeContext(MGW_NODE_NAME, MGW, MGW_TARGET_MODEL_IDENTITY, mgwNodeRef, reader);
        sgsnNormNodeRef = MockUtils.createNormNodeRef(SGSN_NODE_NAME, SGSN_MME, SGSN_OSS_MODEL_IDENTITY, sgsnNodeRef, reader);
        sbgNormNodeRef = MockUtils.createNormNodeRef(SBG_NODE_NAME, SBG, SBG_OSS_MODEL_IDENTITY, sbgNodeRef, reader);
        vsbgNormNodeRef = MockUtils.createNormNodeRef(VSBG_NODE_NAME, vSBG, VSBG_OSS_MODEL_IDENTITY, vsbgNodeRef, reader);
        cscfNormNodeRef = MockUtils.createNormNodeRef(CSCF_NODE_NAME, CSCF, CSCF_OSS_MODEL_IDENTITY, cscfNodeRef, reader);
        vcscfNormNodeRef = MockUtils.createNormNodeRef(VCSCF_NODE_NAME, vCSCF, VCSCF_OSS_MODEL_IDENTITY, vcscfNodeRef, reader);
        mtasNormNodeRef = MockUtils.createNormNodeRef(MTAS_NODE_NAME, MTAS, MTAS_OSS_MODEL_IDENTITY, mtasNodeRef, reader);
        vmtasNormNodeRef = MockUtils.createNormNodeRef(VMTAS_NODE_NAME, vMTAS, VMTAS_OSS_MODEL_IDENTITY, vmtasNodeRef, reader);
        msrbsv1NormNodeRef = MockUtils.createNormNodeRef(MSRBSV1_NODE_NAME, MSRBS_V1, MSRBSV1_OSS_MODEL_IDENTITY, msrbsv1NodeRef, reader);
        sapcNormNodeRef = MockUtils.createNormNodeRef(SAPC_NODE_NAME, SAPC, SAPC_OSS_MODEL_IDENTITY, sapcNodeRef, reader);
        epgNormNodeRef = MockUtils.createNormNodeRef(EPG_NODE_NAME, EPG, EPG_OSS_MODEL_IDENTITY, epgNodeRef, reader);
        vepgNormNodeRef = MockUtils.createNormNodeRef(VEPG_NODE_NAME, VEPG, VEPG_OSS_MODEL_IDENTITY, vepgNodeRef, reader);
        radioNormNodeRef = MockUtils.createNormNodeRef(RADIO_NODE_NAME, RadioNode, RADIO_OSS_MODEL_IDENTITY, radioNodeRef, reader);
        radioTNodeNormNodeRef = MockUtils.createNormNodeRef(RADIOTNODE_NODE_NAME, RadioTNode, RADIOTNODE_OSS_MODEL_IDENTITY, radioTNodeRef, reader);
        er6672NormNodeRef = MockUtils.createNormNodeRef(ER6000_NODE_NAME, Router6672, ER6000_OSS_MODEL_IDENTITY, er6000NodeRef, reader);
        er6675NormNodeRef = MockUtils.createNormNodeRef(ER6000_NODE_NAME, Router6675, ER6000_OSS_MODEL_IDENTITY, er6000NodeRef, reader);
        er6x71NormNodeRef = MockUtils.createNormNodeRef(ER6000_NODE_NAME, Router6x71, ER6000_OSS_MODEL_IDENTITY, er6000NodeRef, reader);
        er6274NormNodeRef = MockUtils.createNormNodeRef(ER6000_NODE_NAME, Router6274, ER6000_OSS_MODEL_IDENTITY, er6000NodeRef, reader);
        miniLinkIndoorNormNodeRef = MockUtils.createNormNodeRefWithMeContext(MINI_LINK_INDOOR_NODE_NAME, MINI_LINK_Indoor, null,
                miniLinkIndoorNodeRef, reader);
        miniLinkcn210NormNodeRef = MockUtils.createNormNodeRefWithMeContext(MINI_LINK_CN210_NODE_NAME, MINI_LINK_CN210, null, miniLinkcn210NodeRef,
                reader);
        miniLinkcn510r1NormNodeRef = MockUtils.createNormNodeRefWithMeContext(MINI_LINK_CN510R1_NODE_NAME, MINI_LINK_CN510R1, null,
                miniLinkcn510r1NodeRef, reader);
        miniLinkcn510r2NormNodeRef = MockUtils.createNormNodeRefWithMeContext(MINI_LINK_CN510R2_NODE_NAME, MINI_LINK_CN510R2, null,
                miniLinkcn510r2NodeRef, reader);
        miniLinkcn810r1NormNodeRef = MockUtils.createNormNodeRefWithMeContext(MINI_LINK_CN810R1_NODE_NAME, MINI_LINK_CN810R1, null,
                miniLinkcn810r1NodeRef, reader);
        miniLinkcn810r2NormNodeRef = MockUtils.createNormNodeRefWithMeContext(MINI_LINK_CN810R2_NODE_NAME, MINI_LINK_CN810R2, null,
                miniLinkcn810r2NodeRef, reader);
        miniLink665xNormNodeRef = MockUtils.createNormNodeRefWithMeContext(MINI_LINK_665x_NODE_NAME, MINI_LINK_665x, null, miniLink665xNodeRef,
                reader);
        miniLink669xNormNodeRef = MockUtils.createNormNodeRefWithMeContext(MINI_LINK_669x_NODE_NAME, MINI_LINK_669x, null, miniLink669xNodeRef,
                reader);
        miniLinkMW2NormNodeRef = MockUtils.createNormNodeRefWithMeContext(MINI_LINK_MW2_NODE_NAME, MINI_LINK_MW2, null, miniLinkMW2NodeRef,
                reader);
        miniLink6352NormNodeRef = MockUtils.createNormNodeRefWithMeContext(MINI_LINK_6352_NODE_NAME, MINI_LINK_6352, null, miniLink6352NodeRef,
                reader);
        miniLink6351NormNodeRef = MockUtils.createNormNodeRefWithMeContext(MINI_LINK_6351_NODE_NAME, MINI_LINK_6351, null, miniLink6351NodeRef,
                reader);
        miniLink6366NormNodeRef = MockUtils.createNormNodeRefWithMeContext(MINI_LINK_6366_NODE_NAME, MINI_LINK_6366, null, miniLink6366NodeRef,
                reader);
        miniLinkPT2020NormNodeRef = MockUtils.createNormNodeRefWithMeContext(MINI_LINK_PT2020_NODE_NAME, MINI_LINK_PT2020, null,
                miniLinkPT2020NodeRef, reader);
        switch6391NormNodeRef = MockUtils.createNormNodeRefWithMeContext(SWITCH_6391_NODE_NAME, Switch_6391, null, switch6391NodeRef, reader);
        fronthaul6392NormNodeRef = MockUtils.createNormNodeRefWithMeContext(FRONTHAUL_6392_NODE_NAME, Fronthaul_6392, null, fronthaul6392NodeRef,
                reader);
        fronthaul6080NormNodeRef = MockUtils.createNormNodeRefWithMeContext(FRONTHAUL_NODE_NAME, FRONTHAUL_6080, null, fronthaulNodeRef, reader);
        fronthaul6020NormNodeRef = MockUtils.createNormNodeRefWithMeContext(FRONTHAUL_6020_NODE_NAME, FRONTHAUL_6020, null, fronthaul6020NodeRef,
                reader);
        ciscoAsr9000NormNodeRef = MockUtils.createNormNodeRefWithMeContext(CISCO_ASR9000_NODE_NAME, CISCO_ASR9000, null, ciscoAsr9000NodeRef, reader);
        ciscoAsr900NormNodeRef = MockUtils.createNormNodeRefWithMeContext(CISCO_ASR900_NODE_NAME, CISCO_ASR900, null, ciscoAsr900NodeRef, reader);
        juniperMxNormNodeRef = MockUtils.createNormNodeRefWithMeContext(JUNIPER_MX_NODE_NAME, JUNIPER_MX, null, juniperMxNodeRef, reader);
        rnNodeNormNodeRef = MockUtils.createNormNodeRef(RNNODE_NODE_NAME, RnNode, RNNODE_OSS_MODEL_IDENTITY, rnNodeNodeRef, reader);
        fiveGRadioNodeNormNodeRef = MockUtils.createNormNodeRef(FIVE_G_RADIONODE_NAME, FIVEGRadioNode, FIVE_G_RADIONODE_OSS_MODEL_IDENTITY,
                fiveGRadioNodeNodeRef, reader);
        vtfRadioNodeNormNodeRef = MockUtils.createNormNodeRef(VTF_RADIONODE_NAME, VTFRadioNode, VTF_RADIONODE_OSS_MODEL_IDENTITY, vtfRadioNodeNodeRef,
                reader);
        vsdNormNodeRef = MockUtils.createNormNodeRef(VSD_NODE_NAME, "vSD", VSD_NODE_OSS_MODEL_IDENTITY, vsdNodeRef, reader);
        rVNFMNodeNormNodeRef = MockUtils.createNormNodeRef(RVNFM_NODE_NAME, RVNFM, RVNFM_NODE_OSS_MODEL_IDENTITY, rVNFMNodeRef, reader);
        vppNormNodeRef = MockUtils.createNormNodeRef(VPP_NODE_NAME, vPP, VPP_OSS_MODEL_IDENTITY, vppNodeRef, reader);
        vrsmNormNodeRef = MockUtils.createNormNodeRef(VRSM_NODE_NAME, vRSM, VRSM_OSS_MODEL_IDENTITY, vrsmNodeRef, reader);
        vrcNormNodeRef = MockUtils.createNormNodeRef(VRC_NODE_NAME, vRC, VRC_OSS_MODEL_IDENTITY, vrcNodeRef, reader);
        vEMENormNodeRef = MockUtils.createNormNodeRef(VEME_NODE_NAME, vEME, VEME_OSS_MODEL_IDENTITY, vEMENodeRef, reader);
        vWCGNormNodeRef = MockUtils.createNormNodeRef(VWCG_NODE_NAME, vWCG, VWCG_OSS_MODEL_IDENTITY, vWCGNodeRef, reader);
        hSSFENormNodeRef = MockUtils.createNormNodeRef(HSSFE_NODE_NAME, HSS_FE, HSSFE_OSS_MODEL_IDENTITY, hSSFENodeRef, reader);
        vhSSFENormNodeRef = MockUtils.createNormNodeRef(VHSSFE_NODE_NAME, VHSS_FE, VHSSFE_OSS_MODEL_IDENTITY, vhSSFENodeRef, reader);
        vIPWorksNormNodeRef = MockUtils.createNormNodeRef(VIPWORKS_NODE_NAME, vIPWorks, VIPWORKS_OSS_MODEL_IDENTITY, vIPWorksNodeRef, reader);
        vUPGNormNodeRef = MockUtils.createNormNodeRef(VUPG_NODE_NAME, vUPG, VUPG_OSS_MODEL_IDENTITY, vUPGNodeRef, reader);
        bSPNormNodeRef = MockUtils.createNormNodeRef(BSP_NODE_NAME, BSP, BSP_OSS_MODEL_IDENTITY, bSPNodeRef, reader);
        vBGFNormNodeRef = MockUtils.createNormNodeRef(VBGF_NODE_NAME, vBGF, VBGF_OSS_MODEL_IDENTITY, vBGFNodeRef, reader);
        vMRFNormNodeRef = MockUtils.createNormNodeRef(VMRF_NODE_NAME, vMRF, VMRF_OSS_MODEL_IDENTITY, vMRFNodeRef, reader);
        bscNormNodeRef = MockUtils.createNormNodeRef(BSC_NODE_NAME, BSC, BSC_OSS_MODEL_IDENTITY, bscNodeRef, reader);
        hlrfeNormNodeRef = MockUtils.createNormNodeRef(HLR_FE_NODE_NAME, HLR_FE, HLR_FE_OSS_MODEL_IDENTITY, hlrfeNodeRef, reader);
        vhlrfeNormNodeRef = MockUtils.createNormNodeRef(vHLR_FE_NODE_NAME, vHLR_FE, vHLR_FE_OSS_MODEL_IDENTITY, vhlrfeNodeRef, reader);
        hlrfebspNormNodeRef = MockUtils.createNormNodeRef(HLR_FE_BSP_NODE_NAME, HLR_FE_BSP, HLR_FE_BSP_OSS_MODEL_IDENTITY, hlrfebspNodeRef, reader);
        hlrfeisNormNodeRef = MockUtils.createNormNodeRef(HLR_FE_IS_NODE_NAME, HLR_FE_IS, HLR_FE_IS_OSS_MODEL_IDENTITY, hlrfeisNodeRef, reader);
        mscNormNodeRef = MockUtils.createNormNodeRef(MSC_NODE_NAME, MSC, MSC_OSS_MODEL_IDENTITY, mscNodeRef, reader);
        hlrNormNodeRef = MockUtils.createNormNodeRef(HLR_NODE_NAME, HLR, HLR_OSS_MODEL_IDENTITY, hlrNodeRef, reader);
        vDuNormNodeRef = MockUtils.createNormNodeRef(VDU_NODE_NAME, VDU, VDU_OSS_MODEL_IDENTITY, vDuNodeRef, reader);
        nullNeTypeVDuNormNodeRef = MockUtils.createNormNodeRef(VDU_NODE_NAME, null, null, vDuNodeRef, reader);

        final Set<Bean<?>> beans = new HashSet<Bean<?>>();
        beans.add(bean);
        when(beanManager.getBeans(any(Type.class), any(Annotation.class))).thenReturn(beans);
        when(beanManager.createCreationalContext(bean)).thenReturn(creationalContext);
        when(beanManager.getReference(Mockito.eq(bean), any(Type.class), Mockito.eq(creationalContext))).thenReturn(capabilityModel);

        enrollmentCAAuthorizationModes = new HashMap<String, String>();
        enrollmentCAAuthorizationModes.put(CertificateType.OAM.name(), "ENROLLMENT_ROOT_CA_FINGERPRINT");
    }

    @Test
    public void testSupportedTargetTypes() {
        final List<String> expectedSupported = Arrays.asList(CSCF, ERBS, SGSN_MME, RadioNode, MSRBS_V1, SAPC, EPG, Router6672, Router6675, Router6x71,
                Router6274, MINI_LINK_Indoor, MINI_LINK_CN210, MINI_LINK_CN510R1, MINI_LINK_CN510R2, MINI_LINK_CN810R1, MINI_LINK_CN810R2,
                MINI_LINK_665x, MINI_LINK_669x, MINI_LINK_MW2, MINI_LINK_6352, MINI_LINK_6351, MINI_LINK_6366, MINI_LINK_PT2020, Switch_6391, MGW, MTAS, RNC, RBS,
                RadioTNode, SBG, CISCO_ASR9000, CISCO_ASR900, JUNIPER_MX, FRONTHAUL_6080, FRONTHAUL_6020, Fronthaul_6392, VEPG, BSC, MSC, HLR, RnNode,
                vPP, vRC, vEME, vWCG, HSS_FE, VHSS_FE, vIPWorks, vUPG, BSP, vBGF, vMRF, FIVEGRadioNode, vMTAS, vSBG, vCSCF, VTFRadioNode, RVNFM,
                HLR_FE, vHLR_FE, HLR_FE_BSP, HLR_FE_IS, vRSM, ECM, vRM, vSD, GenericESA, vECE, VDU, UNKNOWN);
        final List<String> actualSupported = new ArrayList<String>();
        actualSupported.addAll(beanUnderTest.getTargetTypes(TargetTypeInformation.CATEGORY_NODE));
        assertNotNull(actualSupported);
        actualSupported.removeAll(expectedSupported);
        assertTrue(actualSupported.isEmpty());
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsSupportedNullNeType() {
        beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, null);
    }

    @Test
    public void testIsSupportedUnknownNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, UNKNOWN));
    }

    @Test
    public void testIsSupportedFIVEGRADIONODENeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, FIVEGRadioNode));
    }

    @Test
    public void testIsSupportedVTFRADIONODENeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, VTFRadioNode));
    }

    @Test
    public void testIsSupportedRVNFMNODENeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, RVNFM));
    }

    @Test
    public void testIsSupportedRNNODENeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, RnNode));
    }

    @Test
    public void testIsSupportedVPPNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, vPP));
    }

    @Test
    public void testIsSupportedVRSMNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, vRSM));
    }

    @Test
    public void testIsSupportedVRCNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, vRC));
    }

    @Test
    public void testIsSupportedVEMENeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, vEME));
    }

    @Test
    public void testIsSupportedVWCGNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, vWCG));
    }

    @Test
    public void testIsSupportedHSSFENeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, HSS_FE));
    }

    @Test
    public void testIsSupportedVHSSFENeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, VHSS_FE));
    }

    @Test
    public void testIsSupportedVIPWORKSNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, vIPWorks));
    }

    @Test
    public void testIsSupportedVUPGNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, vUPG));
    }

    @Test
    public void testIsSupportedBSPNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, BSP));
    }

    @Test
    public void testIsSupportedVBGFNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, vBGF));
    }

    @Test
    public void testIsSupportedVMRFNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, vMRF));
    }

    @Test
    public void testIsSupportedInvalidNeType() {
        assertFalse(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, "INVALID_NE_TYPE"));
    }

    @Test
    public void testIsSupportedEmptyNeType() {
        assertFalse(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, ""));
    }

    @Test
    public void testIsSupportedErbsNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, ERBS));
    }

    @Test
    public void testIsSupportedRNCNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, RNC));
    }

    @Test
    public void testIsSupportedRBSNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, RBS));
    }

    @Test
    public void testIsSupportedMgwNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, MGW));
    }

    @Test
    public void testIsSupportedSgsnNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, SGSN_MME));
    }

    @Test
    public void testIsSupportedSbgNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, SBG));
    }

    @Test
    public void testIsSupportedVSbgNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, vSBG));
    }

    @Test
    public void testIsSupportedCscfNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, CSCF));
    }

    @Test
    public void testIsSupportedVCscfNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, vCSCF));
    }

    @Test
    public void testIsSupportedMtasNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, MTAS));
    }

    @Test
    public void testIsSupportedVMtasNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, vMTAS));
    }

    @Test
    public void testIsSupportedMSRBSV1NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, MSRBS_V1));
    }

    @Test
    public void testIsSupportedSAPCNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, SAPC));
    }

    @Test
    public void testIsSupportedEPGNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, EPG));
    }

    @Test
    public void testIsSupportedVEPGNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, VEPG));
    }

    @Test
    public void testIsSupportedRadioNodeNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, RadioNode));
    }

    @Test
    public void testIsSupportedRadioTNodeNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, RadioTNode));
    }

    @Test
    public void testIsSupportedEr6000NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, Router6672));
    }

    @Test
    public void testIsSupportedEr6675NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, Router6675));
    }

    @Test
    public void testIsSupportedEr6x71NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, Router6x71));
    }

    @Test
    public void testIsSupportedEr6274NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, Router6274));
    }

    @Test
    public void testIsSupportedMiniLinkIndoorNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, MINI_LINK_Indoor));
    }

    @Test
    public void testIsSupportedMiniLinkCn210NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, MINI_LINK_CN210));
    }

    @Test
    public void testIsSupportedMiniLinkCn510R1NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, MINI_LINK_CN510R1));
    }

    @Test
    public void testIsSupportedMiniLinkCn510R2NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, MINI_LINK_CN510R2));
    }

    @Test
    public void testIsSupportedMiniLinkCn810R1NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, MINI_LINK_CN810R1));
    }

    @Test
    public void testIsSupportedMiniLinkCn810R2NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, MINI_LINK_CN810R2));
    }

    @Test
    public void testIsSupportedFronthaul6080NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, FRONTHAUL_6080));
    }

    @Test
    public void testIsSupportedFronthaul6020NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, FRONTHAUL_6020));
    }

    @Test
    public void testIsSupportedMiniLink6352NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, MINI_LINK_6352));
    }

    @Test
    public void testIsSupportedMiniLink6351NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, MINI_LINK_6351));
    }

    @Test
    public void testIsSupportedMiniLink6366NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, MINI_LINK_6366));
    }

    @Test
    public void testIsSupportedMiniLinkPT2020NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, MINI_LINK_PT2020));
    }

    @Test
    public void testIsSupportedSwitch6391NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, Switch_6391));
    }

    @Test
    public void testIsSupportedFronthaul6392NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, Fronthaul_6392));
    }

    public void testIsSupportedCiscoAsr9000NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, CISCO_ASR9000));
    }

    @Test
    public void testIsSupportedCiscoAsr900NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, CISCO_ASR900));
    }

    @Test
    public void testIsSupportedJuniperMxNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, JUNIPER_MX));
    }

    @Test
    public void testIsSupportedBscNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, BSC));
    }

    @Test
    public void testIsSupportedHlrfeNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, HLR_FE));
    }

    @Test
    public void testIsSupportedvHlrfeNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, vHLR_FE));
    }

    @Test
    public void testIsSupportedHlrfebspNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, HLR_FE_BSP));
    }

    @Test
    public void testIsSupportedHlrfeisNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, HLR_FE_IS));
    }

    @Test
    public void testIsSupportedMscNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, MSC));
    }

    @Test
    public void testIsSupportedHlrNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, HLR));
    }

    @Test
    public void testIsSupportedminilink665xNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, MINI_LINK_665x));
    }

    @Test
    public void testIsSupportedminilink669xNeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, MINI_LINK_669x));
    }

    @Test
    public void testIsSupportedminilinkMW2NeType() {
        assertTrue(beanUnderTest.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, MINI_LINK_MW2));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCliCommandSupportedWithNullNodeRef() {
        final NormalizableNodeReference normNodeRef = null;
        beanUnderTest.isCliCommandSupported(normNodeRef, "any");
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCliCommandSupportedWithNodeRefAndNullCommand() {
        assertTrue(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, null));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCliCommandSupportedWithNodeRefAndEmptyCommand() {
        assertTrue(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, ""));
    }

    @Test
    public void testIsCliCommandSupportedWithUnknownNeType() {
        assertTrue(beanUnderTest.isCliCommandSupported(unknownNormNodeRef, "any"));
    }

    @Test
    public void testIsNewCommandSupportedByDefault() {
        assertTrue(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, "new command"));
    }

    @Test
    public void testIsCommandSupportedForErbs() {
        for (final String targetModelIdentity : erbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(erbsNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, SSHKEY_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, SNMP_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, SECURITYLEVEL_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, LDAP_COMMAND));
            if (erbsTargetModelIdentitiesLesserThan17B.contains(targetModelIdentity)) {
                assertFalse(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, CRLCHECK_COMMAND));
                assertFalse(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, CRLDOWNLOAD_COMMAND));
                assertFalse(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, CIPHERS_COMMAND));
            } else {
                assertTrue(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, CRLCHECK_COMMAND));
                assertTrue(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, CRLDOWNLOAD_COMMAND));
                assertTrue(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, CIPHERS_COMMAND));
            }
            assertTrue(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, RTSEL_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(erbsNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForRnc() {
        for (final String targetModelIdentity : rncTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rncNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(rncNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(rncNormNodeRef, SSHKEY_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(rncNormNodeRef, SNMP_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(rncNormNodeRef, SECURITYLEVEL_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(rncNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(rncNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(rncNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(rncNormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(rncNormNodeRef, LDAP_COMMAND));
            if (rncTargetModelIdentitiesLesserThan17B.contains(targetModelIdentity)) {
                assertFalse(beanUnderTest.isCliCommandSupported(rncNormNodeRef, CRLCHECK_COMMAND));
                assertFalse(beanUnderTest.isCliCommandSupported(rncNormNodeRef, CRLDOWNLOAD_COMMAND));
                assertFalse(beanUnderTest.isCliCommandSupported(rncNormNodeRef, CIPHERS_COMMAND));
            } else {
                assertTrue(beanUnderTest.isCliCommandSupported(rncNormNodeRef, CRLCHECK_COMMAND));
                assertTrue(beanUnderTest.isCliCommandSupported(rncNormNodeRef, CRLDOWNLOAD_COMMAND));
                assertTrue(beanUnderTest.isCliCommandSupported(rncNormNodeRef, CIPHERS_COMMAND));
            }
            assertFalse(beanUnderTest.isCliCommandSupported(rncNormNodeRef, RTSEL_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(rncNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(rncNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForRbs() {
        for (final String targetModelIdentity : rbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rbsNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(rbsNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(rbsNormNodeRef, SSHKEY_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(rbsNormNodeRef, SNMP_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(rbsNormNodeRef, SECURITYLEVEL_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(rbsNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(rbsNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(rbsNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(rbsNormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(rbsNormNodeRef, LDAP_COMMAND));
            if (rbsTargetModelIdentitiesLesserThan17B.contains(targetModelIdentity)) {
                assertFalse(beanUnderTest.isCliCommandSupported(rbsNormNodeRef, CRLCHECK_COMMAND));
                assertFalse(beanUnderTest.isCliCommandSupported(rbsNormNodeRef, CRLDOWNLOAD_COMMAND));
                assertFalse(beanUnderTest.isCliCommandSupported(rbsNormNodeRef, CIPHERS_COMMAND));
            } else {
                assertTrue(beanUnderTest.isCliCommandSupported(rbsNormNodeRef, CRLCHECK_COMMAND));
                assertTrue(beanUnderTest.isCliCommandSupported(rbsNormNodeRef, CRLDOWNLOAD_COMMAND));
                assertTrue(beanUnderTest.isCliCommandSupported(rbsNormNodeRef, CIPHERS_COMMAND));
            }
            assertFalse(beanUnderTest.isCliCommandSupported(rbsNormNodeRef, RTSEL_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(rbsNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(rbsNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForMgw() {
        for (final String targetModelIdentity : mgwTargetModelIdentities) {
            doReturn(targetModelIdentity).when(mgwNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(mgwNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(mgwNormNodeRef, SSHKEY_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(mgwNormNodeRef, SNMP_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(mgwNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(mgwNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(mgwNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(mgwNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(mgwNormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(mgwNormNodeRef, LDAP_COMMAND));
            if (mgwTargetModelIdentitiesLesserThan17B.contains(targetModelIdentity)) {
                assertFalse(beanUnderTest.isCliCommandSupported(mgwNormNodeRef, CRLCHECK_COMMAND));
                assertFalse(beanUnderTest.isCliCommandSupported(mgwNormNodeRef, CRLDOWNLOAD_COMMAND));
                assertFalse(beanUnderTest.isCliCommandSupported(mgwNormNodeRef, CIPHERS_COMMAND));
            } else {
                assertTrue(beanUnderTest.isCliCommandSupported(mgwNormNodeRef, CRLCHECK_COMMAND));
                assertTrue(beanUnderTest.isCliCommandSupported(mgwNormNodeRef, CRLDOWNLOAD_COMMAND));
                assertTrue(beanUnderTest.isCliCommandSupported(mgwNormNodeRef, CIPHERS_COMMAND));
            }
            assertTrue(beanUnderTest.isCliCommandSupported(mgwNormNodeRef, RTSEL_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(mgwNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(mgwNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForSgsn() {
        for (final String targetModelIdentity : sgsnTargetModelIdentities) {
            doReturn(targetModelIdentity).when(sgsnNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(sgsnNormNodeRef, CREDENTIALS_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(sgsnNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(sgsnNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sgsnNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sgsnNormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sgsnNormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sgsnNormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sgsnNormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sgsnNormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sgsnNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sgsnNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sgsnNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sgsnNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sgsnNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sgsnNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForSbg() {
        for (final String targetModelIdentity : sbgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(sbgNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(sbgNormNodeRef, CREDENTIALS_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(sbgNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(sbgNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sbgNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sbgNormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sbgNormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sbgNormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sbgNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(sbgNormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sbgNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sbgNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sbgNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sbgNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sbgNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sbgNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForVSbg() {
        for (final String targetModelIdentity : vsbgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vsbgNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(vsbgNormNodeRef, CREDENTIALS_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vsbgNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vsbgNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vsbgNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vsbgNormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vsbgNormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vsbgNormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vsbgNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vsbgNormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vsbgNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vsbgNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vsbgNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vsbgNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vsbgNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vsbgNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForCscf() {
        for (final String targetModelIdentity : cscfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(cscfNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(cscfNormNodeRef, CREDENTIALS_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(cscfNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(cscfNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(cscfNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(cscfNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(cscfNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(cscfNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(cscfNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(cscfNormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(cscfNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(cscfNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(cscfNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(cscfNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(cscfNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(cscfNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForVCscf() {
        for (final String targetModelIdentity : vcscfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vcscfNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(vcscfNormNodeRef, CREDENTIALS_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vcscfNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vcscfNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vcscfNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vcscfNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vcscfNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vcscfNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vcscfNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vcscfNormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vcscfNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vcscfNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vcscfNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vcscfNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vcscfNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vcscfNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForMtas() {
        for (final String targetModelIdentity : mtasTargetModelIdentities) {
            doReturn(targetModelIdentity).when(mtasNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(mtasNormNodeRef, CREDENTIALS_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(mtasNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(mtasNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(mtasNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(mtasNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(mtasNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(mtasNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(mtasNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(mtasNormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(mtasNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(mtasNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(mtasNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(mtasNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(mtasNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(mtasNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForVMtas() {
        for (final String targetModelIdentity : vmtasTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vmtasNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(vmtasNormNodeRef, CREDENTIALS_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vmtasNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vmtasNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vmtasNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vmtasNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vmtasNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vmtasNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vmtasNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vmtasNormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vmtasNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vmtasNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vmtasNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vmtasNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vmtasNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vmtasNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForMsrbsv1() {
        for (final String targetModelIdentity : msrbsv1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(msrbsv1NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(msrbsv1NormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(msrbsv1NormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(msrbsv1NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(msrbsv1NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(msrbsv1NormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(msrbsv1NormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(msrbsv1NormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(msrbsv1NormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(msrbsv1NormNodeRef, LDAP_COMMAND));
            if (msrbsv1TargetModelIdentitiesLesserThan16B.contains(targetModelIdentity)) {
                assertFalse(beanUnderTest.isCliCommandSupported(msrbsv1NormNodeRef, CRLCHECK_COMMAND));
            } else {
                assertTrue(beanUnderTest.isCliCommandSupported(msrbsv1NormNodeRef, CRLCHECK_COMMAND));
            }
            assertFalse(beanUnderTest.isCliCommandSupported(msrbsv1NormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(msrbsv1NormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(msrbsv1NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(msrbsv1NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(msrbsv1NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForSapc() {
        for (final String targetModelIdentity : sapcTargetModelIdentities) {
            doReturn(targetModelIdentity).when(sapcNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(sapcNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sapcNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(sapcNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sapcNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sapcNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(sapcNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(sapcNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(sapcNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(sapcNormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sapcNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sapcNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sapcNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sapcNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sapcNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(sapcNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForEpg() {
        for (final String targetModelIdentity : epgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(epgNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(epgNormNodeRef, CREDENTIALS_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(epgNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(epgNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(epgNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(epgNormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(epgNormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(epgNormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(epgNormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(epgNormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(epgNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(epgNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(epgNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(epgNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(epgNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(epgNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForRadioNode() {
        for (final String targetModelIdentity : radioNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(radioNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(radioNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(radioNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(radioNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(radioNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(radioNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(radioNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(radioNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(radioNormNodeRef, LDAP_COMMAND));
            if (radioNodeTargetModelIdentitiesLesserThan16B.contains(targetModelIdentity)) {
                assertFalse(beanUnderTest.isCliCommandSupported(radioNormNodeRef, CRLCHECK_COMMAND));
                assertFalse(beanUnderTest.isCliCommandSupported(radioNormNodeRef, CRLDOWNLOAD_COMMAND));
                assertFalse(beanUnderTest.isCliCommandSupported(radioNormNodeRef, CIPHERS_COMMAND));
            } else if (radioNodeTargetModelIdentitiesLesserThan17A.contains(targetModelIdentity)) {
                assertTrue(beanUnderTest.isCliCommandSupported(radioNormNodeRef, CRLCHECK_COMMAND));
                assertTrue(beanUnderTest.isCliCommandSupported(radioNormNodeRef, CRLDOWNLOAD_COMMAND));
                assertFalse(beanUnderTest.isCliCommandSupported(radioNormNodeRef, CIPHERS_COMMAND));
            } else {
                assertTrue(beanUnderTest.isCliCommandSupported(radioNormNodeRef, CRLCHECK_COMMAND));
                assertTrue(beanUnderTest.isCliCommandSupported(radioNormNodeRef, CRLDOWNLOAD_COMMAND));
                assertTrue(beanUnderTest.isCliCommandSupported(radioNormNodeRef, CIPHERS_COMMAND));
            }
            assertFalse(beanUnderTest.isCliCommandSupported(radioNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(radioNormNodeRef, HTTPS_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(radioNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForEr6672() {
        for (final String targetModelIdentity : er6672TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6672NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(er6672NormNodeRef, CREDENTIALS_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6672NormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6672NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(er6672NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(er6672NormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6672NormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6672NormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6672NormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6672NormNodeRef, LDAP_COMMAND));
            if (er6672TargetModelIdentitiesLesserThan17B.contains(targetModelIdentity)) {
                assertFalse(beanUnderTest.isCliCommandSupported(er6672NormNodeRef, CRLCHECK_COMMAND));
                assertFalse(beanUnderTest.isCliCommandSupported(er6672NormNodeRef, CRLDOWNLOAD_COMMAND));
                assertFalse(beanUnderTest.isCliCommandSupported(er6672NormNodeRef, CIPHERS_COMMAND));
            } else {
                assertTrue(beanUnderTest.isCliCommandSupported(er6672NormNodeRef, CRLCHECK_COMMAND));
                assertTrue(beanUnderTest.isCliCommandSupported(er6672NormNodeRef, CRLDOWNLOAD_COMMAND));
                assertTrue(beanUnderTest.isCliCommandSupported(er6672NormNodeRef, CIPHERS_COMMAND));
            }
            assertFalse(beanUnderTest.isCliCommandSupported(er6672NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(er6672NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(er6672NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForEr6675() {
        for (final String targetModelIdentity : er6675TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6675NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(er6675NormNodeRef, CREDENTIALS_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6675NormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6675NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(er6675NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(er6675NormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6675NormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6675NormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6675NormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6675NormNodeRef, LDAP_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6675NormNodeRef, CRLCHECK_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6675NormNodeRef, CRLDOWNLOAD_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6675NormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(er6675NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(er6675NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(er6675NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForEr6x71() {
        for (final String targetModelIdentity : er6x71TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6x71NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(er6x71NormNodeRef, CREDENTIALS_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6x71NormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6x71NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(er6x71NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(er6x71NormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6x71NormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6x71NormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6x71NormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6x71NormNodeRef, LDAP_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6x71NormNodeRef, CRLCHECK_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6x71NormNodeRef, CRLDOWNLOAD_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6x71NormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(er6x71NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(er6x71NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(er6x71NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForEr6274() {
        for (final String targetModelIdentity : er6274TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6274NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(er6274NormNodeRef, CREDENTIALS_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6274NormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6274NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(er6274NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(er6274NormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6274NormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6274NormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6274NormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6274NormNodeRef, LDAP_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6274NormNodeRef, CRLCHECK_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6274NormNodeRef, CRLDOWNLOAD_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(er6274NormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(er6274NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(er6274NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(er6274NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForMiniLinkIndoor() {
        for (final String targetModelIdentity : miniLinkIndoorTargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkIndoorNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(miniLinkIndoorNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkIndoorNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(miniLinkIndoorNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkIndoorNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkIndoorNormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkIndoorNormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkIndoorNormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkIndoorNormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkIndoorNormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkIndoorNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkIndoorNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkIndoorNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkIndoorNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkIndoorNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkIndoorNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForMiniLinkCn210() {
        for (final String targetModelIdentity : miniLinkCn210TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn210NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(miniLinkcn210NormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn210NormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(miniLinkcn210NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn210NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn210NormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn210NormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn210NormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn210NormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn210NormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn210NormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn210NormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn210NormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn210NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn210NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn210NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForMiniLinkCn510R1() {
        for (final String targetModelIdentity : miniLinkCn510R1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn510r1NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(miniLinkcn510r1NormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r1NormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(miniLinkcn510r1NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r1NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r1NormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r1NormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r1NormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r1NormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r1NormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r1NormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r1NormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r1NormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r1NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r1NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r1NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForMiniLinkCn510R2() {
        for (final String targetModelIdentity : miniLinkCn510R2TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn510r2NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(miniLinkcn510r2NormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r2NormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(miniLinkcn510r2NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r2NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r2NormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r2NormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r2NormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r2NormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r2NormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r2NormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r2NormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r2NormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r2NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r2NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn510r2NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForMiniLinkCn810R1() {
        for (final String targetModelIdentity : miniLinkCn810R1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn810r1NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(miniLinkcn810r1NormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r1NormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(miniLinkcn810r1NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r1NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r1NormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r1NormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r1NormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r1NormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r1NormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r1NormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r1NormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r1NormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r1NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r1NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r1NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForMiniLinkCn810R2() {
        for (final String targetModelIdentity : miniLinkCn810R2TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn810r2NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(miniLinkcn810r2NormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r2NormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(miniLinkcn810r2NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r2NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r2NormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r2NormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r2NormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r2NormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r2NormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r2NormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r2NormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r2NormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r2NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r2NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkcn810r2NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForMiniLink665x() {
        for (final String targetModelIdentity : miniLink665xTargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink665xNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(miniLink665xNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink665xNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(miniLink665xNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink665xNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink665xNormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink665xNormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink665xNormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink665xNormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink665xNormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink665xNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink665xNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink665xNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink665xNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink665xNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink665xNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForMiniLink669x() {
        for (final String targetModelIdentity : miniLink669xTargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink669xNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(miniLink669xNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink669xNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(miniLink669xNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink669xNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink669xNormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink669xNormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink669xNormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink669xNormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink669xNormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink669xNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink669xNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink669xNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink669xNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink669xNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink669xNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForMiniLinkMW2() {
        for (final String targetModelIdentity : miniLinkMW2TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkMW2NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(miniLinkMW2NormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkMW2NormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(miniLinkMW2NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkMW2NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkMW2NormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkMW2NormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkMW2NormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkMW2NormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkMW2NormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkMW2NormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkMW2NormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkMW2NormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkMW2NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkMW2NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkMW2NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForMiniLink6352() {
        for (final String targetModelIdentity : miniLink6352TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink6352NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(miniLink6352NormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6352NormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(miniLink6352NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6352NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6352NormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6352NormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6352NormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6352NormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6352NormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6352NormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6352NormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6352NormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6352NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6352NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6352NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForMiniLink6351() {
        for (final String targetModelIdentity : miniLink6351TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink6351NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(miniLink6351NormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6351NormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(miniLink6351NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6351NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6351NormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6351NormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6351NormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6351NormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6351NormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6351NormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6351NormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6351NormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6351NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6351NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLink6351NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForMiniLink6366() {
        assertTrue(beanUnderTest.isCliCommandSupported(miniLink6366NormNodeRef, CREDENTIALS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(miniLink6366NormNodeRef, SSHKEY_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(miniLink6366NormNodeRef, SNMP_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(miniLink6366NormNodeRef, SECURITYLEVEL_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(miniLink6366NormNodeRef, IPSEC_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(miniLink6366NormNodeRef, ENROLLMENT_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(miniLink6366NormNodeRef, CERTIFICATE_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(miniLink6366NormNodeRef, TRUST_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(miniLink6366NormNodeRef, LDAP_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(miniLink6366NormNodeRef, CRLCHECK_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(miniLink6366NormNodeRef, CRLDOWNLOAD_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(miniLink6366NormNodeRef, CIPHERS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(miniLink6366NormNodeRef, RTSEL_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(miniLink6366NormNodeRef, HTTPS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(miniLink6366NormNodeRef, FTPES_COMMAND));
    }

    @Test
    public void testIsCommandSupportedForMiniLinkPt2020() {
        for (final String targetModelIdentity : miniLinkPt2020TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkPT2020NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(miniLinkPT2020NormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkPT2020NormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(miniLinkPT2020NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkPT2020NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkPT2020NormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkPT2020NormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkPT2020NormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkPT2020NormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkPT2020NormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkPT2020NormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkPT2020NormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkPT2020NormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkPT2020NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkPT2020NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(miniLinkPT2020NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForSwitch6391() {
        for (final String targetModelIdentity : switch6391TargetModelIdentities) {
            doReturn(targetModelIdentity).when(switch6391NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(switch6391NormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(switch6391NormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(switch6391NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(switch6391NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(switch6391NormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(switch6391NormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(switch6391NormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(switch6391NormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(switch6391NormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(switch6391NormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(switch6391NormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(switch6391NormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(switch6391NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(switch6391NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(switch6391NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForCiscoAsr9000() {
        for (final String targetModelIdentity : ciscoAsr9000TargetModelIdentities) {
            doReturn(targetModelIdentity).when(ciscoAsr9000NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(ciscoAsr9000NormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr9000NormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(ciscoAsr9000NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr9000NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr9000NormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr9000NormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr9000NormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr9000NormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr9000NormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr9000NormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr9000NormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr9000NormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr9000NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr9000NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr9000NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForCiscoAsr900() {
        for (final String targetModelIdentity : ciscoAsr900TargetModelIdentities) {
            doReturn(targetModelIdentity).when(ciscoAsr900NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(ciscoAsr900NormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr900NormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(ciscoAsr900NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr900NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr900NormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr900NormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr900NormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr900NormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr900NormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr900NormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr900NormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr900NormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr900NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr900NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(ciscoAsr900NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForJuniperMx() {
        for (final String targetModelIdentity : juniperMxTargetModelIdentities) {
            doReturn(targetModelIdentity).when(juniperMxNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(juniperMxNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(juniperMxNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(juniperMxNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(juniperMxNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(juniperMxNormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(juniperMxNormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(juniperMxNormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(juniperMxNormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(juniperMxNormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(juniperMxNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(juniperMxNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(juniperMxNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(juniperMxNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(juniperMxNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(juniperMxNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForRadioTNode() {
        for (final String targetModelIdentity : radioTNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioTNodeNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(radioTNodeNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(radioTNodeNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(radioTNodeNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(radioTNodeNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(radioTNodeNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(radioTNodeNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(radioTNodeNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(radioTNodeNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(radioTNodeNormNodeRef, LDAP_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(radioTNodeNormNodeRef, CRLCHECK_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(radioTNodeNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(radioTNodeNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(radioTNodeNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(radioTNodeNormNodeRef, HTTPS_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(radioTNodeNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForBsp() {
        for (final String targetModelIdentity : bspTargetModelIdentities) {
            doReturn(targetModelIdentity).when(bSPNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(bSPNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(bSPNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(bSPNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(bSPNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(bSPNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(bSPNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(bSPNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(bSPNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(bSPNormNodeRef, LDAP_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(bSPNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(bSPNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(bSPNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(bSPNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(bSPNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(bSPNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForVeme() {
        for (final String targetModelIdentity : vemeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vEMENormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(vEMENormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vEMENormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vEMENormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vEMENormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vEMENormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vEMENormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vEMENormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vEMENormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vEMENormNodeRef, LDAP_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vEMENormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vEMENormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vEMENormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vEMENormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vEMENormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vEMENormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForVwcg() {
        for (final String targetModelIdentity : vwcgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vWCGNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(vWCGNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vWCGNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vWCGNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vWCGNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vWCGNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vWCGNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vWCGNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vWCGNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vWCGNormNodeRef, LDAP_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vWCGNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vWCGNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vWCGNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vWCGNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vWCGNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vWCGNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForHssFe() {
        doReturn(null).when(hSSFENormNodeRef).getOssModelIdentity();
        assertTrue(beanUnderTest.isCliCommandSupported(hSSFENormNodeRef, CREDENTIALS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(hSSFENormNodeRef, SSHKEY_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(hSSFENormNodeRef, SNMP_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(hSSFENormNodeRef, SECURITYLEVEL_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(hSSFENormNodeRef, IPSEC_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(hSSFENormNodeRef, ENROLLMENT_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(hSSFENormNodeRef, CERTIFICATE_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(hSSFENormNodeRef, TRUST_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(hSSFENormNodeRef, LDAP_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(hSSFENormNodeRef, CRLCHECK_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(hSSFENormNodeRef, CRLDOWNLOAD_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(hSSFENormNodeRef, CIPHERS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(hSSFENormNodeRef, RTSEL_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(hSSFENormNodeRef, HTTPS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(hSSFENormNodeRef, FTPES_COMMAND));
    }

    @Test
    public void testIsCommandSupportedForVHssFe() {
        doReturn(null).when(vhSSFENormNodeRef).getOssModelIdentity();
        assertTrue(beanUnderTest.isCliCommandSupported(vhSSFENormNodeRef, CREDENTIALS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vhSSFENormNodeRef, SSHKEY_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(vhSSFENormNodeRef, SNMP_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vhSSFENormNodeRef, SECURITYLEVEL_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vhSSFENormNodeRef, IPSEC_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(vhSSFENormNodeRef, ENROLLMENT_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(vhSSFENormNodeRef, CERTIFICATE_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(vhSSFENormNodeRef, TRUST_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(vhSSFENormNodeRef, LDAP_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(vhSSFENormNodeRef, CRLCHECK_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vhSSFENormNodeRef, CRLDOWNLOAD_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vhSSFENormNodeRef, CIPHERS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vhSSFENormNodeRef, RTSEL_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vhSSFENormNodeRef, HTTPS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vhSSFENormNodeRef, FTPES_COMMAND));
    }

    @Test
    public void testIsCommandSupportedForVIPWorks() {
        for (final String targetModelIdentity : vipworksTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vIPWorksNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(vIPWorksNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vIPWorksNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vIPWorksNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vIPWorksNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vIPWorksNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vIPWorksNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vIPWorksNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vIPWorksNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vIPWorksNormNodeRef, LDAP_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vIPWorksNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vIPWorksNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vIPWorksNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vIPWorksNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vIPWorksNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vIPWorksNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForVbgf() {
        for (final String targetModelIdentity : vbgfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vBGFNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(vBGFNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vBGFNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vBGFNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vBGFNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vBGFNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vBGFNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vBGFNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vBGFNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vBGFNormNodeRef, LDAP_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vBGFNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vBGFNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vBGFNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vBGFNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vBGFNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vBGFNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForVmrf() {
        for (final String targetModelIdentity : vmrfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vMRFNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(vMRFNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vMRFNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vMRFNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vMRFNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vMRFNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vMRFNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vMRFNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vMRFNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vMRFNormNodeRef, LDAP_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vMRFNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vMRFNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vMRFNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vMRFNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vMRFNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vMRFNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForVupg() {
        for (final String targetModelIdentity : vupgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vUPGNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(vUPGNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vUPGNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vUPGNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vUPGNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vUPGNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vUPGNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vUPGNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vUPGNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vUPGNormNodeRef, LDAP_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vUPGNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vUPGNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vUPGNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vUPGNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vUPGNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vUPGNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForVpp() {
        for (final String targetModelIdentity : vppTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vppNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(vppNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vppNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vppNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vppNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vppNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vppNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vppNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vppNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vppNormNodeRef, LDAP_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vppNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vppNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vppNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vppNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vppNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vppNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForFronthaul6080() {
        for (final String targetModelIdentity : fronthaul6080TargetModelIdentities) {
            doReturn(targetModelIdentity).when(fronthaul6080NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(fronthaul6080NormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6080NormNodeRef, SSHKEY_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6080NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6080NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6080NormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6080NormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6080NormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6080NormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6080NormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6080NormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6080NormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6080NormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6080NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6080NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6080NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForFronthaul6020() {
        for (final String targetModelIdentity : fronthaul6020TargetModelIdentities) {
            doReturn(targetModelIdentity).when(fronthaul6020NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(fronthaul6020NormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6020NormNodeRef, SSHKEY_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6020NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6020NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6020NormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6020NormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6020NormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6020NormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6020NormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6020NormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6020NormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6020NormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6020NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6020NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6020NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForFronthaul6392() {
        for (final String targetModelIdentity : fronthaul6392TargetModelIdentities) {
            doReturn(targetModelIdentity).when(fronthaul6392NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(fronthaul6392NormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6392NormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(fronthaul6392NormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6392NormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6392NormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6392NormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6392NormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6392NormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6392NormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6392NormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6392NormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6392NormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6392NormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6392NormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(fronthaul6392NormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForBsc() {
        for (final String targetModelIdentity : bscTargetModelIdentities) {
            doReturn(targetModelIdentity).when(bscNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(bscNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(bscNormNodeRef, SSHKEY_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(bscNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(bscNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(bscNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(bscNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(bscNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(bscNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(bscNormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(bscNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(bscNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(bscNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(bscNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(bscNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(bscNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForHlrfe() {
        for (final String targetModelIdentity : hlrfeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfeNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(hlrfeNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeNormNodeRef, SSHKEY_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(hlrfeNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(hlrfeNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(hlrfeNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(hlrfeNormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForHlr() {
        assertTrue(beanUnderTest.isCliCommandSupported(hlrNormNodeRef, CREDENTIALS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(hlrNormNodeRef, SSHKEY_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(hlrNormNodeRef, SNMP_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(hlrNormNodeRef, SECURITYLEVEL_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(hlrNormNodeRef, IPSEC_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(hlrNormNodeRef, ENROLLMENT_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(hlrNormNodeRef, CERTIFICATE_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(hlrNormNodeRef, TRUST_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(hlrNormNodeRef, LDAP_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(hlrNormNodeRef, CRLCHECK_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(hlrNormNodeRef, CRLDOWNLOAD_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(hlrNormNodeRef, CIPHERS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(hlrNormNodeRef, RTSEL_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(hlrNormNodeRef, HTTPS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(hlrNormNodeRef, FTPES_COMMAND));
    }

    @Test
    public void testIsCommandSupportedForVHlrFe() {
        for (final String targetModelIdentity : vhlrfeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vhlrfeNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(vhlrfeNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vhlrfeNormNodeRef, SSHKEY_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vhlrfeNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vhlrfeNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vhlrfeNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vhlrfeNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vhlrfeNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vhlrfeNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vhlrfeNormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vhlrfeNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vhlrfeNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vhlrfeNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vhlrfeNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vhlrfeNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vhlrfeNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForHlrFeBsp() {
        for (final String targetModelIdentity : hlrfebspTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfebspNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(hlrfebspNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfebspNormNodeRef, SSHKEY_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfebspNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfebspNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfebspNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(hlrfebspNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(hlrfebspNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(hlrfebspNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(hlrfebspNormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfebspNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfebspNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfebspNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfebspNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfebspNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfebspNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForHlrFeIs() {
        for (final String targetModelIdentity : hlrfeisTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfeisNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(hlrfeisNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeisNormNodeRef, SSHKEY_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeisNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeisNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeisNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(hlrfeisNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(hlrfeisNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(hlrfeisNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(hlrfeisNormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeisNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeisNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeisNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeisNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeisNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(hlrfeisNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForMsc() {
        assertTrue(beanUnderTest.isCliCommandSupported(mscNormNodeRef, CREDENTIALS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(mscNormNodeRef, SSHKEY_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(mscNormNodeRef, SNMP_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(mscNormNodeRef, SECURITYLEVEL_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(mscNormNodeRef, IPSEC_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(mscNormNodeRef, ENROLLMENT_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(mscNormNodeRef, CERTIFICATE_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(mscNormNodeRef, TRUST_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(mscNormNodeRef, LDAP_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(mscNormNodeRef, CRLCHECK_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(mscNormNodeRef, CRLDOWNLOAD_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(mscNormNodeRef, CIPHERS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(mscNormNodeRef, RTSEL_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(mscNormNodeRef, HTTPS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(mscNormNodeRef, FTPES_COMMAND));
    }

    @Test
    public void testIsCommandSupportedForVepg() {
        for (final String targetModelIdentity : vepgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vepgNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(vepgNormNodeRef, CREDENTIALS_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vepgNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vepgNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vepgNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vepgNormNodeRef, IPSEC_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vepgNormNodeRef, ENROLLMENT_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vepgNormNodeRef, CERTIFICATE_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vepgNormNodeRef, TRUST_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vepgNormNodeRef, LDAP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vepgNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vepgNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vepgNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vepgNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vepgNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vepgNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForVTFRadioNode() {
        for (final String targetModelIdentity : vtfRadioNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vtfRadioNodeNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(vtfRadioNodeNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vtfRadioNodeNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vtfRadioNodeNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vtfRadioNodeNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vtfRadioNodeNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vtfRadioNodeNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vtfRadioNodeNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vtfRadioNodeNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vtfRadioNodeNormNodeRef, LDAP_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vtfRadioNodeNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vtfRadioNodeNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vtfRadioNodeNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vtfRadioNodeNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vtfRadioNodeNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vtfRadioNodeNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForVsd() {
        for (final String targetModelIdentity : vsdTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vsdNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(vsdNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vsdNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vsdNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vsdNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vsdNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vsdNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vsdNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vsdNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vsdNormNodeRef, LDAP_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(vsdNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vsdNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vsdNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vsdNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vsdNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(vsdNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForRvnfm() {
        for (final String targetModelIdentity : rvnfmTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rVNFMNodeNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCliCommandSupported(rVNFMNodeNormNodeRef, CREDENTIALS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(rVNFMNodeNormNodeRef, SSHKEY_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(rVNFMNodeNormNodeRef, SNMP_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(rVNFMNodeNormNodeRef, SECURITYLEVEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(rVNFMNodeNormNodeRef, IPSEC_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(rVNFMNodeNormNodeRef, ENROLLMENT_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(rVNFMNodeNormNodeRef, CERTIFICATE_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(rVNFMNodeNormNodeRef, TRUST_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(rVNFMNodeNormNodeRef, LDAP_COMMAND));
            assertTrue(beanUnderTest.isCliCommandSupported(rVNFMNodeNormNodeRef, CRLCHECK_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(rVNFMNodeNormNodeRef, CRLDOWNLOAD_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(rVNFMNodeNormNodeRef, CIPHERS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(rVNFMNodeNormNodeRef, RTSEL_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(rVNFMNodeNormNodeRef, HTTPS_COMMAND));
            assertFalse(beanUnderTest.isCliCommandSupported(rVNFMNodeNormNodeRef, FTPES_COMMAND));
        }
    }

    @Test
    public void testIsCommandSupportedForVrsm() {
        assertTrue(beanUnderTest.isCliCommandSupported(vrsmNormNodeRef, CREDENTIALS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vrsmNormNodeRef, SSHKEY_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(vrsmNormNodeRef, SNMP_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vrsmNormNodeRef, SECURITYLEVEL_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vrsmNormNodeRef, IPSEC_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(vrsmNormNodeRef, ENROLLMENT_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(vrsmNormNodeRef, CERTIFICATE_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(vrsmNormNodeRef, TRUST_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(vrsmNormNodeRef, LDAP_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(vrsmNormNodeRef, CRLCHECK_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vrsmNormNodeRef, CRLDOWNLOAD_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vrsmNormNodeRef, CIPHERS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vrsmNormNodeRef, RTSEL_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vrsmNormNodeRef, HTTPS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vrsmNormNodeRef, FTPES_COMMAND));
    }

    @Test
    public void testIsCommandSupportedForFiveGRadioNode() {
        assertTrue(beanUnderTest.isCliCommandSupported(fiveGRadioNodeNormNodeRef, CREDENTIALS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(fiveGRadioNodeNormNodeRef, SSHKEY_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(fiveGRadioNodeNormNodeRef, SNMP_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(fiveGRadioNodeNormNodeRef, SECURITYLEVEL_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(fiveGRadioNodeNormNodeRef, IPSEC_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(fiveGRadioNodeNormNodeRef, ENROLLMENT_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(fiveGRadioNodeNormNodeRef, CERTIFICATE_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(fiveGRadioNodeNormNodeRef, TRUST_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(fiveGRadioNodeNormNodeRef, LDAP_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(fiveGRadioNodeNormNodeRef, CRLCHECK_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(fiveGRadioNodeNormNodeRef, CRLDOWNLOAD_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(fiveGRadioNodeNormNodeRef, CIPHERS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(fiveGRadioNodeNormNodeRef, RTSEL_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(fiveGRadioNodeNormNodeRef, HTTPS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(fiveGRadioNodeNormNodeRef, FTPES_COMMAND));
    }

    @Test
    public void testIsCommandSupportedForRnNode() {
        assertTrue(beanUnderTest.isCliCommandSupported(rnNodeNormNodeRef, CREDENTIALS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(rnNodeNormNodeRef, SSHKEY_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(rnNodeNormNodeRef, SNMP_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(rnNodeNormNodeRef, SECURITYLEVEL_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(rnNodeNormNodeRef, IPSEC_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(rnNodeNormNodeRef, ENROLLMENT_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(rnNodeNormNodeRef, CERTIFICATE_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(rnNodeNormNodeRef, TRUST_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(rnNodeNormNodeRef, LDAP_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(rnNodeNormNodeRef, CRLCHECK_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(rnNodeNormNodeRef, CRLDOWNLOAD_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(rnNodeNormNodeRef, CIPHERS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(rnNodeNormNodeRef, RTSEL_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(rnNodeNormNodeRef, HTTPS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(rnNodeNormNodeRef, FTPES_COMMAND));
    }

    @Test
    public void testIsCommandSupportedForVrc() {
        assertTrue(beanUnderTest.isCliCommandSupported(vrcNormNodeRef, CREDENTIALS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vrcNormNodeRef, SSHKEY_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(vrcNormNodeRef, SNMP_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vrcNormNodeRef, SECURITYLEVEL_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vrcNormNodeRef, IPSEC_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(vrcNormNodeRef, ENROLLMENT_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(vrcNormNodeRef, CERTIFICATE_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(vrcNormNodeRef, TRUST_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(vrcNormNodeRef, LDAP_COMMAND));
        assertTrue(beanUnderTest.isCliCommandSupported(vrcNormNodeRef, CRLCHECK_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vrcNormNodeRef, CRLDOWNLOAD_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vrcNormNodeRef, CIPHERS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vrcNormNodeRef, RTSEL_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vrcNormNodeRef, HTTPS_COMMAND));
        assertFalse(beanUnderTest.isCliCommandSupported(vrcNormNodeRef, FTPES_COMMAND));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsLdapCommonUserSupportedWithNullNodeRef() {
        final NormalizableNodeReference normNodeRef = null;
        beanUnderTest.isLdapCommonUserSupported(normNodeRef);
    }

    @Test
    public void testIsLdapCommonUserSupportedWithUnknownNeType() {
        assertFalse(beanUnderTest.isLdapCommonUserSupported(unknownNormNodeRef));
    }

    @Test
    public void testIsLdapCommonUserSupportedForErbs() {
        for (final String targetModelIdentity : erbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(erbsNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(erbsNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForRnc() {
        for (final String targetModelIdentity : rncTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rncNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(rncNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForRbs() {
        for (final String targetModelIdentity : rbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rbsNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(rbsNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForMgw() {
        for (final String targetModelIdentity : mgwTargetModelIdentities) {
            doReturn(targetModelIdentity).when(mgwNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(mgwNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForSgsn() {
        for (final String targetModelIdentity : sgsnTargetModelIdentities) {
            doReturn(targetModelIdentity).when(sgsnNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(sgsnNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForSbg() {
        for (final String targetModelIdentity : sbgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(sbgNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(sbgNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForVSbg() {
        for (final String targetModelIdentity : vsbgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vsbgNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(vsbgNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForCscf() {
        for (final String targetModelIdentity : cscfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(cscfNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(cscfNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForVCscf() {
        for (final String targetModelIdentity : vcscfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vcscfNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(vcscfNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForMtas() {
        for (final String targetModelIdentity : mtasTargetModelIdentities) {
            doReturn(targetModelIdentity).when(mtasNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(mtasNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForVMtas() {
        for (final String targetModelIdentity : vmtasTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vmtasNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(vmtasNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForMsrbsv1() {
        for (final String targetModelIdentity : msrbsv1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(msrbsv1NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isLdapCommonUserSupported(msrbsv1NormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForSapc() {
        for (final String targetModelIdentity : sapcTargetModelIdentities) {
            doReturn(targetModelIdentity).when(sapcNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(sapcNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForEpg() {
        for (final String targetModelIdentity : epgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(epgNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(epgNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForRadioNode() {
        for (final String targetModelIdentity : radioNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isLdapCommonUserSupported(radioNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForEr6672() {
        for (final String targetModelIdentity : er6672TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6672NormNodeRef).getOssModelIdentity();
            if (er6672TargetModelIdentitiesLesserThan18A.contains(targetModelIdentity)) {
                assertFalse(beanUnderTest.isLdapCommonUserSupported(er6672NormNodeRef));
            } else {
                assertTrue(beanUnderTest.isLdapCommonUserSupported(er6672NormNodeRef));
            }
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForEr6675() {
        for (final String targetModelIdentity : er6675TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6675NormNodeRef).getOssModelIdentity();
            if (er6675TargetModelIdentitiesLesserThan18A.contains(targetModelIdentity)) {
                assertFalse(beanUnderTest.isLdapCommonUserSupported(er6675NormNodeRef));
            } else {
                assertTrue(beanUnderTest.isLdapCommonUserSupported(er6675NormNodeRef));
            }
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForEr6x71() {
        for (final String targetModelIdentity : er6x71TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6x71NormNodeRef).getOssModelIdentity();
            if (er6x71TargetModelIdentitiesLesserThan18A.contains(targetModelIdentity)) {
                assertFalse(beanUnderTest.isLdapCommonUserSupported(er6x71NormNodeRef));
            } else {
                assertTrue(beanUnderTest.isLdapCommonUserSupported(er6x71NormNodeRef));
            }
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForEr6274() {
        for (final String targetModelIdentity : er6274TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6274NormNodeRef).getOssModelIdentity();
            if (er6274TargetModelIdentitiesLesserThan18B.contains(targetModelIdentity)) {
                assertFalse(beanUnderTest.isLdapCommonUserSupported(er6274NormNodeRef));
            } else {
                assertTrue(beanUnderTest.isLdapCommonUserSupported(er6274NormNodeRef));
            }
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForMiniLinkIndoor() {
        for (final String targetModelIdentity : miniLinkIndoorTargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkIndoorNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(miniLinkIndoorNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForMiniLinkCn210() {
        for (final String targetModelIdentity : miniLinkCn210TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn210NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(miniLinkcn210NormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForMiniLinkCn510R1() {
        for (final String targetModelIdentity : miniLinkCn510R1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn510r1NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(miniLinkcn510r1NormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForMiniLinkCn510R2() {
        for (final String targetModelIdentity : miniLinkCn510R2TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn510r2NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(miniLinkcn510r2NormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForMiniLinkCn810R1() {
        for (final String targetModelIdentity : miniLinkCn810R1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn810r1NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(miniLinkcn810r1NormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForMiniLinkCn810R2() {
        for (final String targetModelIdentity : miniLinkCn810R2TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn810r2NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(miniLinkcn810r2NormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForMiniLink665x() {
        for (final String targetModelIdentity : miniLink665xTargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink665xNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(miniLink665xNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForMiniLink669x() {
        for (final String targetModelIdentity : miniLink669xTargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink669xNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(miniLink669xNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForMiniLinkMW2() {
        for (final String targetModelIdentity : miniLinkMW2TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkMW2NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(miniLinkMW2NormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForMiniLink6352() {
        for (final String targetModelIdentity : miniLink6352TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink6352NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(miniLink6352NormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForMiniLink6351() {
        for (final String targetModelIdentity : miniLink6351TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink6351NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(miniLink6351NormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForMiniLink6636() {
        assertFalse(beanUnderTest.isLdapCommonUserSupported(miniLink6366NormNodeRef));
    }

    @Test
    public void testIsLdapCommonUserSupportedForMiniLinkPt2020() {
        for (final String targetModelIdentity : miniLinkPt2020TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkPT2020NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(miniLinkPT2020NormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForSwitch6391() {
        for (final String targetModelIdentity : switch6391TargetModelIdentities) {
            doReturn(targetModelIdentity).when(switch6391NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(switch6391NormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForCiscoAsr9000() {
        for (final String targetModelIdentity : ciscoAsr9000TargetModelIdentities) {
            doReturn(targetModelIdentity).when(ciscoAsr9000NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(ciscoAsr9000NormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForCiscoAsr900() {
        for (final String targetModelIdentity : ciscoAsr900TargetModelIdentities) {
            doReturn(targetModelIdentity).when(ciscoAsr900NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(ciscoAsr900NormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForJuniperMx() {
        for (final String targetModelIdentity : juniperMxTargetModelIdentities) {
            doReturn(targetModelIdentity).when(juniperMxNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(juniperMxNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForRadioTNode() {
        for (final String targetModelIdentity : radioTNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioTNodeNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isLdapCommonUserSupported(radioTNodeNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForBsp() {
        for (final String targetModelIdentity : bspTargetModelIdentities) {
            doReturn(targetModelIdentity).when(bSPNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isLdapCommonUserSupported(bSPNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForVeme() {
        for (final String targetModelIdentity : vemeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vEMENormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isLdapCommonUserSupported(vEMENormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForVwcg() {
        for (final String targetModelIdentity : vwcgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vWCGNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isLdapCommonUserSupported(vWCGNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForHssFe() {
        doReturn(null).when(hSSFENormNodeRef).getOssModelIdentity();
        assertTrue(beanUnderTest.isLdapCommonUserSupported(hSSFENormNodeRef));
    }

    @Test
    public void testIsLdapCommonUserSupportedForVHssFe() {
        doReturn(null).when(vhSSFENormNodeRef).getOssModelIdentity();
        assertTrue(beanUnderTest.isLdapCommonUserSupported(vhSSFENormNodeRef));
    }

    @Test
    public void testIsLdapCommonUserSupportedForVIPWorks() {
        for (final String targetModelIdentity : vipworksTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vIPWorksNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isLdapCommonUserSupported(vIPWorksNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForVbgf() {
        for (final String targetModelIdentity : vbgfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vBGFNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isLdapCommonUserSupported(vBGFNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForVmrf() {
        for (final String targetModelIdentity : vmrfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vMRFNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isLdapCommonUserSupported(vMRFNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForVupg() {
        for (final String targetModelIdentity : vupgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vUPGNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isLdapCommonUserSupported(vUPGNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForVpp() {
        for (final String targetModelIdentity : vppTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vppNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isLdapCommonUserSupported(vppNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForFronthaul6080() {
        for (final String targetModelIdentity : fronthaul6080TargetModelIdentities) {
            doReturn(targetModelIdentity).when(fronthaul6080NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(fronthaul6080NormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForFronthaul6020() {
        for (final String targetModelIdentity : fronthaul6020TargetModelIdentities) {
            doReturn(targetModelIdentity).when(fronthaul6020NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(fronthaul6020NormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForFronthaul6392() {
        for (final String targetModelIdentity : fronthaul6392TargetModelIdentities) {
            doReturn(targetModelIdentity).when(fronthaul6392NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(fronthaul6392NormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForBsc() {
        for (final String targetModelIdentity : bscTargetModelIdentities) {
            doReturn(targetModelIdentity).when(bscNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isLdapCommonUserSupported(bscNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForHlrfe() {
        for (final String targetModelIdentity : hlrfeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfeNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isLdapCommonUserSupported(hlrfeNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForHlr() {
        assertFalse(beanUnderTest.isLdapCommonUserSupported(hlrNormNodeRef));
    }

    @Test
    public void testIsLdapCommonUserSupportedForVHlrFe() {
        for (final String targetModelIdentity : vhlrfeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vhlrfeNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isLdapCommonUserSupported(vhlrfeNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForHlrFeBsp() {
        for (final String targetModelIdentity : hlrfebspTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfebspNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isLdapCommonUserSupported(hlrfebspNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForHlrFeIs() {
        for (final String targetModelIdentity : hlrfeisTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfeisNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isLdapCommonUserSupported(hlrfeisNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForMsc() {
        assertFalse(beanUnderTest.isLdapCommonUserSupported(mscNormNodeRef));
    }

    @Test
    public void testIsLdapCommonUserSupportedForVepg() {
        for (final String targetModelIdentity : vepgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vepgNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isLdapCommonUserSupported(vepgNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForVTFRadioNode() {
        for (final String targetModelIdentity : vtfRadioNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vtfRadioNodeNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isLdapCommonUserSupported(vtfRadioNodeNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForVsd() {
        for (final String targetModelIdentity : vsdTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vsdNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isLdapCommonUserSupported(vsdNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForRvnfm() {
        for (final String targetModelIdentity : rvnfmTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rVNFMNodeNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isLdapCommonUserSupported(rVNFMNodeNormNodeRef));
        }
    }

    @Test
    public void testIsLdapCommonUserSupportedForVrsm() {
        assertTrue(beanUnderTest.isLdapCommonUserSupported(vrsmNormNodeRef));
    }

    @Test
    public void testIsLdapCommonUserSupportedForFiveGRadioNode() {
        assertTrue(beanUnderTest.isLdapCommonUserSupported(fiveGRadioNodeNormNodeRef));
    }

    @Test
    public void testIsLdapCommonUserSupportedForRnNode() {
        assertTrue(beanUnderTest.isLdapCommonUserSupported(rnNodeNormNodeRef));
    }

    @Test
    public void testIsLdapCommonUserSupportedForVrc() {
        assertTrue(beanUnderTest.isLdapCommonUserSupported(vrcNormNodeRef));
    }

    @Test
    public void testCredentialsCommandParamsWithNullNeType() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(nullNeTypeNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(nullNeTypeNormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForErbs() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(erbsNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(erbsNormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForMgw() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(mgwNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(mgwNormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForRnc() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(rncNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(rncNormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForRbs() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(rbsNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(rbsNormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForSgsn() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(sgsnNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(sgsnNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForSbg() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(sbgNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(sbgNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForVSBG() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(vsbgNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(vsbgNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForCscf() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(cscfNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(cscfNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForVCSCF() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(vcscfNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(vcscfNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForMtas() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(mtasNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(mtasNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForVMTAS() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(vmtasNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(vmtasNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForMSRBSV1() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(msrbsv1NormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(msrbsv1NormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForSAPC() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(sapcNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(sapcNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForRNNODE() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(rnNodeNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(rnNodeNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForFIVEGRADIONODE() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(fiveGRadioNodeNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(fiveGRadioNodeNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForVTFRADIONODE() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(vtfRadioNodeNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(vtfRadioNodeNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForVSDNODE() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(vsdNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(vsdNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForRVNFMNODE() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(rVNFMNodeNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(rVNFMNodeNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForVPP() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(vppNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(vppNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForVRSM() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(vrsmNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(vrsmNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForVRC() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(vrcNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(vrcNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForVEME() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(vEMENormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(vEMENormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForVWCG() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(vWCGNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(vWCGNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForHSSFE() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(hSSFENormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(hSSFENormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForVHSSFE() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(vhSSFENormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(vhSSFENormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForVIPWORKS() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(vIPWorksNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(vIPWorksNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForVUPG() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(vUPGNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(vUPGNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForBSP() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(bSPNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(bSPNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForVBGF() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(vBGFNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(vBGFNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForVMRF() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(vMRFNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(vMRFNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForEPG() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(epgNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(epgNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForVEPG() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(vepgNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(vepgNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForRadioNodes() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(radioNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(radioNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForRadioTNodes() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(radioTNodeNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(radioTNodeNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForEr6000() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(er6672NormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(er6672NormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForMiniLinkIndoor() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(miniLinkIndoorNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(miniLinkIndoorNormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForMiniLinkCn210() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(miniLinkcn210NormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(miniLinkcn210NormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForMiniLinkCn510R1() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(miniLinkcn510r1NormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(miniLinkcn510r1NormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForMiniLinkCn510R2() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(miniLinkcn510r2NormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(miniLinkcn510r2NormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForMiniLinkCn810R1() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(miniLinkcn810r1NormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(miniLinkcn810r1NormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForMiniLinkCn810R2() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(miniLinkcn810r2NormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(miniLinkcn810r2NormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForMiniLink665x() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(miniLink665xNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(miniLink665xNormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForMiniLink669x() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(miniLink669xNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(miniLink669xNormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForMiniLinkMW2() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(miniLinkMW2NormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(miniLinkMW2NormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForMiniLink6352() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(miniLink6352NormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(miniLink6352NormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForMiniLink6351() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(miniLink6351NormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(miniLink6351NormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForMiniLink6366() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(miniLink6366NormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(miniLink6366NormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForMiniLinkPT2020() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(miniLinkPT2020NormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(miniLinkPT2020NormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForSwitch6391() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(switch6391NormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(switch6391NormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForFronthaul6392() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(fronthaul6392NormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(fronthaul6392NormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForFronthaul6080() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(fronthaul6080NormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(fronthaul6080NormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForFronthaul6020() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(fronthaul6020NormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(fronthaul6020NormNodeRef);
        final List<String> actualExpectedParams = getCppLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getCppLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForBsc() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(bscNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(bscNormNodeRef);
        final List<String> actualExpectedParams = getAxeLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getAxeLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForMsc() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(mscNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(mscNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForHlr() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(hlrNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(hlrNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForHlrfe() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(hlrfeNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(hlrfeNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForvHlrfe() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(vhlrfeNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(vhlrfeNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForHlrfebsp() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(hlrfebspNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(hlrfebspNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForHlrfeis() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(hlrfeisNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(hlrfeisNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForCiscoAsr9000() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(ciscoAsr9000NormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(ciscoAsr9000NormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForCiscoAsr900() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(ciscoAsr900NormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(ciscoAsr900NormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test
    public void testCredentialsCommandParamsForJuniperMx() {
        final List<String> expectedParams = beanUnderTest.getExpectedCredentialsParams(juniperMxNormNodeRef);
        final List<String> unexpectedParams = beanUnderTest.getUnexpectedCredentialsParams(juniperMxNormNodeRef);
        final List<String> actualExpectedParams = getEcimLikeExpectedCredentialsParams();
        final List<String> actualUnexpectedParams = getEcimLikeUnexpectedCredentialsParams();
        assertTrue(actualExpectedParams.equals(expectedParams));
        assertTrue(actualUnexpectedParams.equals(unexpectedParams));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testSupportedEnrollmentModesForNullNodeReference() {
        final NormalizableNodeReference normNode = null;
        beanUnderTest.getSupportedEnrollmentModes(normNode);
    }

    @Test
    public void testSupportedEnrollmentModesForUnknownNode() {
        final List<String> expected = Arrays.asList("NOT_SUPPORTED");
        final List<String> actual = beanUnderTest.getSupportedEnrollmentModes(unknownNormNodeRef);
        assertTrue(expected.equals(actual));
    }

    @Test
    public void testIsEnrollmentModeSupportedForErbs() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.SCEP);
        expectedSupported.add(EnrollmentMode.CMPv2_INITIAL);
        expectedSupported.add(EnrollmentMode.CMPv2_VC);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            for (final String targetModelIdentity : erbsTargetModelIdentities) {
                final NodeModelInformation erbsNodeModelInfo = new NodeModelInformation(targetModelIdentity, ModelIdentifierType.OSS_IDENTIFIER,
                        ERBS);
                assertTrue(beanUnderTest.isEnrollmentModeSupported(erbsNodeModelInfo, enrollmentMode));
            }
        }
    }

    @Test
    public void testGetSupportedEnrollmentModesForErbsNodeModelInfo() {
        final Set<String> expectedSupported = new HashSet<>();
        expectedSupported.add(EnrollmentMode.SCEP.toString());
        expectedSupported.add(EnrollmentMode.CMPv2_INITIAL.toString());
        expectedSupported.add(EnrollmentMode.CMPv2_VC.toString());

        final List<String> supported = beanUnderTest.getSupportedEnrollmentModes(erbsNodeModelInfo);
        assertTrue(
                supported.size() == expectedSupported.size() && supported.containsAll(expectedSupported) && expectedSupported.containsAll(supported));
    }

    @Test
    public void testIsEnrollmentModeSupportedForRNC() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.SCEP);
        expectedSupported.add(EnrollmentMode.CMPv2_INITIAL);
        expectedSupported.add(EnrollmentMode.CMPv2_VC);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            for (final String targetModelIdentity : rncTargetModelIdentities) {
                final NodeModelInformation rncNodeModelInfo = new NodeModelInformation(targetModelIdentity, ModelIdentifierType.OSS_IDENTIFIER, RNC);
                assertTrue(beanUnderTest.isEnrollmentModeSupported(rncNodeModelInfo, enrollmentMode));
            }
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForRBS() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.SCEP);
        expectedSupported.add(EnrollmentMode.CMPv2_INITIAL);
        expectedSupported.add(EnrollmentMode.CMPv2_VC);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            for (final String targetModelIdentity : rbsTargetModelIdentities) {
                final NodeModelInformation rbsNodeModelInfo = new NodeModelInformation(targetModelIdentity, ModelIdentifierType.OSS_IDENTIFIER, RBS);
                if (rbsTargetModelIdentitiesLesserThan14A.contains(targetModelIdentity)) {
                    if (EnrollmentMode.SCEP.equals(enrollmentMode)) {
                        assertTrue(beanUnderTest.isEnrollmentModeSupported(rbsNodeModelInfo, enrollmentMode));
                    } else {
                        assertFalse(beanUnderTest.isEnrollmentModeSupported(rbsNodeModelInfo, enrollmentMode));
                    }
                } else {
                    assertTrue(beanUnderTest.isEnrollmentModeSupported(rbsNodeModelInfo, enrollmentMode));
                }
            }
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForMgw() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.SCEP);
        expectedSupported.add(EnrollmentMode.CMPv2_INITIAL);
        expectedSupported.add(EnrollmentMode.CMPv2_VC);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            for (final String targetModelIdentity : mgwTargetModelIdentities) {
                final NodeModelInformation mgwNodeModelInfo = new NodeModelInformation(targetModelIdentity, ModelIdentifierType.OSS_IDENTIFIER, MGW);
                assertTrue(beanUnderTest.isEnrollmentModeSupported(mgwNodeModelInfo, enrollmentMode));
            }
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForSgsn() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(sgsnNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForSbg() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(sbgNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForVSBG() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(vsbgNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForMTAS() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(mtasNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForVMTAS() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(vmtasNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForCSCF() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(cscfNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForVCSCF() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(vcscfNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForMSRBSV1() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            for (final String targetModelIdentity : msrbsv1TargetModelIdentities) {
                final NodeModelInformation msrbsv1NodeModelInfo = new NodeModelInformation(targetModelIdentity, ModelIdentifierType.OSS_IDENTIFIER,
                        MSRBS_V1);
                assertTrue(beanUnderTest.isEnrollmentModeSupported(msrbsv1NodeModelInfo, enrollmentMode));
            }
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForSAPC() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(sapcNodeModelInfo, enrollmentMode));
        }
    }

    public void testIsEnrollmentModeSupportedForRNNODE() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        // expectedSupported.add(EnrollmentMode.SCEP);
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(rnNodeNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForFIVEGRADIONODE() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(fiveGRadioNodeNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForVTFRADIONODE() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(vtfRadioNodeNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForVSDNODE() {
        assertTrue(beanUnderTest.isEnrollmentModeSupported(vsdNodeModelInfo, EnrollmentMode.CMPv2_INITIAL));
    }

    @Test
    public void testIsEnrollmentModeSupportedForRVNFMNODE() {
        assertTrue(beanUnderTest.isEnrollmentModeSupported(rVNFMNodeModelInfo, EnrollmentMode.CMPv2_INITIAL));
    }

    @Test
    public void testIsEnrollmentModeSupportedForVPP() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_INITIAL);
        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(vppNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForVRSM() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_INITIAL);
        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(vrsmNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForVRC() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_INITIAL);
        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(vrcNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForVEME() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        // expectedSupported.add(EnrollmentMode.SCEP);
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(vEMENodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForVWCG() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        // expectedSupported.add(EnrollmentMode.SCEP);
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(vWCGNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForHSSFE() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_INITIAL);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(HSSFENodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForVHSSFE() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_INITIAL);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(vHSSFENodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForVIPWORKS() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        // expectedSupported.add(EnrollmentMode.SCEP);
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(vIPWorksNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForVUPG() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        // expectedSupported.add(EnrollmentMode.SCEP);
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(vUPGNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForBSP() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        // expectedSupported.add(EnrollmentMode.SCEP);
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(BSPNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForVBGF() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        // expectedSupported.add(EnrollmentMode.SCEP);
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(vBGFNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForVMRF() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        // expectedSupported.add(EnrollmentMode.SCEP);
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(vMRFNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForRadioNode() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            for (final String targetModelIdentity : radioNodeTargetModelIdentities) {
                final NodeModelInformation radioNodeModelInfo = new NodeModelInformation(targetModelIdentity, ModelIdentifierType.OSS_IDENTIFIER,
                        RadioNode);
                assertTrue(beanUnderTest.isEnrollmentModeSupported(radioNodeModelInfo, enrollmentMode));
            }
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForRadioTNode() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);
        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(radioTNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForBsc() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_INITIAL);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(bscNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForHlrfe() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_INITIAL);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(hlrfeNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForvHlrfe() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_INITIAL);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(vhlrfeNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForHlrfebsp() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_INITIAL);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(hlrfebspNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForHlrfeis() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_INITIAL);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(hlrfeisNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForMsc() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_INITIAL);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(mscNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForHlr() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_INITIAL);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(hlrNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForEr6672() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            for (final String targetModelIdentity : er6672TargetModelIdentities) {
                final NodeModelInformation er6672NodeModelInfo = new NodeModelInformation(targetModelIdentity, ModelIdentifierType.OSS_IDENTIFIER,
                        Router6672);
                assertTrue(beanUnderTest.isEnrollmentModeSupported(er6672NodeModelInfo, enrollmentMode));
            }
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForEr6000() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.CMPv2_VC);
        expectedSupported.add(EnrollmentMode.OFFLINE_CSR);
        expectedSupported.add(EnrollmentMode.OFFLINE_PKCS12);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(er6000NodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForMiniLinkIndoor() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(miniLinkIndoorNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForMiniLinkCn210() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(miniLinkcN210NodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForMiniLink665x() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(miniLink665xNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForMiniLink669x() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(miniLink669xNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForMiniLinkMW2() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(miniLinkMW2NodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForMiniLinkCn510R1() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(miniLinkcn510r1NodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForMiniLinkCn510R2() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(miniLinkcn510r2NodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForMiniLinkCn810R1() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(miniLinkcn810r1NodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForMiniLinkCn810R2() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(miniLinkcn810r2NodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForMiniLink6352() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(miniLink6352NodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForMiniLink6351() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(miniLink6351NodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForMiniLink6366() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(miniLink6366NodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForMiniLinkPT2020() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(miniLinkPT2020NodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForSwitch6391() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(switch6391NodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForFronthaul6392() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(fronthaul6392NodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForCiscoAsr9000() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(ciscoAsr9000NodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForCiscoAsr900() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(ciscoAsr900NodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForJuniperMx() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(juniperMxNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForFronthaul6080() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(fronthaulNodeModelInfo, enrollmentMode));
        }
    }

    @Test
    public void testIsEnrollmentModeSupportedForFronthaul6020() {
        final Set<EnrollmentMode> expectedSupported = new HashSet<EnrollmentMode>();
        expectedSupported.add(EnrollmentMode.NOT_SUPPORTED);

        for (final EnrollmentMode enrollmentMode : expectedSupported) {
            assertTrue(beanUnderTest.isEnrollmentModeSupported(fronthaul6020NodeModelInfo, enrollmentMode));
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultEnrollmentModeWithNullNodeRef() {
        final NormalizableNodeReference normNodeRef = null;
        beanUnderTest.getDefaultEnrollmentMode(normNodeRef);
    }

    @Test
    public void testGetDefaultEnrollmentModeWithUnknownNeType() {
        assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(unknownNormNodeRef)));
    }

    @Test
    public void testGetDefaultEnrollmentModeForErbs() {
        for (final String targetModelIdentity : erbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(erbsNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(erbsNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForErbsNodeModelIdentity() {
        assertEquals("CMPv2_VC", beanUnderTest.getDefaultEnrollmentMode(erbsNodeModelInfo));
    }

    @Test
    public void testGetDefaultEnrollmentModeForRNC() {
        for (final String targetModelIdentity : rncTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rncNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(rncNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForRBS() {
        for (final String targetModelIdentity : rbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rbsNormNodeRef).getOssModelIdentity();
            if (rbsTargetModelIdentitiesLesserThan14A.contains(targetModelIdentity)) {
                assertTrue("SCEP".equals(beanUnderTest.getDefaultEnrollmentMode(rbsNormNodeRef)));
            } else {
                assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(rbsNormNodeRef)));
            }
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForMgw() {
        for (final String targetModelIdentity : mgwTargetModelIdentities) {
            doReturn(targetModelIdentity).when(mgwNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(mgwNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForSgsnMme() {
        for (final String targetModelIdentity : sgsnTargetModelIdentities) {
            doReturn(targetModelIdentity).when(sgsnNormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(sgsnNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForSbg() {
        for (final String targetModelIdentity : sbgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(sbgNormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(sbgNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForVSBG() {
        for (final String targetModelIdentity : vsbgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vsbgNormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(vsbgNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForCSCF() {
        for (final String targetModelIdentity : cscfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(cscfNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(cscfNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForVCSCF() {
        for (final String targetModelIdentity : vcscfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vcscfNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(vcscfNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForMTAS() {
        for (final String targetModelIdentity : mtasTargetModelIdentities) {
            doReturn(targetModelIdentity).when(mtasNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(mtasNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForVMTAS() {
        for (final String targetModelIdentity : vmtasTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vmtasNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(vmtasNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForMSRBSV1() {
        for (final String targetModelIdentity : msrbsv1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(msrbsv1NormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(msrbsv1NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForSAPC() {
        for (final String targetModelIdentity : sapcTargetModelIdentities) {
            doReturn(targetModelIdentity).when(sapcNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(sapcNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForEpg() {
        for (final String targetModelIdentity : epgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(epgNormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(epgNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForVEpg() {
        for (final String targetModelIdentity : vepgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vepgNormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(vepgNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForRadioNode() {
        for (final String targetModelIdentity : radioNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(radioNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForEr6672() {
        for (final String targetModelIdentity : er6672TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6672NormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(er6672NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForEr6675() {
        for (final String targetModelIdentity : er6675TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6675NormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(er6675NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForEr6x71() {
        for (final String targetModelIdentity : er6x71TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6x71NormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(er6x71NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForEr6274() {
        for (final String targetModelIdentity : er6274TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6274NormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(er6274NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForMiniLinkIndoor() {
        for (final String targetModelIdentity : miniLinkIndoorTargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkIndoorNormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(miniLinkIndoorNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForMiniLinkCn210() {
        for (final String targetModelIdentity : miniLinkCn210TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn210NormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(miniLinkcn210NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForMiniLinkCn510R1() {
        for (final String targetModelIdentity : miniLinkCn510R1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn510r1NormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(miniLinkcn510r1NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForMiniLinkCn510R2() {
        for (final String targetModelIdentity : miniLinkCn510R2TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn510r2NormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(miniLinkcn510r2NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForMiniLinkCn810R1() {
        for (final String targetModelIdentity : miniLinkCn810R1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn810r1NormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(miniLinkcn810r1NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForMiniLinkCn810R2() {
        for (final String targetModelIdentity : miniLinkCn810R2TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn810r2NormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(miniLinkcn810r2NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForMiniLink665x() {
        for (final String targetModelIdentity : miniLink665xTargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink665xNormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(miniLink665xNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForMiniLink669x() {
        for (final String targetModelIdentity : miniLink669xTargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink669xNormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(miniLink669xNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForMiniLinkMW2() {
        for (final String targetModelIdentity : miniLinkMW2TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkMW2NormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(miniLinkMW2NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForMiniLink6352() {
        for (final String targetModelIdentity : miniLink6352TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink6352NormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(miniLink6352NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForMiniLink6351() {
        for (final String targetModelIdentity : miniLink6351TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink6351NormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(miniLink6351NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForMiniLink6366() {
        assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(miniLink6366NormNodeRef)));
    }

    @Test
    public void testGetDefaultEnrollmentModeForMiniLinkPT2020() {
        for (final String targetModelIdentity : miniLinkPt2020TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkPT2020NormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(miniLinkPT2020NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForSwitch6391() {
        for (final String targetModelIdentity : switch6391TargetModelIdentities) {
            doReturn(targetModelIdentity).when(switch6391NormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(switch6391NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForCiscoAsr9000() {
        for (final String targetModelIdentity : ciscoAsr9000TargetModelIdentities) {
            doReturn(targetModelIdentity).when(ciscoAsr9000NormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(ciscoAsr9000NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForCiscoAsr900() {
        for (final String targetModelIdentity : ciscoAsr900TargetModelIdentities) {
            doReturn(targetModelIdentity).when(ciscoAsr900NormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(ciscoAsr900NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForJuniperMx() {
        for (final String targetModelIdentity : juniperMxTargetModelIdentities) {
            doReturn(targetModelIdentity).when(juniperMxNormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(juniperMxNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForFronthaul6392() {
        for (final String targetModelIdentity : fronthaul6392TargetModelIdentities) {
            doReturn(targetModelIdentity).when(fronthaul6392NormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(fronthaul6392NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForFronthaul6080() {
        for (final String targetModelIdentity : fronthaul6080TargetModelIdentities) {
            doReturn(targetModelIdentity).when(fronthaul6080NormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(fronthaul6080NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForFronthaul6020() {
        for (final String targetModelIdentity : fronthaul6020TargetModelIdentities) {
            doReturn(targetModelIdentity).when(fronthaul6020NormNodeRef).getOssModelIdentity();
            assertTrue("NOT_SUPPORTED".equals(beanUnderTest.getDefaultEnrollmentMode(fronthaul6020NormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForRNNODE() {
        assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(rnNodeNormNodeRef)));
    }

    @Test
    public void testGetDefaultEnrollmentModeForFIVEGRADIONODE() {
        assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(fiveGRadioNodeNormNodeRef)));
    }

    @Test
    public void testGetDefaultEnrollmentModeForVTFRADIONODE() {
        for (final String targetModelIdentity : vtfRadioNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vtfRadioNodeNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(vtfRadioNodeNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForVSDNODE() {
        for (final String targetModelIdentity : vsdTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vsdNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_INITIAL".equals(beanUnderTest.getDefaultEnrollmentMode(vsdNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForRVNFMNODE() {
        for (final String targetModelIdentity : rvnfmTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rVNFMNodeNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_INITIAL".equals(beanUnderTest.getDefaultEnrollmentMode(rVNFMNodeNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForVPP() {
        for (final String targetModelIdentity : vppTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vppNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_INITIAL".equals(beanUnderTest.getDefaultEnrollmentMode(vppNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForVRSM() {
        assertTrue("CMPv2_INITIAL".equals(beanUnderTest.getDefaultEnrollmentMode(vrsmNormNodeRef)));
    }

    @Test
    public void testGetDefaultEnrollmentModeForVRC() {
        assertTrue("CMPv2_INITIAL".equals(beanUnderTest.getDefaultEnrollmentMode(vrcNormNodeRef)));
    }

    @Test
    public void testGetDefaultEnrollmentModeForVEME() {
        for (final String targetModelIdentity : vemeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vEMENormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(vEMENormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForVWCG() {
        for (final String targetModelIdentity : vwcgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vWCGNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(vWCGNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForHSSFE() {
        doReturn(null).when(hSSFENormNodeRef).getOssModelIdentity();
        assertTrue("CMPv2_INITIAL".equals(beanUnderTest.getDefaultEnrollmentMode(hSSFENormNodeRef)));
    }

    @Test
    public void testGetDefaultEnrollmentModeForVHSSFE() {
        doReturn(null).when(vhSSFENormNodeRef).getOssModelIdentity();
        assertTrue("CMPv2_INITIAL".equals(beanUnderTest.getDefaultEnrollmentMode(vhSSFENormNodeRef)));
    }

    @Test
    public void testGetDefaultEnrollmentModeForVIPWORKS() {
        for (final String targetModelIdentity : vipworksTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vIPWorksNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(vIPWorksNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForVUPG() {
        for (final String targetModelIdentity : vupgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vUPGNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(vUPGNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForBSP() {
        for (final String targetModelIdentity : bspTargetModelIdentities) {
            doReturn(targetModelIdentity).when(bSPNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(bSPNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForVBGF() {
        for (final String targetModelIdentity : vbgfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vBGFNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(vBGFNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForVMRF() {
        for (final String targetModelIdentity : vmrfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vMRFNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(vMRFNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForRadioTNode() {
        for (final String targetModelIdentity : radioTNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioTNodeNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_VC".equals(beanUnderTest.getDefaultEnrollmentMode(radioTNodeNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForBsc() {
        for (final String targetModelIdentity : bscTargetModelIdentities) {
            doReturn(targetModelIdentity).when(bscNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_INITIAL".equals(beanUnderTest.getDefaultEnrollmentMode(bscNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForHlrfe() {
        for (final String targetModelIdentity : hlrfeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfeNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_INITIAL".equals(beanUnderTest.getDefaultEnrollmentMode(hlrfeNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForvHlrfe() {
        for (final String targetModelIdentity : vhlrfeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vhlrfeNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_INITIAL".equals(beanUnderTest.getDefaultEnrollmentMode(vhlrfeNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForHlrfebsp() {
        for (final String targetModelIdentity : hlrfebspTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfebspNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_INITIAL".equals(beanUnderTest.getDefaultEnrollmentMode(hlrfebspNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForHlrfeis() {
        for (final String targetModelIdentity : hlrfeisTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfeisNormNodeRef).getOssModelIdentity();
            assertTrue("CMPv2_INITIAL".equals(beanUnderTest.getDefaultEnrollmentMode(hlrfeisNormNodeRef)));
        }
    }

    @Test
    public void testGetDefaultEnrollmentModeForMsc() {
        assertTrue("CMPv2_INITIAL".equals(beanUnderTest.getDefaultEnrollmentMode(mscNormNodeRef)));
    }

    @Test
    public void testGetDefaultEnrollmentModeForHlr() {
        assertTrue("CMPv2_INITIAL".equals(beanUnderTest.getDefaultEnrollmentMode(hlrNormNodeRef)));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsSynchronousEnrollmentSupportedForNullNode() {
        final NodeModelInformation nmi = null;
        beanUnderTest.isSynchronousEnrollmentSupported(nmi);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForInvalidNode() {
        beanUnderTest.isSynchronousEnrollmentSupported(invalidNodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForErbs() {
        assertFalse(beanUnderTest.isSynchronousEnrollmentSupported(erbsNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForRNC() {
        assertFalse(beanUnderTest.isSynchronousEnrollmentSupported(rncNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForRBS() {
        assertFalse(beanUnderTest.isSynchronousEnrollmentSupported(rbsNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForMgw() {
        assertFalse(beanUnderTest.isSynchronousEnrollmentSupported(mgwNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForSgsn() {
        beanUnderTest.isSynchronousEnrollmentSupported(sgsnNodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForSbg() {
        beanUnderTest.isSynchronousEnrollmentSupported(sbgNodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForMSRBSV1() {
        assertFalse(beanUnderTest.isSynchronousEnrollmentSupported(msrbsv1NodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForSAPC() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(sapcNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForRNNODE() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(rnNodeNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForFIVEGRADIONODE() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(fiveGRadioNodeNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForVTFRADIONODE() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(vtfRadioNodeNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForVSDNODE() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(vsdNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForRVNFMNODE() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(rVNFMNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForVPP() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(vppNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForVRSM() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(vrsmNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForVRC() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(vrcNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForVEME() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(vEMENodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForVWCG() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(vWCGNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForHSSFE() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(HSSFENodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForVHSSFE() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(vHSSFENodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForVIPWORKS() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(vIPWorksNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForVUPG() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(vUPGNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForBSP() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(BSPNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForVBGF() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(vBGFNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForVMRF() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(vMRFNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForRadioNode() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(radioNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForRadioTNode() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(radioTNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForBsc() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(bscNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForHlrfe() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(hlrfeNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForvHlrfe() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(vhlrfeNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForHlrfebsp() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(hlrfebspNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForHlrfeis() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(hlrfeisNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForMsc() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(mscNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForHlr() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(hlrNodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForEr6000() {
        assertTrue(beanUnderTest.isSynchronousEnrollmentSupported(er6000NodeModelInfo));
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForMiniLinkIndoor() {
        beanUnderTest.isSynchronousEnrollmentSupported(miniLinkIndoorNodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForMiniLinkCn210() {
        beanUnderTest.isSynchronousEnrollmentSupported(miniLinkcN210NodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForMiniLink665x() {
        beanUnderTest.isSynchronousEnrollmentSupported(miniLink665xNodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForMiniLink669x() {
        beanUnderTest.isSynchronousEnrollmentSupported(miniLink669xNodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForMiniLinkMW2() {
        beanUnderTest.isSynchronousEnrollmentSupported(miniLinkMW2NodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForMiniLinkCn510R1() {
        beanUnderTest.isSynchronousEnrollmentSupported(miniLinkcn510r1NodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForMiniLinkCn510R2() {
        beanUnderTest.isSynchronousEnrollmentSupported(miniLinkcn510r2NodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForMiniLinkCn810R1() {
        beanUnderTest.isSynchronousEnrollmentSupported(miniLinkcn810r1NodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForMiniLinkCn810R2() {
        beanUnderTest.isSynchronousEnrollmentSupported(miniLinkcn810r2NodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForMiniLink6352() {
        beanUnderTest.isSynchronousEnrollmentSupported(miniLink6352NodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForMiniLink6351() {
        beanUnderTest.isSynchronousEnrollmentSupported(miniLink6351NodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForMiniLink6366() {
        beanUnderTest.isSynchronousEnrollmentSupported(miniLink6366NodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForMiniLinkPT2020() {
        beanUnderTest.isSynchronousEnrollmentSupported(miniLinkPT2020NodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForSwich6391() {
        beanUnderTest.isSynchronousEnrollmentSupported(switch6391NodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForFronthaul6392() {
        beanUnderTest.isSynchronousEnrollmentSupported(fronthaul6392NodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForCiscoAsr9000() {
        beanUnderTest.isSynchronousEnrollmentSupported(ciscoAsr9000NodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForCiscoAsr900() {
        beanUnderTest.isSynchronousEnrollmentSupported(ciscoAsr900NodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForJuniperMx() {
        beanUnderTest.isSynchronousEnrollmentSupported(juniperMxNodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForFronthaul6080() {
        beanUnderTest.isSynchronousEnrollmentSupported(fronthaulNodeModelInfo);
    }

    @Test
    public void testIsSynchronousEnrollmentSupportedForFronthaul6020() {
        beanUnderTest.isSynchronousEnrollmentSupported(fronthaul6020NodeModelInfo);
    }

    @Test
    public void testGetDefaultEntityProfileForErbs() {
        assertTrue("MicroRBSIPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(erbsNodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("MicroRBSOAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(erbsNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForMgw() {
        assertNull(beanUnderTest.getDefaultEntityProfile(mgwNodeModelInfo, NodeEntityCategory.IPSEC));
        assertTrue("MicroRBSOAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(mgwNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForSgsn() {
        assertNull(beanUnderTest.getDefaultEntityProfile(sgsnNodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(sgsnNodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForSbg() {
        assertNull(beanUnderTest.getDefaultEntityProfile(sbgNodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(sbgNodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForMSRBSV1() {
        assertTrue("PicoRBSIPSec_WA_RS_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(msrbsv1NodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("PicoRBSOAM_RS_CHAIN_SAN_EP".equals(beanUnderTest.getDefaultEntityProfile(msrbsv1NodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForSAPC() {
        assertTrue("DUSGen2IPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(sapcNodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(sapcNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForRNNODE() {
        assertTrue("DUSGen2IPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(rnNodeNodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(rnNodeNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForFIVEGRADIONODE() {
        assertTrue("DUSGen2IPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(fiveGRadioNodeNodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(fiveGRadioNodeNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForVTFRADIONODE() {
        assertTrue("DUSGen2IPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vtfRadioNodeNodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vtfRadioNodeNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForVSDNODE() {
        assertTrue("DUSGen2IPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vsdNodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vsdNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForRVNFMNODE() {
        assertTrue("DUSGen2IPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(rVNFMNodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("VNFM_IP_EP".equals(beanUnderTest.getDefaultEntityProfile(rVNFMNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForVPP() {
        assertTrue("DUSGen2IPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vppNodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vppNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForVRSM() {
        assertTrue("DUSGen2IPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vrsmNodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vrsmNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForVRC() {
        assertTrue("DUSGen2IPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vrcNodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vrcNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForVEME() {
        assertTrue("DUSGen2IPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vEMENodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vEMENodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForVWCG() {
        assertTrue("DUSGen2IPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vWCGNodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vWCGNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForHSSFE() {
        assertNull(beanUnderTest.getDefaultEntityProfile(HSSFENodeModelInfo, NodeEntityCategory.IPSEC));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(HSSFENodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForVHSSFE() {
        assertNull(beanUnderTest.getDefaultEntityProfile(vHSSFENodeModelInfo, NodeEntityCategory.IPSEC));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vHSSFENodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForVIPWORKS() {
        assertTrue("DUSGen2IPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vIPWorksNodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vIPWorksNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForVUPG() {
        assertTrue("DUSGen2IPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vUPGNodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vUPGNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForBSP() {
        assertTrue("DUSGen2IPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(BSPNodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(BSPNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForVBGF() {
        assertTrue("DUSGen2IPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vBGFNodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vBGFNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForVMRF() {
        assertTrue("DUSGen2IPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vMRFNodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vMRFNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForRadioNodes() {
        assertTrue("DUSGen2IPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(radioNodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(radioNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForRadioTNodes() {
        assertTrue("DUSGen2IPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(radioTNodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(radioTNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForEr6000() {
        assertTrue("DUSGen2IPSec_SAN_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(er6000NodeModelInfo, NodeEntityCategory.IPSEC)));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(er6000NodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForMiniLinkIndoor() {
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLinkIndoorNodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLinkIndoorNodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForMiniLinkCn210() {
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLinkcN210NodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLinkcN210NodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForMiniLink665x() {
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLink665xNodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLink665xNodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForMiniLink669x() {
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLink669xNodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLink669xNodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForMiniLinkMW2() {
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLinkMW2NodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLinkMW2NodeModelInfo, NodeEntityCategory.OAM));
    }
    @Test
    public void testGetDefaultEntityProfileForMiniLinkCn510R1() {
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLinkcn510r1NodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLinkcn510r1NodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForMiniLinkCn510R2() {
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLinkcn510r2NodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLinkcn510r2NodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForMiniLinkCn810R1() {
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLinkcn810r1NodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLinkcn810r1NodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForMiniLinkCn810R2() {
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLinkcn810r2NodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLinkcn810r2NodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForMiniLink6352() {
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLink6352NodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLink6352NodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForMiniLink6351() {
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLink6351NodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLink6351NodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForMiniLink6366() {
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLink6366NodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLink6366NodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForMiniLinkPT2020() {
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLinkPT2020NodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(miniLinkPT2020NodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForSwitch6391() {
        assertNull(beanUnderTest.getDefaultEntityProfile(switch6391NodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(switch6391NodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForFronthaul6392() {
        assertNull(beanUnderTest.getDefaultEntityProfile(fronthaul6392NodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(fronthaul6392NodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForCiscoAsr9000() {
        assertNull(beanUnderTest.getDefaultEntityProfile(ciscoAsr9000NodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(ciscoAsr9000NodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForCiscoAsr900() {
        assertNull(beanUnderTest.getDefaultEntityProfile(ciscoAsr900NodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(ciscoAsr900NodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileFoJuniperMx() {
        assertNull(beanUnderTest.getDefaultEntityProfile(juniperMxNodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(juniperMxNodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForFronthaul6080() {
        assertNull(beanUnderTest.getDefaultEntityProfile(fronthaulNodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(fronthaulNodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForFronthaul6020() {
        assertNull(beanUnderTest.getDefaultEntityProfile(fronthaul6020NodeModelInfo, NodeEntityCategory.IPSEC));
        assertNull(beanUnderTest.getDefaultEntityProfile(fronthaul6020NodeModelInfo, NodeEntityCategory.OAM));
    }

    @Test
    public void testGetDefaultEntityProfileForBsc() {
        assertNull(beanUnderTest.getDefaultEntityProfile(bscNodeModelInfo, NodeEntityCategory.IPSEC));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(bscNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForHlrfe() {
        assertNull(beanUnderTest.getDefaultEntityProfile(hlrfeNodeModelInfo, NodeEntityCategory.IPSEC));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(hlrfeNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForvHlrfe() {
        assertNull(beanUnderTest.getDefaultEntityProfile(vhlrfeNodeModelInfo, NodeEntityCategory.IPSEC));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(vhlrfeNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForHlrfebsp() {
        assertNull(beanUnderTest.getDefaultEntityProfile(hlrfebspNodeModelInfo, NodeEntityCategory.IPSEC));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(hlrfebspNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForHlrfeis() {
        assertNull(beanUnderTest.getDefaultEntityProfile(hlrfeisNodeModelInfo, NodeEntityCategory.IPSEC));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(hlrfeisNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForMsc() {
        assertNull(beanUnderTest.getDefaultEntityProfile(mscNodeModelInfo, NodeEntityCategory.IPSEC));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(mscNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetDefaultEntityProfileForHlr() {
        assertNull(beanUnderTest.getDefaultEntityProfile(hlrNodeModelInfo, NodeEntityCategory.IPSEC));
        assertTrue("DUSGen2OAM_CHAIN_EP".equals(beanUnderTest.getDefaultEntityProfile(hlrNodeModelInfo, NodeEntityCategory.OAM)));
    }

    @Test
    public void testGetIssueOrReissueCertWfWithUnknownNeType() {
        assertNull(beanUnderTest.getIssueOrReissueCertWf(unknownNodeRef, "any"));
    }

    @Test
    public void testGetIssueOrReissueInvalidCertWf() {
        assertNull(beanUnderTest.getIssueOrReissueCertWf(erbsNodeRef, "OEM"));
    }

    @Test
    public void testGetIssueOrReissueCertWfForErbs() {
        for (final String targetModelIdentity : erbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(erbsNormNodeRef).getOssModelIdentity();
            assertEquals("CPPIssueCert", beanUnderTest.getIssueOrReissueCertWf(erbsNodeRef, "OAM"));
            assertEquals("CPPIssueCert", beanUnderTest.getIssueOrReissueCertWf(erbsNormNodeRef, "OAM"));
            assertEquals("CPPIssueCertIpSec", beanUnderTest.getIssueOrReissueCertWf(erbsNodeRef, "IPSEC"));
            assertEquals("CPPIssueCertIpSec", beanUnderTest.getIssueOrReissueCertWf(erbsNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForRbs() {
        for (final String targetModelIdentity : rbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rbsNormNodeRef).getOssModelIdentity();
            assertEquals("CPPIssueCert", beanUnderTest.getIssueOrReissueCertWf(rbsNodeRef, "OAM"));
            assertEquals("CPPIssueCert", beanUnderTest.getIssueOrReissueCertWf(rbsNormNodeRef, "OAM"));
            assertEquals("CPPIssueCertIpSec", beanUnderTest.getIssueOrReissueCertWf(rbsNodeRef, "IPSEC"));
            assertEquals("CPPIssueCertIpSec", beanUnderTest.getIssueOrReissueCertWf(rbsNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForRnc() {
        for (final String targetModelIdentity : rncTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rncNormNodeRef).getOssModelIdentity();
            assertEquals("CPPIssueCert", beanUnderTest.getIssueOrReissueCertWf(rncNodeRef, "OAM"));
            assertEquals("CPPIssueCert", beanUnderTest.getIssueOrReissueCertWf(rncNormNodeRef, "OAM"));
            assertEquals("CPPIssueCertIpSec", beanUnderTest.getIssueOrReissueCertWf(rncNodeRef, "IPSEC"));
            assertEquals("CPPIssueCertIpSec", beanUnderTest.getIssueOrReissueCertWf(rncNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForMgw() {
        for (final String targetModelIdentity : mgwTargetModelIdentities) {
            doReturn(targetModelIdentity).when(mgwNormNodeRef).getOssModelIdentity();
            assertEquals("CPPIssueCert", beanUnderTest.getIssueOrReissueCertWf(mgwNodeRef, "OAM"));
            assertEquals("CPPIssueCert", beanUnderTest.getIssueOrReissueCertWf(mgwNormNodeRef, "OAM"));
            assertNull(beanUnderTest.getIssueOrReissueCertWf(mgwNodeRef, "IPSEC"));
            assertNull(beanUnderTest.getIssueOrReissueCertWf(mgwNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForCscf() {
        for (final String targetModelIdentity : cscfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(cscfNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(cscfNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(cscfNormNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(cscfNodeRef, "IPSEC"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(cscfNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForVCscf() {
        for (final String targetModelIdentity : vcscfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vcscfNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vcscfNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vcscfNormNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vcscfNodeRef, "IPSEC"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vcscfNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForMtas() {
        for (final String targetModelIdentity : mtasTargetModelIdentities) {
            doReturn(targetModelIdentity).when(mtasNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(mtasNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(mtasNormNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(mtasNodeRef, "IPSEC"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(mtasNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForVMtas() {
        for (final String targetModelIdentity : vmtasTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vmtasNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vmtasNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vmtasNormNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vmtasNodeRef, "IPSEC"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vmtasNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForMSRBSV1() {
        for (final String targetModelIdentity : msrbsv1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(msrbsv1NormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(msrbsv1NodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(msrbsv1NormNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(msrbsv1NodeRef, "IPSEC"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(msrbsv1NormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForSAPC() {
        for (final String targetModelIdentity : sapcTargetModelIdentities) {
            doReturn(targetModelIdentity).when(sapcNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(sapcNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(sapcNormNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(sapcNodeRef, "IPSEC"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(sapcNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForRNNODE() {
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(rnNodeNodeRef, "OAM"));
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(rnNodeNormNodeRef, "OAM"));
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(rnNodeNodeRef, "IPSEC"));
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(rnNodeNormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetIssueOrReissueCertWfForFIVEGRADIONODE() {
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(fiveGRadioNodeNodeRef, "OAM"));
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(fiveGRadioNodeNormNodeRef, "OAM"));
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(fiveGRadioNodeNodeRef, "IPSEC"));
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(fiveGRadioNodeNormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetIssueOrReissueCertWfForVTFRADIONODE() {
        for (final String targetModelIdentity : vtfRadioNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vtfRadioNodeNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vtfRadioNodeNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vtfRadioNodeNormNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vtfRadioNodeNodeRef, "IPSEC"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vtfRadioNodeNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForVSDNODE() {
        for (final String targetModelIdentity : vsdTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vsdNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vsdNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vsdNormNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vsdNodeRef, "IPSEC"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vsdNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForRVNFMNODE() {
        for (final String targetModelIdentity : rvnfmTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rVNFMNodeNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(rVNFMNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(rVNFMNodeNormNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(rVNFMNodeRef, "IPSEC"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(rVNFMNodeNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForVPP() {
        for (final String targetModelIdentity : vppTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vppNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vppNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vppNormNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vppNodeRef, "IPSEC"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vppNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForVRSM() {
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vrsmNodeRef, "OAM"));
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vrsmNormNodeRef, "OAM"));
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vrsmNodeRef, "IPSEC"));
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vrsmNormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetIssueOrReissueCertWfForVRC() {
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vrcNodeRef, "OAM"));
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vrcNormNodeRef, "OAM"));
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vrcNodeRef, "IPSEC"));
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vrcNormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetIssueOrReissueCertWfForVEME() {
        for (final String targetModelIdentity : vemeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vEMENormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vEMENodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vEMENormNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vEMENodeRef, "IPSEC"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vEMENormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForVWCG() {
        for (final String targetModelIdentity : vwcgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vWCGNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vWCGNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vWCGNormNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vWCGNodeRef, "IPSEC"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vWCGNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForHSSFE() {
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(hSSFENodeRef, "OAM"));
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(hSSFENormNodeRef, "OAM"));
        assertNull(beanUnderTest.getIssueOrReissueCertWf(hSSFENodeRef, "IPSEC"));
        assertNull(beanUnderTest.getIssueOrReissueCertWf(hSSFENormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetIssueOrReissueCertWfForVHSSFE() {
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vhSSFENodeRef, "OAM"));
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vhSSFENormNodeRef, "OAM"));
        assertNull(beanUnderTest.getIssueOrReissueCertWf(vhSSFENodeRef, "IPSEC"));
        assertNull(beanUnderTest.getIssueOrReissueCertWf(vhSSFENormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetIssueOrReissueCertWfForVIPWORKS() {
        for (final String targetModelIdentity : vipworksTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vIPWorksNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vIPWorksNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vIPWorksNormNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vIPWorksNodeRef, "IPSEC"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vIPWorksNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForVUPG() {
        for (final String targetModelIdentity : vupgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vUPGNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vUPGNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vUPGNormNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vUPGNodeRef, "IPSEC"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vUPGNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForBSP() {
        for (final String targetModelIdentity : bspTargetModelIdentities) {
            doReturn(targetModelIdentity).when(bSPNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(bSPNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(bSPNormNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(bSPNodeRef, "IPSEC"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(bSPNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForVBGF() {
        for (final String targetModelIdentity : vbgfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vBGFNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vBGFNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vBGFNormNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vBGFNodeRef, "IPSEC"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vBGFNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForVMRF() {
        for (final String targetModelIdentity : vmrfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vMRFNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vMRFNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vMRFNormNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vMRFNodeRef, "IPSEC"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vMRFNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForRadioNodes() {
        for (final String targetModelIdentity : radioNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(radioNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(radioNormNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(radioNodeRef, "IPSEC"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(radioNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForRadioTNodes() {
        for (final String targetModelIdentity : radioTNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioTNodeNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(radioTNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(radioTNodeNormNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(radioTNodeRef, "IPSEC"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(radioTNodeNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForEr6000() {
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(er6000NodeRef, "OAM"));
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(er6672NormNodeRef, "OAM"));
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(er6000NodeRef, "IPSEC"));
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(er6672NormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetIssueOrReissueCertWfForBsc() {
        for (final String targetModelIdentity : bscTargetModelIdentities) {
            doReturn(targetModelIdentity).when(bscNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(bscNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(bscNormNodeRef, "OAM"));
            assertNull(beanUnderTest.getIssueOrReissueCertWf(bscNodeRef, "IPSEC"));
            assertNull(beanUnderTest.getIssueOrReissueCertWf(bscNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForHlrfe() {
        for (final String targetModelIdentity : hlrfeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfeNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(hlrfeNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(hlrfeNormNodeRef, "OAM"));
            assertNull(beanUnderTest.getIssueOrReissueCertWf(hlrfeNodeRef, "IPSEC"));
            assertNull(beanUnderTest.getIssueOrReissueCertWf(hlrfeNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForvHlrfe() {
        for (final String targetModelIdentity : vhlrfeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vhlrfeNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vhlrfeNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(vhlrfeNormNodeRef, "OAM"));
            assertNull(beanUnderTest.getIssueOrReissueCertWf(vhlrfeNodeRef, "IPSEC"));
            assertNull(beanUnderTest.getIssueOrReissueCertWf(vhlrfeNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForHlrfebsp() {
        for (final String targetModelIdentity : hlrfebspTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfebspNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(hlrfebspNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(hlrfebspNormNodeRef, "OAM"));
            assertNull(beanUnderTest.getIssueOrReissueCertWf(hlrfebspNodeRef, "IPSEC"));
            assertNull(beanUnderTest.getIssueOrReissueCertWf(hlrfebspNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForHlrfeis() {
        for (final String targetModelIdentity : hlrfeisTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfeisNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(hlrfeisNodeRef, "OAM"));
            assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(hlrfeisNormNodeRef, "OAM"));
            assertNull(beanUnderTest.getIssueOrReissueCertWf(hlrfeisNodeRef, "IPSEC"));
            assertNull(beanUnderTest.getIssueOrReissueCertWf(hlrfeisNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetIssueOrReissueCertWfForMsc() {
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(mscNodeRef, "OAM"));
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(mscNormNodeRef, "OAM"));
        assertNull(beanUnderTest.getIssueOrReissueCertWf(mscNodeRef, "IPSEC"));
        assertNull(beanUnderTest.getIssueOrReissueCertWf(mscNormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetIssueOrReissueCertWfForHlr() {
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(hlrNodeRef, "OAM"));
        assertEquals("COMIssueCert", beanUnderTest.getIssueOrReissueCertWf(hlrNormNodeRef, "OAM"));
        assertNull(beanUnderTest.getIssueOrReissueCertWf(hlrNodeRef, "IPSEC"));
        assertNull(beanUnderTest.getIssueOrReissueCertWf(hlrNormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetTrustDistributeWfWithUnknownNeType() {
        assertNull(beanUnderTest.getTrustDistributeWf(unknownNodeRef, "any"));
    }

    @Test
    public void testGetTrustDistributeWfWithInvalidCertType() {
        assertNull(beanUnderTest.getTrustDistributeWf(erbsNodeRef, "OEM"));
    }

    @Test
    public void testGetTrustDistributeWfForErbs() {
        for (final String targetModelIdentity : erbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(erbsNormNodeRef).getOssModelIdentity();
            assertEquals("CPPIssueTrustCert", beanUnderTest.getTrustDistributeWf(erbsNodeRef, "OAM"));
            assertEquals("CPPIssueTrustCert", beanUnderTest.getTrustDistributeWf(erbsNormNodeRef, "OAM"));
            assertEquals("CPPIssueTrustCertIpSec", beanUnderTest.getTrustDistributeWf(erbsNodeRef, "IPSEC"));
            assertEquals("CPPIssueTrustCertIpSec", beanUnderTest.getTrustDistributeWf(erbsNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForRbs() {
        for (final String targetModelIdentity : rbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rbsNormNodeRef).getOssModelIdentity();
            assertEquals("CPPIssueTrustCert", beanUnderTest.getTrustDistributeWf(rbsNodeRef, "OAM"));
            assertEquals("CPPIssueTrustCert", beanUnderTest.getTrustDistributeWf(rbsNormNodeRef, "OAM"));
            assertEquals("CPPIssueTrustCertIpSec", beanUnderTest.getTrustDistributeWf(rbsNodeRef, "IPSEC"));
            assertEquals("CPPIssueTrustCertIpSec", beanUnderTest.getTrustDistributeWf(rbsNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForRnc() {
        for (final String targetModelIdentity : rncTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rncNormNodeRef).getOssModelIdentity();
            assertEquals("CPPIssueTrustCert", beanUnderTest.getTrustDistributeWf(rncNodeRef, "OAM"));
            assertEquals("CPPIssueTrustCert", beanUnderTest.getTrustDistributeWf(rncNormNodeRef, "OAM"));
            assertEquals("CPPIssueTrustCertIpSec", beanUnderTest.getTrustDistributeWf(rncNodeRef, "IPSEC"));
            assertEquals("CPPIssueTrustCertIpSec", beanUnderTest.getTrustDistributeWf(rncNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForMgw() {
        for (final String targetModelIdentity : mgwTargetModelIdentities) {
            doReturn(targetModelIdentity).when(mgwNormNodeRef).getOssModelIdentity();
            assertEquals("CPPIssueTrustCert", beanUnderTest.getTrustDistributeWf(mgwNodeRef, "OAM"));
            assertEquals("CPPIssueTrustCert", beanUnderTest.getTrustDistributeWf(mgwNormNodeRef, "OAM"));
            assertNull(beanUnderTest.getTrustDistributeWf(mgwNodeRef, "IPSEC"));
            assertNull(beanUnderTest.getTrustDistributeWf(mgwNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForCscf() {
        for (final String targetModelIdentity : cscfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(cscfNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(cscfNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(cscfNormNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(cscfNodeRef, "IPSEC"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(cscfNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForVCscf() {
        for (final String targetModelIdentity : vcscfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vcscfNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vcscfNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vcscfNormNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vcscfNodeRef, "IPSEC"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vcscfNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForMtas() {
        for (final String targetModelIdentity : mtasTargetModelIdentities) {
            doReturn(targetModelIdentity).when(mtasNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(mtasNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(mtasNormNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(mtasNodeRef, "IPSEC"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(mtasNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForVMtas() {
        for (final String targetModelIdentity : vmtasTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vmtasNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vmtasNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vmtasNormNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vmtasNodeRef, "IPSEC"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vmtasNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForMSRBSV1() {
        for (final String targetModelIdentity : msrbsv1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(msrbsv1NormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(msrbsv1NodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(msrbsv1NormNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(msrbsv1NodeRef, "IPSEC"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(msrbsv1NormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForSAPC() {
        for (final String targetModelIdentity : sapcTargetModelIdentities) {
            doReturn(targetModelIdentity).when(sapcNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(sapcNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(sapcNormNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(sapcNodeRef, "IPSEC"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(sapcNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForRNNODE() {
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(rnNodeNodeRef, "OAM"));
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(rnNodeNormNodeRef, "OAM"));
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(rnNodeNodeRef, "IPSEC"));
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(rnNodeNormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetTrustDistributeWfForFIVEGRADIONODE() {
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(fiveGRadioNodeNodeRef, "OAM"));
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(fiveGRadioNodeNormNodeRef, "OAM"));
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(fiveGRadioNodeNodeRef, "IPSEC"));
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(fiveGRadioNodeNormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetTrustDistributeWfForVTFRADIONODE() {
        for (final String targetModelIdentity : vtfRadioNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vtfRadioNodeNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vtfRadioNodeNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vtfRadioNodeNormNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vtfRadioNodeNodeRef, "IPSEC"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vtfRadioNodeNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForVSDNODE() {
        for (final String targetModelIdentity : vsdTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vsdNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vsdNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vsdNormNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vsdNodeRef, "IPSEC"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vsdNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForRVNFMNODE() {
        for (final String targetModelIdentity : rvnfmTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rVNFMNodeNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(rVNFMNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(rVNFMNodeNormNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(rVNFMNodeRef, "IPSEC"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(rVNFMNodeNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForVPP() {
        for (final String targetModelIdentity : vppTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vppNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vppNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vppNormNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vppNodeRef, "IPSEC"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vppNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForVRSM() {
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vrsmNodeRef, "OAM"));
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vrsmNormNodeRef, "OAM"));
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vrsmNodeRef, "IPSEC"));
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vrsmNormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetTrustDistributeWfForVRC() {
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vrcNodeRef, "OAM"));
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vrcNormNodeRef, "OAM"));
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vrcNodeRef, "IPSEC"));
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vrcNormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetTrustDistributeWfForVEME() {
        for (final String targetModelIdentity : vemeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vEMENormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vEMENodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vEMENormNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vEMENodeRef, "IPSEC"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vEMENormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForVWCG() {
        for (final String targetModelIdentity : vwcgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vWCGNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vWCGNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vWCGNormNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vWCGNodeRef, "IPSEC"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vWCGNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForHSSFE() {
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(hSSFENodeRef, "OAM"));
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(hSSFENormNodeRef, "OAM"));
        assertNull(beanUnderTest.getTrustDistributeWf(hSSFENodeRef, "IPSEC"));
        assertNull(beanUnderTest.getTrustDistributeWf(hSSFENormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetTrustDistributeWfForVHSSFE() {
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vhSSFENodeRef, "OAM"));
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vhSSFENormNodeRef, "OAM"));
        assertNull(beanUnderTest.getTrustDistributeWf(vhSSFENodeRef, "IPSEC"));
        assertNull(beanUnderTest.getTrustDistributeWf(vhSSFENormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetTrustDistributeWfForVIPWORKS() {
        for (final String targetModelIdentity : vipworksTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vIPWorksNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vIPWorksNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vIPWorksNormNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vIPWorksNodeRef, "IPSEC"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vIPWorksNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForVUPG() {
        for (final String targetModelIdentity : vupgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vUPGNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vUPGNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vUPGNormNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vUPGNodeRef, "IPSEC"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vUPGNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForBSP() {
        for (final String targetModelIdentity : bspTargetModelIdentities) {
            doReturn(targetModelIdentity).when(bSPNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(bSPNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(bSPNormNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(bSPNodeRef, "IPSEC"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(bSPNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForVBGF() {
        for (final String targetModelIdentity : vbgfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vBGFNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vBGFNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vBGFNormNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vBGFNodeRef, "IPSEC"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vBGFNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForVMRF() {
        for (final String targetModelIdentity : vmrfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vMRFNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vMRFNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vMRFNormNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vMRFNodeRef, "IPSEC"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vMRFNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForRadioNodes() {
        for (final String targetModelIdentity : radioNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(radioNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(radioNormNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(radioNodeRef, "IPSEC"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(radioNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForRadioTNodes() {
        for (final String targetModelIdentity : radioTNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioTNodeNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(radioTNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(radioTNodeNormNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(radioTNodeRef, "IPSEC"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(radioTNodeNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForEr6000() {
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(er6000NodeRef, "OAM"));
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(er6672NormNodeRef, "OAM"));
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(er6000NodeRef, "IPSEC"));
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(er6672NormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetTrustDistributeWfForBsc() {
        for (final String targetModelIdentity : bscTargetModelIdentities) {
            doReturn(targetModelIdentity).when(bscNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(bscNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(bscNormNodeRef, "OAM"));
            assertNull(beanUnderTest.getTrustDistributeWf(bscNodeRef, "IPSEC"));
            assertNull(beanUnderTest.getTrustDistributeWf(bscNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForHlrfe() {
        for (final String targetModelIdentity : hlrfeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfeNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(hlrfeNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(hlrfeNormNodeRef, "OAM"));
            assertNull(beanUnderTest.getTrustDistributeWf(hlrfeNodeRef, "IPSEC"));
            assertNull(beanUnderTest.getTrustDistributeWf(hlrfeNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForvHlrfe() {
        for (final String targetModelIdentity : vhlrfeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vhlrfeNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vhlrfeNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(vhlrfeNormNodeRef, "OAM"));
            assertNull(beanUnderTest.getTrustDistributeWf(vhlrfeNodeRef, "IPSEC"));
            assertNull(beanUnderTest.getTrustDistributeWf(vhlrfeNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForHlrfebsp() {
        for (final String targetModelIdentity : hlrfebspTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfebspNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(hlrfebspNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(hlrfebspNormNodeRef, "OAM"));
            assertNull(beanUnderTest.getTrustDistributeWf(hlrfebspNodeRef, "IPSEC"));
            assertNull(beanUnderTest.getTrustDistributeWf(hlrfebspNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForHlrfeis() {
        for (final String targetModelIdentity : hlrfeisTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfeisNormNodeRef).getOssModelIdentity();
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(hlrfeisNodeRef, "OAM"));
            assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(hlrfeisNormNodeRef, "OAM"));
            assertNull(beanUnderTest.getTrustDistributeWf(hlrfeisNodeRef, "IPSEC"));
            assertNull(beanUnderTest.getTrustDistributeWf(hlrfeisNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustDistributeWfForMsc() {
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(mscNodeRef, "OAM"));
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(mscNormNodeRef, "OAM"));
        assertNull(beanUnderTest.getTrustDistributeWf(mscNodeRef, "IPSEC"));
        assertNull(beanUnderTest.getTrustDistributeWf(mscNormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetTrustDistributeWfForHlr() {
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(hlrNodeRef, "OAM"));
        assertEquals("COMIssueTrustCert", beanUnderTest.getTrustDistributeWf(hlrNormNodeRef, "OAM"));
        assertNull(beanUnderTest.getTrustDistributeWf(hlrNodeRef, "IPSEC"));
        assertNull(beanUnderTest.getTrustDistributeWf(hlrNormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetTrustRemoveWfWithUnknownNeType() {
        assertNull(beanUnderTest.getTrustRemoveWf(unknownNodeRef, "any"));
    }

    @Test
    public void testGetTrustRemoveWfWithInvalidCertType() {
        assertNull(beanUnderTest.getTrustRemoveWf(erbsNodeRef, "OEM"));
    }

    @Test
    public void testGetTrustRemoveWfForErbs() {
        for (final String targetModelIdentity : erbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(erbsNormNodeRef).getOssModelIdentity();
            assertEquals("CPPRemoveTrustOAM", beanUnderTest.getTrustRemoveWf(erbsNodeRef, "OAM"));
            assertEquals("CPPRemoveTrustOAM", beanUnderTest.getTrustRemoveWf(erbsNormNodeRef, "OAM"));
            assertEquals("CPPRemoveTrustNewIPSEC", beanUnderTest.getTrustRemoveWf(erbsNodeRef, "IPSEC"));
            assertEquals("CPPRemoveTrustNewIPSEC", beanUnderTest.getTrustRemoveWf(erbsNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForRbs() {
        for (final String targetModelIdentity : rbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rbsNormNodeRef).getOssModelIdentity();
            assertEquals("CPPRemoveTrustOAM", beanUnderTest.getTrustRemoveWf(rbsNodeRef, "OAM"));
            assertEquals("CPPRemoveTrustOAM", beanUnderTest.getTrustRemoveWf(rbsNormNodeRef, "OAM"));
            assertEquals("CPPRemoveTrustNewIPSEC", beanUnderTest.getTrustRemoveWf(rbsNodeRef, "IPSEC"));
            assertEquals("CPPRemoveTrustNewIPSEC", beanUnderTest.getTrustRemoveWf(rbsNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForRnc() {
        for (final String targetModelIdentity : rncTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rncNormNodeRef).getOssModelIdentity();
            assertEquals("CPPRemoveTrustOAM", beanUnderTest.getTrustRemoveWf(rncNodeRef, "OAM"));
            assertEquals("CPPRemoveTrustOAM", beanUnderTest.getTrustRemoveWf(rncNormNodeRef, "OAM"));
            assertEquals("CPPRemoveTrustNewIPSEC", beanUnderTest.getTrustRemoveWf(rncNodeRef, "IPSEC"));
            assertEquals("CPPRemoveTrustNewIPSEC", beanUnderTest.getTrustRemoveWf(rncNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForMgw() {
        for (final String targetModelIdentity : mgwTargetModelIdentities) {
            doReturn(targetModelIdentity).when(mgwNormNodeRef).getOssModelIdentity();
            assertEquals("CPPRemoveTrustOAM", beanUnderTest.getTrustRemoveWf(mgwNodeRef, "OAM"));
            assertEquals("CPPRemoveTrustOAM", beanUnderTest.getTrustRemoveWf(mgwNormNodeRef, "OAM"));
            assertNull(beanUnderTest.getTrustRemoveWf(mgwNodeRef, "IPSEC"));
            assertNull(beanUnderTest.getTrustRemoveWf(mgwNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForCscf() {
        for (final String targetModelIdentity : cscfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(cscfNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(cscfNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(cscfNormNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(cscfNodeRef, "IPSEC"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(cscfNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForVCscf() {
        for (final String targetModelIdentity : vcscfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vcscfNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vcscfNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vcscfNormNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vcscfNodeRef, "IPSEC"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vcscfNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForMtas() {
        for (final String targetModelIdentity : mtasTargetModelIdentities) {
            doReturn(targetModelIdentity).when(mtasNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(mtasNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(mtasNormNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(mtasNodeRef, "IPSEC"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(mtasNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForVMtas() {
        for (final String targetModelIdentity : vmtasTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vmtasNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vmtasNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vmtasNormNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vmtasNodeRef, "IPSEC"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vmtasNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForMSRBSV1() {
        for (final String targetModelIdentity : msrbsv1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(msrbsv1NormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(msrbsv1NodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(msrbsv1NormNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(msrbsv1NodeRef, "IPSEC"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(msrbsv1NormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForSAPC() {
        for (final String targetModelIdentity : sapcTargetModelIdentities) {
            doReturn(targetModelIdentity).when(sapcNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(sapcNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(sapcNormNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(sapcNodeRef, "IPSEC"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(sapcNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForRNNODE() {
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(rnNodeNodeRef, "OAM"));
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(rnNodeNormNodeRef, "OAM"));
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(rnNodeNodeRef, "IPSEC"));
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(rnNodeNormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetTrustRemoveWfForFIVEGRADIONODE() {
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(fiveGRadioNodeNodeRef, "OAM"));
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(fiveGRadioNodeNormNodeRef, "OAM"));
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(fiveGRadioNodeNodeRef, "IPSEC"));
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(fiveGRadioNodeNormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetTrustRemoveWfForVTFRADIONODE() {
        for (final String targetModelIdentity : vtfRadioNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vtfRadioNodeNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vtfRadioNodeNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vtfRadioNodeNormNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vtfRadioNodeNodeRef, "IPSEC"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vtfRadioNodeNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForVSDNODE() {
        for (final String targetModelIdentity : vsdTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vsdNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vsdNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vsdNormNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vsdNodeRef, "IPSEC"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vsdNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForRVNFMNODE() {
        for (final String targetModelIdentity : rvnfmTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rVNFMNodeNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(rVNFMNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(rVNFMNodeNormNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(rVNFMNodeRef, "IPSEC"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(rVNFMNodeNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForVPP() {
        for (final String targetModelIdentity : vppTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vppNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vppNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vppNormNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vppNodeRef, "IPSEC"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vppNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForVRSM() {
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vrsmNodeRef, "OAM"));
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vrsmNormNodeRef, "OAM"));
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vrsmNodeRef, "IPSEC"));
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vrsmNormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetTrustRemoveWfForVRC() {
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vrcNodeRef, "OAM"));
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vrcNormNodeRef, "OAM"));
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vrcNodeRef, "IPSEC"));
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vrcNormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetTrustRemoveWfForVEME() {
        for (final String targetModelIdentity : vemeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vEMENormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vEMENodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vEMENormNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vEMENodeRef, "IPSEC"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vEMENormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForVWCG() {
        for (final String targetModelIdentity : vwcgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vWCGNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vWCGNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vWCGNormNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vWCGNodeRef, "IPSEC"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vWCGNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForHSSFE() {
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(hSSFENodeRef, "OAM"));
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(hSSFENormNodeRef, "OAM"));
        assertNull(beanUnderTest.getTrustRemoveWf(hSSFENodeRef, "IPSEC"));
        assertNull(beanUnderTest.getTrustRemoveWf(hSSFENormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetTrustRemoveWfForVHSSFE() {
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vhSSFENodeRef, "OAM"));
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vhSSFENormNodeRef, "OAM"));
        assertNull(beanUnderTest.getTrustRemoveWf(vhSSFENodeRef, "IPSEC"));
        assertNull(beanUnderTest.getTrustRemoveWf(vhSSFENormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetTrustRemoveWfForVIPWORKS() {
        for (final String targetModelIdentity : vipworksTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vIPWorksNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vIPWorksNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vIPWorksNormNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vIPWorksNodeRef, "IPSEC"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vIPWorksNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForVUPG() {
        for (final String targetModelIdentity : vupgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vUPGNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vUPGNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vUPGNormNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vUPGNodeRef, "IPSEC"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vUPGNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForBSP() {
        for (final String targetModelIdentity : bspTargetModelIdentities) {
            doReturn(targetModelIdentity).when(bSPNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(bSPNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(bSPNormNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(bSPNodeRef, "IPSEC"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(bSPNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForVBGF() {
        for (final String targetModelIdentity : vbgfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vBGFNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vBGFNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vBGFNormNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vBGFNodeRef, "IPSEC"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vBGFNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForVMRF() {
        for (final String targetModelIdentity : vmrfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vMRFNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vMRFNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vMRFNormNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vMRFNodeRef, "IPSEC"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vMRFNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForRadioNodes() {
        for (final String targetModelIdentity : radioNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(radioNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(radioNormNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(radioNodeRef, "IPSEC"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(radioNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForRadioTNodes() {
        for (final String targetModelIdentity : radioTNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioTNodeNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(radioTNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(radioTNodeNormNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(radioTNodeRef, "IPSEC"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(radioTNodeNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForEr6000() {
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(er6000NodeRef, "OAM"));
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(er6672NormNodeRef, "OAM"));
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(er6000NodeRef, "IPSEC"));
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(er6672NormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetTrustRemoveWfForBsc() {
        for (final String targetModelIdentity : bscTargetModelIdentities) {
            doReturn(targetModelIdentity).when(bscNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(bscNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(bscNormNodeRef, "OAM"));
            assertNull(beanUnderTest.getTrustRemoveWf(bscNodeRef, "IPSEC"));
            assertNull(beanUnderTest.getTrustRemoveWf(bscNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForHlrfe() {
        for (final String targetModelIdentity : hlrfeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfeNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(hlrfeNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(hlrfeNormNodeRef, "OAM"));
            assertNull(beanUnderTest.getTrustRemoveWf(hlrfeNodeRef, "IPSEC"));
            assertNull(beanUnderTest.getTrustRemoveWf(hlrfeNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForvHlrfe() {
        for (final String targetModelIdentity : vhlrfeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vhlrfeNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vhlrfeNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(vhlrfeNormNodeRef, "OAM"));
            assertNull(beanUnderTest.getTrustRemoveWf(vhlrfeNodeRef, "IPSEC"));
            assertNull(beanUnderTest.getTrustRemoveWf(vhlrfeNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForHlrfebsp() {
        for (final String targetModelIdentity : hlrfebspTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfebspNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(hlrfebspNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(hlrfebspNormNodeRef, "OAM"));
            assertNull(beanUnderTest.getTrustRemoveWf(hlrfebspNodeRef, "IPSEC"));
            assertNull(beanUnderTest.getTrustRemoveWf(hlrfebspNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForHlrfeis() {
        for (final String targetModelIdentity : hlrfeisTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfeisNormNodeRef).getOssModelIdentity();
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(hlrfeisNodeRef, "OAM"));
            assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(hlrfeisNormNodeRef, "OAM"));
            assertNull(beanUnderTest.getTrustRemoveWf(hlrfeisNodeRef, "IPSEC"));
            assertNull(beanUnderTest.getTrustRemoveWf(hlrfeisNormNodeRef, "IPSEC"));
        }
    }

    @Test
    public void testGetTrustRemoveWfForMsc() {
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(mscNodeRef, "OAM"));
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(mscNormNodeRef, "OAM"));
        assertNull(beanUnderTest.getTrustRemoveWf(mscNodeRef, "IPSEC"));
        assertNull(beanUnderTest.getTrustRemoveWf(mscNormNodeRef, "IPSEC"));
    }

    @Test
    public void testGetTrustRemoveWfForHlr() {
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(hlrNodeRef, "OAM"));
        assertEquals("COMRemoveTrust", beanUnderTest.getTrustRemoveWf(hlrNormNodeRef, "OAM"));
        assertNull(beanUnderTest.getTrustRemoveWf(hlrNodeRef, "IPSEC"));
        assertNull(beanUnderTest.getTrustRemoveWf(hlrNormNodeRef, "IPSEC"));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMirrorRootMoForNullNodeReference() {
        beanUnderTest.getMirrorRootMo(null);
    }

    @Test
    public void testGetMirrorRootMoForNullFdn() {
        when(erbsNormNodeRef.getFdn()).thenReturn(null);
        assertNull(beanUnderTest.getMirrorRootMo(erbsNormNodeRef));
    }

    @Test
    public void testGetMirrorRootMoWithNullNeType() {
        when(reader.getNormalizedNodeReference(any(NormalizableNodeReference.class))).thenReturn(nullNeTypeNormNodeRef);
        assertNull(beanUnderTest.getMirrorRootMo(nullNeTypeNormNodeRef));
    }

    public void testIsCertificateSupportedForUnknownNode() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(unknownNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForErbs() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(erbsNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForErbsNodeModelInfo() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(erbsNodeModelInfo));
    }

    @Test
    public void testIsCertificateSupportedForMgw() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(mgwNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForSgsn() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(sgsnNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForSbg() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(sbgNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForVSBG() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(vsbgNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForCscf() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(cscfNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForVCSCF() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(vcscfNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForMtas() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(mtasNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForVMTAS() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(vmtasNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForMSRBSV1() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(msrbsv1NormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForSAPC() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(sapcNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForRNNODE() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(rnNodeNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForFIVEGRADIONODE() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(fiveGRadioNodeNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForVTFRADIONODE() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(vtfRadioNodeNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForVSDNODE() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(vsdNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForRVNFMNODE() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(rVNFMNodeNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForVPP() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(vppNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForVRSM() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(vrsmNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForVRC() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(vrcNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForVEME() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(vEMENormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForVWCG() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(vWCGNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForHSSFE() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(hSSFENormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForVHSSFE() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(vhSSFENormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForVIPWORKS() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(vIPWorksNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForVUPG() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(vUPGNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForBSP() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(bSPNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForVBGF() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(vBGFNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForVMRF() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(vMRFNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForRadioNode() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(radioNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForRadioTNode() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(radioTNodeNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForEr6000() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(er6672NormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForMiniLinkIndoor() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(miniLinkIndoorNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForMiniLinkCn210() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(miniLinkcn210NormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForMiniLink665x() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(miniLink665xNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForMiniLink669x() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(miniLink669xNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForMiniLinkMW2() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(miniLinkMW2NormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForMiniLinkCn510R1() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(miniLinkcn510r1NormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForMiniLinkCn510R2() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(miniLinkcn510r2NormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForMiniLinkCn810R1() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(miniLinkcn810r1NormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForMiniLinkCn810R2() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(miniLinkcn810r2NormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForMiniLink6352() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(miniLink6352NormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForMiniLink6351() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(miniLink6351NormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForMiniLink6366() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(miniLink6366NormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForMiniLinkPT2020() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(miniLinkPT2020NormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForSwitch6391() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(switch6391NormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForFronthaul6392() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(fronthaul6392NormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForCiscoAsr9000() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(ciscoAsr9000NormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForCiscoAsr900() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(ciscoAsr900NormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForJuniperMx() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(juniperMxNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForFronthaul6080() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(fronthaul6080NormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForFronthaul6020() {
        assertFalse(beanUnderTest.isCertificateManagementSupported(fronthaul6020NormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForBsc() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(bscNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForHlrfe() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(hlrfeNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForvHlrfe() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(vhlrfeNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForHlrfebsp() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(hlrfebspNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForHlrfeis() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(hlrfeisNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForMsc() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(mscNormNodeRef));
    }

    @Test
    public void testIsCertificateSupportedForHlr() {
        assertTrue(beanUnderTest.isCertificateManagementSupported(hlrNormNodeRef));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedForNullCert() {
        beanUnderTest.isCertTypeSupported(erbsNormNodeRef, null);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedWithNullNeType() {
        beanUnderTest.isCertTypeSupported(null, "IPSEC");
    }

    @Test
    public void testIsCertTypeSupportedForInvalidCertType() {
        assertFalse(beanUnderTest.isCertTypeSupported(erbsNormNodeRef, "OEM"));
        assertFalse(beanUnderTest.isCertTypeSupported(erbsNormNodeRef, ""));
    }

    @Test
    public void testIsCertTypeSupportedForUnknownNeType() {
        assertTrue(beanUnderTest.isCertTypeSupported(unknownNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(unknownNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForErbs() {
        assertTrue(beanUnderTest.isCertTypeSupported(erbsNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(erbsNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForMgw() {
        assertFalse(beanUnderTest.isCertTypeSupported(mgwNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(mgwNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForSgsn() {
        assertTrue(beanUnderTest.isCertTypeSupported(sgsnNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(sgsnNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForSbg() {
        assertTrue(beanUnderTest.isCertTypeSupported(sbgNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(sbgNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForMSRBSV1() {
        assertTrue(beanUnderTest.isCertTypeSupported(msrbsv1NormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(msrbsv1NormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForSAPC() {
        assertTrue(beanUnderTest.isCertTypeSupported(sapcNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(sapcNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForMTAS() {
        assertTrue(beanUnderTest.isCertTypeSupported(mtasNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(mtasNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForVMTAS() {
        assertTrue(beanUnderTest.isCertTypeSupported(vmtasNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(vmtasNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForCSCF() {
        assertTrue(beanUnderTest.isCertTypeSupported(cscfNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(cscfNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForVCSCF() {
        assertTrue(beanUnderTest.isCertTypeSupported(vcscfNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(vcscfNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForRNNODE() {
        assertTrue(beanUnderTest.isCertTypeSupported(rnNodeNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(rnNodeNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForFIVEGRADIONODE() {
        assertTrue(beanUnderTest.isCertTypeSupported(fiveGRadioNodeNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(fiveGRadioNodeNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForVTFRADIONODE() {
        assertTrue(beanUnderTest.isCertTypeSupported(vtfRadioNodeNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(vtfRadioNodeNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForVSDNODE() {
        assertTrue(beanUnderTest.isCertTypeSupported(vsdNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(vsdNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForRVNFMNODE() {
        assertTrue(beanUnderTest.isCertTypeSupported(rVNFMNodeNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(rVNFMNodeNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForVPP() {
        assertTrue(beanUnderTest.isCertTypeSupported(vppNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(vppNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForVRSM() {
        assertTrue(beanUnderTest.isCertTypeSupported(vrsmNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(vrsmNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForVRC() {
        assertTrue(beanUnderTest.isCertTypeSupported(vrcNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(vrcNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForVEME() {
        assertTrue(beanUnderTest.isCertTypeSupported(vEMENormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(vEMENormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForVWCG() {
        assertTrue(beanUnderTest.isCertTypeSupported(vWCGNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(vWCGNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForHSSFE() {
        assertFalse(beanUnderTest.isCertTypeSupported(hSSFENormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(hSSFENormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForVHSSFE() {
        assertFalse(beanUnderTest.isCertTypeSupported(vhSSFENormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(vhSSFENormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForVIPWORKS() {
        assertTrue(beanUnderTest.isCertTypeSupported(vIPWorksNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(vIPWorksNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForVUPG() {
        assertTrue(beanUnderTest.isCertTypeSupported(vUPGNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(vUPGNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForBSP() {
        assertTrue(beanUnderTest.isCertTypeSupported(bSPNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(bSPNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForVBGF() {
        assertTrue(beanUnderTest.isCertTypeSupported(vBGFNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(vBGFNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForVMRF() {
        assertTrue(beanUnderTest.isCertTypeSupported(vMRFNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(vMRFNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForRadioNode() {
        assertTrue(beanUnderTest.isCertTypeSupported(radioNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(radioNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForRadioTNode() {
        assertTrue(beanUnderTest.isCertTypeSupported(radioTNodeNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(radioTNodeNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForBsc() {
        assertFalse(beanUnderTest.isCertTypeSupported(bscNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(bscNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForHlrfe() {
        assertFalse(beanUnderTest.isCertTypeSupported(hlrfeNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(hlrfeNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForvHlrfe() {
        assertFalse(beanUnderTest.isCertTypeSupported(vhlrfeNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(vhlrfeNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForHlrfebsp() {
        assertFalse(beanUnderTest.isCertTypeSupported(hlrfebspNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(hlrfebspNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForHlrfeis() {
        assertFalse(beanUnderTest.isCertTypeSupported(hlrfeisNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(hlrfeisNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForMsc() {
        assertFalse(beanUnderTest.isCertTypeSupported(mscNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(mscNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForHlr() {
        assertFalse(beanUnderTest.isCertTypeSupported(hlrNormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(hlrNormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForEr6672() {
        assertTrue(beanUnderTest.isCertTypeSupported(er6672NormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(er6672NormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForEr6675() {
        assertTrue(beanUnderTest.isCertTypeSupported(er6675NormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(er6675NormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForEr6x71() {
        assertTrue(beanUnderTest.isCertTypeSupported(er6x71NormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(er6x71NormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForEr6274() {
        assertTrue(beanUnderTest.isCertTypeSupported(er6274NormNodeRef, "IPSEC"));
        assertTrue(beanUnderTest.isCertTypeSupported(er6274NormNodeRef, "OAM"));
    }

    @Test
    public void testIsCertTypeSupportedForMiniLinkIndoor() {
        beanUnderTest.isCertTypeSupported(miniLinkIndoorNormNodeRef, "IPSEC");
        beanUnderTest.isCertTypeSupported(miniLinkIndoorNormNodeRef, "OAM");
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForNull() {
        final NodeModelInformation nmi = null;
        beanUnderTest.getDefaultAlgorithmKeys(nmi);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForNullNodeType() {
        final NodeModelInformation nmi = new NodeModelInformation(null, null, null);
        beanUnderTest.getDefaultAlgorithmKeys(nmi);
    }

    @Test
    public void testGetDefaultAlgorithmKeysForErbs() {
        assertTrue(AlgorithmKeys.RSA_2048.toString().equals(beanUnderTest.getDefaultAlgorithmKeys(erbsNormNodeRef)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForErbsNodeModelInfo() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(erbsNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForMgw() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(mgwNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForSgsn() {
        assertTrue(AlgorithmKeys.RSA_1024.equals(beanUnderTest.getDefaultAlgorithmKeys(sgsnNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForSbg() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(sbgNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForVSBG() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(vsbgNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForCscf() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(cscfNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForVCSCF() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(vcscfNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForMtas() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(mtasNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForVMTAS() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(vmtasNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForMSRBSV1() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(msrbsv1NodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForSAPC() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(sapcNodeModelInfo)));
    }

    public void testGetDefaultAlgorithmKeysForRNNODE() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(rnNodeNodeModelInfo)));
    }

    public void testGetDefaultAlgorithmKeysForFIVEGRADIONODE() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(fiveGRadioNodeNodeModelInfo)));
    }

    public void testGetDefaultAlgorithmKeysForVTFRADIONODE() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(vtfRadioNodeNodeModelInfo)));
    }

    public void testGetDefaultAlgorithmKeysForVSDNODE() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(vsdNodeModelInfo)));
    }

    public void testGetDefaultAlgorithmKeysForRVNFMNODE() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(rVNFMNodeModelInfo)));
    }

    public void testGetDefaultAlgorithmKeysForVPP() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(vppNodeModelInfo)));
    }

    public void testGetDefaultAlgorithmKeysForVRSM() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(vrsmNodeModelInfo)));
    }

    public void testGetDefaultAlgorithmKeysForVRC() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(vrcNodeModelInfo)));
    }

    public void testGetDefaultAlgorithmKeysForVEME() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(vEMENodeModelInfo)));
    }

    public void testGetDefaultAlgorithmKeysForVWCG() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(vWCGNodeModelInfo)));
    }

    public void testGetDefaultAlgorithmKeysForHSSFE() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(HSSFENodeModelInfo)));
    }

    public void testGetDefaultAlgorithmKeysForVHSSFE() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(vHSSFENodeModelInfo)));
    }

    public void testGetDefaultAlgorithmKeysForVIPWORKS() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(vIPWorksNodeModelInfo)));
    }

    public void testGetDefaultAlgorithmKeysForVUPG() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(vUPGNodeModelInfo)));
    }

    public void testGetDefaultAlgorithmKeysForBSP() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(BSPNodeModelInfo)));
    }

    public void testGetDefaultAlgorithmKeysForVBGF() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(vBGFNodeModelInfo)));
    }

    public void testGetDefaultAlgorithmKeysForVMRF() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(vMRFNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForEPG() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(epgNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForVEPG() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(vepgNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForRadioNodes() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(radioNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForRadioTNodes() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(radioTNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForEr6000() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(er6000NodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForBsc() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(bscNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForHlrfe() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(hlrfeNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForvHlrfe() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(vhlrfeNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForHlrfebsp() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(hlrfebspNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForHlrfeis() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(hlrfeisNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForMsc() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(mscNodeModelInfo)));
    }

    @Test
    public void testGetDefaultAlgorithmKeysForHlr() {
        assertTrue(AlgorithmKeys.RSA_2048.equals(beanUnderTest.getDefaultAlgorithmKeys(hlrNodeModelInfo)));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForMiniLinkIndoor() {
        beanUnderTest.getDefaultAlgorithmKeys(miniLinkIndoorNodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForMiniLinkCn210() {
        beanUnderTest.getDefaultAlgorithmKeys(miniLinkcN210NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForMiniLink665x() {
        beanUnderTest.getDefaultAlgorithmKeys(miniLink665xNodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForMiniLink669x() {
        beanUnderTest.getDefaultAlgorithmKeys(miniLink669xNodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForMiniLinkMW2() {
        beanUnderTest.getDefaultAlgorithmKeys(miniLinkMW2NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForMiniLinkCn510R1() {
        beanUnderTest.getDefaultAlgorithmKeys(miniLinkcn510r1NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForMiniLinkCn510R2() {
        beanUnderTest.getDefaultAlgorithmKeys(miniLinkcn510r2NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForMiniLinkCn810R1() {
        beanUnderTest.getDefaultAlgorithmKeys(miniLinkcn810r1NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForMiniLinkCn810R2() {
        beanUnderTest.getDefaultAlgorithmKeys(miniLinkcn810r2NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForMiniLink6352() {
        beanUnderTest.getDefaultAlgorithmKeys(miniLink6352NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForMiniLink6351() {
        beanUnderTest.getDefaultAlgorithmKeys(miniLink6351NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForMiniLink6366() {
        beanUnderTest.getDefaultAlgorithmKeys(miniLink6366NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForMiniLinkPT2020() {
        beanUnderTest.getDefaultAlgorithmKeys(miniLinkPT2020NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForSwitch6391() {
        beanUnderTest.getDefaultAlgorithmKeys(switch6391NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForFronthaul6392() {
        beanUnderTest.getDefaultAlgorithmKeys(fronthaul6392NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForCiscoAsr9000() {
        beanUnderTest.getDefaultAlgorithmKeys(ciscoAsr9000NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForCiscoAsr900() {
        beanUnderTest.getDefaultAlgorithmKeys(ciscoAsr900NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForJuniperMx() {
        beanUnderTest.getDefaultAlgorithmKeys(juniperMxNodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForFronthaul6080() {
        beanUnderTest.getDefaultAlgorithmKeys(fronthaulNodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultAlgorithmKeysForFronthaul6020() {
        beanUnderTest.getDefaultAlgorithmKeys(fronthaul6020NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForNull() {
        final NodeModelInformation nmi = null;
        beanUnderTest.getDefaultDigestAlgorithm(nmi);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForNullNodeType() {
        final NodeModelInformation nmi = new NodeModelInformation(null, null, null);
        beanUnderTest.getDefaultDigestAlgorithm(nmi);
    }

    @Test
    public void testGetDefaultDigestAlgorithmForErbs() {
        for (final String targetModelIdentity : erbsTargetModelIdentities) {
            final NodeModelInformation erbsNodeModelInfo = new NodeModelInformation(targetModelIdentity, ModelIdentifierType.OSS_IDENTIFIER, ERBS);
            assertTrue(DigestAlgorithm.SHA256.equals(beanUnderTest.getDefaultDigestAlgorithm(erbsNodeModelInfo)));
        }
    }

    @Test
    public void testGetDefaultDigestAlgorithmForRnc() {
        for (final String targetModelIdentity : rncTargetModelIdentities) {
            final NodeModelInformation rncNodeModelInfo = new NodeModelInformation(targetModelIdentity, ModelIdentifierType.OSS_IDENTIFIER, RNC);
            assertTrue(DigestAlgorithm.SHA256.equals(beanUnderTest.getDefaultDigestAlgorithm(rncNodeModelInfo)));
        }
    }

    @Test
    public void testGetDefaultDigestAlgorithmForRbs() {
        for (final String targetModelIdentity : rbsTargetModelIdentities) {
            final NodeModelInformation rbsNodeModelInfo = new NodeModelInformation(targetModelIdentity, ModelIdentifierType.OSS_IDENTIFIER, RBS);
            if (rbsTargetModelIdentitiesLesserThan14A.contains(targetModelIdentity)) {
                assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(rbsNodeModelInfo)));
            } else {
                assertTrue(DigestAlgorithm.SHA256.equals(beanUnderTest.getDefaultDigestAlgorithm(rbsNodeModelInfo)));
            }
        }
    }

    @Test
    public void testGetDefaultDigestAlgorithmForMgw() {
        for (final String targetModelIdentity : mgwTargetModelIdentities) {
            final NodeModelInformation mgwNodeModelInfo = new NodeModelInformation(targetModelIdentity, ModelIdentifierType.OSS_IDENTIFIER, MGW);
            assertTrue(DigestAlgorithm.SHA256.equals(beanUnderTest.getDefaultDigestAlgorithm(mgwNodeModelInfo)));
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForSgsn() {
        beanUnderTest.getDefaultDigestAlgorithm(sgsnNodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForSbg() {
        beanUnderTest.getDefaultDigestAlgorithm(sbgNodeModelInfo);
    }

    @Test
    public void testGetDefaultDigestAlgorithmForMSRBSV1() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(msrbsv1NodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForVSBG() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(vsbgNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForSAPC() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(sapcNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForRNNODE() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(rnNodeNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForFIVEGRADIONODE() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(fiveGRadioNodeNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForVTFRADIONODE() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(vtfRadioNodeNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForVSDNODE() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(vsdNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForRVNFMNODE() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(rVNFMNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForVPP() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(vppNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForVRSM() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(vrsmNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForVRC() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(vrcNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForVEME() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(vEMENodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForVWCG() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(vWCGNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForHSSFE() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(HSSFENodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForVHSSFE() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(vHSSFENodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForVIPWORKS() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(vIPWorksNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForVUPG() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(vUPGNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForBSP() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(BSPNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForVBGF() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(vBGFNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForVMRF() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(vMRFNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForRadioNodes() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(radioNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForRadioTNodes() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(radioTNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForBsc() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(bscNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForHlrfe() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(hlrfeNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForvHlrfe() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(vhlrfeNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForHlrfebsp() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(hlrfebspNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForHlrfeis() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(hlrfeisNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForMsc() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(mscNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForHlr() {
        assertTrue(DigestAlgorithm.SHA1.equals(beanUnderTest.getDefaultDigestAlgorithm(hlrNodeModelInfo)));
    }

    @Test
    public void testGetDefaultDigestAlgorithmForEr6000() {
        assertTrue(DigestAlgorithm.SHA256.equals(beanUnderTest.getDefaultDigestAlgorithm(er6000NodeModelInfo)));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForMiniLinkIndoor() {
        beanUnderTest.getDefaultDigestAlgorithm(miniLinkIndoorNodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForMiniLinkCn210() {
        beanUnderTest.getDefaultDigestAlgorithm(miniLinkcN210NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForMiniLink665x() {
        beanUnderTest.getDefaultDigestAlgorithm(miniLink665xNodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForMiniLink669x() {
        beanUnderTest.getDefaultDigestAlgorithm(miniLink669xNodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForMiniLinkMW2() {
        beanUnderTest.getDefaultDigestAlgorithm(miniLinkMW2NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForMiniLinkCn510() {
        beanUnderTest.getDefaultDigestAlgorithm(miniLinkcn510r1NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForMiniLinkCn510R2() {
        beanUnderTest.getDefaultDigestAlgorithm(miniLinkcn510r2NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForMiniLinkCn810R1() {
        beanUnderTest.getDefaultDigestAlgorithm(miniLinkcn810r1NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForMiniLinkCn810R2() {
        beanUnderTest.getDefaultDigestAlgorithm(miniLinkcn810r2NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForMiniLink6352() {
        beanUnderTest.getDefaultDigestAlgorithm(miniLink6352NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForMiniLink6351() {
        beanUnderTest.getDefaultDigestAlgorithm(miniLink6351NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForMiniLink6366() {
        beanUnderTest.getDefaultDigestAlgorithm(miniLink6366NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForMiniLinkPT2020() {
        beanUnderTest.getDefaultDigestAlgorithm(miniLinkPT2020NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForSwitch6391() {
        beanUnderTest.getDefaultDigestAlgorithm(switch6391NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForFronthaul6392() {
        beanUnderTest.getDefaultDigestAlgorithm(fronthaul6392NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForCiscoAsr9000() {
        beanUnderTest.getDefaultDigestAlgorithm(ciscoAsr9000NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForCiscoAsr900() {
        beanUnderTest.getDefaultDigestAlgorithm(ciscoAsr900NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForJuniperMx() {
        beanUnderTest.getDefaultDigestAlgorithm(juniperMxNodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForFronthaul6080() {
        beanUnderTest.getDefaultDigestAlgorithm(fronthaulNodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultDigestAlgorithmForFronthaul6020() {
        beanUnderTest.getDefaultDigestAlgorithm(fronthaul6020NodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertificateAuthorityDnSupportedForNull() {
        final NodeModelInformation nmi = null;
        beanUnderTest.isCertificateAuthorityDnSupported(nmi);
    }

    @Test
    public void testIsCertificateAuthorityDnSupportedForErbsNodeModelInfo() {
        when(nscsModelServiceImpl.isCertificateAuthorityDnDefinedInEnrollmentData(any(String.class), any(String.class), any(String.class)))
                .thenReturn(false);
        assertFalse(beanUnderTest.isCertificateAuthorityDnSupported(erbsNodeModelInfo));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForNullNodeReference() {
        final NodeReference nodeRef = null;
        beanUnderTest.getMomType(nodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForNullNormalizableNodeReference() {
        final NormalizableNodeReference normNodeRef = null;
        beanUnderTest.getMomType(normNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeWithNullNeType() {
        when(reader.getNormalizedNodeReference(any(NormalizableNodeReference.class))).thenReturn(nullNeTypeNormNodeRef);
        beanUnderTest.getMomType(nullNeTypeNormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForInvalidNodeReference() {
        beanUnderTest.getMomType(unknownNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForInvalidNormalizableNodeReference() {
        beanUnderTest.getMomType(unknownNormNodeRef);
    }

    @Test
    public void testGetMomTypeForErbs() {
        assertTrue(CPP_MOM.equals(beanUnderTest.getMomType(erbsNodeRef)));
        assertTrue(CPP_MOM.equals(beanUnderTest.getMomType(erbsNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForMgw() {
        assertTrue(CPP_MOM.equals(beanUnderTest.getMomType(mgwNodeRef)));
        assertTrue(CPP_MOM.equals(beanUnderTest.getMomType(mgwNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForRNC() {
        assertTrue(CPP_MOM.equals(beanUnderTest.getMomType(rncNodeRef)));
        assertTrue(CPP_MOM.equals(beanUnderTest.getMomType(rncNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForRBS() {
        assertTrue(CPP_MOM.equals(beanUnderTest.getMomType(rbsNodeRef)));
        assertTrue(CPP_MOM.equals(beanUnderTest.getMomType(rbsNormNodeRef)));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForSgsnNodeReference() {
        beanUnderTest.getMomType(sgsnNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForSgsnNormalizableNodeReference() {
        beanUnderTest.getMomType(sgsnNormNodeRef);
    }

    @Test
    public void testGetMomTypeForSBG() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(sbgNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(sbgNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForVSBG() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vsbgNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vsbgNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForCSCF() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(cscfNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(cscfNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForVCSCF() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vcscfNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vcscfNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForMTAS() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(mtasNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(mtasNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForVMTAS() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vmtasNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vmtasNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForMSRBSV1() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(msrbsv1NodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(msrbsv1NormNodeRef)));
    }

    @Test
    public void testGetMomTypeForSAPC() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(sapcNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(sapcNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForRNNODE() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(rnNodeNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(rnNodeNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForFIVEGRADIONODE() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(fiveGRadioNodeNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(fiveGRadioNodeNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForVTFRADIONODE() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vtfRadioNodeNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vtfRadioNodeNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForVSDNODE() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vsdNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vsdNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForRVNFMNODE() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(rVNFMNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(rVNFMNodeNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForVPP() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vppNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vppNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForVRSM() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vrsmNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vrsmNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForVRC() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vrcNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vrcNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForVEME() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vEMENormNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vEMENormNodeRef)));
    }

    @Test
    public void testGetMomTypeForVWCG() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vWCGNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vWCGNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForHSSFE() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(hSSFENodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(hSSFENormNodeRef)));
    }

    @Test
    public void testGetMomTypeForVHSSFE() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vhSSFENodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vhSSFENormNodeRef)));
    }

    @Test
    public void testGetMomTypeForVIPWORKS() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vIPWorksNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vIPWorksNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForVUPG() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vUPGNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vUPGNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForBSP() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(bSPNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(bSPNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForVBGF() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vBGFNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vBGFNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForVMRF() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vMRFNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vMRFNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForRadioNode() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(radioNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(radioNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForRadioTNode() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(radioTNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(radioTNodeNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForBsc() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(bscNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(bscNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForHlrfe() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(hlrfeNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(hlrfeNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForvHlrfe() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vhlrfeNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(vhlrfeNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForHlrfe_bsp() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(hlrfebspNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(hlrfebspNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForHlrfeis() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(hlrfeisNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(hlrfeisNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForMsc() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(mscNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(mscNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForHlr() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(hlrNodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(hlrNormNodeRef)));
    }

    @Test
    public void testGetMomTypeForRouter6672() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(er6000NodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(er6672NormNodeRef)));
    }

    @Test
    public void testGetMomTypeForRouter6675() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(er6000NodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(er6675NormNodeRef)));
    }

    @Test
    public void testGetMomTypeForRouter6x71() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(er6000NodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(er6x71NormNodeRef)));
    }

    @Test
    public void testGetMomTypeForRouter6274() {
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(er6000NodeRef)));
        assertTrue(ECIM_MOM.equals(beanUnderTest.getMomType(er6274NormNodeRef)));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLinkIndoorNodeReference() {
        beanUnderTest.getMomType(miniLinkIndoorNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLinkIndoorNormalizableNodeReference() {
        beanUnderTest.getMomType(miniLinkIndoorNormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLinkCn210NodeReference() {
        beanUnderTest.getMomType(miniLinkcn210NodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLink665xNodeReference() {
        beanUnderTest.getMomType(miniLink665xNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLink669xNodeReference() {
        beanUnderTest.getMomType(miniLink669xNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLinkMW2NodeReference() {
        beanUnderTest.getMomType(miniLinkMW2NodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLinkCn210NormalizableNodeReference() {
        beanUnderTest.getMomType(miniLinkcn210NormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLinkCn510R1NodeReference() {
        beanUnderTest.getMomType(miniLinkcn510r1NodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLinkCn510R1NormalizableNodeReference() {
        beanUnderTest.getMomType(miniLinkcn510r1NormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLinkCn510R2NodeReference() {
        beanUnderTest.getMomType(miniLinkcn510r2NodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLinkCn510R2NormalizableNodeReference() {
        beanUnderTest.getMomType(miniLinkcn510r2NormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLinkCn810R1NodeReference() {
        beanUnderTest.getMomType(miniLinkcn810r1NodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLinkCn810R1NormalizableNodeReference() {
        beanUnderTest.getMomType(miniLinkcn810r1NormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLinkCn810R2NodeReference() {
        beanUnderTest.getMomType(miniLinkcn810r2NodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLinkCn810R2NormalizableNodeReference() {
        beanUnderTest.getMomType(miniLinkcn810r2NormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLink6352NodeReference() {
        beanUnderTest.getMomType(miniLink6352NodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLink6352NormalizableNodeReference() {
        beanUnderTest.getMomType(miniLink6352NormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLink6351NodeReference() {
        beanUnderTest.getMomType(miniLink6351NodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLink6351NormalizableNodeReference() {
        beanUnderTest.getMomType(miniLink6351NormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLink6366NodeReference() {
        beanUnderTest.getMomType(miniLink6366NodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLink6366NormalizableNodeReference() {
        beanUnderTest.getMomType(miniLink6366NormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLinkPT2020NodeReference() {
        beanUnderTest.getMomType(miniLinkPT2020NodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForMiniLinkPT2020NormalizableNodeReference() {
        beanUnderTest.getMomType(miniLinkPT2020NormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForSwitch6391NodeReference() {
        beanUnderTest.getMomType(switch6391NodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForSwitch6391NormalizableNodeReference() {
        beanUnderTest.getMomType(switch6391NormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForFronthaul6392NodeReference() {
        beanUnderTest.getMomType(fronthaul6392NodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForFronthaul6392NormalizableNodeReference() {
        beanUnderTest.getMomType(fronthaul6392NormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForCiscoAsr9000NodeReference() {
        beanUnderTest.getMomType(ciscoAsr9000NodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForCiscoAsr9000NormalizableNodeReference() {
        beanUnderTest.getMomType(ciscoAsr9000NormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForCiscoAsr900NodeReference() {
        beanUnderTest.getMomType(ciscoAsr900NodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForCiscoAsr900NormalizableNodeReference() {
        beanUnderTest.getMomType(ciscoAsr900NormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForJuniperMxNodeReference() {
        beanUnderTest.getMomType(juniperMxNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForJuniperMxNormalizableNodeReference() {
        beanUnderTest.getMomType(juniperMxNormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForFronthaul6080NodeReference() {
        beanUnderTest.getMomType(fronthaulNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForFronthaul6080NormalizableNodeReference() {
        beanUnderTest.getMomType(fronthaul6080NormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForFronthaul6020NodeReference() {
        beanUnderTest.getMomType(fronthaul6020NodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetMomTypeForFronthaul6020NormalizableNodeReference() {
        beanUnderTest.getMomType(fronthaul6020NormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsSecurityLevelSupportedForNullNode() {
        beanUnderTest.isSecurityLevelSupported(null, "LEVEL_1");
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsSecurityLevelSupportedForNullSecurityLevel() {
        beanUnderTest.isSecurityLevelSupported(erbsNormNodeRef, null);
    }

    @Test
    public void testIsSecurityLevelSupportedForUnknownNode() {
        assertFalse(beanUnderTest.isSecurityLevelSupported(unknownNormNodeRef, "LEVEL_1"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(unknownNormNodeRef, "LEVEL_2"));
        assertTrue(beanUnderTest.isSecurityLevelSupported(unknownNormNodeRef, "LEVEL_NOT_SUPPORTED"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(unknownNormNodeRef, "LEVEL_UNKNOWN"));
    }

    @Test
    public void testIsSecurityLevelSupportedForErbs() {
        for (final String targetModelIdentity : erbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(erbsNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isSecurityLevelSupported(erbsNormNodeRef, "LEVEL_1"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(erbsNormNodeRef, "LEVEL_2"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(erbsNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(erbsNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForRNC() {
        for (final String targetModelIdentity : rncTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rncNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isSecurityLevelSupported(rncNormNodeRef, "LEVEL_1"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(rncNormNodeRef, "LEVEL_2"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(rncNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(rncNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForRBS() {
        for (final String targetModelIdentity : rbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rbsNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isSecurityLevelSupported(rbsNormNodeRef, "LEVEL_1"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(rbsNormNodeRef, "LEVEL_2"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(rbsNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(rbsNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForMgw() {
        for (final String targetModelIdentity : mgwTargetModelIdentities) {
            doReturn(targetModelIdentity).when(mgwNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isSecurityLevelSupported(mgwNormNodeRef, "LEVEL_1"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(mgwNormNodeRef, "LEVEL_2"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(mgwNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(mgwNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForSgsn() {
        for (final String targetModelIdentity : sgsnTargetModelIdentities) {
            doReturn(targetModelIdentity).when(sgsnNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(sgsnNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(sgsnNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(sgsnNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(sgsnNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForSbg() {
        for (final String targetModelIdentity : sbgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(sbgNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(sbgNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(sbgNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(sbgNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(sbgNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForVSBG() {
        for (final String targetModelIdentity : vsbgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vsbgNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(vsbgNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vsbgNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(vsbgNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vsbgNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForCscf() {
        for (final String targetModelIdentity : cscfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(cscfNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(cscfNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(cscfNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(cscfNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(cscfNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForVCSCF() {
        for (final String targetModelIdentity : vcscfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vcscfNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(vcscfNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vcscfNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(vcscfNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vcscfNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForMtas() {
        for (final String targetModelIdentity : mtasTargetModelIdentities) {
            doReturn(targetModelIdentity).when(mtasNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(mtasNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(mtasNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(mtasNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(mtasNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForVMTAS() {
        for (final String targetModelIdentity : vmtasTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vmtasNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(vmtasNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vmtasNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(vmtasNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vmtasNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForMSRBSV1() {
        for (final String targetModelIdentity : msrbsv1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(msrbsv1NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(msrbsv1NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(msrbsv1NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(msrbsv1NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(msrbsv1NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForSAPC() {
        for (final String targetModelIdentity : sapcTargetModelIdentities) {
            doReturn(targetModelIdentity).when(sapcNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(sapcNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(sapcNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(sapcNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(sapcNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForRNNODE() {
        assertFalse(beanUnderTest.isSecurityLevelSupported(rnNodeNormNodeRef, "LEVEL_1"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(rnNodeNormNodeRef, "LEVEL_2"));
        assertTrue(beanUnderTest.isSecurityLevelSupported(rnNodeNormNodeRef, "LEVEL_NOT_SUPPORTED"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(rnNodeNormNodeRef, "LEVEL_UNKNOWN"));
    }

    @Test
    public void testIsSecurityLevelSupportedForFIVEGRADIONODE() {
        assertFalse(beanUnderTest.isSecurityLevelSupported(fiveGRadioNodeNormNodeRef, "LEVEL_1"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(fiveGRadioNodeNormNodeRef, "LEVEL_2"));
        assertTrue(beanUnderTest.isSecurityLevelSupported(fiveGRadioNodeNormNodeRef, "LEVEL_NOT_SUPPORTED"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(fiveGRadioNodeNormNodeRef, "LEVEL_UNKNOWN"));
    }

    @Test
    public void testIsSecurityLevelSupportedForVTFRADIONODE() {
        for (final String targetModelIdentity : vtfRadioNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vtfRadioNodeNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(vtfRadioNodeNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vtfRadioNodeNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(vtfRadioNodeNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vtfRadioNodeNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForVSDNODE() {
        for (final String targetModelIdentity : vsdTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vsdNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(vsdNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vsdNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(vsdNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vsdNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForRVNFMNODE() {
        for (final String targetModelIdentity : rvnfmTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rVNFMNodeNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(rVNFMNodeNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(rVNFMNodeNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(rVNFMNodeNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(rVNFMNodeNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForVPP() {
        for (final String targetModelIdentity : vppTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vppNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(vppNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vppNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(vppNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vppNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForVRSM() {
        assertFalse(beanUnderTest.isSecurityLevelSupported(vrsmNormNodeRef, "LEVEL_1"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(vrsmNormNodeRef, "LEVEL_2"));
        assertTrue(beanUnderTest.isSecurityLevelSupported(vrsmNormNodeRef, "LEVEL_NOT_SUPPORTED"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(vrsmNormNodeRef, "LEVEL_UNKNOWN"));
    }

    @Test
    public void testIsSecurityLevelSupportedForVRC() {
        assertFalse(beanUnderTest.isSecurityLevelSupported(vrcNormNodeRef, "LEVEL_1"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(vrcNormNodeRef, "LEVEL_2"));
        assertTrue(beanUnderTest.isSecurityLevelSupported(vrcNormNodeRef, "LEVEL_NOT_SUPPORTED"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(vrcNormNodeRef, "LEVEL_UNKNOWN"));
    }

    @Test
    public void testIsSecurityLevelSupportedForVEME() {
        for (final String targetModelIdentity : vemeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vEMENormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(vEMENormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vEMENormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(vEMENormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vEMENormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForVWCG() {
        for (final String targetModelIdentity : vwcgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vWCGNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(vWCGNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vWCGNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(vWCGNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vWCGNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForHSSFE() {
        assertFalse(beanUnderTest.isSecurityLevelSupported(hSSFENormNodeRef, "LEVEL_1"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(hSSFENormNodeRef, "LEVEL_2"));
        assertTrue(beanUnderTest.isSecurityLevelSupported(hSSFENormNodeRef, "LEVEL_NOT_SUPPORTED"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(hSSFENormNodeRef, "LEVEL_UNKNOWN"));
    }

    @Test
    public void testIsSecurityLevelSupportedForVHSSFE() {
        assertFalse(beanUnderTest.isSecurityLevelSupported(vhSSFENormNodeRef, "LEVEL_1"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(vhSSFENormNodeRef, "LEVEL_2"));
        assertTrue(beanUnderTest.isSecurityLevelSupported(vhSSFENormNodeRef, "LEVEL_NOT_SUPPORTED"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(vhSSFENormNodeRef, "LEVEL_UNKNOWN"));
    }

    @Test
    public void testIsSecurityLevelSupportedForVIPWORKS() {
        for (final String targetModelIdentity : vipworksTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vIPWorksNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(vIPWorksNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vIPWorksNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(vIPWorksNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vIPWorksNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForVUPG() {
        for (final String targetModelIdentity : vupgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vUPGNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(vUPGNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vUPGNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(vUPGNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vUPGNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForBSP() {
        for (final String targetModelIdentity : bspTargetModelIdentities) {
            doReturn(targetModelIdentity).when(bSPNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(bSPNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(bSPNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(bSPNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(bSPNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForVBGF() {
        for (final String targetModelIdentity : vbgfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vBGFNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(vBGFNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vBGFNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(vBGFNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vBGFNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForVMRF() {
        for (final String targetModelIdentity : vmrfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vMRFNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(vMRFNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vMRFNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(vMRFNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vMRFNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForEPG() {
        for (final String targetModelIdentity : epgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(epgNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(epgNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(epgNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(epgNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(epgNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForVEPG() {
        for (final String targetModelIdentity : vepgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vepgNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(vepgNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vepgNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(vepgNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vepgNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForRadioNode() {
        for (final String targetModelIdentity : radioNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(radioNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(radioNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(radioNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(radioNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForRadioTNode() {
        for (final String targetModelIdentity : radioTNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioTNodeNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(radioTNodeNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(radioTNodeNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(radioTNodeNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(radioTNodeNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForEr6672() {
        for (final String targetModelIdentity : er6672TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6672NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(er6672NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(er6672NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(er6672NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(er6672NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForEr6675() {
        for (final String targetModelIdentity : er6675TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6675NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(er6675NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(er6675NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(er6675NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(er6675NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForEr6x71() {
        for (final String targetModelIdentity : er6x71TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6x71NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(er6x71NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(er6x71NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(er6x71NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(er6x71NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForEr6274() {
        for (final String targetModelIdentity : er6274TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6274NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(er6274NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(er6274NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(er6274NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(er6274NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForMiniLinkIndoor() {
        for (final String targetModelIdentity : miniLinkIndoorTargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkIndoorNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkIndoorNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkIndoorNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(miniLinkIndoorNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkIndoorNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForMiniLinkCn210() {
        for (final String targetModelIdentity : miniLinkCn210TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn210NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkcn210NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkcn210NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(miniLinkcn210NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkcn210NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForMiniLink665x() {
        for (final String targetModelIdentity : miniLink665xTargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink665xNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLink665xNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLink665xNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(miniLink665xNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLink665xNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForMiniLink669x() {
        for (final String targetModelIdentity : miniLink669xTargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink669xNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLink669xNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLink669xNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(miniLink669xNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLink669xNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForMiniLinkMW2() {
        for (final String targetModelIdentity : miniLinkMW2TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkMW2NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkMW2NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkMW2NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(miniLinkMW2NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkMW2NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForMiniLinkCn510R1() {
        for (final String targetModelIdentity : miniLinkCn510R1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn510r1NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkcn510r1NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkcn510r1NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(miniLinkcn510r1NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkcn510r1NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForMiniLinkCn510R2() {
        for (final String targetModelIdentity : miniLinkCn510R2TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn510r2NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkcn510r2NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkcn510r2NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(miniLinkcn510r2NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkcn510r2NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForMiniLinkCn810R1() {
        for (final String targetModelIdentity : miniLinkCn810R1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn810r1NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkcn810r1NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkcn810r1NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(miniLinkcn810r1NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkcn810r1NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForMiniLinkCn810R2() {
        for (final String targetModelIdentity : miniLinkCn810R2TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn810r2NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkcn810r2NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkcn810r2NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(miniLinkcn810r2NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkcn810r2NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForMiniLink6352() {
        for (final String targetModelIdentity : miniLink6352TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink6352NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLink6352NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLink6352NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(miniLink6352NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLink6352NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForMiniLink6351() {
        for (final String targetModelIdentity : miniLink6351TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink6351NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLink6351NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLink6351NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(miniLink6351NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLink6351NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForMiniLink6366() {
        assertFalse(beanUnderTest.isSecurityLevelSupported(miniLink6366NormNodeRef, "LEVEL_1"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(miniLink6366NormNodeRef, "LEVEL_2"));
        assertTrue(beanUnderTest.isSecurityLevelSupported(miniLink6366NormNodeRef, "LEVEL_NOT_SUPPORTED"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(miniLink6366NormNodeRef, "LEVEL_UNKNOWN"));
    }

    @Test
    public void testIsSecurityLevelSupportedForMiniLinkPT2020() {
        for (final String targetModelIdentity : miniLinkPt2020TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkPT2020NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkPT2020NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkPT2020NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(miniLinkPT2020NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(miniLinkPT2020NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForSwitch6391() {
        for (final String targetModelIdentity : switch6391TargetModelIdentities) {
            doReturn(targetModelIdentity).when(switch6391NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(switch6391NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(switch6391NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(switch6391NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(switch6391NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForFronthaul6392() {
        for (final String targetModelIdentity : fronthaul6392TargetModelIdentities) {
            doReturn(targetModelIdentity).when(fronthaul6392NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(fronthaul6392NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(fronthaul6392NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(fronthaul6392NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(fronthaul6392NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForCiscoAsr9000() {
        for (final String targetModelIdentity : ciscoAsr9000TargetModelIdentities) {
            doReturn(targetModelIdentity).when(ciscoAsr9000NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(ciscoAsr9000NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(ciscoAsr9000NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(ciscoAsr9000NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(ciscoAsr9000NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForCiscoAsr900() {
        for (final String targetModelIdentity : ciscoAsr900TargetModelIdentities) {
            doReturn(targetModelIdentity).when(ciscoAsr900NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(ciscoAsr900NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(ciscoAsr900NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(ciscoAsr900NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(ciscoAsr900NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForJuniperMx() {
        for (final String targetModelIdentity : juniperMxTargetModelIdentities) {
            doReturn(targetModelIdentity).when(juniperMxNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(juniperMxNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(juniperMxNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(juniperMxNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(juniperMxNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForFronthaul6080() {
        for (final String targetModelIdentity : fronthaul6080TargetModelIdentities) {
            doReturn(targetModelIdentity).when(fronthaul6080NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(fronthaul6080NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(fronthaul6080NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(fronthaul6080NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(fronthaul6080NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForFronthaul6020() {
        for (final String targetModelIdentity : fronthaul6020TargetModelIdentities) {
            doReturn(targetModelIdentity).when(fronthaul6020NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(fronthaul6020NormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(fronthaul6020NormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(fronthaul6020NormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(fronthaul6020NormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForBsc() {
        for (final String targetModelIdentity : bscTargetModelIdentities) {
            doReturn(targetModelIdentity).when(bscNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(bscNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(bscNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(bscNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(bscNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForHlrfe() {
        for (final String targetModelIdentity : hlrfeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfeNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(hlrfeNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(hlrfeNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(hlrfeNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(hlrfeNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForvHlrfe() {
        for (final String targetModelIdentity : vhlrfeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vhlrfeNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(vhlrfeNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vhlrfeNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(vhlrfeNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(vhlrfeNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForHlrfebsp() {
        for (final String targetModelIdentity : hlrfebspTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfebspNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(hlrfebspNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(hlrfebspNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(hlrfebspNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(hlrfebspNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForHlrfeis() {
        for (final String targetModelIdentity : hlrfeisTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfeisNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isSecurityLevelSupported(hlrfeisNormNodeRef, "LEVEL_1"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(hlrfeisNormNodeRef, "LEVEL_2"));
            assertTrue(beanUnderTest.isSecurityLevelSupported(hlrfeisNormNodeRef, "LEVEL_NOT_SUPPORTED"));
            assertFalse(beanUnderTest.isSecurityLevelSupported(hlrfeisNormNodeRef, "LEVEL_UNKNOWN"));
        }
    }

    @Test
    public void testIsSecurityLevelSupportedForMsc() {
        assertFalse(beanUnderTest.isSecurityLevelSupported(mscNormNodeRef, "LEVEL_1"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(mscNormNodeRef, "LEVEL_2"));
        assertTrue(beanUnderTest.isSecurityLevelSupported(mscNormNodeRef, "LEVEL_NOT_SUPPORTED"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(mscNormNodeRef, "LEVEL_UNKNOWN"));
    }

    @Test
    public void testIsSecurityLevelSupportedForHlr() {
        assertFalse(beanUnderTest.isSecurityLevelSupported(hlrNormNodeRef, "LEVEL_1"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(hlrNormNodeRef, "LEVEL_2"));
        assertTrue(beanUnderTest.isSecurityLevelSupported(hlrNormNodeRef, "LEVEL_NOT_SUPPORTED"));
        assertFalse(beanUnderTest.isSecurityLevelSupported(hlrNormNodeRef, "LEVEL_UNKNOWN"));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsConfiguredSubjectNameUsedForEnrollmentWithNullNodeRef() {
        final NormalizableNodeReference normNodeRef = null;
        beanUnderTest.isConfiguredSubjectNameUsedForEnrollment(normNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsConfiguredSubjectNameUsedForEnrollmentWithNullNodeModelInfo() {
        final NodeModelInformation nodeModelInfo = null;
        beanUnderTest.isConfiguredSubjectNameUsedForEnrollment(nodeModelInfo);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsConfiguredSubjectNameUsedForEnrollmentWithNullNeType() {
        when(reader.getNormalizedNodeReference(any(NormalizableNodeReference.class))).thenReturn(nullNeTypeNormNodeRef);
        beanUnderTest.isConfiguredSubjectNameUsedForEnrollment(nullNeTypeNormNodeRef);
    }

    @Test
    public void testIsConfiguredSubjectNameUsedForEnrollmentWithValidNeTypeNullTMI() {
        when(radioNormNodeRef.getOssModelIdentity()).thenReturn(null);
        assertTrue(beanUnderTest.isConfiguredSubjectNameUsedForEnrollment(radioNormNodeRef));
    }

    @Test
    public void testIsConfiguredSubjectNameUsedForEnrollmentWithUnsupportedNeTypeNullTMI() {
        when(unknownNormNodeRef.getOssModelIdentity()).thenReturn(null);
        beanUnderTest.isConfiguredSubjectNameUsedForEnrollment(unknownNormNodeRef);
    }

    @Test
    public void testIsConfiguredSubjectNameUsedForEnrollmentWithNeTypeNotSupportingCertNullTMI() {
        when(sgsnNormNodeRef.getOssModelIdentity()).thenReturn(null);
        beanUnderTest.isConfiguredSubjectNameUsedForEnrollment(sgsnNormNodeRef);
    }

    @Test
    public void testIsConfiguredSubjectNameUsedForEnrollmentWithValidNeTypeValidTMI() {
        when(radioNormNodeRef.getOssModelIdentity()).thenReturn("16B-R28GY");
        assertTrue(beanUnderTest.isConfiguredSubjectNameUsedForEnrollment(radioNormNodeRef));
    }

    @Test
    public void testIsConfiguredSubjectNameUsedForEnrollmentForMsrbsv1() {
        assertFalse(beanUnderTest.isConfiguredSubjectNameUsedForEnrollment(msrbsv1NormNodeRef));
        assertFalse(beanUnderTest.isConfiguredSubjectNameUsedForEnrollment(msrbsv1NodeModelInfo));
    }

    @Test
    public void testIsConfiguredSubjectNameUsedForEnrollmentForRadioNode() {
        assertTrue(beanUnderTest.isConfiguredSubjectNameUsedForEnrollment(radioNormNodeRef));
        assertTrue(beanUnderTest.isConfiguredSubjectNameUsedForEnrollment(radioNodeModelInfo));
    }

    @Test
    public void testIsConfiguredSubjectNameUsedForEnrollmentForBsc() {
        assertTrue(beanUnderTest.isConfiguredSubjectNameUsedForEnrollment(bscNormNodeRef));
        assertTrue(beanUnderTest.isConfiguredSubjectNameUsedForEnrollment(bscNodeModelInfo));
    }

    @Test
    public void testIsConfiguredSubjectNameUsedForEnrollmentForEr6672() {
        assertTrue(beanUnderTest.isConfiguredSubjectNameUsedForEnrollment(er6672NormNodeRef));
        assertTrue(beanUnderTest.isConfiguredSubjectNameUsedForEnrollment(er6000NodeModelInfo));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsIkev2PolicyProfileSupportedWithNullNodeRef() {
        final NormalizableNodeReference normNodeRef = null;
        beanUnderTest.isIkev2PolicyProfileSupported(normNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsIkev2PolicyProfileSupportedWithNullNeType() {
        when(reader.getNormalizedNodeReference(any(NormalizableNodeReference.class))).thenReturn(nullNeTypeNormNodeRef);
        beanUnderTest.isIkev2PolicyProfileSupported(nullNeTypeNormNodeRef);
    }

    @Test
    public void testIsIkev2PolicyProfileSupportedWithValidNeTypeNullTMI() {
        when(radioNormNodeRef.getOssModelIdentity()).thenReturn(null);
        assertTrue(beanUnderTest.isIkev2PolicyProfileSupported(radioNormNodeRef));
    }

    @Test
    public void testIsIkev2PolicyProfileSupportedWithUnknownNeTypeNullTMI() {
        when(unknownNormNodeRef.getOssModelIdentity()).thenReturn(null);
        beanUnderTest.isIkev2PolicyProfileSupported(unknownNormNodeRef);
    }

    @Test
    public void testIsIkev2PolicyProfileSupportedWithValidNeTypeValidTMI() {
        when(radioNormNodeRef.getOssModelIdentity()).thenReturn("16B-R28GY");
        assertTrue(beanUnderTest.isIkev2PolicyProfileSupported(radioNormNodeRef));
    }

    @Test
    public void testIsIkev2PolicyProfileSupportedForNotSupporting() {
        assertFalse(beanUnderTest.isIkev2PolicyProfileSupported(erbsNormNodeRef));
        assertFalse(beanUnderTest.isIkev2PolicyProfileSupported(rbsNormNodeRef));
        assertFalse(beanUnderTest.isIkev2PolicyProfileSupported(rncNormNodeRef));
        assertFalse(beanUnderTest.isIkev2PolicyProfileSupported(mgwNormNodeRef));
        assertFalse(beanUnderTest.isIkev2PolicyProfileSupported(msrbsv1NormNodeRef));
        assertFalse(beanUnderTest.isIkev2PolicyProfileSupported(er6672NormNodeRef));
        assertFalse(beanUnderTest.isIkev2PolicyProfileSupported(er6675NormNodeRef));
        assertFalse(beanUnderTest.isIkev2PolicyProfileSupported(er6x71NormNodeRef));
        assertFalse(beanUnderTest.isIkev2PolicyProfileSupported(er6274NormNodeRef));
    }

    @Test
    public void testIsIkev2PolicyProfileSupportedForSupporting() {
        assertTrue(beanUnderTest.isIkev2PolicyProfileSupported(radioNormNodeRef));
        assertTrue(beanUnderTest.isIkev2PolicyProfileSupported(sapcNormNodeRef));
        assertTrue(beanUnderTest.isIkev2PolicyProfileSupported(radioTNodeNormNodeRef));
        assertTrue(beanUnderTest.isIkev2PolicyProfileSupported(bscNormNodeRef));
        assertTrue(beanUnderTest.isIkev2PolicyProfileSupported(mscNormNodeRef));
        assertTrue(beanUnderTest.isIkev2PolicyProfileSupported(hlrNormNodeRef));
        assertTrue(beanUnderTest.isIkev2PolicyProfileSupported(hlrfeNormNodeRef));
        assertTrue(beanUnderTest.isIkev2PolicyProfileSupported(vhlrfeNormNodeRef));
        assertTrue(beanUnderTest.isIkev2PolicyProfileSupported(hlrfebspNormNodeRef));
        assertTrue(beanUnderTest.isIkev2PolicyProfileSupported(hlrfeisNormNodeRef));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetSupportedCiphersProtocolTypesWithNullNodeRef() {
        final NormalizableNodeReference normNodeRef = null;
        beanUnderTest.getSupportedCipherProtocolTypes(normNodeRef);
    }

    @Test
    public void testGetSupportedCiphersProtocolTypesWithUnknownNeType() {
        final List<String> expected = Arrays.asList("SSH/SFTP", "SSL/HTTPS/TLS");
        final List<String> actual = beanUnderTest.getSupportedCipherProtocolTypes(unknownNormNodeRef);
        assertNotNull(actual);
        Collections.sort(expected);
        Collections.sort(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetSupportedCiphersProtocolTypesForErbs() {
        final List<String> expected = Arrays.asList("SSH/SFTP", "SSL/HTTPS/TLS");
        Collections.sort(expected);
        for (final String targetModelIdentity : erbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(erbsNormNodeRef).getOssModelIdentity();
            final List<String> actual = beanUnderTest.getSupportedCipherProtocolTypes(erbsNormNodeRef);
            assertNotNull(actual);
            Collections.sort(actual);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetSupportedCiphersProtocolTypesForRadioTNode() {
        final List<String> expected = Arrays.asList("SSH/SFTP", "SSL/HTTPS/TLS");
        Collections.sort(expected);
        for (final String targetModelIdentity : radioTNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioTNodeNormNodeRef).getOssModelIdentity();
            final List<String> actual = beanUnderTest.getSupportedCipherProtocolTypes(radioTNodeNormNodeRef);
            assertNotNull(actual);
            Collections.sort(actual);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetSupportedCiphersProtocolTypesForEr6672() {
        final List<String> expected = Arrays.asList("SSL/HTTPS/TLS");
        Collections.sort(expected);
        for (final String targetModelIdentity : er6672TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6672NormNodeRef).getOssModelIdentity();
            final List<String> actual = beanUnderTest.getSupportedCipherProtocolTypes(er6672NormNodeRef);
            assertNotNull(actual);
            Collections.sort(actual);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetSupportedCiphersProtocolTypesForEr6675() {
        final List<String> expected = Arrays.asList("SSL/HTTPS/TLS");
        Collections.sort(expected);
        for (final String targetModelIdentity : er6675TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6675NormNodeRef).getOssModelIdentity();
            final List<String> actual = beanUnderTest.getSupportedCipherProtocolTypes(er6675NormNodeRef);
            assertNotNull(actual);
            Collections.sort(actual);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetSupportedCiphersProtocolTypesForEr6x71() {
        final List<String> expected = Arrays.asList("SSL/HTTPS/TLS");
        Collections.sort(expected);
        for (final String targetModelIdentity : er6x71TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6x71NormNodeRef).getOssModelIdentity();
            final List<String> actual = beanUnderTest.getSupportedCipherProtocolTypes(er6x71NormNodeRef);
            assertNotNull(actual);
            Collections.sort(actual);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetSupportedCiphersProtocolTypesForEr6274() {
        final List<String> expected = Arrays.asList("SSL/HTTPS/TLS");
        Collections.sort(expected);
        for (final String targetModelIdentity : er6274TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6274NormNodeRef).getOssModelIdentity();
            final List<String> actual = beanUnderTest.getSupportedCipherProtocolTypes(er6274NormNodeRef);
            assertNotNull(actual);
            Collections.sort(actual);
            assertEquals(expected, actual);
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetCipherMoAttributesWithNullNodeRef() {
        final NormalizableNodeReference normNodeRef = null;
        beanUnderTest.getCipherMoAttributes(normNodeRef);
    }

    @Test
    public void testGetCipherMoAttributesWithUnknownNeType() {
        final Map<String, String> expectedSsh = new HashMap<String, String>();
        expectedSsh.put("selected_mac", "selectedMacs");
        expectedSsh.put("selected_key_exchange", "selectedKeyExchanges");
        expectedSsh.put("selected_cipher", "selectedCiphers");
        expectedSsh.put("supported_mac", "supportedMacs");
        expectedSsh.put("supported_key_exchange", "supportedKeyExchanges");
        expectedSsh.put("supported_cipher", "supportedCiphers");
        final Map<String, String> expectedTls = new HashMap<String, String>();
        expectedTls.put("cipher_filter", "cipherFilter");
        expectedTls.put("enabled_cipher", "enabledCiphers");
        expectedTls.put("supported_cipher", "supportedCiphers");
        final Map<String, Map<String, String>> expected = new HashMap<String, Map<String, String>>();
        expected.put("SSH/SFTP", expectedSsh);
        expected.put("SSL/HTTPS/TLS", expectedTls);

        final Map<String, Map<String, String>> actual = beanUnderTest.getCipherMoAttributes(unknownNormNodeRef);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetCipherMoAttributesForErbs() {
        final Map<String, String> expectedSsh = new HashMap<String, String>();
        expectedSsh.put("selected_mac", "selectedMac");
        expectedSsh.put("selected_key_exchange", "selectedKeyExchange");
        expectedSsh.put("selected_cipher", "selectedCipher");
        expectedSsh.put("supported_mac", "supportedMac");
        expectedSsh.put("supported_key_exchange", "supportedKeyExchange");
        expectedSsh.put("supported_cipher", "supportedCipher");
        final Map<String, String> expectedTls = new HashMap<String, String>();
        expectedTls.put("cipher_filter", "cipherFilter");
        expectedTls.put("enabled_cipher", "enabledCipher");
        expectedTls.put("supported_cipher", "supportedCipher");
        final Map<String, Map<String, String>> expected = new HashMap<String, Map<String, String>>();
        expected.put("SSH/SFTP", expectedSsh);
        expected.put("SSL/HTTPS/TLS", expectedTls);

        for (final String targetModelIdentity : erbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(erbsNormNodeRef).getOssModelIdentity();
            final Map<String, Map<String, String>> actual = beanUnderTest.getCipherMoAttributes(erbsNormNodeRef);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetCipherMoAttributesForRadioTNode() {
        final Map<String, String> expectedSsh = new HashMap<String, String>();
        expectedSsh.put("selected_mac", "selectedMacs");
        expectedSsh.put("selected_key_exchange", "selectedKeyExchanges");
        expectedSsh.put("selected_cipher", "selectedCiphers");
        expectedSsh.put("supported_mac", "supportedMacs");
        expectedSsh.put("supported_key_exchange", "supportedKeyExchanges");
        expectedSsh.put("supported_cipher", "supportedCiphers");
        final Map<String, String> expectedTls = new HashMap<String, String>();
        expectedTls.put("cipher_filter", "cipherFilter");
        expectedTls.put("enabled_cipher", "enabledCiphers");
        expectedTls.put("supported_cipher", "supportedCiphers");
        final Map<String, Map<String, String>> expected = new HashMap<String, Map<String, String>>();
        expected.put("SSH/SFTP", expectedSsh);
        expected.put("SSL/HTTPS/TLS", expectedTls);

        for (final String targetModelIdentity : radioTNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioTNodeNormNodeRef).getOssModelIdentity();
            final Map<String, Map<String, String>> actual = beanUnderTest.getCipherMoAttributes(radioTNodeNormNodeRef);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetCipherMoAttributesForEr6672() {
        final Map<String, String> expectedTls = new HashMap<String, String>();
        expectedTls.put("cipher_filter", "cipherFilter");
        expectedTls.put("enabled_cipher", "enabledCiphers");
        expectedTls.put("supported_cipher", "supportedCiphers");
        final Map<String, Map<String, String>> expected = new HashMap<String, Map<String, String>>();
        expected.put("SSL/HTTPS/TLS", expectedTls);

        for (final String targetModelIdentity : er6672TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6672NormNodeRef).getOssModelIdentity();
            final Map<String, Map<String, String>> actual = beanUnderTest.getCipherMoAttributes(er6672NormNodeRef);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetCipherMoAttributesForEr6675() {
        final Map<String, String> expectedTls = new HashMap<String, String>();
        expectedTls.put("cipher_filter", "cipherFilter");
        expectedTls.put("enabled_cipher", "enabledCiphers");
        expectedTls.put("supported_cipher", "supportedCiphers");
        final Map<String, Map<String, String>> expected = new HashMap<String, Map<String, String>>();
        expected.put("SSL/HTTPS/TLS", expectedTls);

        for (final String targetModelIdentity : er6675TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6675NormNodeRef).getOssModelIdentity();
            final Map<String, Map<String, String>> actual = beanUnderTest.getCipherMoAttributes(er6675NormNodeRef);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetCipherMoAttributesForEr6x71() {
        final Map<String, String> expectedTls = new HashMap<String, String>();
        expectedTls.put("cipher_filter", "cipherFilter");
        expectedTls.put("enabled_cipher", "enabledCiphers");
        expectedTls.put("supported_cipher", "supportedCiphers");
        final Map<String, Map<String, String>> expected = new HashMap<String, Map<String, String>>();
        expected.put("SSL/HTTPS/TLS", expectedTls);

        for (final String targetModelIdentity : er6x71TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6x71NormNodeRef).getOssModelIdentity();
            final Map<String, Map<String, String>> actual = beanUnderTest.getCipherMoAttributes(er6x71NormNodeRef);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetCipherMoAttributesForEr6274() {
        final Map<String, String> expectedTls = new HashMap<String, String>();
        expectedTls.put("cipher_filter", "cipherFilter");
        expectedTls.put("enabled_cipher", "enabledCiphers");
        expectedTls.put("supported_cipher", "supportedCiphers");
        final Map<String, Map<String, String>> expected = new HashMap<String, Map<String, String>>();
        expected.put("SSL/HTTPS/TLS", expectedTls);

        for (final String targetModelIdentity : er6274TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6274NormNodeRef).getOssModelIdentity();
            final Map<String, Map<String, String>> actual = beanUnderTest.getCipherMoAttributes(er6274NormNodeRef);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testIsEmptyValueSupportedForCiphers_MSRBSV2() {
        for (final String targetModelIdentity : radioNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isEmptyValueSupportedForCiphers(radioNormNodeRef));
        }
    }

    @Test
    public void testIsEmptyValueSupportedForCiphers_CiscoAsr900() {
        for (final String targetModelIdentity : ciscoAsr900TargetModelIdentities) {
            doReturn(targetModelIdentity).when(ciscoAsr900NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isEmptyValueSupportedForCiphers(ciscoAsr900NormNodeRef));
        }
    }

    @Test
    public void testIsEmptyValueSupportedForCiphers_ERBS() {
        for (final String targetModelIdentity : erbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(erbsNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isEmptyValueSupportedForCiphers(erbsNormNodeRef));
        }
    }

    @Test
    public void testIsEmptyValueSupportedForCiphers_RBS() {
        for (final String targetModelIdentity : rbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rbsNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isEmptyValueSupportedForCiphers(rbsNormNodeRef));
        }
    }

    @Test
    public void testIsEmptyValueSupportedForCiphers_RNC() {
        for (final String targetModelIdentity : rncTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rncNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isEmptyValueSupportedForCiphers(rncNormNodeRef));
        }
    }

    @Test
    public void testIsEmptyValueSupportedForCiphers_MGW() {
        for (final String targetModelIdentity : mgwTargetModelIdentities) {
            doReturn(targetModelIdentity).when(mgwNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isEmptyValueSupportedForCiphers(mgwNormNodeRef));
        }
    }

    /**
     * test method for {@link NscsCapabilityModelService#isEnrollmentCAFingerprintSupported} to check BSC node is ca finger print supported or not
     */
    @Test
    public void testIsEnrollmentCAFingerprintSupported_BSC() {
        assertTrue(!beanUnderTest.isEnrollmentCAFingerPrintSupported(enrollmentCAAuthorizationModes, CertificateType.OAM.name()));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isEnrollmentCAFingerprintSupported} to check MSC node is ca finger print supported or not
     */
    @Test
    public void testIsEnrollmentCAFingerprintSupported_MSC() {
        assertTrue(!beanUnderTest.isEnrollmentCAFingerPrintSupported(enrollmentCAAuthorizationModes, CertificateType.OAM.name()));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isEnrollmentCAFingerprintSupported} to check HLR node is ca finger print supported or not
     */
    @Test
    public void testIsEnrollmentCAFingerprintSupported_HLR() {
        assertTrue(!beanUnderTest.isEnrollmentCAFingerPrintSupported(enrollmentCAAuthorizationModes, CertificateType.OAM.name()));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isEnrollmentRootCAFingerprintSupported} to check BSC node is Root ca finger print supported
     * or not
     */
    @Test
    public void testIsEnrollmentRootCAFingerprintSupported_BSC() {
        assertTrue(beanUnderTest.isEnrollmentRootCAFingerPrintSupported(enrollmentCAAuthorizationModes, CertificateType.OAM.name()));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isEnrollmentCAFingerprintSupported} to check MSC node is RootCA finger print supported or not
     */
    @Test
    public void testIsEnrollmentRootCAFingerprintSupported_MSC() {
        assertTrue(beanUnderTest.isEnrollmentRootCAFingerPrintSupported(enrollmentCAAuthorizationModes, CertificateType.OAM.name()));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isEnrollmentRootCAFingerprintSupported} to check HLR node is Root ca finger print supported
     * or not
     */
    @Test
    public void testIsEnrollmentRootCAFingerprintSupported_HLR() {
        assertTrue(beanUnderTest.isEnrollmentRootCAFingerPrintSupported(enrollmentCAAuthorizationModes, CertificateType.OAM.name()));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isEnrollmentCACertificateSupported} to check BSC node is ca certificate supported or not
     */
    @Test
    public void testIsEnrollmentCACertificateSupported_BSC() {
        assertTrue(!beanUnderTest.isEnrollmentCACertificateSupported(enrollmentCAAuthorizationModes, CertificateType.OAM.name()));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isEnrollmentCACertificateSupported} to check MSC node is ca certificate supported or not
     */
    @Test
    public void testIsEnrollmentCACertificateSupported_MSC() {
        assertTrue(!beanUnderTest.isEnrollmentCACertificateSupported(enrollmentCAAuthorizationModes, CertificateType.OAM.name()));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isEnrollmentCACertificateSupported} to check HLR node is ca certificate supported or not
     */
    @Test
    public void testIsEnrollmentCACertificateSupported_HLR() {
        assertTrue(!beanUnderTest.isEnrollmentCACertificateSupported(enrollmentCAAuthorizationModes, CertificateType.OAM.name()));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isEnrollmentRootCACertificateSupported} to check BSC node is Root ca certificate supported or
     * not
     */
    @Test
    public void testisEnrollmentRootCACertificateSupported_BSC() {
        assertTrue(!beanUnderTest.isEnrollmentRootCACertificateSupported(enrollmentCAAuthorizationModes, CertificateType.OAM.name()));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isEnrollmentRootCACertificateSupported} to check MSC node is Root ca certificate supported or
     * not
     */
    @Test
    public void testisEnrollmentRootCACertificateSupported_MSC() {
        assertTrue(!beanUnderTest.isEnrollmentRootCACertificateSupported(enrollmentCAAuthorizationModes, CertificateType.OAM.name()));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isEnrollmentRootCACertificateSupported} to check HLR node is Root ca certificate supported or
     * not
     */
    @Test
    public void testisEnrollmentRootCACertificateSupported_HLR() {
        assertTrue(!beanUnderTest.isEnrollmentRootCACertificateSupported(enrollmentCAAuthorizationModes, CertificateType.OAM.name()));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsKSandEMSupportedForNull() {
        final NodeModelInformation nmi = null;
        beanUnderTest.isKSandEMSupported(nmi);
    }

    @Test
    public void testIsKSandEMSupportedForErbsNodeModelInfo() {
        when(nscsModelServiceImpl.isKeyLengthAndEnrollmentModeDefinedInEnrollmentData(any(String.class), any(String.class), any(String.class)))
                .thenReturn(false);
        assertFalse(beanUnderTest.isKSandEMSupported(erbsNodeModelInfo));
    }

    @Test
    public void testGetEnrollmentCAAuthorizationModesForErbs() {
        Map<String, String> expected = new HashMap<>();
        expected.put("IPSEC", "ENROLLMENT_ROOT_CA_FINGERPRINT");
        expected.put("OAM", "ENROLLMENT_ROOT_CA_FINGERPRINT");
        Map<String, String> actual = beanUnderTest.getEnrollmentCAAuthorizationModes(erbsNormNodeRef);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetEnrollmentCAAuthorizationModesForErbsNodeModelInfo() {
        Map<String, String> expected = new HashMap<>();
        expected.put("IPSEC", "ENROLLMENT_ROOT_CA_FINGERPRINT");
        expected.put("OAM", "ENROLLMENT_ROOT_CA_FINGERPRINT");
        Map<String, String> actual = beanUnderTest.getEnrollmentCAAuthorizationModes(erbsNodeModelInfo);
        assertEquals(expected, actual);
    }

    /**
     * test method for {@link NscsCapabilityModelService#isCiphersConfigurationSupported} to check ERBS node is supported or not
     */
    @Test
    public void testIsCiphersConfigurationSupported_erbsUnSupported() {
        final NodeModelInformation erbsUnSupportedNmi = new NodeModelInformation(ERBS_16B_TARGET_MODEL_IDENTITY, ModelIdentifierType.OSS_IDENTIFIER,
                ERBS);
        assertFalse(beanUnderTest.isCiphersConfigurationSupported(erbsUnSupportedNmi));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isCiphersConfigurationSupported} to check ERBS node is supported or not
     */
    @Test
    public void testIsCiphersConfigurationSupported_erbsSupported() {
        final NodeModelInformation erbsSupportedNmi = new NodeModelInformation("17B-H.1.260", ModelIdentifierType.OSS_IDENTIFIER, ERBS);
        assertTrue(beanUnderTest.isCiphersConfigurationSupported(erbsSupportedNmi));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isCiphersConfigurationSupported} to check MGW node is supported or not
     */
    @Test
    public void testIsCiphersConfigurationSupported_mgwUnSupported() {
        assertFalse(beanUnderTest.isCiphersConfigurationSupported(mgwNodeModelInfo));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isCiphersConfigurationSupported} to check MGW node is supported or not
     */
    @Test
    public void testIsCiphersConfigurationSupported_mgwSupported() {
        final NodeModelInformation mgwSupportedNmi = new NodeModelInformation("17B-C.1.288", ModelIdentifierType.OSS_IDENTIFIER, MGW);
        assertTrue(beanUnderTest.isCiphersConfigurationSupported(mgwSupportedNmi));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isCiphersConfigurationSupported} to check RNC node is supported or not
     */
    @Test
    public void testIsCiphersConfigurationSupported_rncUnSupported() {
        final NodeModelInformation rncUnSupportedNmi = new NodeModelInformation("15B-V.5.4658", ModelIdentifierType.OSS_IDENTIFIER, RNC);
        assertFalse(beanUnderTest.isCiphersConfigurationSupported(rncUnSupportedNmi));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isCiphersConfigurationSupported} to check RNC node is supported or not
     */
    @Test
    public void testIsCiphersConfigurationSupported_rncSupported() {
        final NodeModelInformation rncSupportedNmi = new NodeModelInformation("17B-V.9.1240", ModelIdentifierType.OSS_IDENTIFIER, RNC);
        assertTrue(beanUnderTest.isCiphersConfigurationSupported(rncSupportedNmi));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isCiphersConfigurationSupported} to check Radio node is supported or not
     */
    @Test
    public void testIsCiphersConfigurationSupported_radioNodeUnSupported() {
        final NodeModelInformation radioNodeUnSupportedNmi = new NodeModelInformation("15B-R12EC", ModelIdentifierType.OSS_IDENTIFIER, RadioNode);
        assertFalse(beanUnderTest.isCiphersConfigurationSupported(radioNodeUnSupportedNmi));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isCiphersConfigurationSupported} to check Radio node is supported or not
     */
    @Test
    public void testIsCiphersConfigurationSupported_radioNodeSupported() {
        final NodeModelInformation radioNodeSupportedNmi = new NodeModelInformation("17B-R7A42", ModelIdentifierType.OSS_IDENTIFIER, RadioNode);
        assertTrue(beanUnderTest.isCiphersConfigurationSupported(radioNodeSupportedNmi));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isCiphersConfigurationSupported} to check Router node is supported or not
     */
    @Test
    public void testIsCiphersConfigurationSupported_routerUnSupported() {
        final NodeModelInformation routerUnSupportedNmi = new NodeModelInformation("R17A-GA", ModelIdentifierType.OSS_IDENTIFIER, Router6672);
        assertFalse(beanUnderTest.isCiphersConfigurationSupported(routerUnSupportedNmi));
    }

    /**
     * test method for {@link NscsCapabilityModelService#isCiphersConfigurationSupported} to check Router node is supported or not
     */
    @Test
    public void testIsCiphersConfigurationSupported_routerSupported() {
        final NodeModelInformation routerSupportedNmi = new NodeModelInformation("R18A-GA", ModelIdentifierType.OSS_IDENTIFIER, Router6672);
        assertTrue(beanUnderTest.isCiphersConfigurationSupported(routerSupportedNmi));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckWithNullNodeRef() {
        final NormalizableNodeReference normNodeRef = null;
        beanUnderTest.isCertTypeSupportedforCrlCheck(normNodeRef, "any");
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckWithUnknownNeType() {
        assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(unknownNormNodeRef, "any"));
    }

    @Test
    public void testIsCertTypeSupportedforCrlCheckForErbs() {
        for (final String targetModelIdentity : erbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(erbsNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(erbsNormNodeRef, "ALL"));
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(erbsNormNodeRef, "OAM"));
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(erbsNormNodeRef, "IPSEC"));
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(erbsNormNodeRef, "any"));
        }
    }

    @Test
    public void testIsCertTypeSupportedforCrlCheckForRnc() {
        for (final String targetModelIdentity : rncTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rncNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(rncNormNodeRef, "ALL"));
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(rncNormNodeRef, "OAM"));
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(rncNormNodeRef, "IPSEC"));
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(rncNormNodeRef, "any"));
        }
    }

    @Test
    public void testIsCertTypeSupportedforCrlCheckForRbs() {
        for (final String targetModelIdentity : rbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rbsNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(rbsNormNodeRef, "ALL"));
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(rbsNormNodeRef, "OAM"));
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(rbsNormNodeRef, "IPSEC"));
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(rbsNormNodeRef, "any"));
        }
    }

    @Test
    public void testIsCertTypeSupportedforCrlCheckForMgw() {
        for (final String targetModelIdentity : mgwTargetModelIdentities) {
            doReturn(targetModelIdentity).when(mgwNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(mgwNormNodeRef, "ALL"));
            assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(mgwNormNodeRef, "OAM"));
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(mgwNormNodeRef, "IPSEC"));
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(mgwNormNodeRef, "any"));
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForSgsn() {
        for (final String targetModelIdentity : sgsnTargetModelIdentities) {
            doReturn(targetModelIdentity).when(sgsnNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(sgsnNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForSbg() {
        for (final String targetModelIdentity : sbgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(sbgNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(sbgNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForVSbg() {
        for (final String targetModelIdentity : vsbgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vsbgNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(vsbgNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForCscf() {
        for (final String targetModelIdentity : cscfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(cscfNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(cscfNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForVCscf() {
        for (final String targetModelIdentity : vcscfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vcscfNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(vcscfNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForMtas() {
        for (final String targetModelIdentity : mtasTargetModelIdentities) {
            doReturn(targetModelIdentity).when(mtasNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(mtasNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForVMtas() {
        for (final String targetModelIdentity : vmtasTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vmtasNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(vmtasNormNodeRef, "any");
        }
    }

    @Test
    public void testIsCertTypeSupportedforCrlCheckForMsrbsv1() {
        for (final String targetModelIdentity : msrbsv1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(msrbsv1NormNodeRef).getOssModelIdentity();
            if (msrbsv1TargetModelIdentitiesLesserThan16B.contains(targetModelIdentity)) {
                assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(msrbsv1NormNodeRef, "ALL"));
                assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(msrbsv1NormNodeRef, "OAM"));
                assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(msrbsv1NormNodeRef, "IPSEC"));
                assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(msrbsv1NormNodeRef, "any"));
            } else {
                assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(msrbsv1NormNodeRef, "ALL"));
                assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(msrbsv1NormNodeRef, "OAM"));
                assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(msrbsv1NormNodeRef, "IPSEC"));
                assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(msrbsv1NormNodeRef, "any"));
            }
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForSapc() {
        for (final String targetModelIdentity : sapcTargetModelIdentities) {
            doReturn(targetModelIdentity).when(sapcNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(sapcNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForEpg() {
        for (final String targetModelIdentity : epgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(epgNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(epgNormNodeRef, "any");
        }
    }

    @Test
    public void testIsCertTypeSupportedforCrlCheckForRadioNode() {
        for (final String targetModelIdentity : radioNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioNormNodeRef).getOssModelIdentity();
            if (radioNodeTargetModelIdentitiesLesserThan16B.contains(targetModelIdentity)) {
                assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(radioNormNodeRef, "ALL"));
                assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(radioNormNodeRef, "OAM"));
                assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(radioNormNodeRef, "IPSEC"));
                assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(radioNormNodeRef, "any"));
            } else if (radioNodeTargetModelIdentitiesLesserThan17A.contains(targetModelIdentity)) {
                assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(radioNormNodeRef, "ALL"));
                assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(radioNormNodeRef, "OAM"));
                assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(radioNormNodeRef, "IPSEC"));
                assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(radioNormNodeRef, "any"));
            } else {
                assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(radioNormNodeRef, "ALL"));
                assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(radioNormNodeRef, "OAM"));
                assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(radioNormNodeRef, "IPSEC"));
                assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(radioNormNodeRef, "any"));
            }
        }
    }

    @Test
    public void testIsCertTypeSupportedforCrlCheckForEr6672() {
        for (final String targetModelIdentity : er6672TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6672NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(er6672NormNodeRef, "ALL"));
            assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(er6672NormNodeRef, "OAM"));
            assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(er6672NormNodeRef, "IPSEC"));
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(er6672NormNodeRef, "any"));
        }
    }

    @Test
    public void testIsCertTypeSupportedforCrlCheckForEr6675() {
        for (final String targetModelIdentity : er6675TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6675NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(er6675NormNodeRef, "ALL"));
            assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(er6675NormNodeRef, "OAM"));
            assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(er6675NormNodeRef, "IPSEC"));
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(er6675NormNodeRef, "any"));
        }
    }

    @Test
    public void testIsCertTypeSupportedforCrlCheckForEr6x71() {
        for (final String targetModelIdentity : er6x71TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6x71NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(er6x71NormNodeRef, "ALL"));
            assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(er6x71NormNodeRef, "OAM"));
            assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(er6x71NormNodeRef, "IPSEC"));
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(er6x71NormNodeRef, "any"));
        }
    }

    @Test
    public void testIsCertTypeSupportedforCrlCheckForEr6274() {
        for (final String targetModelIdentity : er6274TargetModelIdentities) {
            doReturn(targetModelIdentity).when(er6274NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(er6274NormNodeRef, "ALL"));
            assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(er6274NormNodeRef, "OAM"));
            assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(er6274NormNodeRef, "IPSEC"));
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(er6274NormNodeRef, "any"));
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForMiniLinkIndoor() {
        for (final String targetModelIdentity : miniLinkIndoorTargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkIndoorNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(miniLinkIndoorNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForMiniLinkCn210() {
        for (final String targetModelIdentity : miniLinkCn210TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn210NormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(miniLinkcn210NormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForMiniLinkCn510R1() {
        for (final String targetModelIdentity : miniLinkCn510R1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn510r1NormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(miniLinkcn510r1NormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForMiniLinkCn510R2() {
        for (final String targetModelIdentity : miniLinkCn510R2TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn510r2NormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(miniLinkcn510r2NormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForMiniLinkCn810R1() {
        for (final String targetModelIdentity : miniLinkCn810R1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn810r1NormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(miniLinkcn810r1NormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForMiniLinkCn810R2() {
        for (final String targetModelIdentity : miniLinkCn810R2TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkcn810r2NormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(miniLinkcn810r2NormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForMiniLink665x() {
        for (final String targetModelIdentity : miniLink665xTargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink665xNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(miniLink665xNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForMiniLink669x() {
        for (final String targetModelIdentity : miniLink669xTargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink669xNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(miniLink669xNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForMiniLinkMW2() {
        for (final String targetModelIdentity : miniLinkMW2TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkMW2NormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(miniLinkMW2NormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForMiniLink6352() {
        for (final String targetModelIdentity : miniLink6352TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink6352NormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(miniLink6352NormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForMiniLink6351() {
        for (final String targetModelIdentity : miniLink6351TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLink6351NormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(miniLink6351NormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForMiniLink6366() {
        beanUnderTest.isCertTypeSupportedforCrlCheck(miniLink6366NormNodeRef, "any");
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForMiniLinkPt2020() {
        for (final String targetModelIdentity : miniLinkPt2020TargetModelIdentities) {
            doReturn(targetModelIdentity).when(miniLinkPT2020NormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(miniLinkPT2020NormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForSwitch6391() {
        for (final String targetModelIdentity : switch6391TargetModelIdentities) {
            doReturn(targetModelIdentity).when(switch6391NormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(switch6391NormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForCiscoAsr9000() {
        for (final String targetModelIdentity : ciscoAsr9000TargetModelIdentities) {
            doReturn(targetModelIdentity).when(ciscoAsr9000NormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(ciscoAsr9000NormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForCiscoAsr900() {
        for (final String targetModelIdentity : ciscoAsr900TargetModelIdentities) {
            doReturn(targetModelIdentity).when(ciscoAsr900NormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(ciscoAsr900NormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForJuniperMx() {
        for (final String targetModelIdentity : juniperMxTargetModelIdentities) {
            doReturn(targetModelIdentity).when(juniperMxNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(juniperMxNormNodeRef, "any");
        }
    }

    @Test
    public void testIsCertTypeSupportedforCrlCheckForRadioTNode() {
        for (final String targetModelIdentity : radioTNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioTNodeNormNodeRef).getOssModelIdentity();
            if (radioTNodeTargetModelIdentitiesLesserThan17A.contains(targetModelIdentity)) {
                assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(radioTNodeNormNodeRef, "ALL"));
                assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(radioTNodeNormNodeRef, "OAM"));
                assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(radioTNodeNormNodeRef, "IPSEC"));
                assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(radioTNodeNormNodeRef, "any"));
            } else {
                assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(radioTNodeNormNodeRef, "ALL"));
                assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(radioTNodeNormNodeRef, "OAM"));
                assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(radioTNodeNormNodeRef, "IPSEC"));
                assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(radioTNodeNormNodeRef, "any"));
            }
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForBsp() {
        for (final String targetModelIdentity : bspTargetModelIdentities) {
            doReturn(targetModelIdentity).when(bSPNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(bSPNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForVeme() {
        for (final String targetModelIdentity : vemeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vEMENormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(vEMENormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForVwcg() {
        for (final String targetModelIdentity : vwcgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vWCGNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(vWCGNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForHssFe() {
        doReturn(null).when(hSSFENormNodeRef).getOssModelIdentity();
        beanUnderTest.isCertTypeSupportedforCrlCheck(hSSFENormNodeRef, "any");
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForVHssFe() {
        doReturn(null).when(vhSSFENormNodeRef).getOssModelIdentity();
        beanUnderTest.isCertTypeSupportedforCrlCheck(vhSSFENormNodeRef, "any");
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForVIPWorks() {
        for (final String targetModelIdentity : vipworksTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vIPWorksNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(vIPWorksNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForVbgf() {
        for (final String targetModelIdentity : vbgfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vBGFNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(vBGFNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForVmrf() {
        for (final String targetModelIdentity : vmrfTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vMRFNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(vMRFNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForVupg() {
        for (final String targetModelIdentity : vupgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vUPGNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(vUPGNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForVpp() {
        for (final String targetModelIdentity : vppTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vppNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(vppNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForFronthaul6080() {
        for (final String targetModelIdentity : fronthaul6080TargetModelIdentities) {
            doReturn(targetModelIdentity).when(fronthaul6080NormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(fronthaul6080NormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForFronthaul6020() {
        for (final String targetModelIdentity : fronthaul6020TargetModelIdentities) {
            doReturn(targetModelIdentity).when(fronthaul6020NormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(fronthaul6020NormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForFronthaul6392() {
        for (final String targetModelIdentity : fronthaul6392TargetModelIdentities) {
            doReturn(targetModelIdentity).when(fronthaul6392NormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(fronthaul6392NormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForBsc() {
        for (final String targetModelIdentity : bscTargetModelIdentities) {
            doReturn(targetModelIdentity).when(bscNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(bscNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForHlrfe() {
        for (final String targetModelIdentity : hlrfeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfeNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(hlrfeNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForHlr() {
        beanUnderTest.isCertTypeSupportedforCrlCheck(hlrNormNodeRef, "any");
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForVHlrFe() {
        for (final String targetModelIdentity : vhlrfeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vhlrfeNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(vhlrfeNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForHlrFeBsp() {
        for (final String targetModelIdentity : hlrfebspTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfebspNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(hlrfebspNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForHlrFeIs() {
        for (final String targetModelIdentity : hlrfeisTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfeisNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(hlrfeisNormNodeRef, "any");
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForMsc() {
        beanUnderTest.isCertTypeSupportedforCrlCheck(mscNormNodeRef, "any");
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForVepg() {
        for (final String targetModelIdentity : vepgTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vepgNormNodeRef).getOssModelIdentity();
            beanUnderTest.isCertTypeSupportedforCrlCheck(vepgNormNodeRef, "any");
        }
    }

    @Test
    public void testIsCertTypeSupportedforCrlCheckForVTFRadioNode() {
        for (final String targetModelIdentity : vtfRadioNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vtfRadioNodeNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(vtfRadioNodeNormNodeRef, "ALL"));
            assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(vtfRadioNodeNormNodeRef, "OAM"));
            assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(vtfRadioNodeNormNodeRef, "IPSEC"));
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(vtfRadioNodeNormNodeRef, "any"));
        }
    }

    @Test
    public void testIsCertTypeSupportedforCrlCheckForVsd() {
        for (final String targetModelIdentity : vsdTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vsdNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(vsdNormNodeRef, "ALL"));
            assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(vsdNormNodeRef, "OAM"));
            assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(vsdNormNodeRef, "IPSEC"));
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(vsdNormNodeRef, "any"));
        }
    }

    @Test
    public void testIsCertTypeSupportedforCrlCheckForRvnfm() {
        for (final String targetModelIdentity : rvnfmTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rVNFMNodeNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(rVNFMNodeNormNodeRef, "ALL"));
            assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(rVNFMNodeNormNodeRef, "OAM"));
            assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(rVNFMNodeNormNodeRef, "IPSEC"));
            assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(rVNFMNodeNormNodeRef, "any"));
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForVrsm() {
        beanUnderTest.isCertTypeSupportedforCrlCheck(vrsmNormNodeRef, "any");
    }

    @Test
    public void testIsCertTypeSupportedforCrlCheckForFiveGRadioNode() {
        assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(fiveGRadioNodeNormNodeRef, "ALL"));
        assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(fiveGRadioNodeNormNodeRef, "OAM"));
        assertTrue(beanUnderTest.isCertTypeSupportedforCrlCheck(fiveGRadioNodeNormNodeRef, "IPSEC"));
        assertFalse(beanUnderTest.isCertTypeSupportedforCrlCheck(fiveGRadioNodeNormNodeRef, "any"));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForRnNode() {
        beanUnderTest.isCertTypeSupportedforCrlCheck(rnNodeNormNodeRef, "any");
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsCertTypeSupportedforCrlCheckForVrc() {
        beanUnderTest.isCertTypeSupportedforCrlCheck(vrcNormNodeRef, "any");
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsDeprecatedEnrollmentAuthorityUsedWithNullNodeRef() {
        final NormalizableNodeReference normNodeRef = null;
        beanUnderTest.isDeprecatedEnrollmentAuthorityUsed(normNodeRef);
    }

    @Test
    public void testIsDeprecatedEnrollmentAuthorityUsedWithUnknownNeType() {
        assertFalse(beanUnderTest.isDeprecatedEnrollmentAuthorityUsed(unknownNormNodeRef));
    }

    @Test
    public void testIsDeprecatedEnrollmentAuthorityUsedForErbs() {
        for (final String targetModelIdentity : erbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(erbsNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isDeprecatedEnrollmentAuthorityUsed(erbsNormNodeRef));
        }
    }

    @Test
    public void testIsDeprecatedEnrollmentAuthorityUsedForRadioNode() {
        for (final String targetModelIdentity : radioNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isDeprecatedEnrollmentAuthorityUsed(radioNormNodeRef));
        }
    }

    @Test
    public void testIsDeprecatedEnrollmentAuthorityUsedForMsrbsv1() {
        for (final String targetModelIdentity : msrbsv1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(msrbsv1NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isDeprecatedEnrollmentAuthorityUsed(msrbsv1NormNodeRef));
        }
    }

    @Test
    public void testIsDeprecatedEnrollmentAuthorityUsedForCiscoAsr900() {
        for (final String targetModelIdentity : ciscoAsr900TargetModelIdentities) {
            doReturn(targetModelIdentity).when(ciscoAsr900NormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isDeprecatedEnrollmentAuthorityUsed(ciscoAsr900NormNodeRef));
        }
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsDeprecatedAuthorityTypeSupportedWithNullNodeRef() {
        final NormalizableNodeReference normNodeRef = null;
        beanUnderTest.isDeprecatedAuthorityTypeSupported(normNodeRef);
    }

    @Test
    public void testIsDeprecatedAuthorityTypeSupportedWithUnknownNeType() {
        assertTrue(beanUnderTest.isDeprecatedAuthorityTypeSupported(unknownNormNodeRef));
    }

    @Test
    public void testIsDeprecatedAuthorityTypeSupportedForErbs() {
        for (final String targetModelIdentity : erbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(erbsNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isDeprecatedAuthorityTypeSupported(erbsNormNodeRef));
        }
    }

    @Test
    public void testIsDeprecatedAuthorityTypeSupportedForRbs() {
        for (final String targetModelIdentity : rbsTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rbsNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isDeprecatedAuthorityTypeSupported(rbsNormNodeRef));
        }
    }

    @Test
    public void testIsDeprecatedAuthorityTypeSupportedForRnc() {
        for (final String targetModelIdentity : rncTargetModelIdentities) {
            doReturn(targetModelIdentity).when(rncNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isDeprecatedAuthorityTypeSupported(rncNormNodeRef));
        }
    }

    @Test
    public void testIsDeprecatedAuthorityTypeSupportedForMgw() {
        for (final String targetModelIdentity : mgwTargetModelIdentities) {
            doReturn(targetModelIdentity).when(mgwNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isDeprecatedAuthorityTypeSupported(mgwNormNodeRef));
        }
    }

    @Test
    public void testIsDeprecatedAuthorityTypeSupportedForRadioNode() {
        for (final String targetModelIdentity : radioNodeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(radioNormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isDeprecatedAuthorityTypeSupported(radioNormNodeRef));
        }
    }

    @Test
    public void testIsDeprecatedAuthorityTypeSupportedForMsrbsv1() {
        for (final String targetModelIdentity : msrbsv1TargetModelIdentities) {
            doReturn(targetModelIdentity).when(msrbsv1NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isDeprecatedAuthorityTypeSupported(msrbsv1NormNodeRef));
        }
    }

    @Test
    public void testIsDeprecatedAuthorityTypeSupportedForBsc() {
        for (final String targetModelIdentity : bscTargetModelIdentities) {
            doReturn(targetModelIdentity).when(bscNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isDeprecatedAuthorityTypeSupported(bscNormNodeRef));
        }
    }

    @Test
    public void testIsDeprecatedAuthorityTypeSupportedForHlrfe() {
        for (final String targetModelIdentity : hlrfeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfeNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isDeprecatedAuthorityTypeSupported(hlrfeNormNodeRef));
        }
    }

    @Test
    public void testIsDeprecatedAuthorityTypeSupportedForVHlrfe() {
        for (final String targetModelIdentity : vhlrfeTargetModelIdentities) {
            doReturn(targetModelIdentity).when(vhlrfeNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isDeprecatedAuthorityTypeSupported(vhlrfeNormNodeRef));
        }
    }

    @Test
    public void testIsDeprecatedAuthorityTypeSupportedForHlrfebsp() {
        for (final String targetModelIdentity : hlrfebspTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfebspNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isDeprecatedAuthorityTypeSupported(hlrfebspNormNodeRef));
        }
    }

    @Test
    public void testIsDeprecatedAuthorityTypeSupportedForHlrfeis() {
        for (final String targetModelIdentity : hlrfeisTargetModelIdentities) {
            doReturn(targetModelIdentity).when(hlrfeisNormNodeRef).getOssModelIdentity();
            assertFalse(beanUnderTest.isDeprecatedAuthorityTypeSupported(hlrfeisNormNodeRef));
        }
    }

    @Test
    public void testIsDeprecatedAuthorityTypeSupportedForCiscoAsr900() {
        for (final String targetModelIdentity : ciscoAsr900TargetModelIdentities) {
            doReturn(targetModelIdentity).when(ciscoAsr900NormNodeRef).getOssModelIdentity();
            assertTrue(beanUnderTest.isDeprecatedAuthorityTypeSupported(ciscoAsr900NormNodeRef));
        }
    }

    @Test
    public void testGetLdapConfigureWorkflowForVduNodes() {
        final String ldapConfigureWorkflow = beanUnderTest.getLdapConfigureWorkflow(vDuNormNodeRef);
        assertTrue(ldapConfigureWorkflow.equals(WORKFLOW_CBPOI_CONFIGURE_LDAP.toString()));
    }

    @Test
    public void testGetLdapConfigureWorkflowForComEcimNodes() {
        final String ldapConfigureWorkflow = beanUnderTest.getLdapConfigureWorkflow(radioNormNodeRef);
        assertTrue(ldapConfigureWorkflow.equals(WORKFLOW_COMECIM_CONFIGURE_LDAP.toString()));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetLdapConfigureWorkflowNormRefNull() {
        beanUnderTest.getLdapConfigureWorkflow(null);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetLdapConfigureWorkflowNullTargetTypel() {
        beanUnderTest.getLdapConfigureWorkflow(nullNeTypeVDuNormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetLdapConfigureWorkflowNullCapabilityModel() {
        when(beanManager.getReference(Mockito.eq(bean), any(Type.class), Mockito.eq(creationalContext))).thenReturn(null);
        beanUnderTest.getLdapConfigureWorkflow(vDuNormNodeRef);
    }

    @Test
    public void testGetLdapMoNameforVduNodes() {
        final String ldapMoName = beanUnderTest.getLdapMoName(vDuNormNodeRef);
        assertTrue(ldapMoName.equals(VDU_LDAP_MO.toString()));
    }

    @Test
    public void testGetLdapMoNameforComEcimNodes() {
        final String ldapMoName = beanUnderTest.getLdapMoName(radioNormNodeRef);
        assertTrue(ldapMoName.equals(COMECIM_LDAP_MO.toString()));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetLdapMoNameNormRefNull() {
        beanUnderTest.getLdapMoName(null);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetLdapMoNameNullTargetTypel() {
        beanUnderTest.getLdapMoName(nullNeTypeVDuNormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetLdapMoNameNullCapabilityModel() {
        when(beanManager.getReference(Mockito.eq(bean), any(Type.class), Mockito.eq(creationalContext))).thenReturn(null);
        beanUnderTest.getLdapMoName(vDuNormNodeRef);
    }

    @Test
    public void testGetDefaultEnrollmentCaTrustCategoryIdforVduNodes() {
        final Map<String, String> VDU_DEFAULT_ENROLL_CA_TRUST_CATEGORY_ID_MAP = new HashMap<>();
        VDU_DEFAULT_ENROLL_CA_TRUST_CATEGORY_ID_MAP.put("OAM", VDU_DEFAULT_ENROLL_CA_TRUST_CATEGORY_ID);
        final Map<String, String> defaultEnrollmentCaTrustCategoryId = beanUnderTest.getDefaultEnrollmentCaTrustCategoryId(vDuNormNodeRef);
        assertTrue(defaultEnrollmentCaTrustCategoryId.equals(VDU_DEFAULT_ENROLL_CA_TRUST_CATEGORY_ID_MAP));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testgetDefaultEnrollmentCaTrustCategoryIdNullTargetTypel() {
        NodeModelInformation nodeModelInfo = null;
        beanUnderTest.getDefaultEnrollmentCaTrustCategoryId(nodeModelInfo);
    }

    @Test
    public void testGetDefaultEnrollmentCaTrustCategoryIdforComEcimNodes() {
        final Map<String, String> defaultEnrollmentCaTrustCategoryId = beanUnderTest.getDefaultEnrollmentCaTrustCategoryId(radioNormNodeRef);
        assertNull(defaultEnrollmentCaTrustCategoryId);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultEnrollmentCaTrustCategoryIdNormRefNull() {
        NodeModelInformation nodeModelInfo = null;
        beanUnderTest.getDefaultEnrollmentCaTrustCategoryId(nodeModelInfo);
    }

    @Test
    public void testGetDefaultEnrollmentCaTrustCategorywithModelInfo() {
        beanUnderTest.getDefaultEnrollmentCaTrustCategoryId(vDUNodeModelInfo);
    }

    @Test
    public void testDefaultOtpValidityPeriodInMinutesforVduNodes() {
        final String defaultOtpValidityPeriodInMinutes = beanUnderTest.getDefaultOtpValidityPeriodInMinutes(vDuNormNodeRef);
        assertTrue(defaultOtpValidityPeriodInMinutes.equals(VDU_DEFAULT_OTP_VALIDITY_PERIOD_IN_MINUTES.toString()));
    }

    @Test
    public void testDefaultOtpValidityPeriodInMinutesVduNodesWithModelInfo() {
        beanUnderTest.getDefaultOtpValidityPeriodInMinutes(radioNodeModelInfo);
    }
    
    @Test
    public void testGetDefaultOtpValidityPeriodInMinutesforComEcimNodes() {
        final String defaultOtpValidityPeriodInMinutes = beanUnderTest.getDefaultOtpValidityPeriodInMinutes(radioNormNodeRef);
        assertTrue(defaultOtpValidityPeriodInMinutes.equals(COMECIM_DEFAULT_OTP_VALIDITY_PERIOD_IN_MINUTES.toString()));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testgetDefaultOtpValidityPeriodInMinutesNullTargetTypel() {
        beanUnderTest.getDefaultOtpValidityPeriodInMinutes(nullNeTypeVDuNormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testgetDefaultOtpValidityPeriodInMinutesNullCapabilityModel() {
        when(beanManager.getReference(Mockito.eq(bean), any(Type.class), Mockito.eq(creationalContext))).thenReturn(null);
        beanUnderTest.getDefaultOtpValidityPeriodInMinutes(vDuNormNodeRef);
    }

    @Test
    public void testGetSupportedSecurityLevelsForErbs() {
        List<String> expected = new ArrayList<>();
        expected.add("LEVEL_1");
        expected.add("LEVEL_2");
        List<String> actual = beanUnderTest.getSupportedSecurityLevels(erbsNormNodeRef);
        assertTrue(expected.size() == actual.size() && expected.containsAll(actual) && actual.containsAll(expected));
    }

    @Test
    public void testGetSupportedSecurityLevelsForErbsNodeModelInfo() {
        List<String> expected = new ArrayList<>();
        expected.add("LEVEL_1");
        expected.add("LEVEL_2");
        List<String> actual = beanUnderTest.getSupportedSecurityLevels(erbsNodeModelInfo);
        assertTrue(expected.size() == actual.size() && expected.containsAll(actual) && actual.containsAll(expected));
    }

    @Test
    public void testGetComEcimDefaultNodeCredentialIdsForRadioNode() {
        Map<String, String> expected = new HashMap<>();
        expected.put("OAM", "oamNodeCredential");
        expected.put("IPSEC", "ipsecNodeCredential");
        assertEquals(expected, beanUnderTest.getComEcimDefaultNodeCredentialIds(radioNormNodeRef));
    }

    @Test
    public void testGetComEcimDefaultNodeCredentialIdsForRadioNodeNodeModelInfo() {
        Map<String, String> expected = new HashMap<>();
        expected.put("OAM", "oamNodeCredential");
        expected.put("IPSEC", "ipsecNodeCredential");
        assertEquals(expected, beanUnderTest.getComEcimDefaultNodeCredentialIds(radioNodeModelInfo));
    }

    @Test
    public void testGetComEcimDefaultTrustCategoryIdsForRadioNode() {
        Map<String, String> expected = new HashMap<>();
        expected.put("OAM", "oamTrustCategory");
        expected.put("IPSEC", "ipsecTrustCategory");
        assertEquals(expected, beanUnderTest.getComEcimDefaultTrustCategoryIds(radioNormNodeRef));
    }

    @Test
    public void testGetComEcimDefaultTrustCategoryIdsForRadioNodeNodeModelInfo() {
        Map<String, String> expected = new HashMap<>();
        expected.put("OAM", "oamTrustCategory");
        expected.put("IPSEC", "ipsecTrustCategory");
        assertEquals(expected, beanUnderTest.getComEcimDefaultTrustCategoryIds(radioNodeModelInfo));
    }

    @Test
    public void testGetCrlCheckWfForRadioNode() {
        assertEquals("COMEnableOrDisableCRLCheck", beanUnderTest.getCrlCheckWf(radioNormNodeRef, "OAM"));
    }

    @Test
    public void testGetDefaultInitialOtpCountForRadioNodeNodeModelInfo() {
        assertEquals("5", beanUnderTest.getDefaultInitialOtpCount(radioNodeModelInfo));
    }

    @Test
    public void testGetOnDemandCrlDownloadWfForRadioNode() {
        assertEquals("COMOnDemandCrlDownload", beanUnderTest.getOnDemandCrlDownloadWf(radioNormNodeRef));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetDefaultPasswordHashAlgorithmForRadioNode() {
        beanUnderTest.getDefaultPasswordHashAlgorithm(radioNormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testIsTrustCategoryTypeSupportedForRadioNode() {
        beanUnderTest.isTrustCategoryTypeSupported(radioNormNodeRef, "OAM");
    }

    @Test
    public void testGetPushM2MUserForRadioNodeNodeModelInfo() {
        assertNull(beanUnderTest.getPushM2MUser(radioNodeModelInfo));
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetNtpRemoveWorkflowForRadioNode() {
        beanUnderTest.getNtpRemoveWorkflow(radioNormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetNtpConfigureWorkflowForRadioNode() {
        beanUnderTest.getNtpConfigureWorkflow(radioNormNodeRef);
    }

    @Test(expected = NscsCapabilityModelException.class)
    public void testGetIsNodeSshPrivateKeyImportSupportedForRadioNode() {
        beanUnderTest.isNodeSshPrivateKeyImportSupported(radioNormNodeRef);
    }

    private List<String> getCppLikeExpectedCredentialsParams() {
        return Arrays.asList(NORMAL_USER_NAME_PROPERTY, NORMAL_USER_PASSWORD_PROPERTY, ROOT_USER_NAME_PROPERTY, ROOT_USER_PASSWORD_PROPERTY,
                SECURE_USER_NAME_PROPERTY, SECURE_USER_PASSWORD_PROPERTY);
    }

    private List<String> getCppLikeUnexpectedCredentialsParams() {
        return Arrays.asList(NWIEA_SECURE_USER_NAME_PROPERTY, NWIEA_SECURE_PASSWORD_PROPERTY, NWIEB_SECURE_USER_NAME_PROPERTY,
                NWIEB_SECURE_PASSWORD_PROPERTY);
    }

    private List<String> getEcimLikeExpectedCredentialsParams() {
        return Arrays.asList(SECURE_USER_NAME_PROPERTY, SECURE_USER_PASSWORD_PROPERTY);
    }

    private List<String> getEcimLikeUnexpectedCredentialsParams() {
        return Arrays.asList(NORMAL_USER_NAME_PROPERTY, NORMAL_USER_PASSWORD_PROPERTY, ROOT_USER_NAME_PROPERTY, ROOT_USER_PASSWORD_PROPERTY,
                NWIEA_SECURE_USER_NAME_PROPERTY, NWIEA_SECURE_PASSWORD_PROPERTY, NWIEB_SECURE_USER_NAME_PROPERTY, NWIEB_SECURE_PASSWORD_PROPERTY);
    }

    private List<String> getAxeLikeExpectedCredentialsParams() {
        return Arrays.asList(SECURE_USER_NAME_PROPERTY, SECURE_USER_PASSWORD_PROPERTY, NWIEA_SECURE_USER_NAME_PROPERTY,
                NWIEA_SECURE_PASSWORD_PROPERTY, NWIEB_SECURE_USER_NAME_PROPERTY, NWIEB_SECURE_PASSWORD_PROPERTY);
    }

    private List<String> getAxeLikeUnexpectedCredentialsParams() {
        return Arrays.asList(NORMAL_USER_NAME_PROPERTY, NORMAL_USER_PASSWORD_PROPERTY, ROOT_USER_NAME_PROPERTY, ROOT_USER_PASSWORD_PROPERTY);
    }
}
