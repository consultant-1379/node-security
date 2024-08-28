@Capabilities

Feature: Capabilities

    Background:
        Given The NSCS model service is available
          And The NSCS mock capability model is not used

    Scenario: The NSCS model service should return all expected NSCS capability definitions
        Given The expected NSCS capability definitions are retrieved from the JSON file "NscsCapabilityDefinitions"
         When The actual NSCS capability definitions are retrieved from the NSCS capability service
          And The actual NSCS capability definitions are saved in a temporary JSON file "NscsCapabilityDefinitions"
         Then All expected NSCS capability definitions are equal to the actual ones
# Uncomment following test step to check if new "unexpected" NSCS capabilities have been delivered
#          And All actual NSCS capability definitions are equal to the expected ones

    @ScenariosNotForJenkins
    Scenario: The NSCS model service should return all expected NSCS capability support definitions
        Given The expected NSCS capability support definitions are retrieved from the JSON file "NscsCapabilitySupportDefinitions"
         When The actual NSCS capability support definitions are retrieved for all expected targets read from JSON file "NscsTargetDefinitions"
          And The actual NSCS capability support definitions are saved in a temporary JSON file "NscsCapabilitySupportDefinitions"
         Then All expected NSCS capability support definitions are equal to the actual ones
# Uncomment following test step to check if new "unexpected" targets have been delivered
#          And All actual NSCS capability support definitions are equal to the expected ones
