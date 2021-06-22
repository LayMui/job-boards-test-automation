package demo.stepdefinitions;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.FileLogger;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import demo.util.ApplitoolsEyes;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.serenitybdd.core.Serenity;
import demo.tasks.Add;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import demo.pageobjects.AddJobPage;
import demo.pageobjects.NavigationPage;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actions.SelectFromOptions;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.questions.WebElementQuestion;
import net.thucydides.core.annotations.Steps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;

public class AddJobStepDefinitions {

    private final Logger log = LoggerFactory.getLogger(AddJobStepDefinitions.class);

/*
**   Notice how we did not need to instantiate the Steps class NavigationPage
*    When you annotated a member variable of this class with the @Steps annotation,
*    Serenity BDD will automatically instantiate it for you.
*/
    @Steps
    NavigationPage navigationPage;


    VisualGridRunner runner;
    Eyes eyes;


    ApplitoolsEyes applitoolsEyes;

    @Before()
    public void setTheStage() {

        applitoolsEyes.openEyesAndCheck(eyes,"Jobs", "Jobs Ultra Grid",
           "Jobs Page");
        OnStage.setTheStage(new OnlineCast());
    }

    @After()
    public void drawTheCurtain() {
        OnStage.drawTheCurtain();
        applitoolsEyes.getTestResultFromApplitools(runner, eyes);
    }

    @Given("^(?:.*) is at the job board$")
    public void jamesIsAtTheJobBoard() {
        theActorCalled("james").attemptsTo(Open.browserOn().the(navigationPage));

        applitoolsEyes.openEyesAndCheck(eyes,"Job", "Job Ultra Grid",
               "Job Listing Page");
    }
    
    @When("^(?:.*) add a new job with name \"([^\"]*)\" duration \"([^\"]*)\" and \"([^\"]*)\"")
    public void jamesAddANewJobWithNameDurationAnd(String name, String duration, String date) {
         theActorInTheSpotlight().attemptsTo(Click.on(NavigationPage.ADD_JOB));
         theActorInTheSpotlight().attemptsTo(Add.jobName(name));
         theActorInTheSpotlight().attemptsTo
         (SelectFromOptions.byVisibleText(duration).from(AddJobPage.JOB_DURATION));
         theActorInTheSpotlight().attemptsTo(Add.jobDate(date));

        //  theActorInTheSpotlight()
        //  .should(eventually(seeThat(the(AddJobPage.SUBMIT), isClickable())));
         theActorInTheSpotlight().attemptsTo(Click.on(AddJobPage.SUBMIT));

    }
    
    @Then("^he is able to see the new job added$")
    public void heIsAbleToSeeTheNewJobAdded() {
        applitoolsEyes.eyesCheckPoint(eyes, "Add job page");
    }
}
