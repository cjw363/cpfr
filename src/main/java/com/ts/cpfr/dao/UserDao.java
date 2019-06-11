package com.ts.cpfr.dao;

import com.ts.cpfr.entity.UserInfo;
import com.ts.cpfr.utils.ParamData;

import java.util.List;

/**
 * @Classname UserDao
 * @Description
 * @Date 2018/10/16 14:50
 * @Created by cjw
 */
public interface UserDao {
    UserInfo selectUserByName(ParamData pd);

    boolean insertUser(ParamData pd);

    int selectWidByUserId(String userId);

    void updateUserLoginTime(ParamData pd);

    boolean updateUserPassword(ParamData pd);

    List<ParamData> selectWidList();
}
