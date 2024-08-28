@ExtCATrustDistribute
Feature: External CA trust distribution

  Background:
    Given A user named: "Administrator"
    And No dangling network elements

  Scenario: Trust distribution on network element using legacy command
    Given A network element: "dg2ERBS01" and sync status: "true"
    When Execute trust distribution on the network element "dg2ERBS01" using legacy secadm command
    Then Verify the status of Trust distribution on the network element "dg2ERBS01" with job status "COMPLETED"

  Scenario: External CA trust distribution on network element
    Given A network element: "dg2ERBS01" and sync status: "true"
    When Execute trust distribution on the network element "dg2ERBS01" using xml file and extca option
    Then Verify the status of Trust distribution on the network element "dg2ERBS01" with job status "COMPLETED"

  Scenario: External CA trust distribution on multiple network elements
    Given A network element: "dg2ERBS01" and sync status: "true"
    And Another network element: "dg2ERBS02" and sync status: "true"
    When Execute trust distribution on multiple network element "dg2ERBS01" and "dg2ERBS02" using xml file and extca option
    Then Verify the status of Trust distribution on the network element "dg2ERBS01" and "dg2ERBS02" with job status "COMPLETED"

  Scenario: Trust distribution on network element using xml without extca option invalid command
    Given A network element: "dg2ERBS01" and sync status: "true"
    When Execute trust distribution on the network element "dg2ERBS01" with xml and without extca option
    Then Verify that Trust distribution on the network element "dg2ERBS01" fails with message "xml file and --extca is mandatory for External CA trust distribution"

  Scenario: Trust distribution on network element using legacy command with extca option
    Given A network element: "dg2ERBS01" and sync status: "true"
    When Execute trust distribution on the network element "dg2ERBS01" with extca option and without xml
    Then Verify that Trust distribution on the network element "dg2ERBS01" fails with message "xml file and --extca is mandatory for External CA trust distribution"

  Scenario: External CA trust distribution on unsynchronized network element
    Given A network element: "dg2ERBS01" and sync status: "false"
    When Execute trust distribution on unsynchronized network element "dg2ERBS01"
    Then Verify that Trust distribution on the network element "dg2ERBS01" fails with message "The node specified is not synchronized"