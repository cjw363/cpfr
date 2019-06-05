package com.ts.cpfr.dao;

import com.ts.cpfr.utils.ParamData;

import java.util.List;

/**
 * @Classname GroupDao
 * @Description
 * @Date 2018/11/14 10:17
 * @Created by cjw
 */
public interface GroupDao {
    List<ParamData> selectGroupList(ParamData pd);

    List<ParamData> selectGroupPersonList(ParamData pd);//嵌套查询

    List<ParamData> selectGroupDeviceList(ParamData pd);//嵌套查询

    List<ParamData> selectGroupInPeople(ParamData pd);

    ParamData selectGroup(ParamData pd);

    ParamData selectGroupDevicePerson(ParamData pd);//嵌套查询

    boolean insertGroup(ParamData pd);

    boolean updateGroupInfo(ParamData pd);

    boolean deleteGroup(ParamData pd);

    ParamData selectGroupByGroupName(ParamData pd);

    boolean insertGroupDevice(ParamData pd);

    boolean insertGroupPerson(ParamData pd);

    boolean deleteGroupDevice(ParamData pd);

    boolean deleteGroupPerson(ParamData pd);
}
