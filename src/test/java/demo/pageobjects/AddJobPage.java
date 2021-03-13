package demo.pageobjects;


import net.serenitybdd.core.annotations.findby.By;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.DefaultUrl;

public class AddJobPage extends PageObject {
    public static final Target JOB_NAME = Target.the("name of job").
            locatedBy("css:input[data-qa='jobName']");

    public static final Target JOB_DURATION = Target.the("duration of job").
            locatedBy("css:select[data-qa='jobDuration']");

    public static final Target SELECT_DURATION_DROPDOWN(String duration) {
        String durationSelectedLocator = String.format("css:option[data-qa='%s']", duration);
        return Target.the("duration dropdown selected").
                locatedBy(durationSelectedLocator);
    }

    public static final Target JOB_DATE = Target.the("date of the job").
            locatedBy("css:input[data-qa='jobDate']");

     public static final Target SUBMIT = Target.the("submit button").
            locatedBy("css:button[data-qa='jobSubmit']");
}

