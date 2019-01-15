package com.ts.cpfr.dao;

import com.ts.cpfr.utils.ParamData;

import java.util.List;

/**
 * @Classname DeviceDao
 * @Description
 * @Date 2018/10/19 11:23
 * @Created by cjw
 */
public interface DeviceDao {
    List<ParamData> selectDeviceList(ParamData pd);

    List<ParamData> selectInActDeviceList(ParamData pd);

    ParamData selectInActDevice(ParamData pd);

    ParamData selectDevice(ParamData pd);

    void updateInActDeviceOnline(ParamData pd);

    void updateDeviceOnline(ParamData pd);

    boolean insertDevice(ParamData pd);

    boolean updateDeviceGroupID(ParamData pd);

    List<ParamData> selectDeviceSnList(ParamData pd);

    boolean updateDeviceInfo(ParamData pd);

    List<ParamData> selectAccessDeviceListByPersonId(ParamData pd);

    boolean deleteDeviceByDeviceID(ParamData pd);

    String selectDeviceSnByDeviceID(ParamData pd);

    List<ParamData> searchDeviceByDeviceName(ParamData pd);
}
