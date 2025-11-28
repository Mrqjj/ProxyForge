package com.proxy.forge.service;

import com.proxy.forge.api.pojo.SaveDomain;
import com.proxy.forge.api.pojo.SearchDomain;

import java.io.FileNotFoundException;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service</p>
 * <p>Description: 域名接口类</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-26 19:32
 **/
public interface DomainService {

    /**
     * 保存或更新域名信息。
     *
     * @param saveDomain 待保存的Domain对象，包含域名、状态、是否启用SSL、访问量及创建时间等信息。
     * @return 返回一个整数值，表示受影响的行数。如果返回值大于0，表示至少有一行被成功保存或更新；如果返回值为0，则表示没有找到匹配的记录或保存/更新操作未改变任何数据。
     */
    Object saveDomain(SaveDomain saveDomain) throws Exception;

    /**
     * 获取域名列表。
     *
     * @return 返回包含域名信息的对象。具体返回对象的类型和结构取决于实现逻辑。
     */
    Object domainList(SearchDomain searchDomain);

    /**
     * 获取所有域名的信息。
     *
     * @return 返回一个包含所有域名信息的对象。具体返回对象的类型和结构取决于实现逻辑。
     */
    Object domainAll();

    /**
     * 获取可用的域名列表。
     *
     * @return 返回一个包含可用域名信息的对象。具体返回对象的类型和结构取决于实现逻辑。
     */
    Object availableDomains();

}
