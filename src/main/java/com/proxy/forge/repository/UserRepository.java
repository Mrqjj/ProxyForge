package com.proxy.forge.repository;

import com.proxy.forge.dto.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.repository</p>
 * <p>Description: 用户数据库操作表</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-24 18:30
 **/
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {


    /***
     * 根据提供的用户对象更新数据库中用户的信息。
     * 只有在提供的用户对象中非空字段将被更新。
     * 用户通过提供的用户对象的 id 字段来识别。
     *
     * @param user 包含更新信息的用户对象
     * @return 受更新作影响的行数
     */
    @Transactional
    @Modifying
    @Query(value = "update User u set u.userName=COALESCE(:#{#user.userName},u.userName)," +
            "u.passWord=COALESCE(:#{#user.passWord},u.passWord), " +
            "u.status =COALESCE(:#{#user.status},u.status), " +
            "u.type =COALESCE(:#{#user.type},u.type), " +
            "u.createTime =COALESCE(:#{#user.createTime},u.createTime), " +
            "u.expiredTime =COALESCE(:#{#user.expiredTime},u.expiredTime) " +
            "where u.id=:#{#user.id}")
    int updateUserById(@Param("user") User user);

    /**
     * 查询用户表中的所有记录数。
     *
     * @return 用户表中的总记录数
     */
    @Query(value = "select count(*) from User ")
    int queryAllCount();

    /**
     * 根据提供的用户对象中的用户名、密码和状态查询用户。
     *
     * @param user 包含用户名、密码和状态信息的用户对象
     * @return 如果找到匹配的用户，则返回该用户实体；如果未找到匹配项，则返回null
     */
    @Query(value = "select u from User u where u.userName=:#{#user.userName} and u.passWord=:#{#user.passWord} and u.status=:#{#user.status}")
    User findByUserNameAndPassWord(@Param("user") User user);
}
