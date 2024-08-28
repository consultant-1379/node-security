@Credentials

Feature: Credentials

  Background:
    Given A user named: "Administrator"
    And A clean environment

  Scenario: Create Credentials for a NetworkElement
    Given A NetworkElement Node named: "ne1"
    When Execute secadm command: "credentials create --rootusername u1 --rootuserpassword pw1 --secureusername u2 --secureuserpassword pw2 --normalusername u3 --normaluserpassword pw3 --nodecliusername u4 --nodecliuserpassword pw4 -n ne1"
    Then Command execution completed with message containing: "All credentials were created successfully"

  Scenario: Create Credentials for a NFVO
    Given An NFVO Node named: "nfvo1"
    When Execute secadm command: "credentials create --secureusername someUser --secureuserpassword somePassword --nodecliusername nodeCliUser --nodecliuserpassword nodeCliPassword -n NetworkFunctionVirtualizationOrchestrator=nfvo1"
    Then Command execution completed with message containing: "All credentials were created successfully"

  Scenario: Get Credentials for a NFVO
    Given An NFVO Node named: "nfvo1"
    And   Execute secadm command: "credentials create --secureusername secureUser --secureuserpassword securePassword --nodecliusername nodeCliUser --nodecliuserpassword nodeCliPassword -n NetworkFunctionVirtualizationOrchestrator=nfvo1"
    When  Execute secadm command: "credentials get --nodelist NetworkFunctionVirtualizationOrchestrator=nfvo1"
    Then  Command execution completed with message containing: "value:nfvo1;"
    Then  Command execution completed with message containing: "value:nodeCliUserName:nodeCliUser;"

  Scenario: Update Credentials for a NFVO
    Given An NFVO Node named: "nfvo1"
    And   Execute secadm command: "credentials create --secureusername secureUser --secureuserpassword securePassword --nodecliusername nodeCliUser --nodecliuserpassword nodeCliPassword -n NetworkFunctionVirtualizationOrchestrator=nfvo1"
    And   Execute secadm command: "credentials update --nodecliusername nodeCliNewUser -n NetworkFunctionVirtualizationOrchestrator=nfvo1"
    When  Execute secadm command: "credentials get --nodelist NetworkFunctionVirtualizationOrchestrator=nfvo1"
    Then  Command execution completed with message containing: "value:nodeCliUserName:nodeCliNewUser;"

  Scenario: Create Credentials for a NetworkElement
    Given A NetworkElement Node named: "ne1"
    When Execute secadm command: "credentials create --rootusername u1 --rootuserpassword pw1 --secureusername u2 --secureuserpassword pw2 --normalusername u3 --normaluserpassword pw3 -n ne1"
    Then Command execution completed with message containing: "All credentials were created successfully"

  Scenario: Create Credentials for a NFVO
    Given An NFVO Node named: "nfvo1"
    When Execute secadm command: "credentials create --secureusername someUser --secureuserpassword somePassword -n NetworkFunctionVirtualizationOrchestrator=nfvo1"
    Then Command execution completed with message containing: "All credentials were created successfully"

  Scenario: Get Credentials for a NFVO
    Given An NFVO Node named: "nfvo1"
    And   Execute secadm command: "credentials create --secureusername someUser --secureuserpassword somePassword -n NetworkFunctionVirtualizationOrchestrator=nfvo1"
    When  Execute secadm command: "credentials get --nodelist NetworkFunctionVirtualizationOrchestrator=nfvo1"
    Then  Command execution completed with message containing: "value:nfvo1;"
    Then  Command execution completed with message containing: "value:secureUserName:someUser;"

  Scenario: Update Credentials for a NFVO
    Given An NFVO Node named: "nfvo1"
    And   Execute secadm command: "credentials create --secureusername someUser --secureuserpassword somePassword -n NetworkFunctionVirtualizationOrchestrator=nfvo1"
    And   Execute secadm command: "credentials update --secureusername newUserName -n NetworkFunctionVirtualizationOrchestrator=nfvo1"
    When  Execute secadm command: "credentials get --nodelist NetworkFunctionVirtualizationOrchestrator=nfvo1"
    Then  Command execution completed with message containing: "value:secureUserName:newUserName;"

  Scenario: Invalid Credentials command
    When Execute secadm command: "credentials invalid command"
    Then Command execution failed