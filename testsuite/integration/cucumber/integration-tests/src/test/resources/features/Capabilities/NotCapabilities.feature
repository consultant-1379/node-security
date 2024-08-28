@Capabilities @ScenariosNotForJenkins

Feature: Not Capabilities

    Background:
        Given The NSCS model service is available
          And The NSCS mock capability model is not used

    Scenario Outline: The NSCS model service should return all expected target parameters
        Given The expected values are retrieved from JSON file "<TargetParameter>"
         When The actual values for "<TargetParameter>" are retrieved for all targets in JSON file "NscsTargetDefinitions" from NSCS capability service invoking method "<MethodName>"
          And The actual values are saved in a temporary JSON file "<TargetParameter>"
         Then All expected values for "<TargetParameter>" are equal to the actual ones
        Examples:
        |TargetParameter                   |MethodName                        |
        |RootMoInfo                        |getRootMoType                     |
        |IsKSandEMSupported                |isKSandEMSupported                |
        |IsCertificateAuthorityDnSupported |isCertificateAuthorityDnSupported |
