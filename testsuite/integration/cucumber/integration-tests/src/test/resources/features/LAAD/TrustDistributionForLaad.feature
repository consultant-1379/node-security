@TrustLaad
Feature: Trust distribution for LAAD

  Background: 
    Given A user named: "Administrator"
    And No dangling network elements

  Scenario: Distributes the relevant CA certificates with trust category LAAD to a list of nodes
    Given A CPP network element:  "RNC04"
    When Distribute certificates with LAAD trust category to the node "RNC04" using secadm command
    Then Verify that trust distribution job should be completed for the node "RNC04" with job status "COMPLETED"
    And Trust get with category LAAD on the node "RNC04" should return CA certificate with CN "CN=ENMInfrastructureCA"

  Scenario: Removes all the certificates with trustcategory LAAD with a defined CA_NAME to a list of nodes
    Given A CPP network element:  "RNC04"
    When Remove certificates with LAAD trust category to the node "RNC04" using secadm command
    Then Verify that trust remove job should be completed for the node  "RNC04" with the work flow status "SUCCESS"
