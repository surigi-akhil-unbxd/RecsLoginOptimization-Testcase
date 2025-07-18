package lib;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static org.openqa.selenium.chrome.ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY;

public class BrowserInitializer {

    private enum Browser {
        HTMLUNIT("default"),
        FIREFOX("firefox"),
        CHROME("chrome"),
        PHANTOMJS("phantomjs");

        private String name;

        Browser(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static Browser getBrowser(String name) {
            for (Browser browser : values()) {
                if (browser.getName().equalsIgnoreCase(name)) {
                    return browser;
                }
            }
            return HTMLUNIT;
        }
    }

    private HashMap<String, Object> browserCapabilities = new HashMap<String, Object>();
    private static final String BROWSER_NAME = "browserName";
    private Browser browser;
    private static final String geckoDriverPath="/usr/local/bin/geckodriver";
    private WebDriver driver=null;

    public BrowserInitializer() throws Exception {
        Config.loadConfig();
        EnvironmentConfig.loadConfig();
    }

    public void init() {
        String browserFromConfig = Config.getBrowser();
        System.out.println("[DEBUG] Browser from config: " + browserFromConfig);
        browserCapabilities.put(BROWSER_NAME, browserFromConfig);
        DesiredCapabilities capabilities = new DesiredCapabilities();
        browser=setDesiredCapabilities(capabilities);
        driver=initDriver(browser,capabilities);
        if (driver == null) {
            System.out.println("[DEBUG] WebDriver is null after initDriver! Browser: " + browser + ", Capabilities: " + capabilities);
        }
        else {
            System.out.println("[DEBUG] WebDriver successfully created: " + driver.getClass().getName());
        }
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        Helper.initialize(driver);
    }

    private WebDriver initDriver(Browser browser, DesiredCapabilities capabilities) {
        WebDriver driver = null;
        System.out.println("[DEBUG] initDriver called for browser: " + browser);

        String hubUrl = System.getProperty("hubUrl");
        System.out.println("[DEBUG] hubUrl system property: " + hubUrl);
        boolean useGrid = hubUrl != null && !hubUrl.isEmpty();

        try {
            if (useGrid) {
                System.out.println("[DEBUG] Using Selenium Grid at: " + hubUrl);
                driver = new RemoteWebDriver(new URL(hubUrl), capabilities);
            } else {
                System.err.println("[WARNING] hubUrl is not set! Will use local driver. This may fail on CI.");
                if (browser == Browser.CHROME) {
                    String chromePath = getChromeDriverPath();
                    System.out.println("[DEBUG] Using ChromeDriver path: " + chromePath);
                    System.setProperty(CHROME_DRIVER_EXE_PROPERTY, chromePath);
                    driver = new ChromeDriver(capabilities);
                } else if (browser == Browser.FIREFOX) {
                    System.setProperty("webdriver.gecko.driver", geckoDriverPath);
                    capabilities.setCapability("marionette", true);
                    System.out.println("[DEBUG] Using GeckoDriver path: " + geckoDriverPath);
                    driver = new FirefoxDriver(capabilities);
                } else {
                    System.out.println("[DEBUG] No matching browser found, returning null driver.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return driver;
    }

    private void setChromeCapabilities(DesiredCapabilities capabilities) {
        capabilities.setCapability(CHROME_DRIVER_EXE_PROPERTY, getChromeDriverPath());
        System.setProperty(CHROME_DRIVER_EXE_PROPERTY,getChromeDriverPath());
        capabilities.setCapability("chrome.switches", Arrays.asList(
            "--start-maximized",
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--disable-gpu",
            "--disable-extensions",
            "--disable-popup-blocking",
            "--disable-infobars",
            "--disable-notifications"
        ));
        LoggingPreferences preferences=new LoggingPreferences();
        preferences.enable(LogType.BROWSER, Level.ALL);
        capabilities.setCapability(CapabilityType.LOGGING_PREFS,preferences);

        // Selenoid-specific capabilities
        capabilities.setCapability("name", "RECS-Testcases");
        Map<String, Object> selenoidOptions = new java.util.HashMap<>();
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("enableVideo", false);
        capabilities.setCapability("selenoid:options", selenoidOptions);
    }

    private Browser setDesiredCapabilities(DesiredCapabilities capabilities) {
        browser= Browser.getBrowser(browserCapabilities.get(BROWSER_NAME).toString());
        capabilities.setBrowserName(browser.getName());
        if(browser.name().equals("CHROME"))
            setChromeCapabilities(capabilities);
        return browser;
    }

    private void maximizeBrowserWindow() {
        if(driver==null)
            throw new NullPointerException("The WebDriver is not Initialised ");

        Toolkit toolkit=Toolkit.getDefaultToolkit();
        int width=(int)toolkit.getScreenSize().getWidth();
        int height=(int) toolkit.getScreenSize().getHeight();
        driver.manage().window().setSize(new Dimension(width,height));
        driver.manage().window().fullscreen();
    }

    private String getChromeDriverPath() {
        String path = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "driver" + File.separator;
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("windows")) {
            path = path + "chromedriver.exe";
        } else if (os.contains("mac")) {
            path = path + "chromedriver";
        } else if (os.contains("linux")) {
            path = path + "chromedriver";
        } else {
            System.err.println("[ERROR] Unrecognized OS: " + os + ". Cannot determine ChromeDriver path.");
            return null;
        }

        // Optionally, check if the file exists and log a warning if not
        File driverFile = new File(path);
        if (!driverFile.exists()) {
            System.err.println("[WARNING] ChromeDriver not found at: " + path);
        }

        return path;
    }

    public WebDriver getDriver() {
        return driver;
    }
}
