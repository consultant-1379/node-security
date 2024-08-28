@GdprRest

Feature: GdprRest

    @GdprRestAnonymization
    Scenario: The Gdpr rest response failure due unavailable global.properties
        Given Http Header for Gdpr Rest was built
         When The Rest Post is sent
         Then The Rest response is 406

    @GdprRestAnonymizationWithSalt
    Scenario: The Gdpr rest reposnse is ok and hashed filename was returned
        Given Http Header for Gdpr Rest was built
         When The Rest Post is sent with salt overwritten
         Then The Rest response is 200
