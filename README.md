

# Getting started with Serenity and Cucumber

Serenity BDD is a library that makes it easier to write high quality automated acceptance tests, with powerful reporting and living documentation features. It has strong support for both web testing with Selenium, and API testing using RestAssured. 

Serenity strongly encourages good test automation design, and supports several design patterns, including classic Page Objects, the newer Lean Page Objects/ Action Classes approach, and the more sophisticated and flexible Screenplay pattern.

The latest version of Serenity supports Cucumber 5.5.

## The job-boards project
The best place to start with Serenity and Cucumber is to clone or download the job-boards project on Github ([https://github.com/serenity-bdd/serenity-cucumber-gomo](https://github.com/serenity-bdd/serenity-cucumber-starter)). This project gives you a basic project setup, along with some sample tests and supporting classes. There are two versions to choose from. The master branch uses a more classic approach, using action classes and lightweight page objects, whereas the **[screenplay](https://github.com/serenity-bdd/serenity-cucumber-starter/tree/screenplay)** branch shows the same sample test implemented using Screenplay.

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

This project assumes that you have the latest version of Chrome (83) installed.

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

The `NavigateTo` class is an example of a very simple action class. In a larger application, it might have some other methods related to high level navigation, but in our sample project, it just needs to open the DuckDuckGo home page:
```java
public class NavigateTo {

    DuckDuckGoHomePage duckDuckGoHomePage;

    @Step("Open the DuckDuckGo home page")
    public void theDuckDuckGoHomePage() {
        duckDuckGoHomePage.open();
    }
}
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

The second class, `SearchFor`, is an interaction class. It needs to interact with the web page, and to enable this, we make the class extend the Serenity `UIInteractionSteps`. This gives the class full access to the powerful Serenity WebDriver API, including the `$()` method used below, which locates a web element using a `By` locator or an XPath or CSS expression:
```java
public class SearchFor extends UIInteractionSteps {

    @Step("Search for term {0}")
    public void term(String term) {
        $(SearchForm.SEARCH_FIELD).clear();
        $(SearchForm.SEARCH_FIELD).type(term);
        $(SearchForm.SEARCH_BUTTON).click();
    }
} 
```

The `SearchForm` class is typical of a light-weight Page Object: it is responsible uniquely for locating elements on the page, and it does this by defining locators or occasionally by resolving web elements dynamically. 
```java
class SearchForm {
    static By SEARCH_FIELD = By.cssSelector(".js-search-input");
    static By SEARCH_BUTTON = By.cssSelector(".js-search-button");
}
```

The last step library class used in the step definition code is the `SearchResult` class. The job of this class is to query the web page, and retrieve a list of search results that we can use in the AssertJ assertion at the end of the test. This class also extends `UIInteractionSteps` and 
```java
public class SearchResult extends UIInteractionSteps {
    public List<String> titles() {
        return findAll(SearchResultList.RESULT_TITLES)
                .stream()
                .map(WebElementFacade::getTextContent)
                .collect(Collectors.toList());
    }
}
```

The `SearchResultList` class is a lean Page Object that locates the search result titles on the results page:
```java
class SearchResultList {
    static By RESULT_TITLES = By.cssSelector(".result__title");
}
```

The main advantage of the approach used in this example is not in the lines of code written, although Serenity does reduce a lot of the boilerplate code that you would normally need to write in a web test. The real advantage is in the use of many small, stable classes, each of which focuses on a single job. This application of the _Single Responsibility Principle_ goes a long way to making the test code more stable, easier to understand, and easier to maintain.

## The Screenplay project
If you prefer to use the Screenplay pattern, or want to try it out, check out the _screenplay_ branch instead of the _master_ branch. In this version of the gomo project, the same scenario is implemented using the Screenplay pattern. 

The Screenplay pattern describes tests in terms of actors and the tasks they perform. Tasks are represented as objects performed by an actor, rather than methods. This makes them more flexible and composable, at the cost of being a bit more wordy. Here is an example:
```java
    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @Given("^(.*) is on the DuckDuckGo home page")
    public void on_the_DuckDuckGo_home_page(String actor) {
        theActorCalled(actor).attemptsTo(         NavigateTo.theDuckDuckGoHomePage()     );
    }

    @When("she/he searches for {string}")
    public void search_for(String term) {
        theActorInTheSpotlight().attemptsTo(
             SearchFor.term(term)      );
    }

    @Then("all the result titles should contain the word {string}")
    public void all_the_result_titles_should_contain_the_word(String term) {
        theActorInTheSpotlight().should(
                seeThat("search result titles",
                        SearchResult.titles(),                     hasSize(greaterThan(0))),
                seeThat("search result titles",
                        SearchResult.titles(),                     everyItem(containsIgnoringCase(term)))
        );
    }
```

In both approaches, the Page Objects very close or identical. The differences are mainly in the action classes. Screenplay classes emphasise reusable components and a very readable declarative style, whereas Lean Page Objects and Action Classes opt for a more imperative style.

The `NavigateTo` class performs the same role as it’s equivalent in the Lean Page Object/Action Class version, and looks quite similar:
```java
public class NavigateTo  {

    public static Performable theDuckDuckGoHomePage() {
        return Task.where("{0} opens the DuckDuckGo home page",
                Open.browserOn().the(DuckDuckGoHomePage.class)
        );
    }
} 
```

The `SearchFor` class is also similar: it is shown below:
```java
public class SearchFor {

    public static Performable term(String term) {
        return Task.where("{0} attempts to search for #term",
                Clear.field(SearchForm.SEARCH_FIELD),             Enter.theValue(term).into(SearchForm.SEARCH_FIELD),
                Click.on(SearchForm.SEARCH_BUTTON)
        ).with("term").of(term);
    }
}
```

In Screenplay, there is a clear distinction between actions (which change the system state) and questions (which read the system state). In Screenplay, we fetch the search results using a Question class, like this:
```java
public class SearchResult {
    public static Question<List<String>> titles() {
        return actor ->  
                 TextContent.of(SearchResultList.RESULT_TITLES)
                            .viewedBy(actor)
                            .asList();
    }
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

The project also bundles some of the WebDriver binaries that you need to run Selenium tests in the `src/test/resources/webdriver` directories. These binaries are configured in the `drivers` section of the `serenity.conf` config file:
```json
drivers {
  windows {
    webdriver.chrome.driver = "src/test/resources/webdriver/windows/chromedriver.exe"
    webdriver.gecko.driver = "src/test/resources/webdriver/windows/geckodriver.exe"
  }
  mac {
    webdriver.chrome.driver = "src/test/resources/webdriver/mac/chromedriver"
    webdriver.gecko.driver = "src/test/resources/webdriver/mac/geckodriver"
  }
  linux {
    webdriver.chrome.driver = "src/test/resources/webdriver/linux/chromedriver"
    webdriver.gecko.driver = "src/test/resources/webdriver/linux/geckodriver"
  }
}
```
This configuration means that development machines and build servers do not need to have a particular version of the WebDriver drivers installed for the tests to run correctly.

### Environment-specific configurations
We can also configure environment-specific properties and options, so that the tests can be run in different environments. Here, we configure three environments, __dev__, _staging_ and _prod_, with different starting URLs for each:
```json
environments {
  default {
    webdriver.base.url = "https://duckduckgo.com"
  }
  dev {
    webdriver.base.url = "https://duckduckgo.com/dev"
  }
  staging {
    webdriver.base.url = "https://duckduckgo.com/staging"
  }
  prod {
    webdriver.base.url = "https://duckduckgo.com/prod"
  }
}
```
  
You use the `environment` system property to determine which environment to run against. For example to run the tests in the staging environment, you could run:
```json
$ mvn clean verify -Denvironment=staging
```

