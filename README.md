


## The job-boards project
### The project directory structure
The project has build scripts for both Maven and Gradle, and follows the standard directory structure used in most Serenity projects:
```Gherkin
.
├── README.md
├── azure-pipelines.yml
├── build
│   ├── classes
│   │   └── java
│   │       └── test
│   ├── generated
│   │   └── sources
│   │       ├── annotationProcessor
│   │       │   └── java
│   │       │       └── test
│   │       └── headers
│   │           └── java
│   │               └── test
│   ├── libs
│   │   └── jobs-board-test-automation.jar
│   ├── resources
│   │   └── test
│   │       ├── features
│   │       │   └── addJob.feature
│   │       ├── logback-test.xml
│   │       └── serenity.conf
│   └── tmp
│       ├── compileTestJava
│       │   └── source-classes-mapping.txt
│       └── jar
│           └── MANIFEST.MF
├── build.gradle
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── hiptest-publisher.conf
├── jobs-board-test-automation.iml
├── pom.xml
├── serenity.properties
├── serenity.sh
├── src
│   ├── main
│   │   └── assembly
│   │       └── zip.xml
│   └── test
│       ├── java
│       │   └── demo
│       │       ├── CucumberTestSuite.java
│       │       ├── pageobjects
│       │       │   ├── AddJobPage.java
│       │       │   └── NavigationPage.java
│       │       ├── stepdefinitions
│       │       │   └── AddJobStepDefinitions.java
│       │       └── tasks
│       │           └── Add.java
│       └── resources
│           ├── features
│           │   └── addJob.feature
│           ├── logback-test.xml
│           └── serenity.conf
└── templates
    └── gherkin
        ├── _scenario.hbs
        ├── _scenario_outline_title.hbs
        └── dataset.hbs


```

This project assumes that you have the latest version of Chrome  installed.

## The sample scenario
Both variations of the sample project uses the sample Cucumber scenario. In this scenario, Sergey (who likes to search for stuff) is performing a search on the DuckDuckGo search engine:

```Gherkin
 Scenario Outline: add new job
    In order to add new job
    As a job seeker James
    James want to add new job
    When James add a new job with name "<name>" duration "<duration>" and "<date>"
    Then he is able to see the new job added
```


The glue code for this scenario looks this this:

```java
      @Given("^(?:.*) is at the job board$")
        public void jamesIsAtTheJobBoard() {
            theActorCalled("james").attemptsTo(Open.browserOn().the(navigationPage));
        }
        
        @When("^(?:.*) add a new job with name \"([^\"]*)\" duration \"([^\"]*)\" and \"([^\"]*)\"")
        public void jamesAddANewJobWithNameDurationAnd(String name, String duration, String date) {
            theActorInTheSpotlight().attemptsTo(Click.on(NavigationPage.ADD_JOB));
    //        theActorInTheSpotlight().attemptsTo(Add.selectJobDuration(duration));
    //        theActorInTheSpotlight().attemptsTo(Add.jobDate(date));
    
        }
        
        @Then("^he is able to see the new job added$")
        public void heIsAbleToSeeTheNewJobAdded() {
           
        }
```

### Lean Page Objects and Action Classes
The glue code shown above uses Serenity step libraries as _action classes_ to make the tests easier to read, and to improve maintainability.

These classes are declared using the Serenity `@Steps` annotation, shown below:
```java
    @Steps
    NavigationPage navigationPage;
```

The `@Steps`annotation tells Serenity to create a new instance of the class, and inject any other steps or page objects that this instance might need. 

Each action class models a particular facet of user behaviour: navigating to a particular page, performing a search, or retrieving the results of a search. These classes are designed to be small and self-contained, which makes them more stable and easier to maintain.

The `Add` class is an example of a very simple action class. In a larger application, it might have some other methods related to high level navigation, 
```

It does this using a standard Serenity Page Object. Page Objects are often very minimal, storing just the URL of the page itself:
```java
@DefaultUrl("https://brave-glacier-056a08800.azurestaticapps.net/")
public class NavigationPage extends PageObject {
    public static final Target LIST_OF_JOBS = Target.the("list of jobs").
            locatedBy("css:a[data-qa='listOfJobs']");

    public static final Target ADD_JOB = Target.the("add job").
            locatedBy("css:a[data-qa='addJob']");

}
```


The Screenplay DSL is rich and flexible, and well suited to teams working on large test automation projects with many team members, and who are reasonably comfortable with Java and design patterns. The Lean Page Objects/Action Classes approach proposes a gentler learning curve, but still provides significant advantages in terms of maintainability and reusability.

## Executing the tests
To run the sample project, you can either just run the `CucumberTestSuite` test runner class, or run either `mvn verify` or `gradle test` from the command line.

By default, the tests will run using Chrome. You can run them in Firefox by overriding the `driver` system property, e.g.
```json
$ mvn clean verify -Ddriver=firefox
```
Or 
```json
$ gradle clean test -Pdriver=firefox
```

The test results will be recorded in the `target/site/serenity` directory.

## Simplified WebDriver configuration and other Serenity extras
The sample projects both use some Serenity features which make configuring the tests easier. In particular, Serenity uses the `serenity.conf` file in the `src/test/resources` directory to configure test execution options.  
### Webdriver configuration
The WebDriver configuration is managed entirely from this file, as illustrated below:
```java
webdriver {
    driver = chrome
}
headless.mode = true

chrome.switches="""--start-maximized;--test-type;--no-sandbox;--ignore-certificate-errors;
                   --disable-popup-blocking;--disable-default-apps;--disable-extensions-file-access-check;
                   --incognito;--disable-infobars,--disable-gpu"""

```

### Environment-specific configurations
We can also configure environment-specific properties and options, so that the tests can be run in different environments. Here, we configure three environments, __dev__, _staging_ and _prod_, with different starting URLs for each:
```json
environments {
  dev {
    webdriver.base.url = "https://brave-glacier-056a08800.azurestaticapps.net/"
  }
  staging {
    webdriver.base.url = "https://brave-glacier-056a08800.azurestaticapps.net/"
  }
  prod {
    webdriver.base.url = "https://brave-glacier-056a08800.azurestaticapps.net/"
  }
}
```
  
You use the `environment` system property to determine which environment to run against. For example to run the tests in the staging environment, you could run:
```json
$ mvn clean verify -Denvironment=staging
```

- [serenityBDD-cucumber-starter](https://github.com/serenity-bdd/serenity-cucumber-starter) 

![Link an image](https://docs.microsoft.com/learn/azure-devops/shared/media/mara.png)

[Link to Microsoft Learn MarkDown](https://docs.microsoft.com/en-gb/learn/modules/communicate-using-markdown/2-what-is-markdown)