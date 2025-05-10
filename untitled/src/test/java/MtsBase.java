
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MtsBase {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final WebDriverWait shortWait;


    private final By cookieButton = By.xpath("//*[@id='cookie-agree']");
    private final By phoneInput = By.xpath("//*[@id='connection-phone']");
    private final By sumInput = By.xpath("//*[@id='connection-sum']");
    private final By emailInput = By.xpath("//*[@id='connection-email']");
    final By continueButton = By.xpath("//*[@id='pay-connection']/button");
    private final By homeInternetAccountInput = By.xpath("//input[@placeholder='Номер абонента']");
    private final By installmentAccountInput = By.xpath("//input[@placeholder='Номер счета на 44']");
    private final By debtAccountInput = By.xpath("//input[contains(@placeholder, 'Номер счета на 2073')]");
    private final By homeInternetTab = By.xpath("//*[@id='pay-section']/div/div/div[2]/section/div/div[1]/div[1]/div[2]/button/span[1]");
    private final By servicesTab = By.xpath("//span[text()='Услуги связи']");
    private final By installmentTab = By.xpath("//option[@value='Рассрочка']");
    private final By debtTab = By.xpath("//option[@value='Задолженность' and @data-open='pay-arrears']");
    private final By cardNumberInput = By.xpath("//input[@id='cc-number']");
    private final By cardExpiryInput = By.xpath("//input[@formcontrolname='expirationDate']");
    private final By cardCvvInput = By.xpath("//input[@formcontrolname='cvc']");
    private final By cardNameInput = By.xpath("//input[@placeholder='Имя держателя']");
    private final By modalPhone = By.xpath("//span[contains(., 'Номер:375297777777')]");
    private final By modalSum = By.xpath("//span[contains(., 'BYN')]");
    private final By modalButtonSum = By.xpath("//button[contains(@class, 'colored') and contains(., 'BYN')]");
    private final By paymentSystemIcons = By.xpath("//div[contains(@class, 'cards-brands__container')]");
    private final By visaIcon = By.xpath("//img[contains(@src, 'visa')]");
    private final By mastercardIcon = By.xpath("//img[contains(@src, 'mastercard')]");
    private final By belcardIcon = By.xpath("//img[contains(@src, 'belkart') or contains(@alt, 'Белкарт')]");


    public static final String TEST_PHONE = "(29)777-77-77";
    public static final String TEST_EMAIL = "kupikota@test.com";
    public static final String TEST_SUM = "100";


    public MtsBase(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));

    }

    public void open() {
        driver.get("https://www.mts.by/");
    }

    public void acceptCookies() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(cookieButton)).click();
        } catch (Exception e) {
            System.out.println("Cookie banner not found or already accepted: " + e.getMessage());
        }
    }


    public void enterPhoneNumber(String phone) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(phoneInput));
        element.clear();
        element.sendKeys(phone);
    }

    public void enterEmail(String email) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(emailInput));
        element.clear();
        element.sendKeys(email);
    }

    public void enterSum(String sum) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(sumInput));
        element.clear();
        element.sendKeys(sum);
    }

    public void clickContinue() {
        wait.until(ExpectedConditions.elementToBeClickable(continueButton)).click();
    }

    public void selectServicesTab() {
        wait.until(ExpectedConditions.elementToBeClickable(servicesTab)).click();
    }


    public void selectHomeInternetTab() {
        wait.until(ExpectedConditions.elementToBeClickable(homeInternetTab)).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(homeInternetAccountInput));
    }

    public void selectInstallmentTab() {
        wait.until(ExpectedConditions.elementToBeClickable(installmentTab)).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(installmentAccountInput));
    }

    public void selectDebtTab() {
        wait.until(ExpectedConditions.elementToBeClickable(debtTab)).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(debtAccountInput));
    }


    public String getEnterPhoneNumber() {
        WebElement phoneInputElement = wait.until(ExpectedConditions.presenceOfElementLocated(phoneInput));
        return phoneInputElement.getAttribute("value");
    }

    public String getEnteredSum() {
        WebElement phoneInputElement = wait.until(ExpectedConditions.presenceOfElementLocated(sumInput));
        return phoneInputElement.getAttribute("value");

    }

    public String getPhoneFieldPlaceholder() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(phoneInput))
                .getAttribute("placeholder");
    }

    public String getSumFieldPlaceholder() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(sumInput))
                .getAttribute("placeholder");
    }

    public String getEmailFieldPlaceholder() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(emailInput))
                .getAttribute("placeholder");
    }

    public String getAccountFieldPlaceholder() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(phoneInput))
                .getAttribute("placeholder");
    }

    public String getHomeInternetAccountPlaceholder() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(homeInternetAccountInput))
                .getAttribute("placeholder");
    }

    public String getInstallmentAccountPlaceholder() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(installmentAccountInput))
                .getAttribute("placeholder");
    }

    public String getDebtAccountPlaceholder() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(debtAccountInput))
                .getAttribute("placeholder");
    }

    // Основной метод для заполнения формы
    public PaymentPage fillAndSubmitPaymentForm(String phone, String amount, String email) {
        open();
        acceptCookies();
        selectServicesTab();

        enterPhoneNumber(phone);
        enterEmail(email);
        enterSum(amount);

        clickContinue();

        return new PaymentPage(driver);
    }

    // Вспомогательный метод для форматирования телефона
    private String formatPhoneNumber(String phone) {
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.length() == 9) {
            return String.format("(%s)%s-%s-%s",
                    digits.substring(0, 2),
                    digits.substring(2, 5),
                    digits.substring(5, 7),
                    digits.substring(7));
        }
        return phone;
    }
}