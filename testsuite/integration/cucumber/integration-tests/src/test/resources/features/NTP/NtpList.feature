@NtpList
Feature: NtpList

  Background:
    Given A user named: "Administrator"

 Scenario: Ntp List for the given network element
    Given No dangling network elements
    Given A network element: "RNC01" with ossModelIdentity: "19.Q4-J.4.252"
    When Perform Ntp List on the node "RNC01" using secadm command
    Then Verify Ntp List on the node "RNC01" passed with output as "command executed successfully"