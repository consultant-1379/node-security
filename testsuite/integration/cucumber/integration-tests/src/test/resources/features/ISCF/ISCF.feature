@ISCF

Feature: ISCF

  Scenario: ISCF OAM CPP Test
      Given CPP node created on DPS
      When  Generate ISCF OAM for CPP
      Then  ISCF file is not empty

  Scenario: ISCF Combo CPP Test
      Given CPP node created on DPS
      When  Generate ISCF Combo for CPP
      Then  ISCF file is not empty

  Scenario: ISCF Combo CPP shared step Test
      Given Nodes ERBS/IscfNode
      When  Generate ISCF Combo for CPP shared
      Then  ISCF file is not empty
      And   End Entity "ERBS_ISCF_NODE-oam" exists in PKI with SAN type "" and value ""
      And   End Entity "ERBS_ISCF_NODE-ipsec" exists in PKI with SAN type "IP_ADDRESS" and value "127.0.0.1"

  Scenario: Generate Security Data OAM for node type RadioNode Test
      Given Nodes RADIO_NODE/snmpv3Node1
      When  Generate Security Data OAM for "RadioNode" node "snmpv3Node1" with: enrollment mode "CMPv2_VC", SAN type "" and value ""
      Then  End Entity "snmpv3Node1-oam" exists in PKI with SAN type "" and value ""
      And   Security Data contains Integration Info for Certificate Types
            |OAM|

  Scenario: Generate Security Data IPSec for node type RadioNode Test
      Given Nodes RADIO_NODE/snmpv3Node1
      When  Generate Security Data IPSec for "RadioNode" node "snmpv3Node1" with: enrollment mode "CMPv2_VC", SAN type "IPV4" and value "172.13.14.1"
      Then  End Entity "snmpv3Node1-ipsec" exists in PKI with SAN type "IP_ADDRESS" and value "172.13.14.1"
      And   Security Data contains Integration Info for Certificate Types
            |IPSEC|

  Scenario: Generate Security Data Combo for node type RadioNode Test
      Given Nodes RADIO_NODE/snmpv3Node1
      When  Generate Security Data Combo for "RadioNode" node "snmpv3Node1" with: enrollment mode "CMPv2_VC", SAN type "IPV4" and value "172.13.14.1"
      Then  End Entity "snmpv3Node1-ipsec" exists in PKI with SAN type "IP_ADDRESS" and value "172.13.14.1"
      And   End Entity "snmpv3Node1-oam" exists in PKI with SAN type "" and value ""
      And   Security Data contains Integration Info for Certificate Types
            |OAM  |
            |IPSEC|

  Scenario: Generate Security Data OAM with SAN type IPV4 for node type RVNFM Test
      Given Nodes RVNFM/RvnfmNode
      When  Generate Security Data OAM for "RVNFM" node "RVNFM_001" with: enrollment mode "CMPv2_INITIAL", SAN type "IPV4" and value "172.13.14.1"
      Then  End Entity "RVNFM_001-oam" exists in PKI with SAN type "IP_ADDRESS" and value "172.13.14.1"
      And   Security Data contains Integration Info for Certificate Types
            |OAM|

  Scenario: Generate Security Data OAM with SAN type IPV6 for node type RVNFM Test
      Given Nodes RVNFM/RvnfmNode
      When  Generate Security Data OAM for "RVNFM" node "RVNFM_001" with: enrollment mode "CMPv2_INITIAL", SAN type "IPV6" and value "fe80::a00:27ff:fe6a:e75a"
      Then  End Entity "RVNFM_001-oam" exists in PKI with SAN type "IP_ADDRESS" and value "fe80::a00:27ff:fe6a:e75a"

  Scenario: Generate Security Data OAM with SAN type IPV4 and value ? for node type RVNFM Test
      Given Nodes RVNFM/RvnfmNode
      When  Generate Security Data OAM for "RVNFM" node "RVNFM_001" with: enrollment mode "CMPv2_INITIAL", SAN type "IPV4" and value "?"
      Then  End Entity "RVNFM_001-oam" exists in PKI with SAN type "IP_ADDRESS" and value "?"

  Scenario: Generate Security Data OAM twice, changing SAN type from IPV4 to IPV6 for node type RVNFM Test
      Given Nodes RVNFM/RvnfmNode
      When  Generate Security Data OAM for "RVNFM" node "RVNFM_001" with: enrollment mode "CMPv2_INITIAL", SAN type "IPV4" and value "172.13.14.1"
      And   Generate Security Data OAM for "RVNFM" node "RVNFM_001" with: enrollment mode "CMPv2_INITIAL", SAN type "IPV6" and value "fe80::a00:27ff:fe6a:e75a"
      Then  End Entity "RVNFM_001-oam" exists in PKI with SAN type "IP_ADDRESS" and value "fe80::a00:27ff:fe6a:e75a"

  Scenario: Generate Security Data OAM twice, changing Entity Profile for node type RVNFM Test
      Given Nodes RVNFM/RvnfmNode
      When  Generate Security Data OAM for "RVNFM" node "RVNFM_001" with: enrollment mode "CMPv2_INITIAL", SAN type "IPV4" and value "?"
      And   Update End Entity "RVNFM_001-oam" with profile "DUSGen2OAM_EP"
      And   Generate Security Data OAM for "RVNFM" node "RVNFM_001" with: enrollment mode "CMPv2_INITIAL", SAN type "IPV4" and value "2.2.2.2"
      Then  End Entity "RVNFM_001-oam" exists in PKI with SAN type "IP_ADDRESS" and value "2.2.2.2"
      And   End Entity "RVNFM_001-oam" contains Entity Profile "VNFM_IP_EP"

