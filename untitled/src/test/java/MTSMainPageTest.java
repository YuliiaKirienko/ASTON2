import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));


    }

    @Test
    void verifyBlockTitle() {
        mtsBase.open();
        mtsBase.acceptCookies();

        // Проверка названия блока
        WebElement title = driver.findElement(By.xpath("//h2[contains(., 'Онлайн пополнение без комиссии')]"));
        String actualText = title.getText()
                .replace("\n", " ")
                .replaceAll("\\s+", " ")
                .trim();

        assertEquals("Онлайн пополнение без комиссии", actualText);
    }

    @Test
    void verifyPaymentLogo() {
        mtsBase.open();
        mtsBase.acceptCookies();

        // Проверка логотипов платежных систем
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
    void verifyDetailsLink() {
        mtsBase.open();
        mtsBase.acceptCookies();

        // Проверка ссылки "Подробнее о сервисе"
        WebElement detailsLink = driver.findElement(By.xpath("//a[contains(., 'Подробнее о сервисе')]"));

        assertAll("Проверка ссылки 'Подробнее о сервисе'",
                () -> assertTrue(detailsLink.isDisplayed()),
                () -> assertTrue(detailsLink.isEnabled()),
                () -> assertNotNull(detailsLink.getAttribute("href"))
        );
    }

    @Test
    void verifyPaymentForm() {
        mtsBase.open();
        mtsBase.acceptCookies();
        mtsBase.selectServicesTab();
        mtsBase.enterPhoneNumber(MtsBase.TEST_PHONE);
        mtsBase.enterEmail(MtsBase.TEST_EMAIL);
        mtsBase.enterSum(MtsBase.TEST_SUM);

        //Проверка заполнения полей и кнопки Продолжить

        assertTrue(driver.findElement(mtsBase.continueButton).isEnabled());
        assertEquals(MtsBase.TEST_PHONE, mtsBase.getEnterPhoneNumber());
        assertEquals(MtsBase.TEST_SUM, mtsBase.getEnteredSum());
    }


    @Test
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
    void verifyMobileServicesPaymentProcess() {
        mtsBase.open();
        mtsBase.acceptCookies();
        mtsBase.selectServicesTab();

        // Заполнение формы
        mtsBase.enterPhoneNumber(MtsBase.TEST_PHONE);
        mtsBase.enterEmail(MtsBase.TEST_EMAIL);
        mtsBase.enterSum(MtsBase.TEST_SUM);
        mtsBase.clickContinue();

        assertAll(
                // Проверка плейсхолдеров

                () -> assertEquals("Срок действия", mtsBase.getCardExpiryPlaceholder(),
                        "Неверный плейсхолдер для срока действия"),
                () -> assertEquals("CVC", mtsBase.getCardCvvPlaceholder(),
                        "Неверный плейсхолдер для CVC"),
                () -> assertEquals("Имя держателя (как на карте)", mtsBase.getCardNamePlaceholder(),
                        "Неверный плейсхолдер для имени держателя"),

                // Проверка иконок платежных систем
                () -> assertTrue(mtsBase.isPaymentSystemIconDisplayed("Visa"),
                        "Иконка Visa не отображается"),
                () -> assertTrue(mtsBase.isPaymentSystemIconDisplayed("Mastercard"),
                        "Иконка Mastercard не отображается"),
                () -> assertTrue(mtsBase.isPaymentSystemIconDisplayed("Белкарт"),
                        "Иконка Белкарт не отображается")
        );
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

}

