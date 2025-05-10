import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MTSMainPageTest {
    protected WebDriver driver;
    private MtsBase mtsBase;

    @BeforeAll
    public static void setupAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setupTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        mtsBase = new MtsBase(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
    }

    @Test
    @Order(1)
    void verifyBlockTitle() {
        mtsBase.open();
        mtsBase.acceptCookies();

        WebElement title = driver.findElement(By.xpath("//h2[contains(., 'Онлайн пополнение без комиссии')]"));
        String actualText = title.getText()
                .replace("\n", " ")
                .replaceAll("\\s+", " ")
                .trim();

        assertEquals("Онлайн пополнение без комиссии", actualText);
    }

    @Test
    @Order(2)
    void verifyPaymentLogo() {
        mtsBase.open();
        mtsBase.acceptCookies();

        WebElement logosContainer = driver.findElement(By.cssSelector(".pay__partners"));
        List<WebElement> logoImages = logosContainer.findElements(By.tagName("img"));

        assertAll("Проверка основных логотипов",
                () -> assertTrue(isLogoPresent(logoImages, "Visa")),
                () -> assertTrue(isLogoPresent(logoImages, "Mastercard")),
                () -> assertTrue(isLogoPresent(logoImages, "Белкарт"))
        );
    }

    private boolean isLogoPresent(List<WebElement> logos, String logoName) {
        return logos.stream().anyMatch(logo -> {
            String altText = logo.getAttribute("alt");
            String src = logo.getAttribute("src");
            return (altText != null && altText.contains(logoName)) ||
                    (src != null && src.toLowerCase().contains(logoName.toLowerCase()));
        });
    }

    @Test
    @Order(3)
    void verifyDetailsLink() {
        mtsBase.open();
        mtsBase.acceptCookies();

        WebElement detailsLink = driver.findElement(By.xpath("//a[contains(., 'Подробнее о сервисе')]"));

        assertAll("Проверка ссылки 'Подробнее о сервисе'",
                () -> assertTrue(detailsLink.isDisplayed()),
                () -> assertTrue(detailsLink.isEnabled()),
                () -> assertNotNull(detailsLink.getAttribute("href"))
        );
    }

    @Test
    @Order(4)
    void verifyPaymentForm() {
        mtsBase.open();
        mtsBase.acceptCookies();
        mtsBase.selectServicesTab();
        mtsBase.enterPhoneNumber(MtsBase.TEST_PHONE);
        mtsBase.enterEmail(MtsBase.TEST_EMAIL);
        mtsBase.enterSum(MtsBase.TEST_SUM);

        assertTrue(driver.findElement(mtsBase.continueButton).isEnabled());
        assertEquals(MtsBase.TEST_PHONE, mtsBase.getEnterPhoneNumber());
        assertEquals(MtsBase.TEST_SUM, mtsBase.getEnteredSum());
    }

    @Test
    @Order(5)
    void verifyEmptyFieldsPlaceholdersForMobileServices() {
        mtsBase.open();
        mtsBase.acceptCookies();
        mtsBase.selectServicesTab();

        assertAll("Проверка плейсхолдеров для Услуг связи",
                () -> assertEquals("Номер телефона", mtsBase.getPhoneFieldPlaceholder(),
                        "Неверный плейсхолдер для поля номера телефона"),
                () -> assertEquals("Сумма", mtsBase.getSumFieldPlaceholder(),
                        "Неверный плейсхолдер для поля суммы"),
                () -> assertEquals("E-mail для отправки чека", mtsBase.getEmailFieldPlaceholder(),
                        "Неверный плейсхолдер для поля email")
        );
    }

    @Test
    @Order(6)
    void verifyEmptyFieldsPlaceholdersForHomeInternet() {
        mtsBase.open();
        mtsBase.acceptCookies();
        mtsBase.selectHomeInternetTab();

        assertAll("Проверка для Домашнего интернета",
                () -> assertEquals("Номер абонента", mtsBase.getHomeInternetAccountPlaceholder(),
                        "Неверный плейсхолдер для номера абонента"),
                () -> assertEquals("Сумма", mtsBase.getSumFieldPlaceholder(),
                        "Неверный плейсхолдер для суммы"),
                () -> assertEquals("E-mail для отправки чека", mtsBase.getEmailFieldPlaceholder(),
                        "Неверный плейсхолдер для email")
        );
    }

    @Test
    @Order(7)
    void verifyEmptyFieldsPlaceholdersForInstallment() {
        mtsBase.open();
        mtsBase.acceptCookies();
        mtsBase.selectInstallmentTab();

        assertAll("Проверка для Рассрочки",
                () -> assertEquals("Номер счета на 44", mtsBase.getInstallmentAccountPlaceholder(),
                        "Неверный плейсхолдер для номера счёта"),
                () -> assertEquals("Сумма", mtsBase.getSumFieldPlaceholder(),
                        "Неверный плейсхолдер для суммы"),
                () -> assertEquals("E-mail для отправки чека", mtsBase.getEmailFieldPlaceholder(),
                        "Неверный плейсхолдер для email")
        );
    }

    @Test
    @Order(8)
    void verifyEmptyFieldsPlaceholdersForDebt() {
        mtsBase.open();
        mtsBase.acceptCookies();
        mtsBase.selectDebtTab();

        assertAll("Проверка для Задолженности",
                () -> assertEquals("Номер счета на 2073", mtsBase.getDebtAccountPlaceholder(),
                        "Неверный плейсхолдер для номера счёта"),
                () -> assertEquals("Сумма", mtsBase.getSumFieldPlaceholder(),
                        "Неверный плейсхолдер для суммы"),
                () -> assertEquals("E-mail для отправки чека", mtsBase.getEmailFieldPlaceholder(),
                        "Неверный плейсхолдер для e-mail")
        );
    }

    @Test
    @Order(9)
    void verifyMobileServicesPaymentProcess() {
        PaymentPage paymentPage = mtsBase.fillAndSubmitPaymentForm(
                "297777777",
                "100",
                "kupikota@test.com"
        );

        assertAll("Проверка платежной страницы",
                () -> assertTrue(paymentPage.isPaymentFrameDisplayed(),
                        "Платежное окно не отображается"),
                () -> assertEquals("100.00 BYN", paymentPage.getDisplayedAmount(),
                        "Неверная сумма платежа"),
                () -> assertTrue(paymentPage.getDisplayedPhoneNumber().contains("375297777777"),
                        "Номер телефона не соответствует ожидаемому"),
                () -> assertEquals("Номер карты", paymentPage.getCardNumberLabel(),
                        "Неверная метка для номера карты"),
                () -> assertEquals("Срок действия", paymentPage.getExpiryDateLabel(),
                        "Неверная метка для срока действия"),
                () -> assertEquals("CVC", paymentPage.getCvcLabel(),
                        "Неверная метка для CVC"),
                () -> assertEquals(4, paymentPage.getPaymentSystemsCount(),
                        "Неверное количество платежных систем"),
                () -> assertTrue(paymentPage.getSubmitButtonText().contains("100.00 BYN"),
                        "Неверная сумма на кнопке оплаты")
        );
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

