@HelloCucumber

Feature: HelloCucumber

  Scenario: First Test
      Given Build Test
      When Ready Test
      Then Execute Test

  Scenario: Second Test
      Given InjectUserManager
      When GetUsers
      Then CheckUsers

