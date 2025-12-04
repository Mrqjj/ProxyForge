package com.proxy.forge.service.impl;

import com.proxy.forge.api.pojo.SaveDomain;
import com.proxy.forge.api.pojo.SearchDomain;
import com.proxy.forge.dto.Domain;
import com.proxy.forge.repository.DomainRepository;
import com.proxy.forge.service.DomainService;
import com.proxy.forge.vo.ResponseApi;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service.impl</p>
 * <p>Description: 域名实现类</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-26 19:33
 **/
@Service
public class DomainServiceImpl implements DomainService {

    @Autowired
    DomainRepository domainRepository;


    /**
     * 保存或更新域名信息。
     *
     * @param saveDomain 待保存的Domain对象，包含需要存储的域名相关信息。
     * @return 返回一个整数值，表示操作结果。具体返回值的含义取决于实现逻辑。
     */
    @Override
    public Object saveDomain(SaveDomain saveDomain) throws Exception {
        // 先检查是否存在，存在更新，不存在保存。
        Domain domain = new Domain();
        domain.setDomain(saveDomain.getDomain());
        domain.setEnableSsl(saveDomain.isSsl());
        domain.setRemark(saveDomain.getRemark());
        domain.setStatus(saveDomain.getStatus());
        domain.setMethod(saveDomain.getMethod());

        String domainName = saveDomain.getDomain();
        String safeDomain = domainName.replaceAll("[^a-zA-Z0-9.-]", "_");
        //保存
        String p12Path = "./Certificate/certs/" + safeDomain + "/" + safeDomain + ".p12";   // p12 文件路径
        String password = "xiaoxiong";  // p12 密码
        if (new File(p12Path).exists() && saveDomain.isSsl()) {
            // 加载 keystore
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(p12Path), password.toCharArray());
            for (String alias : Collections.list(keyStore.aliases())) {
                X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
                if (cert != null) {
                    domain.setCertExpTime(LocalDateTime.ofInstant(cert.getNotAfter().toInstant(), ZoneId.systemDefault()));
                }
            }
        }
        domain.setCreateTime(LocalDateTime.now());
        Domain d = domainRepository.queryDomainByDomain(domain);
        if (d != null) {
            //更新
            domain.setId(d.getId());
            return new ResponseApi(200, "提交成功", domainRepository.updateDomainById(domain));
        }
        return new ResponseApi(200, "提交成功", domainRepository.save(domain).getId());
    }

    /**
     * 获取域名列表。
     *
     * @return 返回一个对象，表示获取到的域名列表信息。当前实现中返回null，具体返回类型和内容取决于实现逻辑。
     */
    @Override
    public Object domainList(SearchDomain searchDomain) {

        Pageable pageable = PageRequest.of(
                searchDomain.getPageNum() - 1,   // JPA 页码从 0 开始
                searchDomain.getPageSize()
        );
        Specification<Domain> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. keyword 模糊搜索
            if (searchDomain.getKeyWord() != null && !searchDomain.getKeyWord().isEmpty()) {
                predicates.add(cb.like(root.get("domain"), "%" + searchDomain.getKeyWord() + "%"));
            }

            // 3. status 等于查询
            if (searchDomain.getStatus() != null && !searchDomain.getStatus().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), searchDomain.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        List<Domain> domains = domainRepository.findAll(spec, pageable).getContent();
        return new ResponseApi(200, "获取成功.", domains, domainRepository.count());
    }

    /**
     * 获取所有域名信息。
     *
     * @return 返回一个对象，表示获取到的所有域名信息。当前实现中返回null，具体返回类型和内容取决于实现逻辑。
     */
    @Override
    public Object domainAll() {
        List<Domain> domains = domainRepository.findAll();
        return new ResponseApi(200, "成功", domains);
    }

    /**
     * 查询可用的域名列表。
     *
     * @return 返回一个ResponseApi对象，其中包含状态码、提示信息以及可用的域名列表数据。状态码为200表示请求成功，提示信息为"成功"，data字段包含可用的Domain对象列表。
     */
    @Override
    public Object availableDomains() {
        List<Domain> domains = domainRepository.queryAvailableDomains();
        return new ResponseApi(200, "成功", domains);
    }

}
