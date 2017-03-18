package cn.simafei.test.http;

/**
 * Created by admin on 2017/3/18.
 * http常量
 */
public class Constants {
    /**
     * client连接池最大连接数
     */
    public final static int CLIENT_POOL_MAX_SIZE = 200;
    /***
     * 单个站点最大连接数
     */
    public final static int MAX_PER_ROUTE = 20;
    /**
     * socket超时时间，即数据下载时中断时间间隔(单位毫秒)
     */
    public final static int SOCKET_TIMEOUT = 3000;
    /**
     * 连接超时时间，即连接站点时的超时时间(单位毫秒)
     */
    public final static int CONNECT_TIMEOUT = 3000;
    /**
     * 设置连接默认编码
     */
    public final static String DEFAULT_ENCODE = "utf-8";
    /**
     * 是否开启 tcpNoDelay功能，开启的话优化网络带宽利用率，关闭响应更及时
     */
    public final static boolean IS_TCP_NO_DELAY = true;
}