package core;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;

public class BaseTests implements TestWatcher {
    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected static Alert alert;
    protected static String device = "desktop";

    @BeforeAll
    public static void init() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10)); // how long we will wait the page
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(4)); // how long we will wait the element
        BasePage.setDriver(driver);
    }

    @BeforeEach
    public void setUp() {
        if (device.equals("desktop")) {
            driver.manage().window().maximize();
            driver.get("https://the-internet.herokuapp.com/");
        } else if (device.equals("mobile")) {
            driver.manage().window().setSize(new Dimension(360, 740));
            driver.get("https://www.selenium.dev/");
        }
    }

    @AfterEach
    public void tearDown() {
        saveScreenshot(device);
    }

    @AfterAll
    public static void end() {
        //driver.close(); // tab in browser
        driver.quit(); // process in system
    }

    public boolean isElementDisplayed(WebElement webElement) {
        try {
            webElement.isDisplayed();
            return true;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public boolean isElementReady(WebElement webElement) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(4));
        wait.until(ExpectedConditions.elementToBeClickable(webElement));
        return true;
    }

    public Alert waitForAlertReady() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(4));
        alert = wait.until(ExpectedConditions.alertIsPresent());
        return alert;
    }

    public void mouseOverElement(WebElement webElement) {
        if (isElementReady(webElement)) {
            new Actions(driver).moveToElement(webElement).perform();
        }
    }

    public void scrollToElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public void saveScreenshot() {
        Allure.getLifecycle().addAttachment("Screenshot", "image/png", "png",
                ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
    }

    public void saveScreenshot(String name) {
        Allure.getLifecycle().addAttachment(name, "image/png", "png",
                ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
    }

    public void switchToNewWindow() {
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(1));
    }

    public void switchToDefaultWindow() {
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(0));
    }
}