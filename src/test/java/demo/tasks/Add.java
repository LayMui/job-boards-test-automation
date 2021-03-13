package demo.tasks;

import demo.pageobjects.AddJobPage;
import demo.pageobjects.NavigationPage;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.*;

public class Add {

    public static Performable jobName(String name) {
        return Task.where(
                "{0} attempts to enter job name #name",
                Clear.field(AddJobPage.JOB_NAME),
                Enter.theValue(name).into(AddJobPage.JOB_NAME))
                .with("name")
                .of(name);
    }

    public static Performable jobDate(String date) {
        return Task.where(
                "{0} attempts to enter job date #date",
                Click.on(AddJobPage.JOB_DATE),
                Enter.theValue(date).into(AddJobPage.JOB_DATE));

    }

}
