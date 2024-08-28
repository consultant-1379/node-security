@ExtCACertificateReissue
Feature: External CA certificate reissue

  Background:
    Given A user named: "Administrator"
    And No dangling network elements

  Scenario: Reissue certificate to the network element when issued with ENM CA
    Given A network element: "dg2ERBS01" and sync status: "true"
    And  Certificate enrollment on the network element "dg2ERBS01" using legacy secadm command finishes with job status "COMPLETED"
    When Certificate reissue is executed on Network Element "dg2ERBS01"
    Then Verify the status of certificate reissue on the network element "dg2ERBS01" with job status "COMPLETED"

  Scenario: Reissue certificate to the network element when issued with External CA
    Given A network element: "dg2ERBS01" and sync status: "true"
    And  Certificate enrollment on the network element "dg2ERBS01" using external CA issue command finishes with job status "COMPLETED"
    When Certificate reissue is executed on Network Element "dg2ERBS01"
    Then Verify the status of certificate reissue on the network element "dg2ERBS01" with job status "COMPLETED"

  Scenario: Reissue certificate to the network element when issued with External CA
    Given A network element: "dg2ERBS01" and sync status: "true"
    And Another network element: "dg2ERBS02" and sync status: "true"
    And  Certificate enrollment on the network elements "dg2ERBS01" and "dg2ERBS02" using external CA issue command finishes with job status "COMPLETED"
    When Certificate reissue is executed on Network Elements "dg2ERBS01" and "dg2ERBS02"
    Then Verify the status of certificate reissue on the network elements "dg2ERBS01" and "dg2ERBS02" with job status "COMPLETED"
