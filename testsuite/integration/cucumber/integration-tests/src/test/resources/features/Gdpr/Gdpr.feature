@Gdpr

Feature: Gdpr

    @GdprAnonymization
    Scenario: The Gdpr service returns hashed filename
        Given The Gdpr service is available
         When The hashed filename is returned
         Then The hashed filename is null

    @GdprAnonymizationWithSalt
    Scenario: The Gdpr service returns hashed filename overwriting salt
        Given The Gdpr service is available
         When The hashed filename after overwritten salt is returned
         Then The hashed filename is not null
