package com.ts.cpfr.service.impl;

import com.github.pagehelper.PageHelper;
import com.ts.cpfr.dao.AttendDao;
import com.ts.cpfr.service.AttendService;
import com.ts.cpfr.utils.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Date 2019/3/14
 * @Created by xwr
 */
@Service
public class AttendServiceImpl implements AttendService {

    @Resource
    private AttendDao mAttendDao;
    @Override
    public ResultData<ParamData> addRule(ParamData pd) {
        if (mAttendDao.insertRule(pd)) return new ResultData<>(HandleEnum.SUCCESS);
        else return new ResultData<>(HandleEnum.FAIL);
    }

    @Override
    public ResultData<PageData<ParamData>> getRuleList(ParamData pd) {
        int pageNum = CommUtil.paramConvert(pd.getString("pageNum"), 0);//当前页
        int pageSize = CommUtil.paramConvert(pd.getString("pageSize"), 0);//每一页10条数据

        if (pageSize != 0) PageHelper.startPage(pageNum, pageSize);
        List<ParamData> ruleList = mAttendDao.selectRuleList(pd);
        return new ResultData<>(HandleEnum.SUCCESS, new PageData<>(ruleList));
    }

    @Override
    public ParamData queryAttend(ParamData pd) {
        return mAttendDao.selectAttend(pd);
    }
}
