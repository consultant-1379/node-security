@Laad
Feature: LAAD distribution

  Background:
    Given A user named: "Administrator"
    And No dangling network elements

  Scenario Outline: Generation and Distribution of Laad Files successfully for the give network element
    Given A CPP network element:  "<neName>"
    When Perform LAAD distribution to the node "<neName>" using secadm command
    Then Verify that LAAD distribution job should be completed for the node "<neName>" with the work flow status "<wfStatus>" and job status "<jobStatus>"

    Examples:
      | neName                     | wfStatus               | jobStatus |
      | RNC01NeWithExceedMaxUsers  | Get LAAD failed        | ERROR     |
      | RNC02NeWithNoSecurityUsers | Get LAAD failed        | ERROR     |
      | RNC03NeWithNoTargetGroup   | Get LAAD failed        | ERROR     |
      | RNC04LaadDistribution      | [Get LAAD ... ongoing] | RUNNING   |
