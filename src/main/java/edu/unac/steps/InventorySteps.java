package edu.unac.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import java.time.Duration;
import static java.lang.Thread.sleep;

public class InventorySteps {
    private ChromeDriver driver;

    @Before
    public void setUp(){
        System.setProperty("webdriver.chrome.driver",
                System.getProperty("user.dir") +
                        "/src/main/java/edu/unac/drivers/chromedriver.exe");//ADD YOUR DRIVER
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        chromeOptions.setBinary("C:\\Users\\user\\Downloads\\FuncionTesting\\chrome-win64\\chrome.exe");
        driver = new ChromeDriver(chromeOptions);
        driver.manage().window().maximize();
        driver.get("C:\\Users\\user\\Downloads\\InventoryManagementApplication\\InventoryManagementApplication\\Frontend\\index.html");
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Given("a device is registered with name Projector, type Multimedia, and location Room101")
    public void a_device_is_registered_with_name_projector_type_multimedia_and_location_room() {
        driver.findElement(By.id("deviceName")).sendKeys("Projector");
        driver.findElement(By.id("deviceType")).sendKeys("Multimedia");
        driver.findElement(By.id("deviceLocation")).sendKeys("Room101");

        driver.findElement(By.id("addDeviceBtn")).click();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Given("the device is currently loaned to user Alice")
    public void the_device_is_currently_loaned_to_user_alice() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("loanBorrowedBy")));
        driver.findElement(By.id("loanBorrowedBy")).sendKeys("Alice");
        driver.findElement(By.id("addLoanBtn")).click();
        // Esperar que se procese la acción (puedes esperar algún mensaje o cambio visible)

    }

    @When("the user attempts to delete the device")
    public void the_user_attempts_to_delete_the_device() throws InterruptedException {
        sleep(1000);
        driver.findElement(By.xpath("//*[@id=\"devicesTableBody\"]/tr/td[6]/button")).click();
        sleep(5000);
    }

    @Then("the device should not be deleted")
    public void the_device_should_not_be_deleted() throws InterruptedException {
        sleep(1000);
        driver.findElement(By.xpath("//*[@id=\"devicesTableBody\"]/tr/td[2]")).getText();
        Assert.assertEquals(driver.findElement(By.xpath("//*[@id=\"devicesTableBody\"]/tr/td[2]")).getText(), "Projector");

    }

    @Then("an error message Failed to delete device should be displayed")
    public void an_error_message_failed_to_delete_device_should_be_displayed() throws InterruptedException {
        sleep(1000);
        driver.findElement(By.id("deviceMessage")).isDisplayed();
        String message = driver.findElement(By.id("deviceMessage")).getText();
        Assert.assertEquals(message, "Failed to delete device");
    }
}
