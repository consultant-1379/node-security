@Capabilities @ScenariosNotForJenkins

Feature: Target Definitions

    Background:
        Given The NSCS model service is available
          And The NSCS mock capability model is not used

    Scenario: The NSCS model service should return all expected targets
        Given The expected targets are read from the JSON file "NscsTargetDefinitions"
         When The actual targets are retrieved from the NSCS capability service
          And The actual targets are saved in a temporary JSON file "NscsTargetDefinitions"
         Then All expected targets are equal to the actual ones
# Uncomment following test step to check if new "unexpected" targets have been delivered
#          And All actual targets are equal to the expected ones
