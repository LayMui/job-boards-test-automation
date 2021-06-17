package demo.util;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import demo.pageobjects.NavigationPage;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.pages.PageObjects;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.WebDriver;

public class ApplitoolsEyes {

    @Steps
    NavigationPage navigationPage;

    private void setUp(Eyes eyes) {
        // Initialize the eyes configuration.
        Configuration config = new Configuration();
        // Add this configuration if your tested page includes fixed elements.
        //config.setStitchMode(StitchMode.CSS);
        // You can get your api key from the Applitools dashboard
        // https://applitools.com/docs/api/eyes-sdk/classes-gen/class_eyes/method-eyes-setapikey-selenium-java.html
        // set new batch

        // Add browsers with different viewports
        config.setBatch(new BatchInfo("Job batch"));
        config.addBrowser(800, 600, BrowserType.CHROME);
        config.addBrowser(700, 500, BrowserType.FIREFOX);
        config.addBrowser(1600, 1200, BrowserType.IE_11);
        config.addBrowser(1024, 768, BrowserType.EDGE_CHROMIUM);
        config.addBrowser(800, 600, BrowserType.SAFARI);
        // Add mobile emulation devices in Portrait mode
        config.addDeviceEmulation(DeviceName.iPhone_X, ScreenOrientation.PORTRAIT);
        config.addDeviceEmulation(DeviceName.Pixel_2, ScreenOrientation.PORTRAIT);
        // set the configuration to eyes
        eyes.setConfiguration(config);
    }

    public void getTestResultFromApplitools(VisualGridRunner runner, Eyes eyes) {
        if (System.getenv("APPLITOOLS_API_KEY") != null) {
            eyes.closeAsync();
            // Get test results from Eyes
            TestResultsSummary myTestResults = runner.getAllTestResults(false);
            // Push test results into Serenity report
            Serenity.recordReportData().withTitle("Applitools Report").andContents(myTestResults.toString());
        }
    }

    public void openEyesAndCheck(Eyes eyes, String appName, String testName, String pageName) {
        if (System.getenv("APPLITOOLS_API_KEY") != null) {
            eyes.open(((WebDriverFacade) navigationPage.getDriver()).getProxiedDriver(),
                    appName, testName);
            eyes.check(Target.window().fully().withName(pageName));

        }
    }

    public void eyesCheckPoint(Eyes eyes, String pageName) {
        if (System.getenv("APPLITOOLS_API_KEY") != null) {
            eyes.check(Target.window().fully().withName(pageName));
            eyes.closeAsync();
        }
    }
}