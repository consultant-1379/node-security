@ExtCACertificateEnrollment
Feature: External CA certificate enrollment

  Background:
    Given A user named: "Administrator"
    And No dangling network elements

  Scenario: Issue certificate to the network element using legacy command
    Given A network element: "dg2ERBS01" and sync status: "true"
    When Execute certificate enrollment on the network element "dg2ERBS01" using legacy secadm command
    Then Verify the status of certificate enrollment on the network element "dg2ERBS01" with job status "COMPLETED"

  Scenario: Issue certificate to the network element with extca option in command without TrustedCAInfo in the xml
    Given A network element: "dg2ERBS01" and sync status: "true"
    When Execute certificate enrollment on the network element "dg2ERBS01" using extca option and TrustedCAInfo "false"
    Then Verify the status of certificate enrollment on the network element "dg2ERBS01" with job status "COMPLETED"

  Scenario: Issue certificate to the network element with extca option in command with TrustedCAInfo in the xml
    Given A network element: "dg2ERBS01" and sync status: "true"
    When Execute certificate enrollment on the network element "dg2ERBS01" using extca option and TrustedCAInfo "true"
    Then Verify the status of certificate enrollment on the network element "dg2ERBS01" with job status "COMPLETED"

  Scenario: Issue certificate to multiple network elements with extca option in command with TrustedCAInfo in the xml
    Given A network element: "dg2ERBS01" and sync status: "true"
    And Another network element: "dg2ERBS02" and sync status: "true"
    When Execute certificate enrollment on the network elements "dg2ERBS01" and "dg2ERBS02" using extca option
    Then Verify the status of certificate enrollment on the network elements "dg2ERBS01" and "dg2ERBS02" with job status "COMPLETED"

  Scenario: Issue certificate to the network element using legacy command and new xml
    Given A network element: "dg2ERBS01" and sync status: "true"
    When Execute certificate enrollment on the network element "dg2ERBS01" using legacy secadm command and new xml
    Then Verify that certificate enrollment on the network element "dg2ERBS01" fails with message "Error 10000 : Unsupported command argument : ExternalCAEnrollmentInfo is not supported for command provided."

  Scenario: Issue certificate to the network element using new command and old xml
    Given A network element: "dg2ERBS01" and sync status: "true"
    When Execute certificate enrollment on the network element "dg2ERBS01" using new secadm command and old xml
    Then Verify that certificate enrollment on the network element "dg2ERBS01" fails with message "Error 10014 : Input xml validation with Schema failed"

 Scenario: Execute new issue command with new xml when network element does not exist
    When Execute certificate enrollment when network element does not exist
    Then Verify that certificate enrollment on the network element "dg2ERBS01" fails with message "Error 10004 : The NetworkElement specified does not exist"