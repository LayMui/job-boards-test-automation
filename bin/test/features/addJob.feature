Feature: Add Job
  In order to add a new job to the listing
  As a job seeker James
  James wants to able to add a job

  Background:
    Given James is at the job board
@web
  Scenario Outline: add new job
    In order to add new job
    As a job seeker James
    James want to add new job
    When James add a new job with name "<name>" duration "<duration>" and "<date>"
    Then he is able to see the new job added

    Examples:
      | name | duration | date |
      | iOS Developer | Freelance | 20/03/2021 |

