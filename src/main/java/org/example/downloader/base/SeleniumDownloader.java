package org.example.downloader.base;

import org.example.common.PageRequest;
import org.example.common.PageResponse;
import org.example.downloader.DownLoader;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;

public class SeleniumDownloader extends DownLoader {
    private final Logger logger = LoggerFactory.getLogger(SeleniumDownloader.class);
    private final Duration timeouts = Duration.ofSeconds(30);

    public SeleniumDownloader(String name) {
        super(name);
    }

    @Override
    public void request(PageRequest request, PageResponse pageResponse) {

        String url = request.getUrl();
        WebDriver webDriver = null;
        try {

            ChromeOptions options = new ChromeOptions();
            if (request.isMobileMode()) {
                HashMap<String, String> mobileEmulation = new HashMap<>();
                mobileEmulation.put("deviceName", "iPhone X");
                options.setExperimentalOption("mobileEmulation", mobileEmulation);
            }
            options.setHeadless(true);
            options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
            webDriver = new ChromeDriver(options);
            webDriver.manage().timeouts().pageLoadTimeout(timeouts);
            webDriver.manage().timeouts().scriptTimeout(timeouts);
            webDriver.manage().timeouts().implicitlyWait(timeouts);
            webDriver.get(url);
            String pageSource = webDriver.getPageSource();
            pageResponse.setSuccess(true);
            pageResponse.setStatusCode(200);
            pageResponse.setRawText(pageSource);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("SeleniumDownloader error", e);
        } finally {
            if (webDriver != null) {
                webDriver.close();
                webDriver.quit();
            }

        }

    }

    @Override
    public void destroy() {

    }
}
