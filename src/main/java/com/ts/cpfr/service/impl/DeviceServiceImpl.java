package com.ts.cpfr.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.ts.cpfr.dao.DeviceDao;
import com.ts.cpfr.dao.UserDao;
import com.ts.cpfr.ehcache.Memory;
import com.ts.cpfr.entity.LoginUser;
import com.ts.cpfr.service.DeviceService;
import com.ts.cpfr.utils.CommConst;
import com.ts.cpfr.utils.CommUtil;
import com.ts.cpfr.utils.HandleEnum;
import com.ts.cpfr.utils.ParamData;
import com.ts.cpfr.utils.ResultData;
import com.ts.cpfr.websocket.SocketMessageHandle;

import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Resource
    private DeviceDao mDeviceDao;
    @Resource
    private UserDao mUserDao;
    @Autowired
    private Memory memory;
    @Autowired
    private SocketMessageHandle mSocketMessageHandle;

    @Override
    public ResultData<List<ParamData>> getDeviceList(ParamData pd) {
        int pageNum = CommUtil.paramConvert(pd.getString("pageNum"), 0);//当前页
        int pageSize = CommUtil.paramConvert(pd.getString("pageSize"), 0);//每一页10条数据
        pd.put("wid", memory.getLoginUser().getWId());

        if (pageSize != 0) PageHelper.startPage(pageNum, pageSize);
        List<ParamData> deviceList = mDeviceDao.selectDeviceList(pd);
        return new ResultData<>(HandleEnum.SUCCESS, deviceList);
    }

    @Override
    public ResultData<List<ParamData>> getInActDeviceList(ParamData pd) {
        int pageNum = CommUtil.paramConvert(pd.getString("pageNum"), 0);//当前页
        int pageSize = CommUtil.paramConvert(pd.getString("pageSize"), 0);//每一页10条数据

        if (pageSize != 0) PageHelper.startPage(pageNum, pageSize);
        List<ParamData> inActDeviceList = mDeviceDao.selectInActDeviceList(pd);
        return new ResultData<>(HandleEnum.SUCCESS, inActDeviceList);
    }

    @Override
    public ResultData<ParamData> activateDevice(ParamData pd) throws Exception {
        LoginUser user = memory.getLoginUser();
        ParamData paramData = mDeviceDao.selectInActDevice(pd);
        if (paramData == null) return new ResultData<>(HandleEnum.FAIL, "设备不存在");
        pd.put("admin_id", user.getAdminId());
        if (1 == (Integer) paramData.get("online")) {
            if (mDeviceDao.updateInActDeviceGrantKeyAndStatus(pd)) {
                //激活成功，往对应仓库插入设备，返回
                ParamData insertPd = mDeviceDao.selectInActDevice(pd);
                insertPd.put("wid", user.getWId());
                if (mDeviceDao.insertDevice(insertPd)) {
                    //增加websocketsession的admin_id
                    mSocketMessageHandle.saveAdminIdToSession(insertPd.getString(CommConst.DEVICE_SN), user
                      .getAdminId());

                    //通知设备激活成功
                    String device_sn = insertPd.getString(CommConst.DEVICE_SN);
                    Map<String, Object> jsonMap = new HashMap<>();
                    ParamData data = new ParamData();
                    data.put(CommConst.ADMIN_ID, user.getAdminId());
                    jsonMap.put(CommConst.CODE, CommConst.CODE_1001);
                    jsonMap.put(CommConst.DATA, data);
                    jsonMap.put(CommConst.MESSAGE, "激活成功");
                    mSocketMessageHandle.sendMessageToDevice(device_sn, new TextMessage(JSONObject.toJSONString(jsonMap)));
                    return new ResultData<>(HandleEnum.SUCCESS);
                }
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
        pd.put("wid", memory.getLoginUser().getWId());
        return mDeviceDao.selectDevice(pd);
    }

    @Override
    public void updateDeviceOnline(ParamData pd) {
        mDeviceDao.updateInActDeviceOnline(pd);
        String adminId = pd.getString(CommConst.ADMIN_ID);
        if (!TextUtils.isEmpty(adminId)) {
            int wid = mUserDao.selectWidByAdminId(adminId);
            pd.put("wid", wid);
            mDeviceDao.updateDeviceOnline(pd);
        }
    }
}
