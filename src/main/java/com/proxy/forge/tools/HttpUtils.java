package com.proxy.forge.tools;

import com.alibaba.fastjson2.JSONObject;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.tools</p>
 * <p>Description: 网络请求工具类,支持http代理和socks5代理 的账号密码模式</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-20 01:00
 **/
public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * 使用代理
     *
     * @param url
     * @param headerMap
     * @param proxy
     * @return
     */
    public static byte[] sendGetRequest(String url, Map<String, Object> headerMap, String proxy) throws Exception {
        if (StringUtils.isNotBlank(proxy) && !proxy.startsWith("socks5://") && !proxy.startsWith("http://")) {
            throw new ParseException("Invalid proxy: " + proxy + "; 只支持 http 或 socks5 类型代理.");
        }
        if (StringUtils.isNotBlank(proxy) && proxy.contains("@")) {
            String proxyScheme = proxy.split("://")[0];
            String uri = proxy.split("://")[1];
            String userInfo = uri.split("@")[0];
            String hostPort = uri.split("@")[1];
            String userName = userInfo.split(":")[0];
            String password = userInfo.split(":")[1];
            String host = hostPort.split(":")[0];
            String port = hostPort.split(":")[1];
            return sendGetRequest(url, headerMap, host, Integer.parseInt(port), userName, password, proxyScheme);
        } else if (StringUtils.isNotBlank(proxy) && proxy.startsWith("http://")) {
            String proxyScheme = proxy.split("://")[0];
            String uri = proxy.split("://")[1];
            String host = uri.split(":")[0];
            String port = uri.split(":")[1];
            return sendGetRequest(url, headerMap, host, Integer.parseInt(port), null, null, proxyScheme);
        } else if (StringUtils.isNotBlank(proxy) && proxy.startsWith("socks5://")) {
            String proxyScheme = proxy.split("://")[0];
            String uri = proxy.split("://")[1];
            String host = uri.split(":")[0];
            String port = uri.split(":")[1];
            return sendGetRequest(url, headerMap, host, Integer.parseInt(port), null, null, proxyScheme);
        } else {
            return sendGetRequest(url, headerMap, null, 0, null, null, null);
        }

    }

    /**
     * 发送get 请求
     *
     * @param url       请求地址
     * @param headerMap 请求头
     * @return
     */
    public static byte[] sendGetRequest(String url, Map<String, Object> headerMap) throws Exception {
        return sendGetRequest(url, headerMap, null, 0, null, null, null);
    }


    /**
     * 发送get 请求
     *
     * @param url       请求地址
     * @param headerMap 请求头
     * @param proxyHost 代理信息
     * @return
     */
    public static byte[] sendGetRequest(String url, Map<String, Object> headerMap, String proxyHost, int port, String proxyUserName, String proxyPassword, String proxyScheme) throws Exception {
        return sendGetRequest(url, headerMap, null, proxyHost, port, proxyUserName, proxyPassword, proxyScheme);
    }

    /**
     * 发送get 请求
     *
     * @param url       请求地址
     * @param dnsMap    DNS存储对象
     * @param headerMap 请求头
     * @return 返回服务器响应数据
     */
    public static byte[] sendGetRequest(String url, Map<String, Object> headerMap, Map<String, Object> dnsMap, String proxyHost, int proxyPort, String proxyUserName, String proxyPassword, String proxyScheme) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        if (headerMap != null) {
            // 写入headers
            for (Map.Entry entry : headerMap.entrySet()) {
                httpGet.setHeader(entry.getKey().toString(), entry.getValue() == null ? "" : entry.getValue().toString());
            }
        }
        CloseableHttpClient httpClient;
        HttpResponse response;
        //重尝试请求
        //获取dns的ip配置信息
        String targetServerIP = null;
        if (dnsMap != null) {
            JSONObject o = (JSONObject) dnsMap.get(new URL(url).getHost());
            targetServerIP = o != null ? o.getJSONArray("ip").get(0).toString() : null;
        }
        HttpClientContext context = HttpClientContext.create();
        httpClient = getSslHttpClient(new URL(url).getHost(), targetServerIP, proxyHost, proxyPort, proxyUserName, proxyPassword, proxyScheme, context);
        int timeout = 1000 * 60;
        //超时时间设置
        RequestConfig.Builder requestConfig = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout).setConnectionRequestTimeout(timeout);

        httpGet.setConfig(requestConfig.build());
        response = httpClient.execute(httpGet, context);

        // 从响应中获取响应体
        HttpEntity entity = response.getEntity();
        if (headerMap != null) {
            headerMap.put("response", response);
            headerMap.put("responseStatusCode", String.valueOf(response.getStatusLine().getStatusCode()));
        }
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return EntityUtils.toByteArray(entity);
        } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
                || response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
            return "remote server redirect".getBytes(StandardCharsets.UTF_8);
        } else {
            byte[] body;
            if (entity == null) {
                body = new byte[0];
            } else {
                body = EntityUtils.toByteArray(entity);
            }
            logger.info("发送GET请求响应状态码异常 URL:{},响应状态码:{} 响应内容:{}", url, response.getStatusLine().getStatusCode(), new String(body));
            return body;
        }
    }

    /**
     * 发送header 请求
     *
     * @param url
     * @param headerMap
     * @param proxy
     * @return
     */
    public static JSONObject sendHeadRequest(String url, Map<String, String> headerMap, String proxy, boolean allowRedirects) throws Exception {
        if (StringUtils.isNotBlank(proxy) && !proxy.startsWith("socks5://") && !proxy.startsWith("http://")) {
            throw new ParseException("Invalid proxy: " + proxy + "; 只支持 http 或 socks5 类型代理.");
        }
        if (StringUtils.isNotBlank(proxy) && proxy.contains("@")) {
            String proxyScheme = proxy.split("://")[0];
            String uri = proxy.split("://")[1];
            String userInfo = uri.split("@")[0];
            String hostPort = uri.split("@")[1];
            String userName = userInfo.split(":")[0];
            String password = userInfo.split(":")[1];
            String host = hostPort.split(":")[0];
            String port = hostPort.split(":")[1];
            return sendHeadRequest(url, headerMap, null, host, Integer.parseInt(port), userName, password, allowRedirects);
        } else if (StringUtils.isNotBlank(proxy) && proxy.startsWith("http://")) {
            String proxyScheme = proxy.split("://")[0];
            String uri = proxy.split("://")[1];
            String host = uri.split(":")[0];
            String port = uri.split(":")[1];
            return sendHeadRequest(url, headerMap, null, host, Integer.parseInt(port), null, null, allowRedirects);
        } else if (StringUtils.isNotBlank(proxy) && proxy.startsWith("socks5://")) {
            String proxyScheme = proxy.split("://")[0];
            String uri = proxy.split("://")[1];
            String host = uri.split(":")[0];
            String port = uri.split(":")[1];
            return sendHeadRequest(url, headerMap, null, host, Integer.parseInt(port), null, null, allowRedirects);
        } else {
            return sendHeadRequest(url, headerMap, null, null, 0, null, null, allowRedirects);
        }
    }

    /**
     * 发送 head 请求
     *
     * @param url       请求地址
     * @param dnsMap    dns信息
     * @param headerMap 头信息
     * @param proxyHost 代理信息
     * @return 返回响应头标识
     * @throws Exception 抛出异常
     */
    public static JSONObject sendHeadRequest(String url, Map<String, String> headerMap, Map<String, Object> dnsMap, String proxyHost, int proxyPort, String proxyUserName, String proxyPassword, boolean allowRedirects) throws Exception {
        HttpHead httpHead = new HttpHead(url);
        if (headerMap != null) {
            // 写入headers
            for (Map.Entry entry : headerMap.entrySet()) {
                httpHead.setHeader(entry.getKey().toString(), entry.getValue() == null ? "" : entry.getValue().toString());
            }
        }
        CloseableHttpClient httpClient;
        HttpResponse response;
        //获取dns的ip配置信息
        String targetServerIP = null;
        //超时时间设置
        httpHead.setConfig(RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(3000).setConnectionRequestTimeout(3000).setRedirectsEnabled(allowRedirects).build());
        //重尝试请求
        try {
            if (dnsMap != null) {
                JSONObject o = (JSONObject) dnsMap.get(new URL(url).getHost());
                targetServerIP = o != null ? o.getJSONArray("ip").get(0).toString() : null;
            }
            httpClient = getSslHttpClient(new URL(url).getHost(), targetServerIP, proxyHost, proxyPort, proxyUserName, proxyPassword, null, null);
            HttpClientContext context = HttpClientContext.create();
            response = httpClient.execute(httpHead, context);

            JSONObject jsonObject = new JSONObject();
            for (Header header : response.getAllHeaders()) {
                jsonObject.put(header.getName(), header.getValue());
            }
            return jsonObject;
        } catch (IOException ignored) {
            ignored.printStackTrace();
            logger.info(String.format("发送HEAD请求:[%s], 出现异常IOException, 异常信息:[%s]", url, ignored.getMessage()));
        }
        return null;
    }

    /**
     * 发送post 请求
     *
     * @param url       地址
     * @param headerMap 头部信息
     * @param body      请求体
     * @return 返回响应体
     */
    public static byte[] sendPostRequest(String url, Object body, Map<String, Object> headerMap, String proxy) throws Exception {
        if (StringUtils.isNotBlank(proxy) && !proxy.startsWith("socks5://") && !proxy.startsWith("http://")) {
            throw new ParseException("Invalid proxy: " + proxy + "; 只支持 http 或 socks5 类型代理.");
        }
        if (StringUtils.isNotBlank(proxy) && proxy.contains("@")) {
            String proxyScheme = proxy.split("://")[0];
            String uri = proxy.split("://")[1];
            String userInfo = uri.split("@")[0];
            String hostPort = uri.split("@")[1];
            String userName = userInfo.split(":")[0];
            String password = userInfo.split(":")[1];
            String host = hostPort.split(":")[0];
            String port = hostPort.split(":")[1];
            return sendPostRequest(url, body, headerMap, host, Integer.parseInt(port), userName, password, proxyScheme);
        } else if (StringUtils.isNotBlank(proxy) && proxy.startsWith("http://")) {
            String proxyScheme = proxy.split("://")[0];
            String uri = proxy.split("://")[1];
            String host = uri.split(":")[0];
            String port = uri.split(":")[1];
            return sendPostRequest(url, body, headerMap, host, Integer.parseInt(port), null, null, proxyScheme);
        } else if (StringUtils.isNotBlank(proxy) && proxy.startsWith("socks5://")) {
            String proxyScheme = proxy.split("://")[0];
            String uri = proxy.split("://")[1];
            String host = uri.split(":")[0];
            String port = uri.split(":")[1];
            return sendPostRequest(url, body, headerMap, host, Integer.parseInt(port), null, null, proxyScheme);
        } else {
            return sendPostRequest(url, body, headerMap, null, 0, null, null, null);
        }
    }

    /**
     * 发送post 请求
     *
     * @param url       地址
     * @param headerMap 头部信息
     * @param body      请求体
     * @return 返回响应体
     */
    public static byte[] sendPostRequest(String url, Object body, Map<String, Object> headerMap) throws Exception {
        return sendPostRequest(url, body, headerMap, null, 0, null, null, null);
    }


    /**
     * 发送post 请求
     *
     * @param url       地址
     * @param headerMap 头部信息
     * @param body      请求体
     * @param proxyHost 代理信息
     * @return
     */
    public static byte[] sendPostRequest(String url, Object body, Map<String, Object> headerMap, String proxyHost, int proxyPort, String proxyUserName, String proxyPassword, String proxyScheme) throws Exception {
        return sendPostRequest(url, body, headerMap, null, proxyHost, proxyPort, proxyUserName, proxyPassword, proxyScheme);
    }

    /**
     * 发送post 请求
     *
     * @param url       请求地址
     * @param dnsMap    dns配置map
     * @param headerMap 请求头
     * @param body      请求体
     * @param proxyHost 代理信息
     * @return 返回目标响应体
     */
    public static byte[] sendPostRequest(String url, Object body, Map<String, Object> headerMap, Map<String, Object> dnsMap, String proxyHost, int proxyPort, String proxyUserName, String proxyPassword, String proxyScheme) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        if (headerMap != null) {
            for (Map.Entry entry : headerMap.entrySet()) {
                httpPost.setHeader(entry.getKey().toString(), entry.getValue() == null ? "" : entry.getValue().toString());
            }
        }
        // 根数参数的数据类型来构建不同的body对象
        if (body != null) {
            if (body instanceof byte[]) {
                HttpEntity entity = new ByteArrayEntity((byte[]) body);
                httpPost.setEntity(entity);
            } else if (body instanceof String) {
                HttpEntity entity = new StringEntity(body.toString(), StandardCharsets.UTF_8);
                httpPost.setEntity(entity);
            } else if (body instanceof JSONObject) {
                HttpEntity entity = new StringEntity(((JSONObject) body).toJSONString(), StandardCharsets.UTF_8);
                httpPost.setEntity(entity);
            }
        }

        //获取dns的ip配置信息
        String targetServerIP = null;
        if (dnsMap != null) {
            JSONObject o = (JSONObject) dnsMap.get(new URL(url).getHost());
            targetServerIP = o != null ? o.getJSONArray("ip").get(0).toString() : null;
        }
        HttpClientContext context = HttpClientContext.create();
        HttpResponse response;
        CloseableHttpClient httpClient = getSslHttpClient(new URL(url).getHost(), targetServerIP, proxyHost, proxyPort, proxyUserName, proxyPassword, proxyScheme, context);
        //超时时间设置
        int timeout = 1000 * 60;
        RequestConfig.Builder requestConfig = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout).setConnectionRequestTimeout(timeout);
        httpPost.setConfig(requestConfig.build());
        response = httpClient.execute(httpPost, context);
        // 从响应中获取响应体
        HttpEntity entity = response.getEntity();
        if (headerMap != null) {
            headerMap.put("response", response);
            headerMap.put("statusCode", String.valueOf(response.getStatusLine().getStatusCode()));
        }
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            // 将响应体原始byte数组返回
            return EntityUtils.toByteArray(entity);
        } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
                || response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
            return "remote server redirect".getBytes(StandardCharsets.UTF_8);
        } else {
            byte[] resultBody;
            if (entity == null) {
                resultBody = new byte[0];
            } else {
                resultBody = EntityUtils.toByteArray(entity);
            }
            logger.info("发送POST请求响应状态码异常 URL:{},响应状态码:{} 响应内容:{}", url, response.getStatusLine().getStatusCode(), new String(resultBody));
            return resultBody;
        }
    }

    /**
     * 设置DNS核心方法
     *
     * @param host 请求域名
     * @param ip   设置ip地址
     * @return SSLHTTPClient 对象
     */
    public static CloseableHttpClient getSslHttpClient(String host, String ip, String proxyHost, int proxyPort, String proxyUserName, String proxyPassword, String proxyScheme, HttpClientContext httpClientContext) throws Exception {
        CloseableHttpClient httpClient = null;
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        if (StringUtils.isNotBlank(proxyScheme) && proxyScheme.equals("http")) {
            // 1. SSL 工厂，忽略证书
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    SSLContexts.custom()
                            .loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
                            .build(),
                    NoopHostnameVerifier.INSTANCE
            );

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().
                    register("http",
                            PlainConnectionSocketFactory.getSocketFactory()).
                    register("https", sslsf).build();
            BasicHttpClientConnectionManager connectionManager;
            if (ip != null) {
                //核心：1. 创建MyDnsResolver对象
                DnsResolverHost myDnsResolver = new DnsResolverHost(new HashMap<>());
                //核心：2. 设置DNS
                myDnsResolver.addResolve(host, ip);
                //核心：3. 创建BasicHttpClientConnectionManager对象，指定MyDnsResolver
                connectionManager = new BasicHttpClientConnectionManager(socketFactoryRegistry, null, null, myDnsResolver);
            } else {
                connectionManager = new BasicHttpClientConnectionManager(socketFactoryRegistry);
            }
            httpClientBuilder.setConnectionManager(connectionManager);
            if (proxyHost != null && proxyUserName != null && proxyPassword != null) {
//            代理需要鉴权
                HttpHost httpHost = new HttpHost(proxyHost, proxyPort);
//             创建凭据提供程序并设置用户名和密码
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(new AuthScope(httpHost),
                        new UsernamePasswordCredentials(proxyUserName, proxyPassword));
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                httpClientBuilder.setProxy(httpHost);
            } else if (proxyHost != null && proxyUserName == null && proxyPassword == null) {
//              不需要鉴权
                HttpHost httpHost = new HttpHost(proxyHost, proxyPort);
                httpClientBuilder.setProxy(httpHost);
            }
        } else if (StringUtils.isNotBlank(proxyScheme) && proxyScheme.equals("socks5")) {
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().
                    register("http",
                            new MyConnectionSocketFactory()).
                    register("https", new MySSLConnectionSocketFactory()).build();
            BasicHttpClientConnectionManager connectionManager;
            if (ip != null) {
                //核心：1. 创建MyDnsResolver对象
                DnsResolverHost myDnsResolver = new DnsResolverHost(new HashMap<>());
                //核心：2. 设置DNS
                myDnsResolver.addResolve(host, ip);
                //核心：3. 创建BasicHttpClientConnectionManager对象，指定MyDnsResolver
                connectionManager = new BasicHttpClientConnectionManager(socketFactoryRegistry, null, null, myDnsResolver);
            } else {
                connectionManager = new BasicHttpClientConnectionManager(socketFactoryRegistry);
            }
            httpClientBuilder.setConnectionManager(connectionManager);
            if (StringUtils.isNotBlank(proxyHost) && proxyUserName != null && proxyPassword != null) {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(proxyHost, proxyPort);
                httpClientContext.setAttribute("socks.address", inetSocketAddress);
                httpClientContext.setAttribute("socks.username", proxyUserName);
                httpClientContext.setAttribute("socks.password", proxyPassword);
            } else if (StringUtils.isNotBlank(proxyHost)) {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(proxyHost, proxyPort);
                httpClientContext.setAttribute("socks.address", inetSocketAddress);
            }
        }
        // 禁止重定向
        httpClientBuilder.disableRedirectHandling();
        // 禁止重尝试
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)); // 不重试
        httpClient = httpClientBuilder.build();
        return httpClient;
    }

    /**
     * 自定义 socketFactory
     */
    static class MyConnectionSocketFactory extends PlainConnectionSocketFactory {
        @Override
        public Socket createSocket(final HttpContext context) throws IOException {
            if (context.getAttribute("socks.address") != null) {
//                InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
//                Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr); //Proxy.Type.SOCKS  Proxy.Type.HTTP
//                return new Socket(proxy);
                return new Socket(Proxy.NO_PROXY);
            }
            return new Socket();
        }

        @Override
        public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress,
                                    InetSocketAddress localAddress, HttpContext context) throws IOException {
            Object socksObj = context.getAttribute("socks.address");
            if (socksObj == null) {
                // 无 SOCKS5 代理，直接连接
                if (socket == null) socket = new Socket();
                if (localAddress != null) socket.bind(localAddress);
                socket.connect(remoteAddress, connectTimeout);
                return socket;
            }

            InetSocketAddress socksAddr = (InetSocketAddress) socksObj;
            String username = context.getAttribute("socks.username") != null
                    ? context.getAttribute("socks.username").toString() : null;
            String password = context.getAttribute("socks.password") != null
                    ? context.getAttribute("socks.password").toString() : null;

            if (socket == null) socket = new Socket();
            if (localAddress != null) socket.bind(localAddress);
            socket.connect(socksAddr, connectTimeout);
            socket.setSoTimeout(connectTimeout);

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // ================= SOCKS5 握手 =================
            if (username != null && password != null) {
                // 协商 NO AUTH + USER/PASS
                out.write(new byte[]{0x05, 0x02, 0x00, 0x02});
            } else {
                out.write(new byte[]{0x05, 0x01, 0x00});
            }
            out.flush();

            int ver = in.read();
            int method = in.read();
            if (ver != 0x05) throw new IOException("Invalid SOCKS5 version: " + ver);
            if (method == 0xFF) throw new IOException("SOCKS5 no acceptable auth methods");

            if (method == 0x02) {
                // USER/PASS
                if (username == null || password == null)
                    throw new IOException("SOCKS5 proxy requires username/password");

                byte[] uname = username.getBytes(StandardCharsets.UTF_8);
                byte[] passwd = password.getBytes(StandardCharsets.UTF_8);
                ByteArrayOutputStream auth = new ByteArrayOutputStream();
                auth.write(0x01);
                auth.write(uname.length);
                auth.write(uname);
                auth.write(passwd.length);
                auth.write(passwd);
                out.write(auth.toByteArray());
                out.flush();

                int authVer = in.read();
                int status = in.read();
                if (authVer != 0x01 || status != 0x00) {
                    throw new IOException("SOCKS5 authentication failed, status=" + status);
                }
            }

            // ================= CONNECT 请求 =================
            ByteArrayOutputStream req = new ByteArrayOutputStream();
            req.write(0x05); // ver
            req.write(0x01); // CMD=CONNECT
            req.write(0x00); // RSV

//            InetAddress addr = null;
//            try {
//                addr = InetAddress.getByName(host.getHostName());
//            } catch (Exception ignored) {
//            }
//
//            if (addr instanceof Inet4Address) {
//                req.write(0x01);
//                req.write(addr.getAddress());
//            } else if (addr instanceof Inet6Address) {
//                req.write(0x04);
//                req.write(addr.getAddress());
//            } else {
            byte[] domain = host.getHostName().getBytes(StandardCharsets.UTF_8);
            req.write(0x03);
            req.write(domain.length);
            req.write(domain);
//            }
            req.write((remoteAddress.getPort() >> 8) & 0xFF);
            req.write(remoteAddress.getPort() & 0xFF);
            out.write(req.toByteArray());
            out.flush();

            // 读取代理回复
            int rVer = in.read();
            int rRep = in.read();
            int rRsv = in.read();
            int rAtyp = in.read();
            if (rVer != 0x05) throw new IOException("Invalid SOCKS5 reply version: " + rVer);
            if (rRep != 0x00) throw new IOException("SOCKS5 CONNECT failed, rep=" + rRep);

            // 读取 BND.ADDR 和 BND.PORT
            if (rAtyp == 0x01) readFully(in, 4);
            else if (rAtyp == 0x04) readFully(in, 16);
            else if (rAtyp == 0x03) readFully(in, in.read());
            readFully(in, 2);

            // Socket 连接成功，通过 SOCKS5 隧道到目标
            socket.setSoTimeout(0);
            return socket;
        }

        private static void readFully(InputStream in, int len) throws IOException {
            byte[] buf = new byte[len];
            int off = 0;
            while (off < len) {
                int r = in.read(buf, off, len - off);
                if (r < 0) throw new EOFException("Unexpected EOF");
                off += r;
            }
        }
    }

    /**
     * 自定义sslSocketFactory
     */
    static class MySSLConnectionSocketFactory extends SSLConnectionSocketFactory {

        public MySSLConnectionSocketFactory() throws Exception {
//            super(sslContext, ALLOW_ALL_HOSTNAME_VERIFIER);
//            super(sslContext, NoopHostnameVerifier.INSTANCE);
            // 忽略证书校验（抓包/测试用）
            super(SSLContexts.custom().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build(),
                    (hostname, session) -> true); // 全部主机名允许
        }

        @Override
        public Socket createSocket(final HttpContext context) throws IOException {
            if (context.getAttribute("socks.address") != null) {
                InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr); //Proxy.Type.SOCKS
//                return new Socket(proxy);
                return new Socket(Proxy.NO_PROXY); // 这里交给 connectSocket
            }
            return new Socket();
        }

        @SneakyThrows
        @Override
        public Socket connectSocket(int connectTimeout,
                                    Socket socket,
                                    HttpHost host,
                                    InetSocketAddress remoteAddress,
                                    InetSocketAddress localAddress,
                                    HttpContext context) throws IOException {


            Object socksObj = context.getAttribute("socks.address");
            if (socksObj == null) {
                // 无 SOCKS5 代理，直接连接
                if (socket == null) socket = new Socket();
                if (localAddress != null) socket.bind(localAddress);
                socket.connect(remoteAddress, connectTimeout);
                return socket;
            }

            InetSocketAddress socksAddr = (InetSocketAddress) socksObj;
            String username = context.getAttribute("socks.username") != null
                    ? context.getAttribute("socks.username").toString() : null;
            String password = context.getAttribute("socks.password") != null
                    ? context.getAttribute("socks.password").toString() : null;

            if (socket == null) socket = new Socket();
            if (localAddress != null) socket.bind(localAddress);
            socket.connect(socksAddr, connectTimeout);
            socket.setSoTimeout(connectTimeout);

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // ================= SOCKS5 握手 =================
            if (username != null && password != null) {
                // 协商 NO AUTH + USER/PASS
                out.write(new byte[]{0x05, 0x02, 0x00, 0x02});
            } else {
                out.write(new byte[]{0x05, 0x01, 0x00});
            }
            out.flush();

            int ver = in.read();
            int method = in.read();
            if (ver != 0x05) throw new IOException("Invalid SOCKS5 version: " + ver);
            if (method == 0xFF) throw new IOException("SOCKS5 no acceptable auth methods");

            if (method == 0x02) {
                // USER/PASS
                if (username == null || password == null)
                    throw new IOException("SOCKS5 proxy requires username/password");

                byte[] uname = username.getBytes(StandardCharsets.UTF_8);
                byte[] passwd = password.getBytes(StandardCharsets.UTF_8);
                ByteArrayOutputStream auth = new ByteArrayOutputStream();
                auth.write(0x01);
                auth.write(uname.length);
                auth.write(uname);
                auth.write(passwd.length);
                auth.write(passwd);
                out.write(auth.toByteArray());
                out.flush();

                int authVer = in.read();
                int status = in.read();
                if (authVer != 0x01 || status != 0x00) {
                    throw new IOException("SOCKS5 authentication failed, status=" + status);
                }
            }

            // ================= CONNECT 请求 =================
            ByteArrayOutputStream req = new ByteArrayOutputStream();
            req.write(0x05); // ver
            req.write(0x01); // CMD=CONNECT
            req.write(0x00); // RSV

//            InetAddress addr = null;
//            try {
//                addr = InetAddress.getByName(host.getHostName());
//            } catch (Exception ignored) {
//            }
//
//            if (addr instanceof Inet4Address) {
//                req.write(0x01);
//                req.write(addr.getAddress());
//            } else if (addr instanceof Inet6Address) {
//                req.write(0x04);
//                req.write(addr.getAddress());
//            } else {
            byte[] domain = host.getHostName().getBytes(StandardCharsets.UTF_8);
            req.write(0x03);
            req.write(domain.length);
            req.write(domain);
//            }
            req.write((remoteAddress.getPort() >> 8) & 0xFF);
            req.write(remoteAddress.getPort() & 0xFF);
            out.write(req.toByteArray());
            out.flush();

            // 读取代理回复
            int rVer = in.read();
            int rRep = in.read();
            int rRsv = in.read();
            int rAtyp = in.read();
            if (rVer != 0x05) throw new IOException("Invalid SOCKS5 reply version: " + rVer);
            if (rRep != 0x00) throw new IOException("SOCKS5 CONNECT failed, rep=" + rRep);

            // 读取 BND.ADDR 和 BND.PORT
            if (rAtyp == 0x01) readFully(in, 4);
            else if (rAtyp == 0x04) readFully(in, 16);
            else if (rAtyp == 0x03) readFully(in, in.read());
            readFully(in, 2);

            // Socket 连接成功，通过 SOCKS5 隧道到目标
            socket.setSoTimeout(0);

            // 2. 使用 SSLFactory 包装
            // ---------- SSL/TLS 包装 ----------
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, (chain, authType) -> true)
                    .build();
            SSLSocketFactory sslFactory = sslContext.getSocketFactory();
            SSLSocket sslSocket = (SSLSocket) sslFactory.createSocket(socket, host.getHostName(), remoteAddress.getPort(), true);
            SSLParameters sslParams = sslSocket.getSSLParameters();
            sslParams.setServerNames(List.of(new SNIHostName(host.getHostName())));
            sslSocket.setSSLParameters(sslParams);
            sslSocket.startHandshake();
            return sslSocket;
        }

        private static void readFully(InputStream in, int len) throws IOException {
            byte[] buf = new byte[len];
            int off = 0;
            while (off < len) {
                int r = in.read(buf, off, len - off);
                if (r < 0) throw new EOFException("Unexpected EOF");
                off += r;
            }
        }

    }
}
