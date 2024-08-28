/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.capabilitymodel.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.util.MimVersion;
import com.ericsson.oss.itpf.modeling.modelservice.typed.capabilities.Capability;

/**
 * Mocks the NSCS Capability Model for test only.
 */
@NscsCapabilityModelType(isCapabilityModelMock = true)
public class NscsCapabilityModelMock implements NscsCapabilityModel {

    private static final String ERBS_14A_MIM_VERSION = "5.1.63";
    private static final String ERBS_14A_OSS_MODEL_IDENTITY = "6824-690-779";
    private static final String DUMMY_OSS_MODEL_IDENTITY = "123-456-789";

    /**
     * Target types
     */
    private static final String ECM = "ECM";
    private static final String CSCF = "CSCF";
    private static final String ERBS = "ERBS";
    private static final String SGSN_MME = "SGSN-MME";
    private static final String RadioNode = "RadioNode";
    private static final String MSRBS_V1 = "MSRBS_V1";
    private static final String SAPC = "SAPC";
    private static final String EPG = "EPG";
    private static final String Router6672 = "Router6672";
    private static final String Router6675 = "Router6675";
    private static final String Router6x71 = "Router6x71";
    private static final String Router6274 = "Router6274";
    private static final String MINI_LINK_Indoor = "MINI-LINK-Indoor";
    private static final String MINI_LINK_CN210 = "MINI-LINK-CN210";
    private static final String MINI_LINK_CN510R1 = "MINI-LINK-CN510R1";
    private static final String MINI_LINK_CN510R2 = "MINI-LINK-CN510R2";
    private static final String MINI_LINK_CN810R1 = "MINI-LINK-CN810R1";
    private static final String MINI_LINK_CN810R2 = "MINI-LINK-CN810R2";
    private static final String MINI_LINK_665x = "MINI-LINK-665x";
    private static final String MINI_LINK_669x = "MINI-LINK-669x";
    private static final String MINI_LINK_MW2 = "MINI-LINK-MW2";
    private static final String MINI_LINK_6352 = "MINI-LINK-6352";
    private static final String MINI_LINK_6351 = "MINI-LINK-6351";
    private static final String MINI_LINK_6366 = "MINI-LINK-6366";
    private static final String MINI_LINK_PT2020 = "MINI-LINK-PT2020";
    private static final String Switch_6391 = "Switch-6391";
    private static final String MGW = "MGW";
    private static final String MTAS = "MTAS";
    private static final String RNC = "RNC";
    private static final String RBS = "RBS";
    private static final String RadioTNode = "RadioTNode";
    private static final String SBG = "SBG";
    private static final String CISCO_ASR9000 = "CISCO-ASR9000";
    private static final String CISCO_ASR900 = "CISCO-ASR900";
    private static final String JUNIPER_MX = "JUNIPER-MX";
    private static final String FRONTHAUL_6080 = "FRONTHAUL-6080";
    private static final String FRONTHAUL_6020 = "FRONTHAUL-6020";
    private static final String Fronthaul_6392 = "Fronthaul-6392";
    private static final String VEPG = "VEPG";
    private static final String BSC = "BSC";
    private static final String MSC = "MSC";
    private static final String HLR = "HLR";
    private static final String RnNode = "RnNode";
    private static final String vPP = "vPP";
    private static final String vRC = "vRC";
    private static final String vRM = "vRM";
    private static final String vEME = "vEME";
    private static final String vWCG = "vWCG";
    private static final String HSS_FE = "HSS-FE";
    private static final String VHSS_FE = "vHSS-FE";
    private static final String vIPWorks = "vIPWorks";
    private static final String vUPG = "vUPG";
    private static final String BSP = "BSP";
    private static final String vBGF = "vBGF";
    private static final String vMRF = "vMRF";
    private static final String FIVEGRadioNode = "5GRadioNode";
    private static final String vMTAS = "vMTAS";
    private static final String vSBG = "vSBG";
    private static final String vCSCF = "vCSCF";
    private static final String VTFRadioNode = "VTFRadioNode";
    private static final String vSD = "vSD";
    private static final String RVNFM = "RVNFM";
    private static final String HLR_FE = "HLR-FE";
    private static final String vHLR_FE = "vHLR-FE";
    private static final String HLR_FE_BSP = "HLR-FE-BSP";
    private static final String HLR_FE_IS = "HLR-FE-IS";
    private static final String vRSM = "vRSM";
    private static final String GenericESA = "GenericESA";
    private static final String VDU = "vDU";
    private static final String UNKNOWN = "UNKNOWN";

    /**
     * Mocks the supported target types for tests only.
     */
    static final List<String> theSupportedTargetTypes = new ArrayList<>();
    static {
        theSupportedTargetTypes.add(ECM);
        theSupportedTargetTypes.add(ERBS);
        theSupportedTargetTypes.add(RNC);
        theSupportedTargetTypes.add(RBS);
        theSupportedTargetTypes.add(MGW);
        theSupportedTargetTypes.add(SGSN_MME);
        theSupportedTargetTypes.add(SBG);
        theSupportedTargetTypes.add(vSBG);
        theSupportedTargetTypes.add(CSCF);
        theSupportedTargetTypes.add(vCSCF);
        theSupportedTargetTypes.add(MTAS);
        theSupportedTargetTypes.add(vMTAS);
        theSupportedTargetTypes.add(MSRBS_V1);
        theSupportedTargetTypes.add(SAPC);
        theSupportedTargetTypes.add(EPG);
        theSupportedTargetTypes.add(RadioNode);
        theSupportedTargetTypes.add(Router6672);
        theSupportedTargetTypes.add(Router6675);
        theSupportedTargetTypes.add(Router6x71);
        theSupportedTargetTypes.add(Router6274);
        theSupportedTargetTypes.add(MINI_LINK_Indoor);
        theSupportedTargetTypes.add(MINI_LINK_CN210);
        theSupportedTargetTypes.add(MINI_LINK_CN510R1);
        theSupportedTargetTypes.add(MINI_LINK_CN510R2);
        theSupportedTargetTypes.add(MINI_LINK_CN810R1);
        theSupportedTargetTypes.add(MINI_LINK_CN810R2);
        theSupportedTargetTypes.add(MINI_LINK_665x);
        theSupportedTargetTypes.add(MINI_LINK_669x);
        theSupportedTargetTypes.add(MINI_LINK_MW2);
        theSupportedTargetTypes.add(MINI_LINK_6352);
        theSupportedTargetTypes.add(MINI_LINK_6351);
        theSupportedTargetTypes.add(MINI_LINK_6366);
        theSupportedTargetTypes.add(MINI_LINK_PT2020);
        theSupportedTargetTypes.add(Switch_6391);
        theSupportedTargetTypes.add(CISCO_ASR9000);
        theSupportedTargetTypes.add(CISCO_ASR900);
        theSupportedTargetTypes.add(JUNIPER_MX);
        theSupportedTargetTypes.add(RadioTNode);
        theSupportedTargetTypes.add(BSP);
        theSupportedTargetTypes.add(vEME);
        theSupportedTargetTypes.add(vWCG);
        theSupportedTargetTypes.add(HSS_FE);
        theSupportedTargetTypes.add(VHSS_FE);
        theSupportedTargetTypes.add(vIPWorks);
        theSupportedTargetTypes.add(vBGF);
        theSupportedTargetTypes.add(vMRF);
        theSupportedTargetTypes.add(vUPG);
        theSupportedTargetTypes.add(RnNode);
        theSupportedTargetTypes.add(vPP);
        theSupportedTargetTypes.add(vRM);
        theSupportedTargetTypes.add(vRC);
        theSupportedTargetTypes.add(FRONTHAUL_6080);
        theSupportedTargetTypes.add(FRONTHAUL_6020);
        theSupportedTargetTypes.add(Fronthaul_6392);
        theSupportedTargetTypes.add(BSC);
        theSupportedTargetTypes.add(MSC);
        theSupportedTargetTypes.add(HLR);
        theSupportedTargetTypes.add(VEPG);
        theSupportedTargetTypes.add(FIVEGRadioNode);
        theSupportedTargetTypes.add(VTFRadioNode);
        theSupportedTargetTypes.add(vSD);
        theSupportedTargetTypes.add(RVNFM);
        theSupportedTargetTypes.add(HLR_FE);
        theSupportedTargetTypes.add(vHLR_FE);
        theSupportedTargetTypes.add(HLR_FE_BSP);
        theSupportedTargetTypes.add(HLR_FE_IS);
        theSupportedTargetTypes.add(vRSM);
        theSupportedTargetTypes.add(GenericESA);
        theSupportedTargetTypes.add(VDU);
        theSupportedTargetTypes.add(UNKNOWN);
    };

    /**
     * This models the mapping of target model identities to capabilitysupport versions different from "1.0.0" for a target type.
     *
     * If a target type is not present or if, for a target type, a target model identity is not present, the "1.0.0" is used.
     */
    static final Map<String, Map<String, String>> theCapabilitySupportVersions = new HashMap<>();

    static {
        final Map<String, String> erbsTMIsToCapabilitySupportVersions = new HashMap<>();
        erbsTMIsToCapabilitySupportVersions.put("6824-690-779", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("4322-436-393", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("1998-184-092", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("4322-940-032", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("1777-370-163", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("4280-987-331", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("3958-644-341", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("3520-829-806", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("6607-651-025", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("4613-704-163", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("5981-462-912", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("2754-962-591", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("1826-077-154", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("16A-G.1.143", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("16A-G.1.142", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("2042-630-876", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("1147-458-334", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("16B-G.1.281", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("1116-673-956", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("6385-946-582", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("16B-G.1.260", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("16B-G.1.308", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("16B-G.1.301", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("17A-H.1.190", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("17A-H.1.60", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("17A-H.1.140", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("17A-H.1.20", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("17A-H.1.80", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("17A-H.1.120", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("17A-H.1.40", "14.0.0");
        erbsTMIsToCapabilitySupportVersions.put("17A-H.1.160", "14.0.0");

        final Map<String, String> rncTMIsToCapabilitySupportVersions = new HashMap<>();
        rncTMIsToCapabilitySupportVersions.put("15B-V.5.4658", "15.1.0");
        rncTMIsToCapabilitySupportVersions.put("15B-V.5.4658-G4", "15.1.0");
        rncTMIsToCapabilitySupportVersions.put("16A-V.6.940", "15.1.0");
        rncTMIsToCapabilitySupportVersions.put("16A-V.6.940-J2", "15.1.0");
        rncTMIsToCapabilitySupportVersions.put("16B-V.7.1659", "15.1.0");
        rncTMIsToCapabilitySupportVersions.put("16B-V.7.1659-M9", "15.1.0");
        rncTMIsToCapabilitySupportVersions.put("17A-V.8.1349", "15.1.0");

        final Map<String, String> rbsTMIsToCapabilitySupportVersions = new HashMap<>();
        rbsTMIsToCapabilitySupportVersions.put("13B-S2.1.100", "13.1.0");
        rbsTMIsToCapabilitySupportVersions.put("13B-S.1.100", "13.1.0");
        rbsTMIsToCapabilitySupportVersions.put("15B-U.4.91", "15.1.0");
        rbsTMIsToCapabilitySupportVersions.put("16A-U.4.210", "15.1.0");
        rbsTMIsToCapabilitySupportVersions.put("16B-U.4.340", "15.1.0");
        rbsTMIsToCapabilitySupportVersions.put("17A-U.4.460", "15.1.0");

        final Map<String, String> mgwTMIsToCapabilitySupportVersions = new HashMap<>();
        mgwTMIsToCapabilitySupportVersions.put("14B-C.1.141", "14.1.0");
        mgwTMIsToCapabilitySupportVersions.put("1484-383-806", "14.1.0");
        mgwTMIsToCapabilitySupportVersions.put("16A-C.1.214", "14.1.0");
        mgwTMIsToCapabilitySupportVersions.put("16A-C.1.203", "14.1.0");
        mgwTMIsToCapabilitySupportVersions.put("16B-C.1.243", "14.1.0");
        mgwTMIsToCapabilitySupportVersions.put("17A-C.1.267", "14.1.0");
        mgwTMIsToCapabilitySupportVersions.put("17A-C.1.257", "14.1.0");

        final Map<String, String> msrbsv1TMIsToCapabilitySupportVersions = new HashMap<>();
        msrbsv1TMIsToCapabilitySupportVersions.put("16A-R9F", "16.0.0");
        msrbsv1TMIsToCapabilitySupportVersions.put("16A-R4B", "16.0.0");

        final Map<String, String> er6672TMIsToCapabilitySupportVersions = new HashMap<>();
        er6672TMIsToCapabilitySupportVersions.put("R17A-GA", "17.0.0");
        er6672TMIsToCapabilitySupportVersions.put("R17B-GA", "17.1.0");

        final Map<String, String> radioNodeTMIsToCapabilitySupportVersions = new HashMap<>();
        radioNodeTMIsToCapabilitySupportVersions.put("15B-R12EC", "15.1.0");
        radioNodeTMIsToCapabilitySupportVersions.put("16A-R28CJ", "15.1.0");
        radioNodeTMIsToCapabilitySupportVersions.put("16A-R22AC", "15.1.0");
        radioNodeTMIsToCapabilitySupportVersions.put("16A-R29AJ", "15.1.0");
        radioNodeTMIsToCapabilitySupportVersions.put("16B-R2CJ", "16.1.0");
        radioNodeTMIsToCapabilitySupportVersions.put("16B-R2HH", "16.1.0");
        radioNodeTMIsToCapabilitySupportVersions.put("16B-R28GY", "16.1.0");
        radioNodeTMIsToCapabilitySupportVersions.put("16B-R2ZV", "16.1.0");
        radioNodeTMIsToCapabilitySupportVersions.put("16B-R28DS", "16.1.0");

        final Map<String, String> radioTNodeTMIsToCapabilitySupportVersions = new HashMap<>();
        radioTNodeTMIsToCapabilitySupportVersions.put("16B-R2JH", "16.1.0");

        theCapabilitySupportVersions.put(ERBS, erbsTMIsToCapabilitySupportVersions);
        theCapabilitySupportVersions.put(RNC, rncTMIsToCapabilitySupportVersions);
        theCapabilitySupportVersions.put(RBS, rbsTMIsToCapabilitySupportVersions);
        theCapabilitySupportVersions.put(MGW, mgwTMIsToCapabilitySupportVersions);
        theCapabilitySupportVersions.put(MSRBS_V1, msrbsv1TMIsToCapabilitySupportVersions);
        theCapabilitySupportVersions.put(Router6672, er6672TMIsToCapabilitySupportVersions);
        theCapabilitySupportVersions.put(RadioNode, radioNodeTMIsToCapabilitySupportVersions);
        theCapabilitySupportVersions.put(RadioTNode, radioTNodeTMIsToCapabilitySupportVersions);
    }

    /**
     * Mock the 'unsupportedSecadmCliCommans' capability.
     *
     * This capability models the unsupported 'secadm' CLI commands for a target type.
     *
     * A default value is provided: add an element in the node map for each specific node type whose specific capability value differs from the
     * default.
     */
    final static List<String> keygenCommands = Arrays.asList("sshkey");
    final static List<String> snmpCommands = Arrays.asList("snmp");
    final static List<String> securityLevelCommands = Arrays.asList("securitylevel");
    final static List<String> ipsecCommands = Arrays.asList("ipsec");
    final static List<String> enrollmentCommands = Arrays.asList("enrollment");
    final static List<String> certificateCommands = Arrays.asList("certificate");
    final static List<String> trustCommands = Arrays.asList("trust");
    final static List<String> ldapCommand = Arrays.asList("ldap");
    final static List<String> crlCheckCommand = Arrays.asList("crlcheck");
    final static List<String> onDemandCrlDownloadCommand = Arrays.asList("crldownload");
    final static List<String> setCiphersCommand = Arrays.asList("ciphers");
    final static List<String> httpsCommands = Arrays.asList("https");
    final static List<String> ftpesCommands = Arrays.asList("ftpes");
    final static List<String> rtselCommand = Arrays.asList("rtsel");

    static final List<String> defaultUnsupportedCommands = new ArrayList<>();
    static {
        defaultUnsupportedCommands.addAll(keygenCommands);
        defaultUnsupportedCommands.addAll(securityLevelCommands);
        defaultUnsupportedCommands.addAll(ipsecCommands);
        defaultUnsupportedCommands.addAll(enrollmentCommands);
        defaultUnsupportedCommands.addAll(certificateCommands);
        defaultUnsupportedCommands.addAll(trustCommands);
        defaultUnsupportedCommands.addAll(ldapCommand);
        defaultUnsupportedCommands.addAll(crlCheckCommand);
        defaultUnsupportedCommands.addAll(onDemandCrlDownloadCommand);
        defaultUnsupportedCommands.addAll(setCiphersCommand);
        defaultUnsupportedCommands.addAll(rtselCommand);
        defaultUnsupportedCommands.addAll(httpsCommands);
        defaultUnsupportedCommands.addAll(ftpesCommands);
    }

    static final Map<String, Map<String, List<String>>> theUnsupportedCommands = new HashMap<>();
    static {
        final List<String> erbsUnsupported = new ArrayList<>();
        erbsUnsupported.addAll(keygenCommands);
        erbsUnsupported.addAll(snmpCommands);
        erbsUnsupported.addAll(ldapCommand);
        erbsUnsupported.addAll(ftpesCommands);
        final List<String> erbs14AUnsupported = new ArrayList<>();
        erbs14AUnsupported.addAll(erbsUnsupported);
        erbs14AUnsupported.addAll(crlCheckCommand);
        erbs14AUnsupported.addAll(onDemandCrlDownloadCommand);
        erbs14AUnsupported.addAll(setCiphersCommand);
        final Map<String, List<String>> erbsUnsupportedCommands = new HashMap<>();
        erbsUnsupportedCommands.put("1.0.0", erbsUnsupported);
        erbsUnsupportedCommands.put("14.0.0", erbs14AUnsupported);

        final List<String> rncUnsupported = new ArrayList<>();
        rncUnsupported.addAll(keygenCommands);
        rncUnsupported.addAll(snmpCommands);
        rncUnsupported.addAll(ldapCommand);
        rncUnsupported.addAll(rtselCommand);
        rncUnsupported.addAll(ftpesCommands);
        final List<String> rnc15BUnsupported = new ArrayList<>();
        rnc15BUnsupported.addAll(rncUnsupported);
        rnc15BUnsupported.addAll(crlCheckCommand);
        rnc15BUnsupported.addAll(onDemandCrlDownloadCommand);
        rnc15BUnsupported.addAll(setCiphersCommand);
        final Map<String, List<String>> rncUnsupportedCommands = new HashMap<>();
        rncUnsupportedCommands.put("1.0.0", rncUnsupported);
        rncUnsupportedCommands.put("15.1.0", rnc15BUnsupported);

        final List<String> rbsUnsupported = new ArrayList<>();
        rbsUnsupported.addAll(keygenCommands);
        rbsUnsupported.addAll(snmpCommands);
        rbsUnsupported.addAll(ldapCommand);
        rbsUnsupported.addAll(rtselCommand);
        rbsUnsupported.addAll(ftpesCommands);
        final List<String> rbs13BUnsupported = new ArrayList<>();
        rbs13BUnsupported.addAll(rbsUnsupported);
        rbs13BUnsupported.addAll(crlCheckCommand);
        rbs13BUnsupported.addAll(onDemandCrlDownloadCommand);
        rbs13BUnsupported.addAll(setCiphersCommand);
        final Map<String, List<String>> rbsUnsupportedCommands = new HashMap<>();
        rbsUnsupportedCommands.put("1.0.0", rbsUnsupported);
        rbsUnsupportedCommands.put("13.1.0", rbs13BUnsupported);
        rbsUnsupportedCommands.put("15.1.0", rbs13BUnsupported);

        final List<String> mgwUnsupported = new ArrayList<>();
        mgwUnsupported.addAll(keygenCommands);
        mgwUnsupported.addAll(snmpCommands);
        mgwUnsupported.addAll(ipsecCommands);
        mgwUnsupported.addAll(ldapCommand);
        mgwUnsupported.addAll(ftpesCommands);
        final List<String> mgw14BUnsupported = new ArrayList<>();
        mgw14BUnsupported.addAll(mgwUnsupported);
        mgw14BUnsupported.addAll(crlCheckCommand);
        mgw14BUnsupported.addAll(onDemandCrlDownloadCommand);
        mgw14BUnsupported.addAll(setCiphersCommand);
        final Map<String, List<String>> mgwUnsupportedCommands = new HashMap<>();
        mgwUnsupportedCommands.put("1.0.0", mgwUnsupported);
        mgwUnsupportedCommands.put("14.1.0", mgw14BUnsupported);

        final List<String> sgsnMmeEpgUnsupported = new ArrayList<>();
        sgsnMmeEpgUnsupported.addAll(securityLevelCommands);
        sgsnMmeEpgUnsupported.addAll(ipsecCommands);
        sgsnMmeEpgUnsupported.addAll(enrollmentCommands);
        sgsnMmeEpgUnsupported.addAll(certificateCommands);
        sgsnMmeEpgUnsupported.addAll(trustCommands);
        sgsnMmeEpgUnsupported.addAll(ldapCommand);
        sgsnMmeEpgUnsupported.addAll(crlCheckCommand);
        sgsnMmeEpgUnsupported.addAll(onDemandCrlDownloadCommand);
        sgsnMmeEpgUnsupported.addAll(setCiphersCommand);
        sgsnMmeEpgUnsupported.addAll(rtselCommand);
        sgsnMmeEpgUnsupported.addAll(httpsCommands);
        sgsnMmeEpgUnsupported.addAll(ftpesCommands);
        final Map<String, List<String>> sgsnMmeEpgUnsupportedCommands = new HashMap<>();
        sgsnMmeEpgUnsupportedCommands.put("1.0.0", sgsnMmeEpgUnsupported);

        final List<String> sbgUnsupported = new ArrayList<>();
        sbgUnsupported.addAll(securityLevelCommands);
        sbgUnsupported.addAll(ipsecCommands);
        sbgUnsupported.addAll(enrollmentCommands);
        sbgUnsupported.addAll(certificateCommands);
        sbgUnsupported.addAll(trustCommands);
        sbgUnsupported.addAll(crlCheckCommand);
        sbgUnsupported.addAll(onDemandCrlDownloadCommand);
        sbgUnsupported.addAll(setCiphersCommand);
        sbgUnsupported.addAll(rtselCommand);
        sbgUnsupported.addAll(httpsCommands);
        sbgUnsupported.addAll(ftpesCommands);
        final Map<String, List<String>> sbgUnsupportedCommands = new HashMap<>();
        sbgUnsupportedCommands.put("1.0.0", sbgUnsupported);

        final List<String> cscfMtasUnsupported = new ArrayList<>();
        cscfMtasUnsupported.addAll(securityLevelCommands);
        cscfMtasUnsupported.addAll(ipsecCommands);
        cscfMtasUnsupported.addAll(crlCheckCommand);
        cscfMtasUnsupported.addAll(onDemandCrlDownloadCommand);
        cscfMtasUnsupported.addAll(setCiphersCommand);
        cscfMtasUnsupported.addAll(rtselCommand);
        cscfMtasUnsupported.addAll(httpsCommands);
        cscfMtasUnsupported.addAll(ftpesCommands);
        final Map<String, List<String>> cscfMtasUnsupportedCommands = new HashMap<>();
        cscfMtasUnsupportedCommands.put("1.0.0", cscfMtasUnsupported);

        final List<String> msrbsv1Unsupported = new ArrayList<>();
        msrbsv1Unsupported.addAll(keygenCommands);
        msrbsv1Unsupported.addAll(securityLevelCommands);
        msrbsv1Unsupported.addAll(ipsecCommands);
        msrbsv1Unsupported.addAll(onDemandCrlDownloadCommand);
        msrbsv1Unsupported.addAll(setCiphersCommand);
        msrbsv1Unsupported.addAll(rtselCommand);
        msrbsv1Unsupported.addAll(httpsCommands);
        msrbsv1Unsupported.addAll(ftpesCommands);
        final List<String> msrbsv116AUnsupported = new ArrayList<>();
        msrbsv116AUnsupported.addAll(msrbsv1Unsupported);
        msrbsv116AUnsupported.addAll(crlCheckCommand);
        final Map<String, List<String>> msrbsv1UnsupportedCommands = new HashMap<>();
        msrbsv1UnsupportedCommands.put("1.0.0", msrbsv1Unsupported);
        msrbsv1UnsupportedCommands.put("16.0.0", msrbsv116AUnsupported);

        final List<String> sapcUnsupported = new ArrayList<>();
        sapcUnsupported.addAll(keygenCommands);
        sapcUnsupported.addAll(securityLevelCommands);
        sapcUnsupported.addAll(ipsecCommands);
        sapcUnsupported.addAll(crlCheckCommand);
        sapcUnsupported.addAll(onDemandCrlDownloadCommand);
        sapcUnsupported.addAll(setCiphersCommand);
        sapcUnsupported.addAll(rtselCommand);
        sapcUnsupported.addAll(httpsCommands);
        sapcUnsupported.addAll(ftpesCommands);
        final Map<String, List<String>> sapcUnsupportedCommands = new HashMap<>();
        sapcUnsupportedCommands.put("1.0.0", sapcUnsupported);

        final List<String> radioNodeUnsupported = new ArrayList<>();
        radioNodeUnsupported.addAll(keygenCommands);
        radioNodeUnsupported.addAll(securityLevelCommands);
        radioNodeUnsupported.addAll(ipsecCommands);
        radioNodeUnsupported.addAll(rtselCommand);
        radioNodeUnsupported.addAll(httpsCommands);
        final List<String> radioNode15BUnsupported = new ArrayList<>();
        radioNode15BUnsupported.addAll(radioNodeUnsupported);
        radioNode15BUnsupported.addAll(crlCheckCommand);
        radioNode15BUnsupported.addAll(onDemandCrlDownloadCommand);
        radioNode15BUnsupported.addAll(setCiphersCommand);
        final List<String> radioNode16BUnsupported = new ArrayList<>();
        radioNode16BUnsupported.addAll(radioNodeUnsupported);
        radioNode16BUnsupported.addAll(setCiphersCommand);
        final Map<String, List<String>> radioNodeUnsupportedCommands = new HashMap<>();
        radioNodeUnsupportedCommands.put("1.0.0", radioNodeUnsupported);
        radioNodeUnsupportedCommands.put("15.1.0", radioNode15BUnsupported);
        radioNodeUnsupportedCommands.put("16.1.0", radioNode16BUnsupported);

        final Map<String, List<String>> radioTNodeUnsupportedCommands = new HashMap<>();
        radioTNodeUnsupportedCommands.put("1.0.0", radioNodeUnsupported);
        radioTNodeUnsupportedCommands.put("16.1.0", radioNodeUnsupported);

        final List<String> er6000Unsupported = new ArrayList<>();
        er6000Unsupported.addAll(securityLevelCommands);
        er6000Unsupported.addAll(ipsecCommands);
        er6000Unsupported.addAll(rtselCommand);
        er6000Unsupported.addAll(httpsCommands);
        er6000Unsupported.addAll(ftpesCommands);
        final Map<String, List<String>> er6000UnsupportedCommands = new HashMap<>();
        er6000UnsupportedCommands.put("1.0.0", er6000Unsupported);

        final List<String> er667217AUnsupported = new ArrayList<>();
        er667217AUnsupported.addAll(er6000Unsupported);
        er667217AUnsupported.addAll(crlCheckCommand);
        er667217AUnsupported.addAll(onDemandCrlDownloadCommand);
        er667217AUnsupported.addAll(setCiphersCommand);
        final Map<String, List<String>> er6672UnsupportedCommands = new HashMap<>();
        er6672UnsupportedCommands.put("1.0.0", er6000Unsupported);
        er6672UnsupportedCommands.put("17.0.0", er667217AUnsupported);
        er6672UnsupportedCommands.put("17.1.0", er6000Unsupported);

        final List<String> fronthaulUnsupported = new ArrayList<>();
        fronthaulUnsupported.addAll(keygenCommands);
        fronthaulUnsupported.addAll(snmpCommands);
        fronthaulUnsupported.addAll(securityLevelCommands);
        fronthaulUnsupported.addAll(ipsecCommands);
        fronthaulUnsupported.addAll(enrollmentCommands);
        fronthaulUnsupported.addAll(certificateCommands);
        fronthaulUnsupported.addAll(trustCommands);
        fronthaulUnsupported.addAll(ldapCommand);
        fronthaulUnsupported.addAll(crlCheckCommand);
        fronthaulUnsupported.addAll(onDemandCrlDownloadCommand);
        fronthaulUnsupported.addAll(setCiphersCommand);
        fronthaulUnsupported.addAll(rtselCommand);
        fronthaulUnsupported.addAll(httpsCommands);
        fronthaulUnsupported.addAll(ftpesCommands);
        final Map<String, List<String>> fronthaulUnsupportedCommands = new HashMap<>();
        fronthaulUnsupportedCommands.put("1.0.0", fronthaulUnsupported);

        final List<String> axeUnsupported = new ArrayList<>();
        axeUnsupported.addAll(keygenCommands);
        axeUnsupported.addAll(snmpCommands);
        axeUnsupported.addAll(securityLevelCommands);
        axeUnsupported.addAll(ipsecCommands);
        axeUnsupported.addAll(crlCheckCommand);
        axeUnsupported.addAll(onDemandCrlDownloadCommand);
        axeUnsupported.addAll(setCiphersCommand);
        axeUnsupported.addAll(rtselCommand);
        axeUnsupported.addAll(httpsCommands);
        axeUnsupported.addAll(ftpesCommands);
        final Map<String, List<String>> axeUnsupportedCommands = new HashMap<>();
        axeUnsupportedCommands.put("1.0.0", axeUnsupported);

        final List<String> fiveGUnsupported = new ArrayList<>();
        fiveGUnsupported.addAll(keygenCommands);
        fiveGUnsupported.addAll(securityLevelCommands);
        fiveGUnsupported.addAll(ipsecCommands);
        fiveGUnsupported.addAll(onDemandCrlDownloadCommand);
        fiveGUnsupported.addAll(setCiphersCommand);
        fiveGUnsupported.addAll(rtselCommand);
        fiveGUnsupported.addAll(httpsCommands);
        fiveGUnsupported.addAll(ftpesCommands);
        final Map<String, List<String>> fiveGUnsupportedCommands = new HashMap<>();
        fiveGUnsupportedCommands.put("1.0.0", fiveGUnsupported);

        theUnsupportedCommands.put(ERBS, erbsUnsupportedCommands);
        theUnsupportedCommands.put(RNC, rncUnsupportedCommands);
        theUnsupportedCommands.put(RBS, rbsUnsupportedCommands);
        theUnsupportedCommands.put(MGW, mgwUnsupportedCommands);
        theUnsupportedCommands.put(SGSN_MME, sgsnMmeEpgUnsupportedCommands);
        theUnsupportedCommands.put(EPG, sgsnMmeEpgUnsupportedCommands);
        theUnsupportedCommands.put(VEPG, sgsnMmeEpgUnsupportedCommands);
        theUnsupportedCommands.put(SBG, sbgUnsupportedCommands);
        theUnsupportedCommands.put(vSBG, sbgUnsupportedCommands);
        theUnsupportedCommands.put(CSCF, cscfMtasUnsupportedCommands);
        theUnsupportedCommands.put(vCSCF, cscfMtasUnsupportedCommands);
        theUnsupportedCommands.put(MTAS, cscfMtasUnsupportedCommands);
        theUnsupportedCommands.put(vMTAS, cscfMtasUnsupportedCommands);
        theUnsupportedCommands.put(MSRBS_V1, msrbsv1UnsupportedCommands);
        theUnsupportedCommands.put(SAPC, sapcUnsupportedCommands);
        theUnsupportedCommands.put(RadioNode, radioNodeUnsupportedCommands);
        theUnsupportedCommands.put(RadioTNode, radioTNodeUnsupportedCommands);
        theUnsupportedCommands.put(Router6672, er6672UnsupportedCommands);
        theUnsupportedCommands.put(Router6675, er6000UnsupportedCommands);
        theUnsupportedCommands.put(Router6x71, er6000UnsupportedCommands);
        theUnsupportedCommands.put(Router6274, er6000UnsupportedCommands);
        theUnsupportedCommands.put(FRONTHAUL_6080, fronthaulUnsupportedCommands);
        theUnsupportedCommands.put(FRONTHAUL_6020, fronthaulUnsupportedCommands);
        theUnsupportedCommands.put(BSC, axeUnsupportedCommands);
        theUnsupportedCommands.put(HLR_FE, axeUnsupportedCommands);
        theUnsupportedCommands.put(vHLR_FE, axeUnsupportedCommands);
        theUnsupportedCommands.put(HLR_FE_BSP, axeUnsupportedCommands);
        theUnsupportedCommands.put(HLR_FE_IS, axeUnsupportedCommands);
        theUnsupportedCommands.put(MSC, axeUnsupportedCommands);
        theUnsupportedCommands.put(HLR, axeUnsupportedCommands);
        theUnsupportedCommands.put(vEME, fiveGUnsupportedCommands);
        theUnsupportedCommands.put(vWCG, fiveGUnsupportedCommands);
        theUnsupportedCommands.put(HSS_FE, fiveGUnsupportedCommands);
        theUnsupportedCommands.put(VHSS_FE, fiveGUnsupportedCommands);
        theUnsupportedCommands.put(vIPWorks, fiveGUnsupportedCommands);
        theUnsupportedCommands.put(vUPG, fiveGUnsupportedCommands);
        theUnsupportedCommands.put(BSP, fiveGUnsupportedCommands);
        theUnsupportedCommands.put(vBGF, fiveGUnsupportedCommands);
        theUnsupportedCommands.put(vMRF, fiveGUnsupportedCommands);
        theUnsupportedCommands.put(RnNode, fiveGUnsupportedCommands);
        theUnsupportedCommands.put(vRC, fiveGUnsupportedCommands);
        theUnsupportedCommands.put(vPP, fiveGUnsupportedCommands);
        theUnsupportedCommands.put(vRM, fiveGUnsupportedCommands);
        theUnsupportedCommands.put(FIVEGRadioNode, fiveGUnsupportedCommands);
        theUnsupportedCommands.put(VTFRadioNode, fiveGUnsupportedCommands);
        theUnsupportedCommands.put(vSD, fiveGUnsupportedCommands);
        theUnsupportedCommands.put(RVNFM, fiveGUnsupportedCommands);
        theUnsupportedCommands.put(vRSM, fiveGUnsupportedCommands);
    }

    /**
     * Mock the 'credentialsParams' capability.
     *
     * This capability models the expected and unexpected parameters in credentials create/update commands for a target type.
     *
     * A default value is provided: add an element in the node map for each specific node type whose specific capability value differs from the
     * default.
     */
    static final Map<String, List<String>> defaultCredentialsParams = new HashMap<>();
    static {
        final List<String> defaultExpectedParams = new ArrayList<>();
        defaultExpectedParams.add("secureusername");
        defaultExpectedParams.add("secureuserpassword");

        final List<String> defaultUnexpectedParams = new ArrayList<>();
        defaultUnexpectedParams.add("normalusername");
        defaultUnexpectedParams.add("normaluserpassword");
        defaultUnexpectedParams.add("rootusername");
        defaultUnexpectedParams.add("rootuserpassword");
        defaultUnexpectedParams.add("nwieasecureusername");
        defaultUnexpectedParams.add("nwieasecureuserpassword");
        defaultUnexpectedParams.add("nwiebsecureusername");
        defaultUnexpectedParams.add("nwiebsecureuserpassword");

        defaultCredentialsParams.put("expected", defaultExpectedParams);
        defaultCredentialsParams.put("unexpected", defaultUnexpectedParams);
    }

    static final Map<String, Map<String, List<String>>> theCredentialsParams = new HashMap<String, Map<String, List<String>>>();
    static {
        final List<String> cppExpectedParams = new ArrayList<>();
        cppExpectedParams.add("normalusername");
        cppExpectedParams.add("normaluserpassword");
        cppExpectedParams.add("rootusername");
        cppExpectedParams.add("rootuserpassword");
        cppExpectedParams.add("secureusername");
        cppExpectedParams.add("secureuserpassword");

        final List<String> cppUnexpectedParams = new ArrayList<>();
        cppUnexpectedParams.add("nwieasecureusername");
        cppUnexpectedParams.add("nwieasecureuserpassword");
        cppUnexpectedParams.add("nwiebsecureusername");
        cppUnexpectedParams.add("nwiebsecureuserpassword");

        final List<String> bscExpectedParams = new ArrayList<>();
        bscExpectedParams.add("secureusername");
        bscExpectedParams.add("secureuserpassword");
        bscExpectedParams.add("nwieasecureusername");
        bscExpectedParams.add("nwieasecureuserpassword");
        bscExpectedParams.add("nwiebsecureusername");
        bscExpectedParams.add("nwiebsecureuserpassword");

        final List<String> bscUnexpectedParams = new ArrayList<>();
        bscUnexpectedParams.add("normalusername");
        bscUnexpectedParams.add("normaluserpassword");
        bscUnexpectedParams.add("rootusername");
        bscUnexpectedParams.add("rootuserpassword");

        final List<String> esaExpectedParams = new ArrayList<>();
        esaExpectedParams.add("secureusername");
        esaExpectedParams.add("secureuserpassword");
        esaExpectedParams.add("normalusername");
        esaExpectedParams.add("normaluserpassword");

        final List<String> esaUnexpectedParams = new ArrayList<>();
        esaUnexpectedParams.add("nwieasecureusername");
        esaUnexpectedParams.add("nwieasecureuserpassword");
        esaUnexpectedParams.add("nwiebsecureusername");
        esaUnexpectedParams.add("nwiebsecureuserpassword");
        esaUnexpectedParams.add("rootusername");
        esaUnexpectedParams.add("rootuserpassword");

        final Map<String, List<String>> bscCommandParams = new HashMap<>();
        bscCommandParams.put("expected", bscExpectedParams);
        bscCommandParams.put("unexpected", bscUnexpectedParams);

        final Map<String, List<String>> cppCommandParams = new HashMap<>();
        cppCommandParams.put("expected", cppExpectedParams);
        cppCommandParams.put("unexpected", cppUnexpectedParams);

        final Map<String, List<String>> minilinkCommandParams = new HashMap<>();
        minilinkCommandParams.put("expected", cppExpectedParams);
        minilinkCommandParams.put("unexpected", cppUnexpectedParams);

        final Map<String, List<String>> fronthaulCommandParams = new HashMap<>();
        fronthaulCommandParams.put("expected", cppExpectedParams);
        fronthaulCommandParams.put("unexpected", cppUnexpectedParams);

        final Map<String, List<String>> esaCommandParams = new HashMap<>();
        esaCommandParams.put("expected", esaExpectedParams);
        esaCommandParams.put("unexpected", esaUnexpectedParams);

        theCredentialsParams.put(ERBS, cppCommandParams);
        theCredentialsParams.put(RNC, cppCommandParams);
        theCredentialsParams.put(RBS, cppCommandParams);
        theCredentialsParams.put(MGW, cppCommandParams);
        theCredentialsParams.put(MINI_LINK_Indoor, minilinkCommandParams);
        theCredentialsParams.put(MINI_LINK_CN210, minilinkCommandParams);
        theCredentialsParams.put(MINI_LINK_CN510R1, minilinkCommandParams);
        theCredentialsParams.put(MINI_LINK_CN510R2, minilinkCommandParams);
        theCredentialsParams.put(MINI_LINK_CN810R1, minilinkCommandParams);
        theCredentialsParams.put(MINI_LINK_CN810R2, minilinkCommandParams);
        theCredentialsParams.put(MINI_LINK_665x, minilinkCommandParams);
        theCredentialsParams.put(MINI_LINK_669x, minilinkCommandParams);
        theCredentialsParams.put(MINI_LINK_MW2, minilinkCommandParams);
        theCredentialsParams.put(MINI_LINK_6352, minilinkCommandParams);
        theCredentialsParams.put(MINI_LINK_6351, minilinkCommandParams);
        theCredentialsParams.put(MINI_LINK_6366, minilinkCommandParams);
        theCredentialsParams.put(MINI_LINK_PT2020, minilinkCommandParams);
        theCredentialsParams.put(Switch_6391, fronthaulCommandParams);
        theCredentialsParams.put(Fronthaul_6392, fronthaulCommandParams);
        theCredentialsParams.put(FRONTHAUL_6080, fronthaulCommandParams);
        theCredentialsParams.put(FRONTHAUL_6020, fronthaulCommandParams);
        theCredentialsParams.put(BSC, bscCommandParams);
        theCredentialsParams.put(GenericESA, esaCommandParams);
    }

    /**
     * Mock the 'supportedCertificateTypes' capability.
     *
     * This capability models the supported certificate types in certificate/trust commands for a target type.
     *
     * A default value is provided: add an element in the node map for each specific node type supporting certificate management and whose specific
     * capability value differs from the default.
     */
    static final List<String> defaultSupportedCertTypes = Arrays.asList("OAM", "IPSEC");

    static final Map<String, List<String>> theSupportedCertTypes = new HashMap<>();
    static {
        final List<String> mgwSupportedCertTypes = Arrays.asList("OAM");
        final List<String> axeSupportedCertTypes = Arrays.asList("OAM");
        final List<String> imsSupportedCertTypes = Arrays.asList("OAM");

        theSupportedCertTypes.put(MGW, mgwSupportedCertTypes);
        theSupportedCertTypes.put(BSC, axeSupportedCertTypes);
        theSupportedCertTypes.put(HLR_FE, axeSupportedCertTypes);
        theSupportedCertTypes.put(vHLR_FE, axeSupportedCertTypes);
        theSupportedCertTypes.put(HLR_FE_BSP, axeSupportedCertTypes);
        theSupportedCertTypes.put(HLR_FE_IS, axeSupportedCertTypes);
        theSupportedCertTypes.put(MSC, axeSupportedCertTypes);
        theSupportedCertTypes.put(HLR, axeSupportedCertTypes);
        theSupportedCertTypes.put(HSS_FE, imsSupportedCertTypes);
        theSupportedCertTypes.put(VHSS_FE, imsSupportedCertTypes);
    }

    /**
     * Mock the 'crlCheckSupportedCertificateTypes' capability.
     *
     * This capability models the supported certificate types in crlcheck commands for a target type.
     *
     * A default value is provided: add an element in the node map for each specific node type supporting crlcheck and whose specific capability value
     * differs from the default.
     */
    static final List<String> defaultCrlCheckSupportedCertTypes = null;

    static final Map<String, Map<String, List<String>>> theCrlCheckSupportedCertTypes = new HashMap<>();
    static {
        final List<String> oamIpsecSupportedCertTypes = Arrays.asList("OAM", "IPSEC");
        final List<String> oamIpsecAllSupportedCertTypes = Arrays.asList("OAM", "IPSEC", "ALL");

        final Map<String, List<String>> er6000SupportedCertTypesForModelVersions = new HashMap<>();
        er6000SupportedCertTypesForModelVersions.put("1.0.0", oamIpsecSupportedCertTypes);
        er6000SupportedCertTypesForModelVersions.put("17.0.0", oamIpsecSupportedCertTypes);
        er6000SupportedCertTypesForModelVersions.put("17.1.0", oamIpsecSupportedCertTypes);

        final Map<String, List<String>> ecimSupportedCertTypesForModelVersions = new HashMap<>();
        ecimSupportedCertTypesForModelVersions.put("1.0.0", oamIpsecSupportedCertTypes);
        ecimSupportedCertTypesForModelVersions.put("15.1.0", oamIpsecSupportedCertTypes);
        ecimSupportedCertTypesForModelVersions.put("16.0.0", oamIpsecSupportedCertTypes);
        ecimSupportedCertTypesForModelVersions.put("16.1.0", oamIpsecSupportedCertTypes);
        ecimSupportedCertTypesForModelVersions.put("17.0.0", oamIpsecSupportedCertTypes);
        ecimSupportedCertTypesForModelVersions.put("17.1.0", oamIpsecSupportedCertTypes);

        final List<String> cppSupportedCertTypes = Arrays.asList("ALL");
        final Map<String, List<String>> cppSupportedCertTypesForModelVersions = new HashMap<>();
        cppSupportedCertTypesForModelVersions.put("1.0.0", cppSupportedCertTypes);
        cppSupportedCertTypesForModelVersions.put("13.1.0", cppSupportedCertTypes);
        cppSupportedCertTypesForModelVersions.put("14.0.0", cppSupportedCertTypes);
        cppSupportedCertTypesForModelVersions.put("14.1.0", cppSupportedCertTypes);
        cppSupportedCertTypesForModelVersions.put("15.1.0", cppSupportedCertTypes);

        final List<String> mgwSupportedCertTypes = Arrays.asList("OAM", "ALL");
        final Map<String, List<String>> mgwSupportedCertTypesForModelVersions = new HashMap<>();
        mgwSupportedCertTypesForModelVersions.put("1.0.0", mgwSupportedCertTypes);
        mgwSupportedCertTypesForModelVersions.put("14.1.0", mgwSupportedCertTypes);

        final List<String> msrbsv1Before16BSupportedCertTypes = new ArrayList<>();
        final Map<String, List<String>> msrbsv1SupportedCertTypesForModelVersions = new HashMap<>();
        msrbsv1SupportedCertTypesForModelVersions.put("1.0.0", oamIpsecAllSupportedCertTypes);
        msrbsv1SupportedCertTypesForModelVersions.put("16.0.0", msrbsv1Before16BSupportedCertTypes);

        final List<String> radioNodeBefore16BSupportedCertTypes = new ArrayList<>();
        final List<String> radioNodeBefore17ASupportedCertTypes = Arrays.asList("OAM");
        final Map<String, List<String>> radioNodeSupportedCertTypesForModelVersions = new HashMap<>();
        radioNodeSupportedCertTypesForModelVersions.put("15.1.0", radioNodeBefore16BSupportedCertTypes);
        radioNodeSupportedCertTypesForModelVersions.put("16.1.0", radioNodeBefore17ASupportedCertTypes);
        radioNodeSupportedCertTypesForModelVersions.put("1.0.0", oamIpsecAllSupportedCertTypes);

        final List<String> radioTNodeBefore17ASupportedCertTypes = Arrays.asList("OAM");
        final Map<String, List<String>> radioTNodeSupportedCertTypesForModelVersions = new HashMap<>();
        radioTNodeSupportedCertTypesForModelVersions.put("16.1.0", radioTNodeBefore17ASupportedCertTypes);
        radioTNodeSupportedCertTypesForModelVersions.put("1.0.0", oamIpsecAllSupportedCertTypes);

        theCrlCheckSupportedCertTypes.put(ERBS, cppSupportedCertTypesForModelVersions);
        theCrlCheckSupportedCertTypes.put(RNC, cppSupportedCertTypesForModelVersions);
        theCrlCheckSupportedCertTypes.put(RBS, cppSupportedCertTypesForModelVersions);
        theCrlCheckSupportedCertTypes.put(MGW, mgwSupportedCertTypesForModelVersions);
        theCrlCheckSupportedCertTypes.put(MSRBS_V1, msrbsv1SupportedCertTypesForModelVersions);
        theCrlCheckSupportedCertTypes.put(RadioNode, radioNodeSupportedCertTypesForModelVersions);
        theCrlCheckSupportedCertTypes.put(RadioTNode, radioTNodeSupportedCertTypesForModelVersions);
        theCrlCheckSupportedCertTypes.put(Router6672, er6000SupportedCertTypesForModelVersions);
        theCrlCheckSupportedCertTypes.put(Router6675, er6000SupportedCertTypesForModelVersions);
        theCrlCheckSupportedCertTypes.put(Router6x71, er6000SupportedCertTypesForModelVersions);
        theCrlCheckSupportedCertTypes.put(Router6274, er6000SupportedCertTypesForModelVersions);
        theCrlCheckSupportedCertTypes.put(FIVEGRadioNode, ecimSupportedCertTypesForModelVersions);
        theCrlCheckSupportedCertTypes.put(VTFRadioNode, ecimSupportedCertTypesForModelVersions);
        theCrlCheckSupportedCertTypes.put(vSD, ecimSupportedCertTypesForModelVersions);
        theCrlCheckSupportedCertTypes.put(RVNFM, ecimSupportedCertTypesForModelVersions);
    }

    /**
     * Mock the 'defaultEntityProfiles' capability.
     *
     * This capability models the default entity profiles for the supported certificate types for a target type.
     *
     * A default value is provided: add an element in the node map for each specific target type whose specific capability value differs from the
     * default.
     */
    static final Map<String, String> defaultDefaultEntityProfiles = null;

    static final Map<String, Map<String, String>> theDefaultEntityProfiles = new HashMap<>();
    static {
        final Map<String, String> cppDefaultEntityProfiles = new HashMap<>();
        cppDefaultEntityProfiles.put("IPSEC", "MicroRBSIPSec_SAN_CHAIN_EP");
        cppDefaultEntityProfiles.put("OAM", "MicroRBSOAM_CHAIN_EP");

        final Map<String, String> mgwDefaultEntityProfiles = new HashMap<>();
        mgwDefaultEntityProfiles.put("OAM", "MicroRBSOAM_CHAIN_EP");

        final Map<String, String> msrbsv1DefaultEntityProfiles = new HashMap<>();
        msrbsv1DefaultEntityProfiles.put("IPSEC", "PicoRBSIPSec_WA_RS_SAN_CHAIN_EP");
        msrbsv1DefaultEntityProfiles.put("OAM", "PicoRBSOAM_RS_CHAIN_SAN_EP");

        final Map<String, String> radioNodeDefaultEntityProfiles = new HashMap<>();
        radioNodeDefaultEntityProfiles.put("IPSEC", "DUSGen2IPSec_SAN_CHAIN_EP");
        radioNodeDefaultEntityProfiles.put("OAM", "DUSGen2OAM_CHAIN_EP");

        final Map<String, String> ecimDefaultEntityProfiles = new HashMap<>();
        ecimDefaultEntityProfiles.put("IPSEC", "DUSGen2IPSec_SAN_CHAIN_EP");
        ecimDefaultEntityProfiles.put("OAM", "DUSGen2OAM_CHAIN_EP");

        final Map<String, String> axeDefaultEntityProfiles = new HashMap<>();
        axeDefaultEntityProfiles.put("OAM", "DUSGen2OAM_CHAIN_EP");

        final Map<String, String> imsDefaultEntityProfiles = new HashMap<>();
        imsDefaultEntityProfiles.put("OAM", "DUSGen2OAM_CHAIN_EP");

        final Map<String, String> rVNFMNodeDefaultEntityProfiles = new HashMap<>();
        rVNFMNodeDefaultEntityProfiles.put("IPSEC", "DUSGen2IPSec_SAN_CHAIN_EP");
        rVNFMNodeDefaultEntityProfiles.put("OAM", "VNFM_IP_EP");

        theDefaultEntityProfiles.put(ERBS, cppDefaultEntityProfiles);
        theDefaultEntityProfiles.put(RNC, cppDefaultEntityProfiles);
        theDefaultEntityProfiles.put(RBS, cppDefaultEntityProfiles);
        theDefaultEntityProfiles.put(MGW, mgwDefaultEntityProfiles);

        theDefaultEntityProfiles.put(MSRBS_V1, msrbsv1DefaultEntityProfiles);
        theDefaultEntityProfiles.put(SAPC, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(MTAS, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(CSCF, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(RadioNode, radioNodeDefaultEntityProfiles);
        theDefaultEntityProfiles.put(RadioTNode, ecimDefaultEntityProfiles);

        theDefaultEntityProfiles.put(Router6672, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(Router6675, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(Router6x71, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(Router6274, ecimDefaultEntityProfiles);

        theDefaultEntityProfiles.put(vMTAS, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(vCSCF, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(BSC, axeDefaultEntityProfiles);
        theDefaultEntityProfiles.put(HLR_FE, axeDefaultEntityProfiles);
        theDefaultEntityProfiles.put(vHLR_FE, axeDefaultEntityProfiles);
        theDefaultEntityProfiles.put(HLR_FE_BSP, axeDefaultEntityProfiles);
        theDefaultEntityProfiles.put(HLR_FE_IS, axeDefaultEntityProfiles);
        theDefaultEntityProfiles.put(vPP, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(vEME, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(vWCG, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(HSS_FE, imsDefaultEntityProfiles);
        theDefaultEntityProfiles.put(VHSS_FE, imsDefaultEntityProfiles);
        theDefaultEntityProfiles.put(vIPWorks, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(vUPG, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(BSP, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(vBGF, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(vMRF, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(VTFRadioNode, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(vSD, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(RVNFM, rVNFMNodeDefaultEntityProfiles);

        theDefaultEntityProfiles.put(MSC, axeDefaultEntityProfiles);
        theDefaultEntityProfiles.put(HLR, axeDefaultEntityProfiles);
        theDefaultEntityProfiles.put(RnNode, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(vRM, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(vRC, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(FIVEGRadioNode, ecimDefaultEntityProfiles);
        theDefaultEntityProfiles.put(vRSM, ecimDefaultEntityProfiles);
    }

    /**
     * Mock the 'issueCertificateWorkflows' capability.
     *
     * This capability models the certificate issue/reissue workflow names for all supported certificate types for a node type.
     *
     * A default value is provided: add an element in the node map for each specific node type supporting certificate management and whose specific
     * capability value differs from the default.
     */
    static final Map<String, String> defaultIssueCertWorkflows = new HashMap<>();
    static {
        defaultIssueCertWorkflows.put("IPSEC", "COMIssueCert");
        defaultIssueCertWorkflows.put("OAM", "COMIssueCert");
    }

    static final Map<String, Map<String, String>> theIssueCertWorkflows = new HashMap<>();
    static {
        final Map<String, String> cppIssueCertWorkflows = new HashMap<>();
        cppIssueCertWorkflows.put("IPSEC", "CPPIssueCertIpSec");
        cppIssueCertWorkflows.put("OAM", "CPPIssueCert");

        final Map<String, String> mgwIssueCertWorkflows = new HashMap<>();
        mgwIssueCertWorkflows.put("OAM", "CPPIssueCert");

        final Map<String, String> axeIssueCertWorkflows = new HashMap<>();
        axeIssueCertWorkflows.put("OAM", "COMIssueCert");

        final Map<String, String> imsIssueCertWorkflows = new HashMap<>();
        imsIssueCertWorkflows.put("OAM", "COMIssueCert");

        theIssueCertWorkflows.put(ERBS, cppIssueCertWorkflows);
        theIssueCertWorkflows.put(RNC, cppIssueCertWorkflows);
        theIssueCertWorkflows.put(RBS, cppIssueCertWorkflows);
        theIssueCertWorkflows.put(MGW, mgwIssueCertWorkflows);
        theIssueCertWorkflows.put(BSC, axeIssueCertWorkflows);
        theIssueCertWorkflows.put(HLR_FE, axeIssueCertWorkflows);
        theIssueCertWorkflows.put(vHLR_FE, axeIssueCertWorkflows);
        theIssueCertWorkflows.put(HLR_FE_BSP, axeIssueCertWorkflows);
        theIssueCertWorkflows.put(HLR_FE_IS, axeIssueCertWorkflows);
        theIssueCertWorkflows.put(MSC, axeIssueCertWorkflows);
        theIssueCertWorkflows.put(HLR, axeIssueCertWorkflows);
        theIssueCertWorkflows.put(HSS_FE, imsIssueCertWorkflows);
        theIssueCertWorkflows.put(VHSS_FE, imsIssueCertWorkflows);
    }

    /**
     * Mock the 'crlCheckWorkflows' capability.
     *
     * This capability models the crlcheck workflow names for all supported crl certificate types for a node type.
     *
     * A default value is provided: add an element in the node map for each specific node type supporting certificate management and whose specific
     * capability value differs from the default.
     */
    static final Map<String, String> defaultCrlCheckWorkflows = new HashMap<>();
    static {
        defaultCrlCheckWorkflows.put("IPSEC", "COMEnableOrDisableCRLCheck");
        defaultCrlCheckWorkflows.put("OAM", "COMEnableOrDisableCRLCheck");
        defaultCrlCheckWorkflows.put("ALL", "COMEnableOrDisableCRLCheck");
    }

    static final Map<String, Map<String, String>> theCrlCheckWorkflows = new HashMap<>();
    static {
        final Map<String, String> cppCrlCheckWorkflows = new HashMap<>();
        cppCrlCheckWorkflows.put("IPSEC", "CPPEnableOrDisableCRLCheck");
        cppCrlCheckWorkflows.put("OAM", "CPPEnableOrDisableCRLCheck");
        cppCrlCheckWorkflows.put("ALL", "CPPEnableOrDisableCRLCheck");

        final Map<String, String> mgwCrlCheckWorkflows = new HashMap<>();
        mgwCrlCheckWorkflows.put("OAM", "CPPEnableOrDisableCRLCheck");
        mgwCrlCheckWorkflows.put("ALL", "CPPEnableOrDisableCRLCheck");

        theCrlCheckWorkflows.put(ERBS, cppCrlCheckWorkflows);
        theCrlCheckWorkflows.put(RNC, cppCrlCheckWorkflows);
        theCrlCheckWorkflows.put(RBS, cppCrlCheckWorkflows);
        theCrlCheckWorkflows.put(MGW, mgwCrlCheckWorkflows);
    }

    /**
     * Mock the 'trustDistributeWorkflows' capability.
     *
     * This capability models the trust distribute workflow names for all supported certificate types for a node type.
     *
     * A default value is provided: add an element in the node map for each specific node type supporting certificate management and whose specific
     * capability value differs from the default.
     */
    static final Map<String, String> defaultTrustDistrWorkflows = new HashMap<>();
    static {
        defaultTrustDistrWorkflows.put("IPSEC", "COMIssueTrustCert");
        defaultTrustDistrWorkflows.put("OAM", "COMIssueTrustCert");
    }

    static final Map<String, Map<String, String>> theTrustDistrWorkflows = new HashMap<>();
    static {
        final Map<String, String> cppTrustDistrWorkflows = new HashMap<>();
        cppTrustDistrWorkflows.put("IPSEC", "CPPIssueTrustCertIpSec");
        cppTrustDistrWorkflows.put("OAM", "CPPIssueTrustCert");

        final Map<String, String> mgwTrustDistrWorkflows = new HashMap<>();
        mgwTrustDistrWorkflows.put("OAM", "CPPIssueTrustCert");

        final Map<String, String> axeTrustDistrWorkflows = new HashMap<>();
        axeTrustDistrWorkflows.put("OAM", "COMIssueTrustCert");

        final Map<String, String> imsTrustDistrWorkflows = new HashMap<>();
        imsTrustDistrWorkflows.put("OAM", "COMIssueTrustCert");

        theTrustDistrWorkflows.put(ERBS, cppTrustDistrWorkflows);
        theTrustDistrWorkflows.put(RNC, cppTrustDistrWorkflows);
        theTrustDistrWorkflows.put(RBS, cppTrustDistrWorkflows);
        theTrustDistrWorkflows.put(MGW, mgwTrustDistrWorkflows);
        theTrustDistrWorkflows.put(BSC, axeTrustDistrWorkflows);
        theTrustDistrWorkflows.put(HLR_FE, axeTrustDistrWorkflows);
        theTrustDistrWorkflows.put(vHLR_FE, axeTrustDistrWorkflows);
        theTrustDistrWorkflows.put(HLR_FE_BSP, axeTrustDistrWorkflows);
        theTrustDistrWorkflows.put(HLR_FE_IS, axeTrustDistrWorkflows);
        theTrustDistrWorkflows.put(MSC, axeTrustDistrWorkflows);
        theTrustDistrWorkflows.put(HLR, axeTrustDistrWorkflows);
        theTrustDistrWorkflows.put(HSS_FE, imsTrustDistrWorkflows);
        theTrustDistrWorkflows.put(VHSS_FE, imsTrustDistrWorkflows);
    }

    /**
     * Mock the 'trustRemoveWorkflows' capability.
     *
     * This capability models the trust remove workflow names for all supported certificate types for a node type.
     *
     * A default value is provided: add an element in the node map for each specific node type supporting certificate management and whose specific
     * capability value differs from the default.
     */
    static final Map<String, String> defaultTrustRemoveWorkflows = new HashMap<>();
    static {
        defaultTrustRemoveWorkflows.put("IPSEC", "COMRemoveTrust");
        defaultTrustRemoveWorkflows.put("OAM", "COMRemoveTrust");
    }

    static final Map<String, Map<String, String>> theTrustRemoveWorkflows = new HashMap<>();
    static {
        final Map<String, String> cppTrustRemoveWorkflows = new HashMap<>();
        cppTrustRemoveWorkflows.put("IPSEC", "CPPRemoveTrustNewIPSEC");
        cppTrustRemoveWorkflows.put("OAM", "CPPRemoveTrustOAM");

        final Map<String, String> mgwTrustRemoveWorkflows = new HashMap<>();
        mgwTrustRemoveWorkflows.put("OAM", "CPPRemoveTrustOAM");

        final Map<String, String> axeTrustRemoveWorkflows = new HashMap<>();
        axeTrustRemoveWorkflows.put("OAM", "COMRemoveTrust");

        final Map<String, String> imsTrustRemoveWorkflows = new HashMap<>();
        imsTrustRemoveWorkflows.put("OAM", "COMRemoveTrust");

        theTrustRemoveWorkflows.put(ERBS, cppTrustRemoveWorkflows);
        theTrustRemoveWorkflows.put(RNC, cppTrustRemoveWorkflows);
        theTrustRemoveWorkflows.put(RBS, cppTrustRemoveWorkflows);
        theTrustRemoveWorkflows.put(MGW, mgwTrustRemoveWorkflows);
        theTrustRemoveWorkflows.put(BSC, axeTrustRemoveWorkflows);
        theTrustRemoveWorkflows.put(HLR_FE, axeTrustRemoveWorkflows);
        theTrustRemoveWorkflows.put(vHLR_FE, axeTrustRemoveWorkflows);
        theTrustRemoveWorkflows.put(HLR_FE_BSP, axeTrustRemoveWorkflows);
        theTrustRemoveWorkflows.put(HLR_FE_IS, axeTrustRemoveWorkflows);
        theTrustRemoveWorkflows.put(MSC, axeTrustRemoveWorkflows);
        theTrustRemoveWorkflows.put(HLR, axeTrustRemoveWorkflows);
        theTrustRemoveWorkflows.put(HSS_FE, imsTrustRemoveWorkflows);
        theTrustRemoveWorkflows.put(VHSS_FE, imsTrustRemoveWorkflows);
    }

    /**
     * Mock the 'momType' capability.
     *
     * This capability models the supported MOM (if any) for a node type.
     *
     * A default value is provided: add an element in the node map for each specific node type supporting a MOM and whose specific capability value
     * differs from the default.
     */
    static final String defaultMomType = null;

    static final Map<String, String> theMomType = new HashMap<>();
    static {
        final String ecimMomType = "ECIM";
        final String cppMomType = "CPP";

        theMomType.put(ERBS, cppMomType);
        theMomType.put(RNC, cppMomType);
        theMomType.put(RBS, cppMomType);
        theMomType.put(MGW, cppMomType);
        theMomType.put(MSRBS_V1, ecimMomType);
        theMomType.put(SAPC, ecimMomType);
        theMomType.put(RadioNode, ecimMomType);
        theMomType.put(RadioTNode, ecimMomType);
        theMomType.put(SBG, ecimMomType);
        theMomType.put(vSBG, ecimMomType);
        theMomType.put(CSCF, ecimMomType);
        theMomType.put(vCSCF, ecimMomType);
        theMomType.put(MTAS, ecimMomType);
        theMomType.put(vMTAS, ecimMomType);
        theMomType.put(BSC, ecimMomType);
        theMomType.put(HLR_FE, ecimMomType);
        theMomType.put(vHLR_FE, ecimMomType);
        theMomType.put(HLR_FE_BSP, ecimMomType);
        theMomType.put(HLR_FE_IS, ecimMomType);
        theMomType.put(MSC, ecimMomType);
        theMomType.put(HLR, ecimMomType);
        theMomType.put(RnNode, ecimMomType);
        theMomType.put(vPP, ecimMomType);
        theMomType.put(vRM, ecimMomType);
        theMomType.put(vRC, ecimMomType);
        theMomType.put(vEME, ecimMomType);
        theMomType.put(vWCG, ecimMomType);
        theMomType.put(HSS_FE, ecimMomType);
        theMomType.put(VHSS_FE, ecimMomType);
        theMomType.put(vIPWorks, ecimMomType);
        theMomType.put(vUPG, ecimMomType);
        theMomType.put(BSP, ecimMomType);
        theMomType.put(vBGF, ecimMomType);
        theMomType.put(vMRF, ecimMomType);
        theMomType.put(Router6672, ecimMomType);
        theMomType.put(Router6675, ecimMomType);
        theMomType.put(Router6x71, ecimMomType);
        theMomType.put(Router6274, ecimMomType);
        theMomType.put(FIVEGRadioNode, ecimMomType);
        theMomType.put(VTFRadioNode, ecimMomType);
        theMomType.put(vSD, ecimMomType);
        theMomType.put(RVNFM, ecimMomType);
        theMomType.put(vRSM, ecimMomType);
    }

    /**
     * Mock the 'isCertificateManagementSupported' capability.
     *
     * This capability models if the certificate management (in terms of node certificate and trusted certificates) is supported for a target type.
     *
     * For target types supporting CPP MOM, this means that Security MO and/or Ipsec MO configuration is supported.
     *
     * For target types supporting ECIM MOM, this means that SecM/CertM fragment is supported.
     *
     * A default value is provided: add an element in the node map for each specific target type whose specific capability value differs from the
     * default.
     */
    static final Boolean defaultIsCertificateManagementSupported = false;

    static final Map<String, Boolean> theIsCertificateManagementSupported = new HashMap<>();
    static {
        final boolean isSupported = true;

        theIsCertificateManagementSupported.put(ERBS, isSupported);
        theIsCertificateManagementSupported.put(RNC, isSupported);
        theIsCertificateManagementSupported.put(RBS, isSupported);
        theIsCertificateManagementSupported.put(MGW, isSupported);

        theIsCertificateManagementSupported.put(MSRBS_V1, isSupported);
        theIsCertificateManagementSupported.put(RadioNode, isSupported);
        theIsCertificateManagementSupported.put(CSCF, isSupported);
        theIsCertificateManagementSupported.put(MTAS, isSupported);
        theIsCertificateManagementSupported.put(RadioTNode, isSupported);
        theIsCertificateManagementSupported.put(SAPC, isSupported);

        theIsCertificateManagementSupported.put(Router6672, isSupported);
        theIsCertificateManagementSupported.put(Router6675, isSupported);
        theIsCertificateManagementSupported.put(Router6x71, isSupported);
        theIsCertificateManagementSupported.put(Router6274, isSupported);

        theIsCertificateManagementSupported.put(vSD, isSupported);
        theIsCertificateManagementSupported.put(vMTAS, isSupported);
        theIsCertificateManagementSupported.put(vIPWorks, isSupported);
        theIsCertificateManagementSupported.put(vUPG, isSupported);
        theIsCertificateManagementSupported.put(VHSS_FE, isSupported);
        theIsCertificateManagementSupported.put(BSP, isSupported);
        theIsCertificateManagementSupported.put(vPP, isSupported);
        theIsCertificateManagementSupported.put(vCSCF, isSupported);
        theIsCertificateManagementSupported.put(vMRF, isSupported);
        theIsCertificateManagementSupported.put(vBGF, isSupported);
        theIsCertificateManagementSupported.put(RVNFM, isSupported);
        theIsCertificateManagementSupported.put(vWCG, isSupported);
        theIsCertificateManagementSupported.put(HSS_FE, isSupported);
        theIsCertificateManagementSupported.put(vEME, isSupported);
        theIsCertificateManagementSupported.put(BSC, isSupported);
        theIsCertificateManagementSupported.put(HLR_FE, isSupported);
        theIsCertificateManagementSupported.put(vHLR_FE, isSupported);
        theIsCertificateManagementSupported.put(HLR_FE_BSP, isSupported);
        theIsCertificateManagementSupported.put(HLR_FE_IS, isSupported);
        theIsCertificateManagementSupported.put(VTFRadioNode, isSupported);

        theIsCertificateManagementSupported.put(RnNode, isSupported);
        theIsCertificateManagementSupported.put(vRM, isSupported);
        theIsCertificateManagementSupported.put(vRC, isSupported);
        theIsCertificateManagementSupported.put(MSC, isSupported);
        theIsCertificateManagementSupported.put(HLR, isSupported);
        theIsCertificateManagementSupported.put(FIVEGRadioNode, isSupported);
        theIsCertificateManagementSupported.put(vRSM, isSupported);
    }

    /**
     * Mock the 'supportedSecurityLevels' capability.
     *
     * This capability models the supported security levels for a target type.
     *
     * A default value is provided: add an element in the node map for each specific CPP target type whose specific capability value differs from the
     * default.
     */
    static final List<String> defaultSupportedSecurityLevels = Arrays.asList("LEVEL_NOT_SUPPORTED");

    static final Map<String, List<String>> theSupportedSecurityLevels = new HashMap<>();
    static {
        final List<String> cppSupportedSecurityLevels = Arrays.asList("LEVEL_1", "LEVEL_2");

        theSupportedSecurityLevels.put(ERBS, cppSupportedSecurityLevels);
        theSupportedSecurityLevels.put(RNC, cppSupportedSecurityLevels);
        theSupportedSecurityLevels.put(RBS, cppSupportedSecurityLevels);
        theSupportedSecurityLevels.put(MGW, cppSupportedSecurityLevels);
    }

    /**
     * Mock the 'supportedEnrollmentModes' capability.
     *
     * This capability models the supported enrollment modes for a target type.
     *
     * A default value is provided: add an element in the node map for each specific target type whose specific capability value differs from the
     * default.
     */
    static final List<String> defaultSupportedEnrollmentModes = Arrays.asList("NOT_SUPPORTED");

    static final Map<String, Map<String, List<String>>> theSupportedEnrollmentModes = new HashMap<>();
    static {
        final List<String> ecimSupportedEnrollmentModes = new ArrayList<>();
        ecimSupportedEnrollmentModes.add("CMPv2_VC");
        ecimSupportedEnrollmentModes.add("OFFLINE_PKCS12");
        ecimSupportedEnrollmentModes.add("OFFLINE_CSR");
        final Map<String, List<String>> ecimSupportedEnrollmentModesForModelVersions = new HashMap<>();
        ecimSupportedEnrollmentModesForModelVersions.put("1.0.0", ecimSupportedEnrollmentModes);
        ecimSupportedEnrollmentModesForModelVersions.put("15.1.0", ecimSupportedEnrollmentModes);
        ecimSupportedEnrollmentModesForModelVersions.put("16.0.0", ecimSupportedEnrollmentModes);
        ecimSupportedEnrollmentModesForModelVersions.put("16.1.0", ecimSupportedEnrollmentModes);
        ecimSupportedEnrollmentModesForModelVersions.put("17.0.0", ecimSupportedEnrollmentModes);
        ecimSupportedEnrollmentModesForModelVersions.put("17.1.0", ecimSupportedEnrollmentModes);

        final List<String> cppSupportedEnrollmentModes = new ArrayList<>();
        cppSupportedEnrollmentModes.add("SCEP");
        cppSupportedEnrollmentModes.add("CMPv2_VC");
        cppSupportedEnrollmentModes.add("CMPv2_INITIAL");
        final Map<String, List<String>> cppSupportedEnrollmentModesForModelVersions = new HashMap<>();
        cppSupportedEnrollmentModesForModelVersions.put("1.0.0", cppSupportedEnrollmentModes);
        cppSupportedEnrollmentModesForModelVersions.put("14.0.0", cppSupportedEnrollmentModes);
        cppSupportedEnrollmentModesForModelVersions.put("14.1.0", cppSupportedEnrollmentModes);
        cppSupportedEnrollmentModesForModelVersions.put("15.1.0", cppSupportedEnrollmentModes);

        final List<String> default13brbsSupportedEnrollmentModes = new ArrayList<>();
        default13brbsSupportedEnrollmentModes.add("SCEP");
        final Map<String, List<String>> rbsSupportedEnrollmentModesForModelVersions = new HashMap<>();
        rbsSupportedEnrollmentModesForModelVersions.put("1.0.0", cppSupportedEnrollmentModes);
        rbsSupportedEnrollmentModesForModelVersions.put("13.1.0", default13brbsSupportedEnrollmentModes);
        rbsSupportedEnrollmentModesForModelVersions.put("15.1.0", cppSupportedEnrollmentModes);

        final List<String> virtualNeSupportedEnrollmentModes = new ArrayList<>();
        virtualNeSupportedEnrollmentModes.add("CMPv2_INITIAL");

        final Map<String, List<String>> virtualNeSupportedEnrollmentModesForModelVersions = new HashMap<>();
        virtualNeSupportedEnrollmentModesForModelVersions.put("1.0.0", virtualNeSupportedEnrollmentModes);

        final List<String> axeSupportedEnrollmentModes = new ArrayList<>();
        axeSupportedEnrollmentModes.add("CMPv2_INITIAL");

        final Map<String, List<String>> axeNeSupportedEnrollmentModesForModelVersions = new HashMap<>();
        axeNeSupportedEnrollmentModesForModelVersions.put("1.0.0", axeSupportedEnrollmentModes);

        final List<String> imsSupportedEnrollmentModes = new ArrayList<>();
        imsSupportedEnrollmentModes.add("CMPv2_INITIAL");

        final Map<String, List<String>> imsNeSupportedEnrollmentModesForModelVersions = new HashMap<>();
        imsNeSupportedEnrollmentModesForModelVersions.put("1.0.0", imsSupportedEnrollmentModes);

        theSupportedEnrollmentModes.put(ERBS, cppSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(RNC, cppSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(RBS, rbsSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(MGW, cppSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(CSCF, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(MTAS, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(MSRBS_V1, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(SAPC, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(RadioNode, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(RadioTNode, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(Router6675, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(Router6x71, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(Router6672, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(Router6274, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(vCSCF, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(vMTAS, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(vPP, virtualNeSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(BSC, axeNeSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(HLR_FE, axeNeSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(vHLR_FE, axeNeSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(HLR_FE_BSP, axeNeSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(HLR_FE_IS, axeNeSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(vEME, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(vWCG, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(HSS_FE, imsNeSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(VHSS_FE, imsNeSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(vIPWorks, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(vUPG, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(BSP, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(vBGF, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(vMRF, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(VTFRadioNode, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(RVNFM, virtualNeSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(vSD, virtualNeSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(vRM, virtualNeSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(vRC, virtualNeSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(MSC, axeNeSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(HLR, axeNeSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(RnNode, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(FIVEGRadioNode, ecimSupportedEnrollmentModesForModelVersions);
        theSupportedEnrollmentModes.put(vRSM, virtualNeSupportedEnrollmentModesForModelVersions);
    }

    /**
     * Mock the 'defaultEnrollmentMode' capability.
     *
     * This capability models the default enrollment mode for a target type.
     *
     * A default value is provided: add an element in the node map for each specific target type whose specific capability value differs from the
     * default.
     */
    static final String defaultDefaultEnrollmentMode = "NOT_SUPPORTED";

    static final Map<String, Map<String, String>> theDefaultEnrollmentMode = new HashMap<>();
    static {
        final Map<String, String> cppDefaultEnrollmentMode = new HashMap<>();
        cppDefaultEnrollmentMode.put("1.0.0", "CMPv2_VC");
        cppDefaultEnrollmentMode.put("14.0.0", "CMPv2_VC");
        cppDefaultEnrollmentMode.put("14.1.0", "CMPv2_VC");
        cppDefaultEnrollmentMode.put("15.1.0", "CMPv2_VC");

        final Map<String, String> rbsDefaultEnrollmentMode = new HashMap<>();
        rbsDefaultEnrollmentMode.put("1.0.0", "CMPv2_VC");
        rbsDefaultEnrollmentMode.put("15.1.0", "CMPv2_VC");
        rbsDefaultEnrollmentMode.put("13.1.0", "SCEP");

        final Map<String, String> ecimDefaultEnrollmentMode = new HashMap<>();
        ecimDefaultEnrollmentMode.put("1.0.0", "CMPv2_VC");
        ecimDefaultEnrollmentMode.put("15.1.0", "CMPv2_VC");
        ecimDefaultEnrollmentMode.put("16.0.0", "CMPv2_VC");
        ecimDefaultEnrollmentMode.put("16.1.0", "CMPv2_VC");
        ecimDefaultEnrollmentMode.put("17.0.0", "CMPv2_VC");
        ecimDefaultEnrollmentMode.put("17.1.0", "CMPv2_VC");

        final Map<String, String> virtualNeDefaultEnrollmentMode = new HashMap<>();
        virtualNeDefaultEnrollmentMode.put("1.0.0", "CMPv2_INITIAL");

        final Map<String, String> axeDefaultEnrollmentMode = new HashMap<>();
        axeDefaultEnrollmentMode.put("1.0.0", "CMPv2_INITIAL");

        final Map<String, String> imsDefaultEnrollmentMode = new HashMap<>();
        imsDefaultEnrollmentMode.put("1.0.0", "CMPv2_INITIAL");

        theDefaultEnrollmentMode.put(ERBS, cppDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(RNC, cppDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(RBS, rbsDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(MGW, cppDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(CSCF, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(MTAS, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(MSRBS_V1, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(SAPC, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(RadioNode, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(RadioTNode, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(Router6672, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(Router6675, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(Router6x71, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(Router6274, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(vCSCF, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(vMTAS, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(vPP, virtualNeDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(BSC, axeDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(HLR_FE, axeDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(vHLR_FE, axeDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(HLR_FE_BSP, axeDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(HLR_FE_IS, axeDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(vEME, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(vWCG, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(HSS_FE, imsDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(VHSS_FE, imsDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(vIPWorks, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(vUPG, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(BSP, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(vBGF, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(vMRF, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(VTFRadioNode, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(RVNFM, virtualNeDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(vSD, virtualNeDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(RnNode, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(vRM, virtualNeDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(vRC, virtualNeDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(MSC, axeDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(HLR, axeDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(FIVEGRadioNode, ecimDefaultEnrollmentMode);
        theDefaultEnrollmentMode.put(vRSM, virtualNeDefaultEnrollmentMode);
    }

    /**
     * Mock the 'defaultKeyAlgorithm' capability.
     *
     * This capability models the default key algorithm for a target type.
     *
     * A default value is provided: add an element in the node map for each specific target type whose specific capability value differs from the
     * default.
     */
    static final String defaultDefaultKeyAlgorithm = null;

    static final Map<String, String> theDefaultKeyAlgorithm = new HashMap<>();
    static {
        final String defaultKeyAlgorithm = "RSA_2048";
        final String sgsnMmeKeyAlgorithm = "RSA_1024";

        theDefaultKeyAlgorithm.put(ERBS, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(RNC, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(RBS, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(MGW, defaultKeyAlgorithm);

        theDefaultKeyAlgorithm.put(Router6672, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(Router6675, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(Router6x71, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(Router6274, defaultKeyAlgorithm);

        theDefaultKeyAlgorithm.put(ECM, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(SGSN_MME, sgsnMmeKeyAlgorithm);
        theDefaultKeyAlgorithm.put(SBG, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(CSCF, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(MTAS, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(MSRBS_V1, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(SAPC, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(RadioNode, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(RadioTNode, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(EPG, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(VEPG, defaultKeyAlgorithm);

        theDefaultKeyAlgorithm.put(vSBG, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(vCSCF, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(vMTAS, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(BSC, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(HLR_FE, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(vHLR_FE, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(HLR_FE_BSP, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(HLR_FE_IS, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(MSC, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(HLR, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(vPP, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(vEME, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(vWCG, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(HSS_FE, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(VHSS_FE, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(vIPWorks, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(vUPG, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(BSP, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(vBGF, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(vMRF, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(VTFRadioNode, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(vSD, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(RVNFM, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(vRSM, defaultKeyAlgorithm);

        theDefaultKeyAlgorithm.put(RnNode, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(vRM, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(vRC, defaultKeyAlgorithm);
        theDefaultKeyAlgorithm.put(FIVEGRadioNode, defaultKeyAlgorithm);
    }

    /**
     * Mock the 'defaultFingerprintAlgorithm' capability.
     *
     * This capability models the default fingerprint algorithm for a target type.
     *
     * A default value is provided: add an element in the node map for each specific target type whose specific capability value differs from the
     * default.
     */
    static final String defaultDefaultFingerprintAlgorithm = null;

    static final Map<String, Map<String, String>> theDefaultFingerprintAlgorithm = new HashMap<>();
    static {
        final Map<String, String> cppDefaultFingerprintAlgorithm = new HashMap<>();
        cppDefaultFingerprintAlgorithm.put("1.0.0", "SHA256");
        cppDefaultFingerprintAlgorithm.put("14.0.0", "SHA256");
        cppDefaultFingerprintAlgorithm.put("14.1.0", "SHA256");
        cppDefaultFingerprintAlgorithm.put("15.1.0", "SHA256");

        final Map<String, String> ecimDefaultFingerprintAlgorithm = new HashMap<>();
        ecimDefaultFingerprintAlgorithm.put("1.0.0", "SHA1");
        ecimDefaultFingerprintAlgorithm.put("15.1.0", "SHA1");
        ecimDefaultFingerprintAlgorithm.put("16.0.0", "SHA1");
        ecimDefaultFingerprintAlgorithm.put("16.1.0", "SHA1");
        ecimDefaultFingerprintAlgorithm.put("17.0.0", "SHA1");
        ecimDefaultFingerprintAlgorithm.put("17.1.0", "SHA1");

        final Map<String, String> er6000DefaultFingerprintAlgorithm = new HashMap<>();
        er6000DefaultFingerprintAlgorithm.put("1.0.0", "SHA256");
        er6000DefaultFingerprintAlgorithm.put("17.0.0", "SHA256");
        er6000DefaultFingerprintAlgorithm.put("17.1.0", "SHA256");

        final Map<String, String> rbsDefaultFingerprintAlgorithm = new HashMap<>();
        rbsDefaultFingerprintAlgorithm.put("1.0.0", "SHA256");
        rbsDefaultFingerprintAlgorithm.put("13.1.0", "SHA1");
        rbsDefaultFingerprintAlgorithm.put("15.1.0", "SHA256");

        theDefaultFingerprintAlgorithm.put(ERBS, cppDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(RNC, cppDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(RBS, rbsDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(MGW, cppDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(CSCF, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(MTAS, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(MSRBS_V1, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(SAPC, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(RadioNode, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(RadioTNode, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(Router6672, er6000DefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(Router6675, er6000DefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(Router6x71, er6000DefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(Router6274, er6000DefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(vCSCF, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(vMTAS, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(vPP, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(BSC, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(HLR_FE, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(vHLR_FE, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(HLR_FE_BSP, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(HLR_FE_IS, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(vEME, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(vWCG, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(HSS_FE, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(VHSS_FE, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(vIPWorks, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(vUPG, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(BSP, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(vBGF, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(vMRF, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(VTFRadioNode, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(RVNFM, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(vSD, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(vRSM, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(RnNode, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(vRM, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(vRC, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(MSC, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(HLR, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(FIVEGRadioNode, ecimDefaultFingerprintAlgorithm);
        theDefaultFingerprintAlgorithm.put(vSBG, ecimDefaultFingerprintAlgorithm);
    }

    /**
     * Mock the 'isSynchronousEnrollmentSupported' capability.
     *
     * This capability models if the synchronous enrollment is supported for a target type.
     *
     * A default value is provided: add an element in the node map for each specific node type supporting the certificate management and whose
     * specific capability value differs from the default.
     */
    static final Boolean defaultIsSynchronousEnrollmentSupported = true;

    static final Map<String, Boolean> theIsSynchronousEnrollmentSupported = new HashMap<>();
    static {
        final boolean cppIsSynchronousEnrollmentSupported = false;
        final boolean msrbsv1IsSynchronousEnrollmentSupported = false;

        theIsSynchronousEnrollmentSupported.put(ERBS, cppIsSynchronousEnrollmentSupported);
        theIsSynchronousEnrollmentSupported.put(RNC, cppIsSynchronousEnrollmentSupported);
        theIsSynchronousEnrollmentSupported.put(RBS, cppIsSynchronousEnrollmentSupported);
        theIsSynchronousEnrollmentSupported.put(MGW, cppIsSynchronousEnrollmentSupported);
        theIsSynchronousEnrollmentSupported.put(MSRBS_V1, msrbsv1IsSynchronousEnrollmentSupported);
    }

    /**
     * Mock the 'isConfiguredSubjectNameUsedForEnrollment' capability.
     *
     * This capability models if the subject name, as configured in the node MO modeling the node certificate, is used by the node during its
     * enrollment procedure or if an "hard-coded" subject name is used instead.
     *
     * A default value is provided: add an element in the node map for each specific node type supporting the certificate management and whose
     * specific capability value differs from the default.
     */
    static final Boolean defaultIsConfiguredSubjectNameUsedForEnrollment = true;

    static final Map<String, Boolean> theIsConfiguredSubjectNameUsedForEnrollment = new HashMap<>();
    static {
        theIsConfiguredSubjectNameUsedForEnrollment.put(MSRBS_V1, false);
    }

    /**
     * Mock the 'defaultInitialOtpCount' capability.
     *
     * This capability describes the initial default value for OTP count for target type.
     *
     * A default value is provided: add an element in the node map for each specific target type supporting certificate management and whose specific
     * capability value differs from the default.
     */
    static final String defaultDefaultInitialOtpCount = "5";

    static final Map<String, String> theDefaultInitialOtpCount = new HashMap<>();
    static {
        theDefaultInitialOtpCount.put(vRC, "2");
        theDefaultInitialOtpCount.put(vPP, "2");
        theDefaultInitialOtpCount.put(vRM, "2");
        theDefaultInitialOtpCount.put(vRSM, "2");
    }

    /**
     * Mock the 'isIkev2PolicyProfileSupported' capability.
     *
     * This capability describes if ECIM Ikev2PolicyProfile MO is supported.
     *
     * A default value is provided: add an element in the node map for each specific node type supporting the certificate management and whose
     * specific capability value differs from the default.
     */
    static final Boolean defaultIsIkev2PolicyProfileSupported = true;

    static final Map<String, Boolean> theIsIkev2PolicyProfileSupported = new HashMap<>();
    static {
        theIsIkev2PolicyProfileSupported.put(ERBS, false);
        theIsIkev2PolicyProfileSupported.put(RNC, false);
        theIsIkev2PolicyProfileSupported.put(RBS, false);
        theIsIkev2PolicyProfileSupported.put(MGW, false);

        theIsIkev2PolicyProfileSupported.put(MSRBS_V1, false);
        theIsIkev2PolicyProfileSupported.put(Router6672, false);
        theIsIkev2PolicyProfileSupported.put(Router6675, false);
        theIsIkev2PolicyProfileSupported.put(Router6x71, false);
        theIsIkev2PolicyProfileSupported.put(Router6274, false);
    }

    /**
     * Mock the 'isLdapCommonUserSupported' capability.
     *
     * This capability describes if ldapApplicationUser is supported, as configured in the NetworkElementSecurity MO modeling the node credentials.
     *
     * A default value is provided: add an element in the node map for each specific target type whose specific capability value differs from the
     * default.
     */
    static final Boolean defaultIsLdapCommonUserSupported = false;

    static final Map<String, Map<String, Boolean>> theIsLdapCommonUserSupported = new HashMap<>();
    static {
        final Map<String, Boolean> msrbsv1IsLdapCommonUserProfileSupported = new HashMap<>();
        msrbsv1IsLdapCommonUserProfileSupported.put("16.0.0", true);
        msrbsv1IsLdapCommonUserProfileSupported.put("1.0.0", true);
        final Map<String, Boolean> radioNodeIsLdapCommonUserProfileSupported = new HashMap<>();
        radioNodeIsLdapCommonUserProfileSupported.put("15.1.0", true);
        radioNodeIsLdapCommonUserProfileSupported.put("16.1.0", true);
        radioNodeIsLdapCommonUserProfileSupported.put("1.0.0", true);
        final Map<String, Boolean> radioTNodeIsLdapCommonUserProfileSupported = new HashMap<>();
        radioTNodeIsLdapCommonUserProfileSupported.put("16.1.0", true);
        radioTNodeIsLdapCommonUserProfileSupported.put("1.0.0", true);
        final Map<String, Boolean> er6672IsLdapCommonUserProfileSupported = new HashMap<>();
        er6672IsLdapCommonUserProfileSupported.put("17.0.0", false);
        er6672IsLdapCommonUserProfileSupported.put("17.1.0", false);
        er6672IsLdapCommonUserProfileSupported.put("1.0.0", true);
        final Map<String, Boolean> ecimIsLdapCommonUserProfileSupported = new HashMap<>();
        ecimIsLdapCommonUserProfileSupported.put("1.0.0", true);

        theIsLdapCommonUserSupported.put(MSRBS_V1, msrbsv1IsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(RadioNode, radioNodeIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(RadioTNode, radioTNodeIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(vUPG, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(BSP, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(vEME, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(vWCG, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(HSS_FE, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(VHSS_FE, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(vIPWorks, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(vBGF, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(vMRF, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(vSD, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(RVNFM, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(BSC, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(HLR_FE, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(vHLR_FE, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(HLR_FE_BSP, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(HLR_FE_IS, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(vPP, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(VTFRadioNode, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(vRSM, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(RnNode, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(vRM, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(vRC, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(FIVEGRadioNode, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(Router6672, er6672IsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(Router6675, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(Router6x71, ecimIsLdapCommonUserProfileSupported);
        theIsLdapCommonUserSupported.put(Router6274, ecimIsLdapCommonUserProfileSupported);
    }

    /**
     * Mock the 'enrollmentCAAuthorizationModes' capability.
     *
     * The capability is meaningful for COM/ECIM node types only.
     *
     * This capability describes which enrollment CA authorization modes are used during the enrollment of supported certificate types for a node
     * type.
     *
     * A default value is provided: add an element in the node map for each specific COM/ECIM node type supporting the SecM fragment and whose
     * specific capability value differs from the default.
     */
    static final Map<String, String> defaultEnrollmentCAAuthorizationModes = new HashMap<>();
    static {
        defaultEnrollmentCAAuthorizationModes.put("OAM", "ENROLLMENT_ROOT_CA_FINGERPRINT");
        defaultEnrollmentCAAuthorizationModes.put("IPSEC", "ENROLLMENT_ROOT_CA_FINGERPRINT");
    }

    static final Map<String, Map<String, String>> theEnrollmentCAAuthorizationModes = new HashMap<>();
    static {
        final Map<String, String> er6000EnrollmentCAAuthorizationModes = new HashMap<>();
        er6000EnrollmentCAAuthorizationModes.put("OAM", "ENROLLMENT_ROOT_CA_CERTIFICATE");
        er6000EnrollmentCAAuthorizationModes.put("IPSEC", "ENROLLMENT_ROOT_CA_CERTIFICATE");

        final Map<String, String> axeIsEnrollmentCAAuthorizationModes = new HashMap<>();
        axeIsEnrollmentCAAuthorizationModes.put("OAM", "ENROLLMENT_ROOT_CA_CERTIFICATE");

        final Map<String, String> imsIsEnrollmentCAAuthorizationModes = new HashMap<>();
        imsIsEnrollmentCAAuthorizationModes.put("OAM", "ENROLLMENT_ROOT_CA_CERTIFICATE");

        theEnrollmentCAAuthorizationModes.put(BSC, axeIsEnrollmentCAAuthorizationModes);
        theEnrollmentCAAuthorizationModes.put(HLR_FE, axeIsEnrollmentCAAuthorizationModes);
        theEnrollmentCAAuthorizationModes.put(vHLR_FE, axeIsEnrollmentCAAuthorizationModes);
        theEnrollmentCAAuthorizationModes.put(HLR_FE_BSP, axeIsEnrollmentCAAuthorizationModes);
        theEnrollmentCAAuthorizationModes.put(HLR_FE_IS, axeIsEnrollmentCAAuthorizationModes);
        theEnrollmentCAAuthorizationModes.put(MSC, axeIsEnrollmentCAAuthorizationModes);
        theEnrollmentCAAuthorizationModes.put(HLR, axeIsEnrollmentCAAuthorizationModes);
        theEnrollmentCAAuthorizationModes.put(HSS_FE, imsIsEnrollmentCAAuthorizationModes);
        theEnrollmentCAAuthorizationModes.put(VHSS_FE, imsIsEnrollmentCAAuthorizationModes);
        theEnrollmentCAAuthorizationModes.put(Router6672, er6000EnrollmentCAAuthorizationModes);
        theEnrollmentCAAuthorizationModes.put(Router6675, er6000EnrollmentCAAuthorizationModes);
        theEnrollmentCAAuthorizationModes.put(Router6x71, er6000EnrollmentCAAuthorizationModes);
        theEnrollmentCAAuthorizationModes.put(Router6274, er6000EnrollmentCAAuthorizationModes);
    }

    /**
     * Mock the 'isDeprecatedEnrollmentAuthorityUsed' capability.
     *
     * This capability models whether the deprecated enrollmentAuthority attribute of EnrollmentServer MO is used for a node type.
     *
     * A default value is provided: add an element in the node map for each specific ECIM node type supporting the certificate management and whose
     * specific capability value differs from the default.
     */
    static final Boolean defaultIsDeprecatedEnrollmentAuthorityUsed = false;

    static final Map<String, Boolean> theIsDeprecatedEnrollmentAuthorityUsed = new HashMap<>();
    static {
        theIsDeprecatedEnrollmentAuthorityUsed.put(MSRBS_V1, true);
    }

    /**
     * Mock the 'isDeprecatedAuthorityTypeSupported' capability.
     *
     * This capability models whether the deprecated authorityType attribute of EnrollmentAuthority MO is supported by a node type. Some node types
     * (AXE nodes) completely hide such attribute and neither allow read access to it.
     *
     * This capability is meaningful only for node types implementing ECIM model and supporting SecM fragment.
     *
     * A default value is provided: add an element in the node map for each specific ECIM node type supporting SecM fragment and whose specific
     * capability value differs from the default.
     */
    static final Boolean defaultIsDeprecatedAuthorityTypeSupported = true;

    static final Map<String, Boolean> theIsDeprecatedAuthorityTypeSupported = new HashMap<>();
    static {
        // CPP
        theIsDeprecatedAuthorityTypeSupported.put(ERBS, false);
        theIsDeprecatedAuthorityTypeSupported.put(RNC, false);
        theIsDeprecatedAuthorityTypeSupported.put(RBS, false);
        theIsDeprecatedAuthorityTypeSupported.put(MGW, false);

        // AXE
        theIsDeprecatedAuthorityTypeSupported.put(BSC, false);
        theIsDeprecatedAuthorityTypeSupported.put(HLR_FE, false);
        theIsDeprecatedAuthorityTypeSupported.put(vHLR_FE, false);
        theIsDeprecatedAuthorityTypeSupported.put(HLR_FE_BSP, false);
        theIsDeprecatedAuthorityTypeSupported.put(HLR_FE_IS, false);
    }

    /**
     * Mock the 'onDemandCrlDownloadWorkflow' capability.
     *
     * This capability models the ondemandcrldownload workflow name for a target type.
     *
     * A default value is provided: add an element in the node map for each specific node type supporting certificate management and whose specific
     * capability value differs from the default.
     */
    static final String defaultOnDemandCrlDownloadWorkflow = "COMOnDemandCrlDownload";

    static final Map<String, String> theOnDemandCrlDownloadWorkflow = new HashMap<>();
    static {
        final String cppOnDemandCrlDownloadWorkflow = "CPPOnDemandCrlDownload";

        theOnDemandCrlDownloadWorkflow.put(ERBS, cppOnDemandCrlDownloadWorkflow);
        theOnDemandCrlDownloadWorkflow.put(RNC, cppOnDemandCrlDownloadWorkflow);
        theOnDemandCrlDownloadWorkflow.put(RBS, cppOnDemandCrlDownloadWorkflow);
        theOnDemandCrlDownloadWorkflow.put(MGW, cppOnDemandCrlDownloadWorkflow);
    }

    /**
     * Mock the 'supportedCipherProtocolTypes' capability.
     *
     * This capability models the supported protocol types specified in cipher commands for a node type.
     *
     * A default value is provided: add an element in the node map for each specific node type supporting the cipher commands and whose specific
     * capability value differs from the default.
     */

    static final List<String> defaultSupportedCipherProtocolTypes = Arrays.asList("SSH_SFTP", "SSL_HTTPS_TLS");

    static final Map<String, List<String>> theSupportedCipherProtocolTypes = new HashMap<>();
    static {
        final List<String> er6672SupportedCipherProtocolTypes = Arrays.asList("SSL_HTTPS_TLS");
        theSupportedCipherProtocolTypes.put(Router6672, er6672SupportedCipherProtocolTypes);

        final List<String> er6675SupportedCipherProtocolTypes = Arrays.asList("SSL_HTTPS_TLS");
        theSupportedCipherProtocolTypes.put(Router6675, er6675SupportedCipherProtocolTypes);

        final List<String> er6x71SupportedCipherProtocolTypes = Arrays.asList("SSL_HTTPS_TLS");
        theSupportedCipherProtocolTypes.put(Router6x71, er6x71SupportedCipherProtocolTypes);

        final List<String> er6274SupportedCipherProtocolTypes = Arrays.asList("SSL_HTTPS_TLS");
        theSupportedCipherProtocolTypes.put(Router6274, er6274SupportedCipherProtocolTypes);

    }

    /**
     * Mock the 'cipherMoAttributes' capability.
     *
     * This capability models the names of the attributes of the MOs involved in cipher modernization for the valid protocol types for a node type.
     *
     * A default value is provided: add an element in the node map for each specific node type supporting the cipher command and whose specific
     * capability value differs from the default.
     */
    static final Map<String, Map<String, String>> defaultCipherMoAttributes = new HashMap<>();

    static {
        final Map<String, String> defaultSshCipherMoAttributes = new HashMap<>();
        // selectedMacs ,selectedKeyExchanges ,selectedCiphers ,supportedMacs ,supportedKeyExchanges ,supportedCiphers
        defaultSshCipherMoAttributes.put("selected_mac", "selectedMacs");
        defaultSshCipherMoAttributes.put("selected_key_exchange", "selectedKeyExchanges");
        defaultSshCipherMoAttributes.put("selected_cipher", "selectedCiphers");
        defaultSshCipherMoAttributes.put("supported_mac", "supportedMacs");
        defaultSshCipherMoAttributes.put("supported_key_exchange", "supportedKeyExchanges");
        defaultSshCipherMoAttributes.put("supported_cipher", "supportedCiphers");
        final Map<String, String> defaultTlsCipherMoAttributes = new HashMap<>();
        // cipherFilter ,enabledCiphers ,supportedCiphers
        defaultTlsCipherMoAttributes.put("cipher_filter", "cipherFilter");
        defaultTlsCipherMoAttributes.put("enabled_cipher", "enabledCiphers");
        defaultTlsCipherMoAttributes.put("supported_cipher", "supportedCiphers");

        defaultCipherMoAttributes.put("SSH_SFTP", defaultSshCipherMoAttributes);
        defaultCipherMoAttributes.put("SSL_HTTPS_TLS", defaultTlsCipherMoAttributes);
    }

    static final Map<String, Map<String, Map<String, String>>> theCipherMoAttributes = new HashMap<>();

    static {
        final Map<String, String> cppSshCipherMoAttributes = new HashMap<>();
        // selectedMac ,selectedKeyExchange ,selectedCipher ,supportedMac ,supportedKeyExchange,supportedCipher
        cppSshCipherMoAttributes.put("selected_mac", "selectedMac");
        cppSshCipherMoAttributes.put("selected_key_exchange", "selectedKeyExchange");
        cppSshCipherMoAttributes.put("selected_cipher", "selectedCipher");
        cppSshCipherMoAttributes.put("supported_mac", "supportedMac");
        cppSshCipherMoAttributes.put("supported_key_exchange", "supportedKeyExchange");
        cppSshCipherMoAttributes.put("supported_cipher", "supportedCipher");
        final Map<String, String> cppTlsCipherMoAttributes = new HashMap<>();
        // cipherFilter ,enabledCipher ,supportedCipher
        cppTlsCipherMoAttributes.put("cipher_filter", "cipherFilter");
        cppTlsCipherMoAttributes.put("enabled_cipher", "enabledCipher");
        cppTlsCipherMoAttributes.put("supported_cipher", "supportedCipher");
        final Map<String, String> er6000TlsCipherMoAttributes = new HashMap<>();
        // cipherFilter ,enabledCiphers ,supportedCiphers
        er6000TlsCipherMoAttributes.put("cipher_filter", "cipherFilter");
        er6000TlsCipherMoAttributes.put("enabled_cipher", "enabledCiphers");
        er6000TlsCipherMoAttributes.put("supported_cipher", "supportedCiphers");

        final Map<String, Map<String, String>> cppCipherMoAttributes = new HashMap<>();
        cppCipherMoAttributes.put("SSH_SFTP", cppSshCipherMoAttributes);
        cppCipherMoAttributes.put("SSL_HTTPS_TLS", cppTlsCipherMoAttributes);
        final Map<String, Map<String, String>> er6000cipherMoAttributes = new HashMap<>();
        er6000cipherMoAttributes.put("SSL_HTTPS_TLS", er6000TlsCipherMoAttributes);

        theCipherMoAttributes.put(ERBS, cppCipherMoAttributes);
        theCipherMoAttributes.put(RBS, cppCipherMoAttributes);
        theCipherMoAttributes.put(MGW, cppCipherMoAttributes);
        theCipherMoAttributes.put(RNC, cppCipherMoAttributes);
        theCipherMoAttributes.put(Router6672, er6000cipherMoAttributes);
        theCipherMoAttributes.put(Router6675, er6000cipherMoAttributes);
        theCipherMoAttributes.put(Router6x71, er6000cipherMoAttributes);
        theCipherMoAttributes.put(Router6274, er6000cipherMoAttributes);
    }

    /**
     * Mock the 'isEmptyCiphersSupported' capability.
     *
     * This capability models whether the empty ciphers are supported for a node type for Ciphers Configuration.
     *
     * A default value is provided: add a specific NE type in the static map only if the capability value for that specific NE type differs from the
     * default.
     */
    static final Boolean defaultIsEmptyCiphersSupported = false;

    static final Map<String, Boolean> theIsEmptyCiphersSupported = new HashMap<>();
    static {
        theIsEmptyCiphersSupported.put(ERBS, true);
        theIsEmptyCiphersSupported.put(RBS, true);
        theIsEmptyCiphersSupported.put(MGW, true);
        theIsEmptyCiphersSupported.put(RNC, true);
    }

    /**
     * Mock the 'comEcimDefaultNodeCredentialId' capability.
     *
     * This capability models the default id of a NodeCredential MO for the supported certificate types for a node type. This capability is meaningful
     * only for COM/ECIM nodes supporting the SecM fragment.
     *
     * A default value is provided: add an element in the node map for each specific COM/ECIM node type supporting the SecM fragment and whose
     * specific capability value differs from the default.
     */
    static final Map<String, String> defaultComEcimDefaultNodeCredentialId = new HashMap<>();

    static {
        defaultComEcimDefaultNodeCredentialId.put("OAM", "oamNodeCredential");
        defaultComEcimDefaultNodeCredentialId.put("IPSEC", "ipsecNodeCredential");
    }

    static final Map<String, Map<String, String>> theComEcimDefaultNodeCredentialId = new HashMap<>();

    static {
        final Map<String, String> cppDefaultNodeCredentialId = new HashMap<>();

        final Map<String, String> msrbsV1DefaultNodeCredentialId = new HashMap<>();
        msrbsV1DefaultNodeCredentialId.put("OAM", "1");
        msrbsV1DefaultNodeCredentialId.put("IPSEC", "2");

        final Map<String, String> oamOnlyDefaultNodeCredentialId = new HashMap<>();
        oamOnlyDefaultNodeCredentialId.put("OAM", "oamNodeCredential");

        theComEcimDefaultNodeCredentialId.put(MSRBS_V1, msrbsV1DefaultNodeCredentialId);

        // OAM only
        theComEcimDefaultNodeCredentialId.put(BSC, oamOnlyDefaultNodeCredentialId);
        theComEcimDefaultNodeCredentialId.put(HLR_FE, oamOnlyDefaultNodeCredentialId);
        theComEcimDefaultNodeCredentialId.put(vHLR_FE, oamOnlyDefaultNodeCredentialId);
        theComEcimDefaultNodeCredentialId.put(HLR_FE_BSP, oamOnlyDefaultNodeCredentialId);
        theComEcimDefaultNodeCredentialId.put(HLR_FE_IS, oamOnlyDefaultNodeCredentialId);
        theComEcimDefaultNodeCredentialId.put(MSC, oamOnlyDefaultNodeCredentialId);
        theComEcimDefaultNodeCredentialId.put(HLR, oamOnlyDefaultNodeCredentialId);
        theComEcimDefaultNodeCredentialId.put(HSS_FE, oamOnlyDefaultNodeCredentialId);
        theComEcimDefaultNodeCredentialId.put(VHSS_FE, oamOnlyDefaultNodeCredentialId);

        // CPP
        theComEcimDefaultNodeCredentialId.put(ERBS, cppDefaultNodeCredentialId);
        theComEcimDefaultNodeCredentialId.put(RNC, cppDefaultNodeCredentialId);
        theComEcimDefaultNodeCredentialId.put(RBS, cppDefaultNodeCredentialId);
        theComEcimDefaultNodeCredentialId.put(MGW, cppDefaultNodeCredentialId);
    }

    /**
     * Mock the 'comEcimDefaultTrustCategoryId' capability.
     *
     * This capability models the default id of a TrustCategory MO for the supported certificate types for a node type. This capability is meaningful
     * only for COM/ECIM nodes supporting the SecM fragment.
     *
     * A default value is provided: add an element in the node map for each specific COM/ECIM node type supporting the SecM fragment and whose
     * specific capability value differs from the default.
     */
    static final Map<String, String> defaultComEcimDefaultTrustCategoryId = new HashMap<>();

    static {
        defaultComEcimDefaultTrustCategoryId.put("OAM", "oamTrustCategory");
        defaultComEcimDefaultTrustCategoryId.put("IPSEC", "ipsecTrustCategory");
    }

    static final Map<String, Map<String, String>> theComEcimDefaultTrustCategoryId = new HashMap<>();

    static {
        final Map<String, String> cppDefaultTrustCategoryId = new HashMap<>();

        final Map<String, String> msrbsV1DefaultTrustCategoryId = new HashMap<>();
        msrbsV1DefaultTrustCategoryId.put("OAM", "1");
        msrbsV1DefaultTrustCategoryId.put("IPSEC", "2");

        final Map<String, String> oamOnlyDefaultTrustCategoryId = new HashMap<>();
        oamOnlyDefaultTrustCategoryId.put("OAM", "oamTrustCategory");

        theComEcimDefaultTrustCategoryId.put(MSRBS_V1, msrbsV1DefaultTrustCategoryId);

        // OAM only
        theComEcimDefaultTrustCategoryId.put(BSC, oamOnlyDefaultTrustCategoryId);
        theComEcimDefaultTrustCategoryId.put(HLR_FE, oamOnlyDefaultTrustCategoryId);
        theComEcimDefaultTrustCategoryId.put(vHLR_FE, oamOnlyDefaultTrustCategoryId);
        theComEcimDefaultTrustCategoryId.put(HLR_FE_BSP, oamOnlyDefaultTrustCategoryId);
        theComEcimDefaultTrustCategoryId.put(HLR_FE_IS, oamOnlyDefaultTrustCategoryId);
        theComEcimDefaultTrustCategoryId.put(MSC, oamOnlyDefaultTrustCategoryId);
        theComEcimDefaultTrustCategoryId.put(HLR, oamOnlyDefaultTrustCategoryId);
        theComEcimDefaultTrustCategoryId.put(HSS_FE, oamOnlyDefaultTrustCategoryId);
        theComEcimDefaultTrustCategoryId.put(VHSS_FE, oamOnlyDefaultTrustCategoryId);

        // CPP
        theComEcimDefaultTrustCategoryId.put(ERBS, cppDefaultTrustCategoryId);
        theComEcimDefaultTrustCategoryId.put(RNC, cppDefaultTrustCategoryId);
        theComEcimDefaultTrustCategoryId.put(RBS, cppDefaultTrustCategoryId);
        theComEcimDefaultTrustCategoryId.put(MGW, cppDefaultTrustCategoryId);
    }

    /**
     * Mock the 'configureLdapWorkflow' capability.
     *
     * This capability models the configure LdapWorkflow workflow name for a target type.
     *
     * A default value is provided: add an element in the node map for each specific node type supporting certificate management and whose specific
     * capability value differs from the default.
     */
    static final String DEFAULT_CONFIGURE_LDAP_WORKFLOW = "COMConfigureLdap";

    static final Map<String, String> theConfigureLdapWorkflow = new HashMap<>();
    static {
        final String vduConfigureLdapWorkflow = "CbpOiConfigureLdap";

        theConfigureLdapWorkflow.put(VDU, vduConfigureLdapWorkflow);
    }

    /**
     * Mock the 'configureLdapMoName' capability.
     *
     * This capability models the configure Ldap MO name for a target type.
     *
     * A default value is provided: add an element in the node map for each specific node type supporting certificate management and whose specific
     * capability value differs from the default.
     */
    static final String DEFAULT_CONFIGURE_LDAP_MO_NAME = "Ldap";

    static final Map<String, String> theConfigureLdapMoName = new HashMap<>();
    static {
        final String vduConfigureLdapMoName = "ldap";

        theConfigureLdapMoName.put(VDU, vduConfigureLdapMoName);
    }

    /**
     * Mock the 'defaultEnrollmentCaTrustCategoryId' capability.
     *
     * Defaut value is null (non entry into the map)
     */
    static final Map<String, Map<String, String>> theDefaulConfiguretEnrollmentCaTrustCategoryId = new HashMap<>();

    static {
        final Map<String, String> vDuDefaulConfiguretEnrollmentCaTrustCategoryId = new HashMap<>();
        vDuDefaulConfiguretEnrollmentCaTrustCategoryId.put("OAM", "oamCmpCaTrustCategory");

        theDefaulConfiguretEnrollmentCaTrustCategoryId.put(VDU, vDuDefaulConfiguretEnrollmentCaTrustCategoryId);

    }

    /**
     * Mock the 'defaultOtpValidityPeriodInMinutes' capability.
     *
     * This capability models the configure default Otp validity period in minutes for a target type.
     *
     * A default value is provided: add an element in the node map for each specific node type supporting certificate management and whose specific
     * capability value differs from the default.
     */
    static final String DEFAULT_CONFIGURE_OTP_VALIDITY_PERIOD_IN_MINUTES = "30";

    static final Map<String, String> theConfigureDefaultOtpValidityPeriodInMinutes = new HashMap<>();
    static {
        final String vduConfigureDefaultOtpValidityPeriodInMinutes = "60";

        theConfigureDefaultOtpValidityPeriodInMinutes.put(VDU, vduConfigureDefaultOtpValidityPeriodInMinutes);
    }

    @Inject
    private Logger logger;

    @Override
    public Collection<String> getTargetTypes(final String targetCategory) throws NscsCapabilityModelException {
        return theSupportedTargetTypes;
    }

    @Override
    public Object getCapabilityValue(final String targetCategory, final String targetType, final String targetModelIdentity, final String function,
            final String capabilityName) {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "] function [" + function + "] capability [" + capabilityName + "]";
        logger.debug("[MOCK] get CapabilityValue: starts for {}", inputParams);

        Object value = null;
        final String capabilitySupportVersion = getCapabilitySupportVersion(targetCategory, targetType, targetModelIdentity);
        logger.debug("[MOCK] get CapabilityValue: capabilitySupportVersion [{}] for {}", capabilitySupportVersion, inputParams);

        switch (function) {
        case NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL: {
            switch (capabilityName) {
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_UNSUPPORTED_COMMANDS: {
                if (theUnsupportedCommands.get(targetType) != null) {
                    value = theUnsupportedCommands.get(targetType).get(capabilitySupportVersion);
                } else {
                    value = defaultUnsupportedCommands;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_ENROLLMENT_MODE: {
                if (theDefaultEnrollmentMode.get(targetType) != null) {
                    value = theDefaultEnrollmentMode.get(targetType).get(capabilitySupportVersion);
                } else {
                    value = defaultDefaultEnrollmentMode;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_SUPPORTED_ENROLLMENT_MODES: {
                if (theSupportedEnrollmentModes.get(targetType) != null) {
                    value = theSupportedEnrollmentModes.get(targetType).get(capabilitySupportVersion);
                } else {
                    value = defaultSupportedEnrollmentModes;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_FINGERPRINT_ALGORITHM: {
                if (theDefaultFingerprintAlgorithm.get(targetType) != null) {
                    value = theDefaultFingerprintAlgorithm.get(targetType).get(capabilitySupportVersion);
                } else {
                    value = defaultDefaultFingerprintAlgorithm;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_LDAP_COMMON_USER_SUPPORTED: {
                if (theIsLdapCommonUserSupported.get(targetType) != null) {
                    value = theIsLdapCommonUserSupported.get(targetType).get(capabilitySupportVersion);
                } else {
                    value = defaultIsLdapCommonUserSupported;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_CRL_CHECK_SUPPORTED_CERT_TYPES: {
                if (theCrlCheckSupportedCertTypes.get(targetType) != null) {
                    value = theCrlCheckSupportedCertTypes.get(targetType).get(capabilitySupportVersion);
                } else {
                    value = defaultCrlCheckSupportedCertTypes;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_CREDS_PARAMS: {
                value = theCredentialsParams.get(targetType);
                if (value == null) {
                    value = defaultCredentialsParams;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHER_MO_ATTRIBUTES: {
                value = theCipherMoAttributes.get(targetType);
                if (value == null) {
                    value = defaultCipherMoAttributes;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_SUPPORTED_CIPHER_PROTOCOL_TYPES: {
                value = theSupportedCipherProtocolTypes.get(targetType);
                if (value == null) {
                    value = defaultSupportedCipherProtocolTypes;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_EMPTY_CIPHER_SUPPORTED: {
                value = theIsEmptyCiphersSupported.get(targetType);
                if (value == null) {
                    value = defaultIsEmptyCiphersSupported;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_SUPPORTED_CERT_TYPES: {
                value = theSupportedCertTypes.get(targetType);
                if (value == null) {
                    value = defaultSupportedCertTypes;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_ENTITY_PROFILES: {
                value = theDefaultEntityProfiles.get(targetType);
                if (value == null) {
                    value = defaultDefaultEntityProfiles;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_ISSUE_CERT_WORKFLOWS: {
                value = theIssueCertWorkflows.get(targetType);
                if (value == null) {
                    value = defaultIssueCertWorkflows;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CRL_CHECK_WORKFLOWS: {
                value = theCrlCheckWorkflows.get(targetType);
                if (value == null) {
                    value = defaultCrlCheckWorkflows;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_TRUST_DISTR_WORKFLOWS: {
                value = theTrustDistrWorkflows.get(targetType);
                if (value == null) {
                    value = defaultTrustDistrWorkflows;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_TRUST_REMOVE_WORKFLOWS: {
                value = theTrustRemoveWorkflows.get(targetType);
                if (value == null) {
                    value = defaultTrustRemoveWorkflows;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_MOM_TYPE: {
                value = theMomType.get(targetType);
                if (value == null) {
                    value = defaultMomType;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_CERT_MANAGEMENT_SUPPORTED: {
                value = theIsCertificateManagementSupported.get(targetType);
                if (value == null) {
                    value = defaultIsCertificateManagementSupported;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_SUPPORTED_SECURITY_LEVELS: {
                value = theSupportedSecurityLevels.get(targetType);
                if (value == null) {
                    value = defaultSupportedSecurityLevels;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_KEY_ALGORITHM: {
                value = theDefaultKeyAlgorithm.get(targetType);
                if (value == null) {
                    value = defaultDefaultKeyAlgorithm;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_SYNC_ENROLLMENT_SUPPORTED: {
                value = theIsSynchronousEnrollmentSupported.get(targetType);
                if (value == null) {
                    value = defaultIsSynchronousEnrollmentSupported;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_CONF_SUBJECT_NAME_USED_FOR_ENROLL: {
                value = theIsConfiguredSubjectNameUsedForEnrollment.get(targetType);
                if (value == null) {
                    value = defaultIsConfiguredSubjectNameUsedForEnrollment;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_INITIAL_OTP_COUNT: {
                value = theDefaultInitialOtpCount.get(targetType);
                if (value == null) {
                    value = defaultDefaultInitialOtpCount;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_IKEV2_POLICY_PROFILE_SUPPORTED: {
                value = theIsIkev2PolicyProfileSupported.get(targetType);
                if (value == null) {
                    value = defaultIsIkev2PolicyProfileSupported;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_ENROLLMENT_CA_AUTHORIZATION_MODES: {
                value = theEnrollmentCAAuthorizationModes.get(targetType);
                if (value == null) {
                    value = defaultEnrollmentCAAuthorizationModes;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_DEPRECATED_ENROLLMENT_AUTHORITY_USED: {
                value = theIsDeprecatedEnrollmentAuthorityUsed.get(targetType);
                if (value == null) {
                    value = defaultIsDeprecatedEnrollmentAuthorityUsed;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_DEPRECATED_AUTHORITY_TYPE_SUPPORTED: {
                value = theIsDeprecatedAuthorityTypeSupported.get(targetType);
                if (value == null) {
                    value = defaultIsDeprecatedAuthorityTypeSupported;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_ON_DEMAND_CRL_DOWNLOAD_WORKFLOW: {
                value = theOnDemandCrlDownloadWorkflow.get(targetType);
                if (value == null) {
                    value = defaultOnDemandCrlDownloadWorkflow;
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_COM_ECIM_DEFAULT_NODE_CREDENTIAL_ID: {
                value = theComEcimDefaultNodeCredentialId.get(targetType);
                if (value == null) {
                    value = defaultComEcimDefaultNodeCredentialId;
                } else {
                    @SuppressWarnings("unchecked")
                    final Map<String, String> ids = (Map<String, String>) value;
                    if (ids.isEmpty()) {
                        value = null;
                    }
                }
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_COM_ECIM_DEFAULT_TRUST_CATEGORY_ID: {
                value = theComEcimDefaultTrustCategoryId.get(targetType);
                if (value == null) {
                    value = defaultComEcimDefaultTrustCategoryId;
                } else {
                    @SuppressWarnings("unchecked")
                    final Map<String, String> ids = (Map<String, String>) value;
                    if (ids.isEmpty()) {
                        value = null;
                    }
                }
            }
            break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_CONFIGURE_LDAP_WORKFLOW:
                value = getConfigureLdapWorkflow(targetType);
            break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_LDAP_MO_NAME:
                value = getConfigureLdapMoName(targetType);
            break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_ENROLL_CA_TRUST_CATEGORY_ID:
                    value = getDefaulConfiguretEnrollmentCaTrustCategoryId(targetType);
            break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_OTP_VALIDITY_PERIOD_IN_MINUTES:
                value = getConfigureDefaultOtpValidityPeriodInMinutes(targetType);
            break;
            default:
                logger.error("[MOCK] get CapabilityValue: unknown capability for {}", inputParams);
                break;
            }
        }
            break;
        default:
            logger.error("[MOCK] get CapabilityValue: unknown function for {}", inputParams);
            break;
        }

        if (value == null) {
            logger.info("[MOCK] get CapabilityValue: NOT FOUND for {}", inputParams);
        } else {
            logger.debug("[MOCK] get CapabilityValue: returns [{}]", value);
        }

        return value;
    }

    private Object getConfigureLdapWorkflow(String targetType ) {
        Object value = theConfigureLdapWorkflow.get(targetType);
        if (value == null) {
            value = DEFAULT_CONFIGURE_LDAP_WORKFLOW;
        }
        return value;
    }

    private Object getConfigureLdapMoName(String targetType ) {
        Object value = theConfigureLdapMoName.get(targetType);
        if (value == null) {
            value = DEFAULT_CONFIGURE_LDAP_MO_NAME;
        }
        return value;
    }

    private Object getDefaulConfiguretEnrollmentCaTrustCategoryId(String targetType ) {
        return theDefaulConfiguretEnrollmentCaTrustCategoryId.get(targetType);
    }

    private Object getConfigureDefaultOtpValidityPeriodInMinutes(String targetType ) {
        Object value = theConfigureDefaultOtpValidityPeriodInMinutes.get(targetType);
        if (value == null) {
            value = DEFAULT_CONFIGURE_OTP_VALIDITY_PERIOD_IN_MINUTES;
        }
        return value;
    }

    @Override
    public String getTargetModelIdentityFromMimVersion(final String targetCategory, final String targetType, final String mimVersion) {
        final MimVersion theErbs14AMimVersion = new MimVersion(ERBS_14A_MIM_VERSION);
        if (ERBS.equals(targetType) && theErbs14AMimVersion.isEqualTo(mimVersion)) {
            return ERBS_14A_OSS_MODEL_IDENTITY;
        }
        return DUMMY_OSS_MODEL_IDENTITY;
    }

    @Override
    public String getTargetModelIdentityFromProductNumber(final String targetCategory, final String targetType, final String productNumber) {
        return DUMMY_OSS_MODEL_IDENTITY;
    }

    /**
     * @param targetCategory
     * @param targetType
     * @param targetModelIdentity
     * @return
     */
    private String getCapabilitySupportVersion(final String targetCategory, final String targetType, final String targetModelIdentity) {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("[MOCK] get CapabilitySupportVersion: starts for {}", inputParams);

        String capabilitySupportVersion = null;
        final Map<String, String> capabilitySupportVersions = theCapabilitySupportVersions.get(targetType);

        if (capabilitySupportVersions != null) {
            capabilitySupportVersion = capabilitySupportVersions.get(targetModelIdentity);
        }
        if (capabilitySupportVersion == null) {
            capabilitySupportVersion = NscsCapabilityModelConstants.NSCS_TARGET_TYPE_SPECIFIC_CAPABILITYSUPPORT_MODEL_VER;
            logger.debug("[MOCK] get CapabilitySupportVersion: no specific capabilitysupport version for {}", inputParams);
        }

        logger.debug("[MOCK] get CapabilitySupportVersion: returns [{}]", capabilitySupportVersion);
        return capabilitySupportVersion;
    }

    @Override
    public Collection<Capability> getCapabilities(final String function, final String capabilityName) {
        return null;
    }

    @Override
    public Map<String, Object> getCapabilities(final String targetCategory, final String targetType, final String function, final String version) {
        return null;
    }

    @Override
    public Object getDefaultValue(final String capabilityModelName, final String capabilityName) {

        logger.debug("[MOCK] get capability default value for model[{}]  capability[{}]", capabilityModelName, capabilityName);

        Object value = null;

        switch (capabilityModelName) {
        case NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL: {
            switch (capabilityName) {
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_UNSUPPORTED_COMMANDS: {
                value = defaultUnsupportedCommands;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_CREDS_PARAMS: {
                value = defaultCredentialsParams;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_CERT_MANAGEMENT_SUPPORTED: {
                value = defaultIsCertificateManagementSupported;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_SUPPORTED_SECURITY_LEVELS: {
                value = defaultSupportedSecurityLevels;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_KEY_ALGORITHM: {
                value = defaultDefaultKeyAlgorithm;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_SYNC_ENROLLMENT_SUPPORTED: {
                value = defaultIsSynchronousEnrollmentSupported;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_SUPPORTED_ENROLLMENT_MODES: {
                value = defaultSupportedEnrollmentModes;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_ENROLLMENT_MODE: {
                value = defaultDefaultEnrollmentMode;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_FINGERPRINT_ALGORITHM: {
                value = defaultDefaultFingerprintAlgorithm;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_ISSUE_CERT_WORKFLOWS: {
                value = defaultIssueCertWorkflows;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CRL_CHECK_WORKFLOWS: {
                value = defaultCrlCheckWorkflows;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_TRUST_DISTR_WORKFLOWS: {
                value = defaultTrustDistrWorkflows;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_TRUST_REMOVE_WORKFLOWS: {
                value = defaultTrustRemoveWorkflows;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_ON_DEMAND_CRL_DOWNLOAD_WORKFLOW: {
                value = defaultOnDemandCrlDownloadWorkflow;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_MOM_TYPE: {
                value = defaultMomType;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_SUPPORTED_CERT_TYPES: {
                value = defaultSupportedCertTypes;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_CRL_CHECK_SUPPORTED_CERT_TYPES: {
                value = defaultCrlCheckSupportedCertTypes;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_ENTITY_PROFILES: {
                value = defaultDefaultEntityProfiles;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHER_MO_ATTRIBUTES: {
                value = defaultCipherMoAttributes;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_SUPPORTED_CIPHER_PROTOCOL_TYPES: {
                value = defaultSupportedCipherProtocolTypes;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_EMPTY_CIPHER_SUPPORTED: {
                value = defaultIsEmptyCiphersSupported;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_CONF_SUBJECT_NAME_USED_FOR_ENROLL: {
                value = defaultIsConfiguredSubjectNameUsedForEnrollment;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_INITIAL_OTP_COUNT: {
                value = defaultDefaultInitialOtpCount;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_IKEV2_POLICY_PROFILE_SUPPORTED: {
                value = defaultIsIkev2PolicyProfileSupported;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_ENROLLMENT_CA_AUTHORIZATION_MODES: {
                value = defaultEnrollmentCAAuthorizationModes;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_DEPRECATED_ENROLLMENT_AUTHORITY_USED: {
                value = defaultIsDeprecatedEnrollmentAuthorityUsed;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_DEPRECATED_AUTHORITY_TYPE_SUPPORTED: {
                value = defaultIsDeprecatedAuthorityTypeSupported;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_COM_ECIM_DEFAULT_NODE_CREDENTIAL_ID: {
                value = defaultComEcimDefaultNodeCredentialId;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_COM_ECIM_DEFAULT_TRUST_CATEGORY_ID: {
                value = defaultComEcimDefaultTrustCategoryId;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_LDAP_COMMON_USER_SUPPORTED: {
                value = defaultIsLdapCommonUserSupported;
            }
                break;
            default:
                break;
            }
        }
            break;
        default:
            logger.error("[MOCK]: unknown model[{}] while getting default value for capability[{}]", capabilityModelName, capabilityName);
            break;
        }

        logger.debug("[MOCK] get capability default value returns object[{}]", value);
        return value;
    }

}
