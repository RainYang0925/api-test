package cn.simafei.test.utils;

import org.testng.Reporter;

public class ReportUtil {
    public static void log(String msg) {
        Reporter.log(msg, true);
    }
}
