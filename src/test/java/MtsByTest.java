import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MtsByTest {
    WebDriver driver;
    WebDriverWait wait;
    private static final String SITE_URL = "https://www.mts.by/";
    private static final String TEST_PHONE = "297777777";

    private boolean isLogoPresent(List<WebElement> logos, String logoName) {
        return logos.stream().anyMatch(logo -> {
            String altText = logo.getAttribute("alt");
            String src = logo.getAttribute("src");
            return (altText != null && altText.contains(logoName)) ||
                    (src != null && src.toLowerCase().contains(logoName.toLowerCase()));
        });
    }

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(90));
        driver.get(SITE_URL);
        wait.until(d -> ((JavascriptExecutor) d)
                .executeScript("return document.readyState").equals("complete"));
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void verifyBlockTitle() {
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(., 'Онлайн')]")));

        String actualText = title.getText()
                .replace("\n", " ")
                .replaceAll("\\s+", " ")
                .trim();

        assertEquals("Онлайн пополнение без комиссии", actualText,
                "Заголовок не соответствует ожидаемому");
    }

    @Test
    void verifyPaymentLogos() {
        WebElement logosContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".pay__partners")));

        List<WebElement> logoImages = logosContainer.findElements(By.tagName("img"));

        assertAll("Проверка основных логотипов",
                () -> assertTrue(isLogoPresent(logoImages, "Visa")),
                () -> assertTrue(isLogoPresent(logoImages, "Mastercard")),
                () -> assertTrue(isLogoPresent(logoImages, "Белкарт"))
        );
    }

    @Test
    void verifyDetailsLink() {
        WebElement detailsLink = driver.findElement(
                By.xpath("//a[contains(., 'Подробнее о сервисе')]"));

        assertTrue(detailsLink.isDisplayed(), "Ссылка не отображается");
        assertTrue(detailsLink.isEnabled(), "Ссылка неактивна");

        String linkUrl = detailsLink.getAttribute("href");
        assertNotNull(linkUrl, "Ссылка не содержит URL");
        System.out.println("URL ссылки: " + linkUrl);

        detailsLink.click();

    }

    @Test
    void verifyPaymentForm() {
        WebElement serviceTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[contains(@class, 'select__now') and text()='Услуги связи']")
        ));
        serviceTab.click();

        WebElement phoneInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input#connection-phone")
        ));
        phoneInput.sendKeys(TEST_PHONE);

        WebElement emailInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input#connection-email")
        ));
        emailInput.sendKeys("kupikota@test.com");

        WebElement amountInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input#connection-sum")
        ));
        amountInput.sendKeys("10");

        WebElement submitButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[contains(@class, 'button__default') and text()='Продолжить']")
        ));
        assertTrue(submitButton.isEnabled(), "Кнопка 'Продолжить' должна быть активна");
    }
}