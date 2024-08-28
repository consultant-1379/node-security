@SshkeyImport
Feature: Sshkey Import

  Background:
    Given A user named: "Administrator"
    And No dangling network elements

  Scenario: Sshkey Import on valid network element
    Given A vECE neType Node named: "VECE01"
    And Execute secadm command: "credentials create --secureusername u1 --secureuserpassword pw1 --normalusername u2 --normaluserpassword pw2 -n VECE01"
    When Execute sshkey import on the network element "VECE01" using sshprivatekey file and sshprivatekeyfile option
    Then Verify the response of sshkey import on the network element "VECE01"

  Scenario: Sshkey Import on network element which do not have secure credentials defined
    Given A vECE neType Node named: "VECE02"
    When Execute sshkey import on the network element "VECE02" using sshprivatekey file and sshprivatekeyfile option
    Then Verify the response of sshkey import on the network element "VECE02" which do not have secure credentials defined