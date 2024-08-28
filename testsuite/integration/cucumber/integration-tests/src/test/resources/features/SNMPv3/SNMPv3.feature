@SNMPv3GET

Feature: SNMPv3GET

  Background:
    Given A user named: "Administrator"
    And A clean environment

  Scenario: Get Snmpv3 AuthPriv for a NetworkElement
    Given A NetworkElement Node named: "Snmpv3Erbs1"
    And   Execute secadm command: "credentials create --rootusername u1 --rootuserpassword pw1 --secureusername u2 --secureuserpassword pw2 --normalusername u3 --normaluserpassword pw3 -n Snmpv3Erbs1"
    When  Execute secadm command: "snmp get -pt show -n Snmpv3Erbs1"
    Then  Command execution completed with message containing: "Error 10019 : Operation not allowed for this node type : ERBS"

    Given Nodes RADIO_NODE/snmpv3Node1
    And   Execute secadm command: "snmp authpriv --auth_algo MD5 --auth_password authpassword --priv_algo DES --priv_password privpassword -n snmpv3Node1"
    Then  Command execution completed with message containing: "Snmp Authpriv Command OK."
    When  Execute secadm command: "snmp get -pt show -n snmpv3Node1"
    Then  Command execution completed with message containing: "value:snmpv3Node1;"
    Then  Command execution completed with message containing: "value:MD5;"
    Then  Command execution completed with message containing: "value:authpassword;"
    Then  Command execution completed with message containing: "value:privpassword;"
    
    When  Execute secadm command: "snmp get -pt hide -n snmpv3Node1"
    Then  Command execution completed with message containing: "value:snmpv3Node1;"
    Then  Command execution completed with message containing: "value:MD5;"
    Then  Command execution completed with message containing: "value:***********;"
    Then  Command execution completed with message containing: "value:***********;"
    
    Given Nodes RADIO_NODE/snmpv3Node2
    And   Execute secadm command: "snmp authnopriv --auth_algo SHA1 --auth_password authnoprivpassword -n snmpv3Node2"
    Then  Command execution completed with message containing: "Snmp Authnopriv Command OK."
    When  Execute secadm command: "snmp get -pt show -n snmpv3Node2"
    Then  Command execution completed with message containing: "value:snmpv3Node2;"
    Then  Command execution completed with message containing: "value:SHA1;"
    Then  Command execution completed with message containing: "value:authnoprivpassword;"
    
    When  Execute secadm command: "snmp get -n snmpv3Node2"
    Then  Command execution completed with message containing: "value:snmpv3Node2;"
    Then  Command execution completed with message containing: "value:SHA1;"
    Then  Command execution completed with message containing: "value:***********;"

    When Execute secadm command: "secadm snmp authpriv --auth_algo SHA1 --auth_password asd --priv_algo SHA1 --priv_password asd --collection DOES_NOT_EXIST"
    Then  Command execution completed with message containing: "Error 10001 : Command syntax error"

    When Execute secadm command: "secadm snmp authpriv --auth_algo SHA1 --auth_password asd --priv_algo SHA1 --priv_password asd --savedsearch DOES_NOT_EXIST"
    Then  Command execution completed with message containing: "Error 10001 : Command syntax error"

