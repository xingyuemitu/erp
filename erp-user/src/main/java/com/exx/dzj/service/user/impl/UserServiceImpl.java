package com.exx.dzj.service.user.impl;

import com.exx.dzj.constant.CommonConstant;
import com.exx.dzj.entity.user.*;
import com.exx.dzj.excepte.ErpException;
import com.exx.dzj.mapper.user.UserInfoMapper;
import com.exx.dzj.page.ERPage;
import com.exx.dzj.result.Result;
import com.exx.dzj.service.user.UserService;
import com.exx.dzj.util.GenerateSequenceUtil;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author
 * @Date 2019/1/7 0007 15:13
 * @Description 用户(业务员) service
 */
@Service("salesmanService")
public class UserServiceImpl implements UserService {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserInfoMapper userMapper;

    /**
     * 查询 业务员列表
     * @return
     */
    @Cacheable(value = {"userInfo"}, keyGenerator = "myKeyGenerator")
    @Override
    public List<UserInfo> querySalesman() {
        return userMapper.querySalesman();
    }

    /**
     * 保存 业务员信息
     * @param bean
     */
    @Override
    public String saveSalesman(UserVo bean) throws ErpException{
        try{
            String userCode = null;
            if(null != bean && StringUtils.isBlank(bean.getUserCode())){
                //获取 userCode
                userCode = GenerateSequenceUtil.generateSequenceNo();
                //判断 userCode 是否已存在
                if(checkUserCode(userCode) > 0){
                    // 类似自旋锁
                    saveSalesman(bean);
                }
                bean.setUserCode(userCode);
                /**
                 * 默认密码
                 * info.setPassWord("exx88dzj");
                 */
                userMapper.insertSelective(bean);
            }else{
                userMapper.updateByPrimaryKeySelective(bean);
                userCode = bean.getUserCode();
            }
            return userCode;
        }catch(Exception ex){
            LOGGER.error("异常方法:{}异常信息:{}", UserServiceImpl.class.getName()+".saveSalesman", ex.getMessage());
            throw new ErpException(400, "保存业务员信息失败!");
        }
    }

    /**
     * 通过 用户名和密码获取用户信息
     * @param info
     * @return
     */
    @Override
    public UserVo queryUserInfo(UserInfo info) {
        return userMapper.queryUserInfo(info);
    }

    /**
     * 查询 用户信息，包括用户角色信息(用于前端用户修改时显示)
     * @param userCode
     * @return
     */
    @Override
    public UserVo queryUserBean(String userCode) {
        UserInfo info = new UserInfo();
        info.setUserCode(userCode);
        return userMapper.queryUserBean(info);
    }

    /**
     * 查询用户(公司员工)列表数据
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @Override
    public Result list(int pageNum, int pageSize, UserQuery query){
        Result result = Result.responseSuccess();
        PageHelper.startPage(pageNum, pageSize);
        List<UserModel> list = userMapper.queryUserList(query);
        ERPage<UserModel> pages = new ERPage<UserModel>(list);
        result.setData(pages);
        return result;
    }

    /**
     * 检查 用户账号是否被注册
     * @param userName
     * @return
     */
    @Override
    public Result checkUserName(String userName) {
        Result result = Result.responseSuccess();
        UserInfoExample example= new UserInfoExample();
        UserInfoExample.Criteria criteria = example.createCriteria();
        criteria.andUserNameEqualTo(userName);
        long count = userMapper.countByExample(example);
        if(count > 0){
            result.setCode(400);
            result.setMsg("该账号已被注册!");
        }
        return result;
    }

    /**
     * 检查 userCode 是否已存在
     * @param userCode
     * @return
     */
    private long checkUserCode(String userCode) {
        UserInfoExample example= new UserInfoExample();
        UserInfoExample.Criteria criteria = example.createCriteria();
        criteria.andUserCodeEqualTo(userCode);
        return userMapper.countByExample(example);
    }

    /**
     * 判断当前的业务编码是否有人使用
     * @param salesmanCode
     * @return
     */
    @Override
    public Result checkSalesmanCode(String salesmanCode) {
        Result result = Result.responseSuccess();
        UserInfoExample example= new UserInfoExample();
        UserInfoExample.Criteria criteria = example.createCriteria();
        criteria.andSalesmanCodeEqualTo(salesmanCode);
        criteria.andIsQuitEqualTo(CommonConstant.DEFAULT_VALUE_ONE);
        long count = userMapper.countByExample(example);
        if(count > 0){
            result.setCode(400);
            result.setMsg("该业务编码已有人使用!");
        }
        return result;
    }

    @Override
    public Result quitUser(String userCode) {
        Result result = Result.responseSuccess();
        try {
            UserInfo record = new UserInfo();
            record.setUserCode(userCode);
            record.setIsQuit(CommonConstant.DEFAULT_VALUE_ZERO);
            userMapper.updateByPrimaryKeySelective(record);
        } catch(Exception ex){
            result.setCode(400);
            result.setMsg("操作失败!");
            LOGGER.error("异常方法:{}异常信息:{}", UserServiceImpl.class.getName()+".quitUser", ex.getMessage());
        }
        return result;
    }

    @Cacheable(value="selectionUserInfo", keyGenerator = "myKeyGenerator")
    @Override
    public List<UserModel> selectionUserInfo() {

        return userMapper.selectionUserInfo();
    }


}
