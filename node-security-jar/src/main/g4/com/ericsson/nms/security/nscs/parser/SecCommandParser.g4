parser grammar SecCommandParser;

import SecCommandBaseParser;

options { tokenVocab=SecCommandLexer; }

/*
Defining new command: STEP 2 and 3  (for STEP 1 see SecCommandLexer.g4 file)

2- Create a new rule representing your command syntax. A new rule has the following format :

    <rule name> :
        <command entry defined in SecCommandLexer.g4 file> <parameter definition>
    ;

    - "parameter definition" can be zero or more of the below functions:

        Observation: <param name> or <prop1> = the name that will be used as Key of Map returned by the parser

        FUNCTION                                                        DESCRIPTION
        textValue["<param name>"]                                       Any text value parameter.
        textValueFromList["<param name>", list("<opt1>","<optn>")]      Any text value parameter, but it has to be one of provided options,
                                                                        otherwise it is considered a syntax error.
        intValue["<param name>"]                                        A int parameter
        fileValue["<param name>"]                                       A parameter that specifies a file name. Has to be in the format:
                                                                            file=<file name>
        listValue["<param name>"]                                       A parameter made of one or more values. Expects as single value like ' val1 '
                                                                        or a list in the format : ' val1, val2, val3; '
        property                                                        A parameter in the format : -key value . This parameter will be added to resulting
                                                                        Map as is, but dashes at the beginning of the property name will be discarded.
        propertyFromList[list("<prop1>","<propn>")]                     Same as 'property' but the KEY has to be one of provided options
                                                                        otherwise it is considered a syntax error.
        propertyValueFromList["<prop1>",list("<opt1>","<optn>")]        Same as 'property' but the VALUE has to be one of provided options
                                                                        otherwise it is considered a syntax error.
        propertyList                                                    Expects one or more 'property' separated by spaces
        propertyListFromList[list("<prop1>","<propn>")]                 Same as 'propertyList' but keys have to be among provided options
                                                                        otherwise it is considered a syntax error.
        propertyFromListWithoutValue[list("<prop1>","<propn>")]         Same as 'propertyFromList' but the value is not required. If value is there , 
        																then we should use propertyFromList.
        propertyListFromListWithoutValue[list("<prop1>","<propn>")] 	Same as 'propertyListFromList' but all properties must not have any value															                                                       

        ALIASES:
            Properties can have aliases. To define a property with alias use the function withAlias("<prop>","<alias1>","<aliasn>"). Eg.:

                propertyFromList[ list(withAlias("--nodelist:list","-n")) ]
                    OR
                propertyListFromList[ list(withAlias("--rootusername","-run"), withAlias("--rootuserpassword","-rup") ]
                    OR
                propertyValueFromList[ withAlias("--switch:text","-s"), list("on", "off") ]

3- Add new rule to the list of possible commands in main rule called 'command'

                                                        insert rule name here
                                                                |
    command:                                                    v
        (cppGetSl|cppSetSl|cppInstallLaad|createCredentials|<rule name>) ...

*/

command:
    (cppGetSl|createCredentials|updateCredentials|getCredentials|getSnmp|addTargetGroups|snmpAuthpriv|snmpAuthnopriv|setEnrollment|getCertEnrollState|getTrustCertInstallState) (allNodes | nodeListOrNodeFile)
    | (cppIpSecStatus) | certIssue | cppSetSl (xmlFile) | (cppIpsec) | (createSshkey nodeListAlgorithm) | (updateSshkey nodeListAlgorithmUpdate) | (deleteSshkey)
    | (trustDistr) | (trustRm) | (certReissue) |(testCommand)| (ldapCommand) | (crlCheckEnable) |  (getJob) | (crlCheckDisable) | (crlCheckGetStatus) | (onDemandCrlDownload)
    | (setCiphers) | (getCiphers) | (enrollmentInfoFileDownload) | (rtselActivate) | (rtselDeactivate) | (rtselGet) | (rtselDelete)
    | (httpsActivate) | (httpsDeactivate) | (httpsGetStatus) | (ftpesActivate) | (ftpesDeactivate) | (ftpesGetStatus) | (getNodeSpecificPassword) | (capabilityGet) | (laadFilesDistribute) | (ntpConfigure) | (ntpRemove) | (ntpList) | (ssoEnable) | (ssoDisable) | (ssoGetStatus)
    | (importNodeSshPrivateKey) ;

rtselActivate :
	RTSEL_ACTIVATE xmlFile
;

rtselDeactivate :
	RTSEL_DEACTIVATE rtselNodeListOrNodeFile
;

rtselGet :
	RTSEL_GET nodeListOrNodeFile
;

rtselDelete :
	RTSEL_DELETE xmlFile
;

laadFilesDistribute :
LAAD_FILES_DISTRIBUTE nodeList
;

httpsActivate :
    HTTPS_ACTIVATE nodeList
;

httpsDeactivate :
    HTTPS_DEACTIVATE nodeList
;

httpsGetStatus :
    HTTPS_GET_STATUS nodeList
;

ftpesActivate :
    FTPES_ACTIVATE nodeList
;

ftpesDeactivate :
    FTPES_DEACTIVATE nodeList
;

ftpesGetStatus :
    FTPES_GET_STATUS nodeList
;

ssoEnable :
    SSO_ENABLE nodeList
;

ssoDisable :
    SSO_DISABLE nodeList
;

ssoGetStatus :
    SSO_GET nodeList
;

ntpConfigure : NTP_CONFIGURE nodeList
;

ntpRemove : NTP_REMOVE ((nodeName keyIdsOrServerIds) | xmlFile)
;

ntpList : NTP_LIST nodeList;

getCiphers :
GET_CIPHERS protocol ciphersNodeListOrNodeFile;

setCiphers :
SET_CIPHERS (setCiphersUsingXmlFile | setCiphersUsingNodeListOrNodeFile) ;

setCiphersUsingNodeListOrNodeFile :
protocol ciphersEncrypt ciphersKex ciphersMac ciphersNodeListOrNodeFile
;

enrollmentInfoFileDownload:
	ENROLLMENT_INFO_FILE verboseEnrollmentInfoOption? xmlFile
;

onDemandCrlDownload :
ON_DEMAND_CRL_DOWNLOAD downloadCrlNodeListorNodeFile
;

crlCheckGetStatus :
CRL_CHECK_GET_STATUS certType crlCheckNodeListorNodeFile
;

crlCheckDisable :
CRL_CHECK_DISABLE certType crlCheckDisableEnableNodeListorNodeFile
;

crlCheckEnable :
CRL_CHECK_ENABLE certType crlCheckDisableEnableNodeListorNodeFile
;

cppGetSl : 
// TODO TORF-38537 - Disabled for 14B to be re-enabled once pki solution is in place
	CPP_GET_SL (propertyValueFromList[withAlias("--level:text","-l"), list(1,2)])?
;

cppSetSl :
// TODO TORF-38537 - Disabled for 14B to be re-enabled once pki solution is in place
  CPP_SET_SL propertyValueFromList[withAlias("--level:text","-l"), list(1,2)]
;

cppInstallLaad :
//    CPP_INSTALL_LAAD
;

continueOption :
//    (propertyFromListWithoutValue[list(withAlias("--continue", "-c"))])?
;

attributeOption :
    propertyListFromList[list(withAlias("--rootusername","-rn"), withAlias("--rootuserpassword","-rp") , withAlias("--secureusername","-sn"), withAlias("--secureuserpassword","-sp") , withAlias("--normalusername", "-nn"), withAlias("--normaluserpassword","-np"), withAlias("--ldapuser","-lu"), withAlias("--nwieasecureusername","-nasn"), withAlias("--nwieasecureuserpassword","-nasp"), withAlias("--nwiebsecureusername","-nbsn"), withAlias("--nwiebsecureuserpassword","-nbsp"), withAlias("--nodecliusername", "-ncn"), withAlias("--nodecliuserpassword","-ncp") )]?
;

attributeGetOption :
    (propertyListFromList[list(withAlias("--usertype","-ut"), withAlias("--plaintext","-pt") )])?
;

verboseEnrollmentInfoOption :
    (propertyFromListWithoutValue[list(withAlias("--verbose", "-v"))])?
;

createCredentials :
    CREATE_CREDENTIALS continueOption attributeOption
;

updateCredentials :
    UPDATE_CREDENTIALS attributeOption continueOption
;

getCredentials :
    GET_CREDENTIALS attributeGetOption
;

addTargetGroups :
// TODO TORF-38537 - Disabled for 14B to be re-enabled once pki solution is in place
//    ADD_TARGET_GROUPS propertyFromList[list(withAlias("--targetgroups:list","-tg"))]
;


cppIpSecStatus :
// TODO TORF-38537 - Disabled for 14B to be re-enabled once pki solution is in place
	CPP_IPSEC_STATUS cppIpsecStatusNodeFile
;
cppIpsec :
// TODO TORF-38537 - Disabled for 14B to be re-enabled once pki solution is in place
	CPP_IPSEC cppIpsecXmlFile
;

testCommand :
	TEST_COMMAND testParameters
;

createSshkey :
	CREATE_SSH_KEY nodeListAlgorithm
;

updateSshkey :
	//UPDATE_SSH_KEY nodeList optionalAlgorithm
	UPDATE_SSH_KEY nodeListAlgorithmUpdate
;

deleteSshkey :
	DELETE_SSH_KEY nodeListOrNodeFile
;

importNodeSshPrivateKey :
    IMPORT_NODE_SSH_PRIVATE_KEY sshprivatekeyFile nodeName
;

certIssue :
    CERTIFICATE_ISSUE issueAttributeOption xmlFile externalCA?
;

snmpAuthpriv :
    SNMP_AUTHPRIV snmpAuthprivParameters
;

snmpAuthnopriv :
    SNMP_AUTHNOPRIV snmpAuthnoprivParameters
;

getSnmp :
    GET_SNMP attributeGetSnmpOption
;

trustDistr :
    TRUST_DISTRIBUTE trustDistrCategory trustDistCAorNodeList trustDistrNodelistAttribute externalCA?
;

trustRm :
    TRUST_REMOVE trustRmCategoryAttribute trustRmIssuerAttributes trustRmSnAttribute nodelistAttribute
;

setEnrollment :
   // SET_ENROLLMENT enrollmentParameter
;

getCertEnrollState :
    GET_CERT_ENROLL_STATE getEnrollStateOption
;

getTrustCertInstallState :
    GET_TRUST_CERT_INSTALL_STATE getTrustStateOption
;

certReissue :
    CERTIFICATE_REISSUE certTypeAttribute certReissueCAorNodeList certReissueNodesOrSn reasonOption
;

ldapCommand : ( ldapConfiguration | ldapReConfiguration | ldapRenew | ldapProxyGet | ldapProxySet | ldapProxyDelete );

ldapConfiguration :
	LDAP_CONFIGURATION  ( ldapConfigurationFile | manual )
;

ldapReConfiguration :
	LDAP_RECONFIGURATION ldapConfigurationFile
;

ldapRenew :
	LDAP_RENEW ldapConfigurationFile force?
;

ldapProxyGet :
	LDAP_PROXY_GET ( allLdapProxies | ldapProxyGetRequested ) ldapProxyGetOptionals? ldapProxyGetCount?
;

allLdapProxies : wildcardValue["proxylist"];

ldapProxyGetRequested :
    propertyFromList[list(withAlias("--inactivity-seconds:text", "-inacts"), withAlias("--inactivity-hours:text", "-inacth"), withAlias("--inactivity-days:text", "-inactd"), withAlias("--admin-status:text","-admin"))]
;

ldapProxyGetOptionals :
    (propertyFromListWithoutValue[list(withAlias("--summary","-su"), withAlias("--legacy","-le"))])
;

ldapProxyGetCount :
    propertyFromList[list(withAlias("--count:text", "-c"))]
;

ldapProxySet :
	LDAP_PROXY_SET ( ldapProxyAdminStatus ) ldapConfigurationFile force?
;

ldapProxyAdminStatus : 
    propertyValueFromList[withAlias("--admin-status:text","-admin"), list("DISABLED","ENABLED")]
;

ldapProxyDelete :
	LDAP_PROXY_DELETE ldapConfigurationFile force?
;

force :
    (propertyFromListWithoutValue[list("--force")])
;

getJob :
    GET_JOB (jobList | allJobs)(wfFilter) summary?
;

getNodeSpecificPassword :
    GET_NODE_SPECIFIC_PASSWORD neTypeOption
;

capabilityGet :
    CAPABILITY_GET (capabilityGetTargetsCapabilities | capabilityGetAllTargetsCapabilities)(capabilityGetCheck)
;

allJobs : wildcardValue["joblist"];

jobList :
    propertyFromList[list(withAlias("--joblist:list", "-j"))]
;

allNodes : wildcardValue["nodelist"];

wfFilter: (propertyFromList[list("-wf:list")])?;

nodeList : propertyFromList[list(withAlias("--nodelist:list", "-n"), withAlias("--nodefile:file", "-nf"), withAlias("--savedsearch:list", "-sa"), withAlias("--collection:list", "-co"))] ;

nodeListOrNodeFile : propertyFromList[list(withAlias("--nodelist:list", "-n"), withAlias("--nodefile:file", "-nf"))] ;

rtselNodeListOrNodeFile : propertyFromList[list(withAlias("--nodelist:list", "-n"), withAlias("--nodefile:file", "-nf"), withAlias("--savedsearch:list", "-sa"), withAlias("--collection:list", "-co"))] ;

nodeListAlgorithm : propertyFromList[list(withAlias("--nodelist:list", "-n"), withAlias("--nodefile:file", "-nf"), withAlias("--algorithm-type-size","-t"))] ;

nodeListAlgorithmUpdate : propertyFromList[list(withAlias("--nodelist:list", "-n"), withAlias("--nodefile:file", "-nf"), withAlias("--algorithm-type-size","-t"))] | (propertyFromList[list(withAlias("--algorithm-type-size","-t"))])*;

//issueAttributeOption : propertyValueFromList[ withAlias("--certtype:text","-ct"), list ("IPSEC","OAM") ];
issueAttributeOption : propertyFromList[ list(withAlias("--certtype:text","-ct")) ];

//trustDistrAttributes : (propertyFromList[ list(withAlias("--certtype:text","-ct"), ("-ca"), withAlias("--nodelist:list", "-n"), withAlias("--nodefile:file", "-nf")) ])*;
trustDistrAttributes : propertyFromList[ list(withAlias("--certtype:text","-ct"), ("-ca")) ];

trustDistrCategory : propertyFromList[ list(withAlias("--certtype:text","-ct"), withAlias("--trustcategory:text", "-tc") ) ];

trustDistCAorNodeList: propertyFromList[ list(("-ca"), withAlias("--nodelist:list", "-n"), withAlias("--nodefile:file", "-nf"), withAlias("--savedsearch:list", "-sa"), withAlias("--collection:list", "-co"), withAlias("--xmlfile:file","-xf") ) ];

trustDistrCA : (propertyFromList[ list(("-ca")) ])?;

trustDistrNodelistAttribute : (allNodes | nodeList | xmlFile)?;

trustRmIssuerAttributes : propertyFromList[ list(withAlias("--issuer-dn:text","-isdn"), ("-ca")) ];

trustRmSnAttribute : propertyFromList[ list(withAlias("--serialnumber:text","-sn")) ];

trustRmCategoryAttribute : propertyFromList[ list(withAlias("--certtype:text","-ct"), withAlias("--trustcategory:text","-tc")) ];

nodelistAttribute : (allNodes | nodeList);

certTypeAttribute : propertyFromList[ list(withAlias("--certtype:text","-ct")) ];

certReissueCAorNodeList : propertyFromList[ list(("-ca"), withAlias("--nodelist:list", "-n"), withAlias("--nodefile:file", "-nf"), withAlias("--savedsearch:list", "-sa"), withAlias("--collection:list", "-co")) ];

certReissueNodesOrSn : (propertyFromList [list(withAlias("--serialnumber:text","-sn"), withAlias("--nodelist:list", "-n"), withAlias("--nodefile:file", "-nf"), withAlias("--savedsearch:list", "-sa"), withAlias("--collection:list", "-co"), withAlias("--reason", "-r")) ])?;

reasonOption : (propertyFromList[list(withAlias("--reason", "-r"))])?;

enrollmentParameter : propertyFromList[ list(withAlias("--enrollmentmode:text","-em")) ];

getEnrollStateOption : propertyFromList[ list(withAlias("--certtype:text","-ct")) ];

getTrustStateOption : propertyFromList[ list(withAlias("--certtype:text","-ct"), withAlias("--trustcategory:text", "-tc")) ];

/*For Ldap Configure|Reconfigure Command we need to use this xmlFile */
 ldapConfigurationFile:propertyFromList[list(withAlias("--xmlfile:file", "-xf"))];

/*
For "ipsec --status" command we will use nodeFile and for "ipsec" command we need to use xmlFile
*/

xmlFile :propertyFromList[list(withAlias("--xmlfile:file", "-xf"))];

sshprivatekeyFile :propertyFromList[list(withAlias("--sshprivatekeyfile:file", "-skf"))];

nodeFile : propertyFromList[list(withAlias("--nodefile:file", "-nf"))];

testParameters :
	(propertyFromList[list(withAlias("--workflows:text","-wfs"))])*
;

manual :
	(propertyFromListWithoutValue[list(withAlias("--manual","-ml"))])
;

snmpAuthprivParameters :
        propertyListFromList[ list(withAlias("--auth_algo:text","-aa"), withAlias("--auth_password:text","-ap"), withAlias("--priv_algo:text","-pa"), withAlias("--priv_password:text","-pp"))]
;

snmpAuthnoprivParameters :
        propertyListFromList[ list(withAlias("--auth_algo:text","-aa"), withAlias("--auth_password:text","-ap"))]
;

attributeGetSnmpOption :
    (propertyListFromList[list(withAlias("--plaintext","-pt") )])?
;

snmpGetParameters :
        propertyListFromList[ list(withAlias("--plaintext:text","-pt")) ]
;

certType :
		propertyFromList[ list(withAlias("--certtype:text","-ct")) ]
;

crlCheckNodeListorNodeFile :
 		propertyFromList[list(withAlias("--nodelist:list", "-n"), withAlias("--nodefile:file", "-nf"))]
;

crlCheckDisableEnableNodeListorNodeFile :
 		propertyFromList[list(withAlias("--nodelist:list", "-n"), withAlias("--nodefile:file", "-nf"), withAlias("--savedsearch:list", "-sa"), withAlias("--collection:list", "-co"))]
;

downloadCrlNodeListorNodeFile :
 		propertyFromList[list(withAlias("--nodelist:list", "-n"), withAlias("--nodefile:file", "-nf"), withAlias("--savedsearch:list", "-sa"), withAlias("--collection:list", "-co"))]
;

cppIpsecXmlFile :
	   propertyFromList[list(withAlias("--xmlfile:file", "-xf"))];

cppIpsecStatusNodeFile :
       propertyFromList[list(withAlias("--nodefile:file", "-nf"))];


ciphersNodeListOrNodeFile :
 		propertyFromList[list(withAlias("--nodelist:list", "-n"), withAlias("--nodefile:file", "-nf"))]
;

setCiphersUsingXmlFile :
		propertyFromList[list(withAlias("--xmlfile:file", "-xf"))]
;

ciphersEncrypt :
      (propertyFromList [list(withAlias("--encryptalgos:list","-enc"), withAlias("--keyexchangealgos:list", "-kex"), withAlias("--macalgos:list", "-mac"), withAlias("--cipherfilter:text", "-cf")) ])
;

ciphersKex :
      (propertyFromList [list(withAlias("--encryptalgos:list","-enc"), withAlias("--keyexchangealgos:list", "-kex"), withAlias("--macalgos:list", "-mac"))] )?
;

ciphersMac :
      (propertyFromList [list(withAlias("--encryptalgos:list","-enc"), withAlias("--keyexchangealgos:list", "-kex"), withAlias("--macalgos:list", "-mac")) ])?
;

protocol :
	 propertyFromList[list(withAlias("--protocol:text","-pr"))]
;

capabilityGetAllTargetsCapabilities :
	( capabilityGetAllTargets ) capabilityNameOption?
;

capabilityGetAllTargets :
	wildcardValue["netype"]
;

capabilityGetTargetsCapabilities :
	( neTypeOption ) capabilityGetOptionalTargetsCapabilities?
;

capabilityGetOptionalTargetsCapabilities :
        ( propertyListFromList[list(withAlias("--ossmodelidentity:text", "-omi"), withAlias("--targetcategory:text", "-cat"), withAlias("--capabilityname:text", "-cap"))] )
;

capabilityNameOption :
        (propertyFromList[list(withAlias("--capabilityname:text", "-cap"))])
;

capabilityGetCheck :
	(propertyFromListWithoutValue[list(withAlias("--skipconsistencycheck","-scc"))])?
;

ossModelIdentityOption :
        (propertyFromList[list(withAlias("--ossmodelidentity:text", "-omi"))])
;

targetCategoryOption :
        (propertyFromList[list(withAlias("--targetcategory:text", "-cat"))])
;

neTypeOption :
        propertyFromList[list(withAlias("--netype:text", "-nt"))]
;

externalCA :
(propertyFromListWithoutValue[list(withAlias("--extca","-ec"))])
;

summary :
    (propertyFromListWithoutValue[list(withAlias("--summary","-su"))])
;

keyIdsOrServerIds :
(propertyFromList[list(withAlias("--serveridlist:list","-sl"), withAlias("--keyidlist:list","-kl"))])
;

nodeName :
(propertyFromList[list(withAlias("--nodename:text","-nn"))])
;
