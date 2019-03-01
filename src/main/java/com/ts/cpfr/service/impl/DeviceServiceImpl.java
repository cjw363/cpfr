package com.ts.cpfr.service.impl;

import com.github.pagehelper.PageHelper;
import com.ts.cpfr.dao.DeviceDao;
import com.ts.cpfr.dao.PersonDao;
import com.ts.cpfr.ehcache.AppMemory;
import com.ts.cpfr.ehcache.WebMemory;
import com.ts.cpfr.entity.AppDevice;
import com.ts.cpfr.entity.LoginUser;
import com.ts.cpfr.service.DeviceService;
import com.ts.cpfr.utils.CommConst;
import com.ts.cpfr.utils.CommUtil;
import com.ts.cpfr.utils.HandleEnum;
import com.ts.cpfr.utils.PageData;
import com.ts.cpfr.utils.ParamData;
import com.ts.cpfr.utils.ResultData;
import com.ts.cpfr.utils.SocketEnum;
import com.ts.cpfr.websocket.SocketMessageHandle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

/**
 * @Classname DeviceServiceImpl
 * @Description
 * @Date 2018/10/19 11:22
 * @Created by cjw
 */
@Service
@Transactional
public class DeviceServiceImpl implements DeviceService {

    private static final int STATUS_1_DEVICE_ONLINE = 1;
    private static final int STATUS_1_DEVICE_ACTIVATED = 1;

    @Resource
    private DeviceDao mDeviceDao;
    @Resource
    private PersonDao mPersonDao;
    @Autowired
    private WebMemory memory;
    @Autowired
    private AppMemory appMemory;
    @Autowired
    private SocketMessageHandle mSocketMessageHandle;

    @Override
    public ResultData<PageData<ParamData>> getDeviceList(ParamData pd) {
        int pageNum = CommUtil.paramConvert(pd.getString("pageNum"), 0);//当前页
        int pageSize = CommUtil.paramConvert(pd.getString("pageSize"), 0);//每一页10条数据

        if (pageSize != 0)
            PageHelper.startPage(pageNum, pageSize);
        List<ParamData> deviceList = mDeviceDao.selectDeviceList(pd);
        return new ResultData<>(HandleEnum.SUCCESS, new PageData<>(deviceList));
    }

    @Override
    public ResultData<PageData<ParamData>> getInActDeviceList(ParamData pd) {
        int pageNum = CommUtil.paramConvert(pd.getString("pageNum"), 0);//当前页
        int pageSize = CommUtil.paramConvert(pd.getString("pageSize"), 0);//每一页10条数据

        if (pageSize != 0)
            PageHelper.startPage(pageNum, pageSize);
        List<ParamData> inActDeviceList = mDeviceDao.selectInActDeviceList(pd);
        return new ResultData<>(HandleEnum.SUCCESS, new PageData<>(inActDeviceList));
    }

    @Override
    public ResultData<ParamData> activateDevice(ParamData pd) throws Exception {
        LoginUser user = memory.getCache(pd.getString(CommConst.ACCESS_CPFR_TOKEN));
        ParamData paramData = mDeviceDao.selectInActDevice(pd);
        if (paramData == null)
            return new ResultData<>(HandleEnum.FAIL, "设备不存在");
        pd.put("admin_id", user.getAdminId());
        pd.put("wid", user.getWid());
        if (STATUS_1_DEVICE_ONLINE == (Integer) paramData.get("online")) {
            //激活成功，往对应仓库插入设备，返回
            if (mDeviceDao.insertDevice(pd)) {
                //激活成功 缓存token
                String device_sn = pd.getString(CommConst.DEVICE_SN);
                AppDevice device = new AppDevice(device_sn, user.getWid());
                appMemory.putCache(device);

                //通知设备激活成功
                ParamData data = new ParamData();
                data.put(CommConst.ACCESS_APP_TOKEN, appMemory.getToken(device_sn));
                TextMessage message = mSocketMessageHandle.obtainMessage(SocketEnum.CODE_1001_DEVICE_ACTIVATE, data);
                mSocketMessageHandle.sendMessageToDevice(device_sn, message);
                return new ResultData<>(HandleEnum.SUCCESS);
            }
            return new ResultData<>(HandleEnum.FAIL);
        } else {
            return new ResultData<>(HandleEnum.FAIL, "设备不在线");
        }
    }

    @Override
    public ParamData queryInActDevice(ParamData pd) {
        return mDeviceDao.selectInActDevice(pd);
    }

    @Override
    public ParamData queryDevice(ParamData pd) {
        return mDeviceDao.selectDevice(pd);
    }

    @Override
    public void updateDeviceOnline(ParamData pd) {
        mDeviceDao.updateInActDeviceOnline(pd);
        if (STATUS_1_DEVICE_ACTIVATED == mDeviceDao.selectDeviceStatusByDeviceSn(pd)) {
            int wid = mDeviceDao.selectWidByDeviceSn(pd);
            pd.put("wid", wid);
            mDeviceDao.updateDeviceOnline(pd);
        }
    }

    @Override
    public ResultData<PageData<ParamData>> getGrantPersonList(ParamData pd) {
        int pageNum = CommUtil.paramConvert(pd.getString("pageNum"), 0);//当前页
        int pageSize = CommUtil.paramConvert(pd.getString("pageSize"), 0);//每一页10条数据

        if (pageSize != 0)
            PageHelper.startPage(pageNum, pageSize);
        List<ParamData> personList = mPersonDao.selectGrantPersonListByDeviceSn(pd);
        return new ResultData<>(HandleEnum.SUCCESS, new PageData<>(personList));
    }

    @Override
    public ResultData<ParamData> changeDeviceInfo(ParamData pd) throws Exception {
        if (mDeviceDao.updateDeviceInfo(pd)) {
            TextMessage message = mSocketMessageHandle.obtainMessage(SocketEnum.CODE_1002_DEVICE_UPDATE, null);
            mSocketMessageHandle.sendMessageToDevice(pd.getString(CommConst.DEVICE_SN), message);
            return new ResultData<>(HandleEnum.SUCCESS);
        } else
            return new ResultData<>(HandleEnum.FAIL);
    }

    @Override
    public ResultData<ParamData> deleteDevice(ParamData pd) throws Exception {
        String deviceSn = mDeviceDao.selectDeviceSnByDeviceID(pd);
        if (mDeviceDao.deleteDeviceByDeviceID(pd)) {
            TextMessage message = mSocketMessageHandle.obtainMessage(SocketEnum.CODE_1005_DEVICE_DELETE, null);
            mSocketMessageHandle.sendMessageToDevice(deviceSn, message);
            //移除app缓存
            appMemory.removeCache(deviceSn);
            return new ResultData<>(HandleEnum.SUCCESS);
        } else
            return new ResultData<>(HandleEnum.FAIL);
    }

    @Override
    public int getWidByDeviceSn(ParamData pd) {
        return mDeviceDao.selectWidByDeviceSn(pd);
    }

    @Override
    public int getDeviceActStatusByDeviceSn(ParamData pd) {
        return mDeviceDao.selectDeviceStatusByDeviceSn(pd);
    }

}
