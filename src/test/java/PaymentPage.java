import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

class PaymentPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By paymentFrame = By.xpath("//iframe[@class='bepaid-iframe']");
    private final By sumDisplay = By.xpath("//div[@class='pay-description__cost']");
    private final By phoneDisplay = By.xpath("//div[@class='pay-description__text']");
    private final By cardNumberInput = By.xpath("//label[@class='ng-tns-c2312288139-1 ng-star-inserted']");
    private final By expiryDateInput = By.xpath("//label[@class='ng-tns-c2312288139-4 ng-star-inserted']");
    private final By cvcInput = By.xpath("//label[@class='ng-tns-c2312288139-5 ng-star-inserted']");
    private final By paymentSystemIcons = By.cssSelector("img[src*='system']:not([style*='opacity: 0'])");
    private final By submitButton = By.xpath("//button[@class='colored disabled']");

    public PaymentPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private void switchToPaymentFrame() {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(paymentFrame));
    }
    private void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    public boolean isPaymentFrameDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(paymentFrame)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getDisplayedSum() {
        switchToPaymentFrame();
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(sumDisplay)).getText();
        } finally {
            switchToDefaultContent();
        }
    }

    public String getDisplayedPhoneNumber() {
        switchToPaymentFrame();
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(phoneDisplay)).getText();
        } finally {
            switchToDefaultContent();
        }
    }

    public String getCardNumberLabel() {
        switchToPaymentFrame();
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(cardNumberInput)).getText();
        } finally {
            switchToDefaultContent();
        }
    }

    public String getExpiryDateLabel() {
        switchToPaymentFrame();
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(expiryDateInput)).getText();
        } finally {
            switchToDefaultContent();
        }
    }

    public String getCvcLabel() {
        switchToPaymentFrame();
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(cvcInput)).getText();
        } finally {
            switchToDefaultContent();
        }
    }

    public int getPaymentSystemsCount() {
        switchToPaymentFrame();
        try {
            return driver.findElements(paymentSystemIcons).size();
        } finally {
            switchToDefaultContent();
        }
    }

    public String getSubmitButtonText() {
        switchToPaymentFrame();
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(submitButton)).getText();
        } finally {
            switchToDefaultContent();
        }
    }
}