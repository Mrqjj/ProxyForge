package com.proxy.forge.tools;

import com.alibaba.fastjson2.JSONObject;
import org.xbill.DNS.*;
import org.xbill.DNS.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * DNS 工具类（修复版）
 * 每个 DNS 查询一次，并返回对应的结果
 */
public class DNSUtils {

    // 默认公共 DNS 列表
    private static final String[] DEFAULT_DNS_SERVERS = {
            "8.8.8.8",
            "8.8.4.4",
            "1.1.1.1",
            "1.0.0.1",
            "9.9.9.9",
            "149.112.112.112",
            "208.67.222.222",
            "208.67.220.220",
            "94.140.14.14",
            "94.140.15.15",
            "185.228.168.9",
            "185.228.169.9",
            "64.6.64.6",
            "64.6.65.6",
            "8.26.56.26",
            "8.20.247.20",
            "4.2.2.1",
            "4.2.2.2",
            "195.46.39.39",
            "195.46.39.40",
            "199.85.126.10",
            "199.85.127.10",
            "156.154.70.1",
            "156.154.71.1",
            "114.114.114.114",
            "114.114.115.115",
            "223.5.5.5",
            "223.6.6.6",
            "119.29.29.29",
            "119.28.28.28",
            "180.76.76.76",
            "1.2.4.8",
            "210.2.4.8",
            "117.50.10.10",
            "117.50.20.20",
            "101.226.4.6",
            "218.30.118.6"
    };

    /**
     * 单个 DNS 查询的结果结构
     */
    public static class ResultItem {
        public String dns;
        public List<String> answers = new ArrayList<>();
        public int result = -1;

        public String toString() {
            return "DNS=" + dns + ", result=" + result + ", answers=" + answers;
        }
    }

    /**
     * 对每个 DNS 服务器执行一次查询
     */
    private static List<ResultItem> queryAllDNS(String domain, int type) {
        List<ResultItem> resultList = new ArrayList<>();

        for (String dns : DEFAULT_DNS_SERVERS) {
            ResultItem item = new ResultItem();
            item.dns = dns;

            try {
                SimpleResolver resolver = new SimpleResolver(dns);
                resolver.setTimeout(3);
                resolver.setTCP(true);

                Lookup lookup = new Lookup(domain, type);
                lookup.setResolver(resolver);

                lookup.run();
                item.result = lookup.getResult();

                if (lookup.getAnswers() != null) {
                    for (Record r : lookup.getAnswers()) {
                        item.answers.add(r.rdataToString());
                    }
                }

            } catch (Exception e) {
                item.result = Lookup.TRY_AGAIN;
            }

            resultList.add(item);
        }

        return resultList;
    }

    // ----------------------
    //    各种查询方法
    // ----------------------

    public static List<ResultItem> getA(String domain) {
        List<ResultItem> list = queryAllDNS(domain, Type.A);

        // 转换 ARecord
        for (ResultItem item : list) {
            List<String> ips = new ArrayList<>();
            for (String r : item.answers) {
                ips.add(r);
            }
            item.answers = ips;
        }

        return list;
    }

    public static List<ResultItem> getAAAA(String domain) {
        return queryAllDNS(domain, Type.AAAA);
    }

    public static List<ResultItem> getCNAME(String domain) {
        return queryAllDNS(domain, Type.CNAME);
    }

    public static List<ResultItem> getTXT(String domain) {
        return queryAllDNS(domain, Type.TXT);
    }

    public static List<ResultItem> getMX(String domain) {
        return queryAllDNS(domain, Type.MX);
    }

    public static List<ResultItem> getNS(String domain) {
        return queryAllDNS(domain, Type.NS);
    }

    public static List<ResultItem> getSOA(String domain) {
        return queryAllDNS(domain, Type.SOA);
    }

    public static List<ResultItem> getSRV(String domain) {
        return queryAllDNS(domain, Type.SRV);
    }

    public static List<ResultItem> getNAPTR(String domain) {
        return queryAllDNS(domain, Type.NAPTR);
    }

    public static List<ResultItem> getCAA(String domain) {
        return queryAllDNS(domain, Type.CAA);
    }

    public static List<ResultItem> getPTR(String domain) {
        return queryAllDNS(domain, Type.PTR);
    }

    /**
     * 判断是否至少一个 DNS 返回 A 或 AAAA
     */
    public static boolean hasAorAAAA(String domain) {
        return getA(domain).stream().anyMatch(r -> !r.answers.isEmpty()) ||
                getAAAA(domain).stream().anyMatch(r -> !r.answers.isEmpty());
    }

    // 查询域名解析结果 使用dns服务器节点查询
    public static void main(String[] args) {
        List<DNSUtils.ResultItem> list = getA("sdf.notifiction.art");
        System.out.println(JSONObject.toJSONString(list));
        for (DNSUtils.ResultItem r : list) {
            System.out.println(r);
        }
    }
}