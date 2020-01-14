package cn.devkits.client.util;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DKNetworkUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DKNetworkUtil.class);

    public static boolean socketReachable(String address, int port) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(address, port), 1000);
            boolean portAvailable = socket.isConnected();
            socket.close();
            return portAvailable;
        } catch (UnknownHostException uhe) {
            LOGGER.error("UnknownHostException: " + address);
        } catch (IOException ioe) {
            LOGGER.error("Socket IOException: address is {} and port is {} ", address, port);
        }
        return false;
    }

    public static boolean hostReachable(String ipOrDomain) {
        if (DKStringUtil.isIP(ipOrDomain) || DKStringUtil.isDomain(ipOrDomain)) {
            try {
                InetAddress byName = InetAddress.getByName(ipOrDomain);
                return byName.isReachable(500);
            } catch (UnknownHostException e) {
                LOGGER.error("Unknown Host Check: " + ipOrDomain);
                return false;
            } catch (IOException e) {
                LOGGER.error("IO exception occurred on checking reachable: " + ipOrDomain);
                return false;
            }
        }
        return false;
    }

    /**
     * 获得内网IP
     * 
     * @return 内网IP
     */
    public static String getIntranetIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            LOGGER.error("get local host address error!");
            throw new RuntimeException(e);
        }
    }

    /**
     * 获得外网IP
     * 
     * @return 外网IP
     */
    public static String getInternetIp() {
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            Enumeration<InetAddress> addrs;
            while (networks.hasMoreElements()) {
                addrs = networks.nextElement().getInetAddresses();
                while (addrs.hasMoreElements()) {
                    ip = addrs.nextElement();
                    if (ip != null && ip instanceof Inet4Address && ip.isSiteLocalAddress() && !ip.getHostAddress().equals(getIntranetIp())) {
                        return ip.getHostAddress();
                    }
                }
            }

            // 如果没有外网IP，就返回内网IP
            return getIntranetIp();
        } catch (Exception e) {
            LOGGER.error("get internet ip address error!");
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取本机MAC地址
     * 
     * @return 本机MAC
     */
    public static String getMac() {
        try {

            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();
            // TODO 断网情况下，获取为空
            if (mac == null) {
                return null;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            return sb.toString();
        } catch (UnknownHostException e) {
            LOGGER.error("UnknownHostException: " + e.getMessage());
        } catch (SocketException e) {
            LOGGER.error("SocketException: " + e.getMessage());
        }

        return null;
    }
}
