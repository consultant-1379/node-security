@NtpRemove

Feature: NTP Remove

  Background:
    Given A user named: "Administrator"
    And No dangling network elements

  Scenario: Node validation for NTP remove use case for unsupported node release version.
    Given A network element: "dg2ERBS01" and sync status: "true"
    When Perform ntp remove on the node "dg2ERBS01" using secadm command
    Then Verify that ntp remove on the network element "dg2ERBS01" failed with message "Node does not have supportedKeyAlgorithm attribute under Ntp MO"

  Scenario: Command validation for NTP remove use case
    Given A network element : "ERBS01"
    When Perform ntp remove on the node "ERBS01" using invalid secadm command
    Then Verify that ntp remove on the network element "ERBS01" failed with message "Command syntax error"

   Scenario: NTP remove workflow completed
    Given A network element: "ERBS01" with ossModelIdentity: "19.Q4-J.4.252"
    When Perform ntp remove use-case on the node "ERBS01" using secadm command
    Then Verify that ntp remove on the network element "ERBS01" passed with message "ERROR"