package org.example.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SiteConfig {
    private boolean redirectFlag = false;
    private boolean retryFlag = false;
    private int retryTimes = 0;
    private int resCode = 200;
    private int retryTime = 1;
    private int redirectTime = 1;
    private int redirectTimes = 0;
    private List<Integer> noRetryCodes = new ArrayList<>();

    public static SiteConfig custom() {
        SiteConfig siteConfig = new SiteConfig();
        siteConfig.setRetryTimes(3);
        siteConfig.setRedirectTimes(3);
        siteConfig.setNoRetryCodes(Collections.singletonList(200));
        return siteConfig;
    }

    public int getRedirectTimes() {
        return redirectTimes;
    }

    public void setRedirectTimes(int redirectTimes) {
        this.redirectTimes = redirectTimes;
        this.redirectFlag = true;
    }

    public List<Integer> getNoRetryCodes() {
        return noRetryCodes;
    }

    public void setNoRetryCodes(List<Integer> noRetryCodes) {
        this.noRetryCodes = noRetryCodes;
    }

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        this.retryFlag = true;
    }

    public boolean checkRetryFlag() {
        return this.retryFlag;
    }

    public boolean checkRedirectFlag() {
        return this.redirectFlag;
    }


    public void retry() {
        retryTime += 1;
        if (retryTime > retryTimes) {
            this.retryFlag = false;
        }
    }

    public void redirect() {
        redirectTime += 1;
        if (redirectTime > redirectTimes) {
            this.redirectFlag = false;
        }
    }

    @Override
    public String toString() {
        return "SiteConfig{" +
                "retryFlag=" + retryFlag +
                ", retryTimes=" + retryTimes +
                ", retryTime=" + retryTime +
                '}';
    }
}
