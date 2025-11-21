package com.proxy.forge.tools;

import com.alibaba.fastjson2.JSONObject;
import org.xbill.DNS.*;
import org.xbill.DNS.Record;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.tools</p>
 * <p>Description: dns查询工具类</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-21 16:17
 **/

/**
 * DNS 工具类
 * 支持 A/AAAA/CNAME/TXT/MX/NS/SOA/SRV/NAPTR/CAA/PTR 等查询
 * 默认绕过系统 DNS，使用可信公共 DNS 服务器
 */


public class DNSUtils {

    // 默认公共 DNS 服务器，避免被增强模式劫持
    private static final String[] DEFAULT_DNS_SERVERS = {
            "8.8.8.8",      // Google
            "1.1.1.1",      // Cloudflare
            "1.0.0.1",      // Cloudflare
            "8.8.4.4",      // Google
            "9.9.9.9",       // Quad9
            "114.114.114.114", //114
            "223.5.5.5",       //阿里
            "223.6.6.6",       //阿里
            "119.29.29.29",    //腾讯
            "180.76.76.76",    //百度
    };

    /**
     * 创建带超时的 Resolver
     */
    private static Resolver createResolver(String dns) throws Exception {
        SimpleResolver resolver = new SimpleResolver(dns);
        resolver.setTCP(true);
        resolver.setTimeout(3); // 3 秒超时
        return resolver;
    }

    /**
     * 运行 Lookup，自动 fallback 多个 DNS
     */
    private static Lookup runLookup(String domain, int type) {
        try {
            Lookup lookup = new Lookup(domain, type);
            for (String dns : DEFAULT_DNS_SERVERS) {
                lookup.setResolver(createResolver(dns));
                lookup.run();
                if (lookup.getResult() == Lookup.SUCCESSFUL) {
                    return lookup;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 查询 A 记录
     */
    public static List<String> getA(String domain) {
        List<String> list = new ArrayList<>();
        Lookup lookup = runLookup(domain, Type.A);
        if (lookup.getResult() == Lookup.SUCCESSFUL) {
            for (Record r : lookup.getAnswers()) {
                list.add(((ARecord) r).getAddress().getHostAddress());
            }
        }
        return list;
    }

    /**
     * 查询 AAAA 记录
     */
    public static List<String> getAAAA(String domain) {
        List<String> list = new ArrayList<>();
        Lookup lookup = runLookup(domain, Type.AAAA);
        if (lookup.getResult() == Lookup.SUCCESSFUL) {
            for (Record r : lookup.getAnswers()) {
                list.add(((AAAARecord) r).getAddress().getHostAddress());
            }
        }
        return list;
    }

    /**
     * 查询 CNAME
     */
    public static String getCNAME(String domain) {
        Lookup lookup = runLookup(domain, Type.CNAME);
        if (lookup.getResult() == Lookup.SUCCESSFUL && lookup.getAnswers().length > 0) {
            return ((CNAMERecord) lookup.getAnswers()[0]).getTarget().toString(true);
        }
        return null;
    }

    /**
     * 查询 TXT 记录
     */
    public static List<String> getTXT(String domain) {
        List<String> list = new ArrayList<>();
        Lookup lookup = runLookup(domain, Type.TXT);
        if (lookup.getResult() == Lookup.SUCCESSFUL) {
            for (Record r : lookup.getAnswers()) {
                list.addAll(((TXTRecord) r).getStrings());
            }
        }
        return list;
    }

    /**
     * 查询 MX 记录
     */
    public static List<String> getMX(String domain) {
        List<String> list = new ArrayList<>();
        Lookup lookup = runLookup(domain, Type.MX);
        if (lookup.getResult() == Lookup.SUCCESSFUL) {
            for (Record r : lookup.getAnswers()) {
                MXRecord mx = (MXRecord) r;
                list.add(mx.getTarget().toString(true) + " (priority=" + mx.getPriority() + ")");
            }
        }
        return list;
    }

    /**
     * 查询 NS 记录
     */
    public static List<String> getNS(String domain) {
        List<String> list = new ArrayList<>();
        Lookup lookup = runLookup(domain, Type.NS);
        if (lookup.getResult() == Lookup.SUCCESSFUL) {
            for (Record r : lookup.getAnswers()) {
                list.add(((NSRecord) r).getTarget().toString(true));
            }
        }
        return list;
    }

    /**
     * 查询 SOA 记录
     */
    public static String getSOA(String domain) {
        Lookup lookup = runLookup(domain, Type.SOA);
        if (lookup.getResult() == Lookup.SUCCESSFUL && lookup.getAnswers().length > 0) {
            SOARecord soa = (SOARecord) lookup.getAnswers()[0];
            return "MNAME=" + soa.getHost() + ", RNAME=" + soa.getAdmin() +
                    ", SERIAL=" + soa.getSerial() + ", REFRESH=" + soa.getRefresh();
        }
        return null;
    }

    /**
     * 查询 SRV 记录
     */
    public static List<String> getSRV(String domain) {
        List<String> list = new ArrayList<>();
        Lookup lookup = runLookup(domain, Type.SRV);
        if (lookup.getResult() == Lookup.SUCCESSFUL) {
            for (Record r : lookup.getAnswers()) {
                SRVRecord srv = (SRVRecord) r;
                list.add(srv.getTarget().toString(true) + ":" + srv.getPort() +
                        " (priority=" + srv.getPriority() + ", weight=" + srv.getWeight() + ")");
            }
        }
        return list;
    }

    /**
     * 查询 NAPTR 记录
     */
    public static List<String> getNAPTR(String domain) {
        List<String> list = new ArrayList<>();
        Lookup lookup = runLookup(domain, Type.NAPTR);
        if (lookup.getResult() == Lookup.SUCCESSFUL) {
            for (Record r : lookup.getAnswers()) {
                NAPTRRecord naptr = (NAPTRRecord) r;
                list.add("Order=" + naptr.getOrder() + ", Pref=" + naptr.getPreference() +
                        ", Flags=" + naptr.getFlags() + ", Service=" + naptr.getService() +
                        ", Regexp=" + naptr.getRegexp() + ", Replacement=" + naptr.getReplacement());
            }
        }
        return list;
    }

    /**
     * 查询 CAA 记录
     */
    public static List<String> getCAA(String domain) {
        List<String> list = new ArrayList<>();
        Lookup lookup = runLookup(domain, Type.CAA);
        if (lookup.getResult() == Lookup.SUCCESSFUL) {
            for (Record r : lookup.getAnswers()) {
                CAARecord caa = (CAARecord) r;
                list.add("Tag=" + caa.getTag() + ", Value=" + caa.getValue());
            }
        }
        return list;
    }

    /**
     * 查询 PTR 记录
     */
    public static List<String> getPTR(String domain) {
        List<String> list = new ArrayList<>();
        Lookup lookup = runLookup(domain, Type.PTR);
        if (lookup.getResult() == Lookup.SUCCESSFUL) {
            for (Record r : lookup.getAnswers()) {
                list.add(((PTRRecord) r).getTarget().toString(true));
            }
        }
        return list;
    }

    /**
     * 检查是否有有效 A 或 AAAA（适合 ACME）
     */
    public static boolean hasAorAAAA(String domain) {
        return !getA(domain).isEmpty() || !getAAAA(domain).isEmpty();
    }

    /**
     * 打印完整 DNS 信息
     */
    public static void printAll(String domain) {
        System.out.println("==== DNS Info for: " + domain + " ====");
        System.out.println("A: " + getA(domain));
        System.out.println("AAAA: " + getAAAA(domain));
        System.out.println("CNAME: " + getCNAME(domain));
        System.out.println("MX: " + getMX(domain));
        System.out.println("NS: " + getNS(domain));
        System.out.println("TXT: " + getTXT(domain));
        System.out.println("SOA: " + getSOA(domain));
        System.out.println("SRV: " + getSRV(domain));
        System.out.println("NAPTR: " + getNAPTR(domain));
        System.out.println("CAA: " + getCAA(domain));
        System.out.println("PTR: " + getPTR(domain));
        System.out.println("Has A/AAAA? " + hasAorAAAA(domain));
    }

    public static void main(String[] args) {
        System.out.println(getTXT("_acme-challenge.vivcms.com"));
    }

    /**
     * 检查域名解析是否生效到本机
     * 该方法尝试通过DNS解析给定域名并返回
     * 一个布尔值表示解析是否成功。
     *
     * @param domain 用于检测分辨率
     * @return 如果域名解析正确，则为 true;否则为 false
     */
    public static boolean checkDomainNameResolution_A(String domain) {
        String getIpUrl = "https://ipinfo.io";
        try {
            byte[] res = HttpUtils.sendGetRequest(getIpUrl, null, null);
            String ip = JSONObject.parseObject(new String(res)).getString("ip");
            System.out.println("当前IP地址:  " + ip);
            if (getA(domain).contains(ip)) {
                return true;
            } else {
                System.err.println("域名 [" + domain + "] 解析未生效...");
                return false;
            }
        } catch (Exception e) {
            return true;
        }
    }
}
