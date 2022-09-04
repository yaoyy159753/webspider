package org.example.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RetryConfig {
    private boolean retryFlag = false;
    private int retryTimes = 0;
    private int resCode = 200;
    private int retryTime = 1;
    private List<Integer> noRetryCodes = new ArrayList<>();

    public static RetryConfig custom() {
        RetryConfig retryConfig = new RetryConfig();
        retryConfig.setRetryTimes(3);
        retryConfig.setNoRetryCodes(Collections.singletonList(200));
        return retryConfig;
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


    public void retry() {
        retryTime += 1;
        if (retryTime > retryTimes) {
            this.retryFlag = false;
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
