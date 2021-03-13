package demo.pageobjects;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.targets.Target;


public class NavigationPage extends PageObject {
    public static final Target LIST_OF_JOBS = Target.the("list of jobs").
            locatedBy("css:a[data-qa='listOfJobs']");

    public static final Target ADD_JOB = Target.the("add job").
            locatedBy("css:a[data-qa='addJob']");

            
}
