@NtpConfigure
Feature: NtpConfigure

  Background:
    Given A user named: "Administrator"
    And No dangling network elements

   Scenario: Ntp Configure for the given network element with Error message
    Given A network element: "RNC04" with ossModelIdentity: "19.Q4-J.4.252"
    When Perform Ntp Configure on the node "RNC04" using secadm command
    Then Verify the Ntp Configure job on the node "RNC04" with the work flow status "Get NTP Key Data failed" and job status "ERROR"