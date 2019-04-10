package com.exx.dzj.service.sys.impl;

import com.exx.dzj.constant.CommonConstant;
import com.exx.dzj.entity.role.*;
import com.exx.dzj.entity.user.UserRole;
import com.exx.dzj.entity.user.UserRoleExample;
import com.exx.dzj.excepte.ErpException;
import com.exx.dzj.mapper.role.RoleBeanMapper;
import com.exx.dzj.mapper.role.RoleMenuBeanMapper;
import com.exx.dzj.mapper.user.UserRoleMapper;
import com.exx.dzj.page.ERPage;
import com.exx.dzj.result.Result;
import com.exx.dzj.service.sys.RoleService;
import com.exx.dzj.service.user.impl.UserServiceImpl;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.management.relation.RoleInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author
 * @Date 2019/4/2 0002 14:32
 * @Description 角色 service
 */
@Component
public class RoleServiceImpl implements RoleService {

    private final static Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private RoleBeanMapper roleMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleMenuBeanMapper roleMenuMapper;

    /**
     * 查询角色列表数据
     * @param pagaNum
     * @param pageSize
     * @param query
     * @return
     */
    @Override
    public Result queryList(int pagaNum, int pageSize, RoleQuery query) {
        Result result = Result.responseSuccess();
        PageHelper.startPage(pagaNum, pageSize);
        RoleBeanExample example = new RoleBeanExample();
        RoleBeanExample.Criteria criteria = example.createCriteria();
        criteria.andIsEnableEqualTo(CommonConstant.DEFAULT_VALUE_ONE);
        if(null != query && StringUtils.isNotBlank(query.getRoleCode())) {
            criteria.andRoleCodeLike(query.getRoleCode());
        }
        if(null != query && StringUtils.isNotBlank(query.getRoleName())) {
            criteria.andRoleNameLike(query.getRoleName());
        }
        List<RoleBean> list = roleMapper.selectByExample(example);
        ERPage<RoleBean> pages = new ERPage<>(list);
        result.setData(pages);
        return result;
    }

    /**
     * 查询角色列表数据
     * @return
     */
    @Override
    public List<RoleBean> queryList() {
        RoleBeanExample example = new RoleBeanExample();
        RoleBeanExample.Criteria criteria = example.createCriteria();
        criteria.andIsEnableEqualTo(CommonConstant.DEFAULT_VALUE_ONE);
        return roleMapper.selectByExample(example);
    }

    /**
     * 根据用户编码查询用户角色
     * @param userCode
     * @return
     */
    @Override
    public List<UserRole> queryRoleByUserCode(String userCode) {
        UserRoleExample example = new UserRoleExample();
        UserRoleExample.Criteria criteria = example.createCriteria();
        criteria.andUserCodeEqualTo(userCode);
        return userRoleMapper.selectByExample(example);
    }

    /**
     * 保存 用户角色
     * @param userCode
     * @param roles
     * @return
     */
    @Override
    public Result saveUserRole(String userCode, String operator, List<String> roles){
        try {
            Result result = Result.responseSuccess();
            if(CollectionUtils.isEmpty(roles)){
                result.setCode(400);
                result.setMsg("未选择角色!");
                return result;
            }
            List<UserRole> list = new ArrayList<>();
            UserRole userRole  = null;
            for(String role : roles){
                if(StringUtils.isNotBlank(role)){
                    userRole = new UserRole();
                    userRole.setUserCode(userCode);
                    userRole.setRoleCode(role);
                    userRole.setCreateUser(operator);
                    userRole.setUpdateUser(operator);
                    list.add(userRole);
                }
            }
            userRoleMapper.saveBatchUserRole(list);
            return result;
        } catch(Exception ex) {
            LOGGER.error("异常方法:{}异常信息:{}", RoleServiceImpl.class.getName()+".saveUserRole", ex.getMessage());
            throw new ErpException(400, "保存用户角色失败!");
        }
    }

    /**
     * 删除 用户角色
     * @param userCode
     * @return
     */
    @Override
    public Result delByUserCode(String userCode){
        try {
            Result result = Result.responseSuccess();
            UserRoleExample example = new UserRoleExample();
            UserRoleExample.Criteria criteria = example.createCriteria();
            criteria.andUserCodeEqualTo(userCode);
            userRoleMapper.deleteByExample(example);
            return result;
        } catch(Exception ex) {
            LOGGER.error("异常方法:{}异常信息:{}", RoleServiceImpl.class.getName()+".delByUserCode", ex.getMessage());
            throw new ErpException(400, "删除用户角色失败!");
        }
    }

    /**
     * 保存角色数据
     * @param info
     * @return
     */
    @Override
    public Result saveRole(RoleBean info) {
        Result result = Result.responseSuccess();
        try {
            if(null != info && null != info.getId()){
                roleMapper.updateByPrimaryKeySelective(info);
                return result;
            }
            // 查询目前最大的排序字段值
            int seq = roleMapper.queryMaxSeq();
            info.setSeq(seq + 1);
            roleMapper.insertSelective(info);
        } catch(Exception ex) {
            LOGGER.error("异常方法:{}异常信息:{}", RoleServiceImpl.class.getName()+".saveRole", ex.getMessage());
            result.setCode(400);
            result.setMsg("保存角色信息失败!");
        }
        return result;
    }

    /**
     * 检查角色编码是否存在
     * @param roleCode
     * @return
     */
    @Override
    public Result checkRole(String roleCode) {
        Result result = Result.responseSuccess();
        RoleBeanExample example = new RoleBeanExample();
        RoleBeanExample.Criteria criteria = example.createCriteria();
        criteria.andRoleCodeEqualTo(roleCode);
        long count = roleMapper.countByExample(example);
        if(count > 0){
            result.setCode(400);
            result.setMsg("角色编码已存在!");
        }
        return result;
    }

    /**
     * 查询 角色所拥有的的菜单权限数据
     * @param roleCode
     * @return
     */
    @Override
    public List<String> queryRolePermission(String roleCode) {
        return roleMenuMapper.queryRolePermission(roleCode);
    }

    /**
     * 删除角色
     * @param roleCode
     * @param operator
     * @return
     */
    @Override
    public Result cancelRole(String roleCode, String operator) {
        Result result = Result.responseSuccess();
        try {
            RoleBean record = new RoleBean();
            record.setUpdateUser(operator);
            record.setIsEnable(CommonConstant.DEFAULT_VALUE_ZERO);

            RoleBeanExample example = new RoleBeanExample();
            RoleBeanExample.Criteria criteria = example.createCriteria();
            criteria.andRoleCodeEqualTo(roleCode);
            roleMapper.updateByExampleSelective(record, example);
        } catch(Exception ex) {
            LOGGER.error("异常方法:{}异常信息:{}", RoleServiceImpl.class.getName()+".cancelRole", ex.getMessage());
            result.setCode(400);
            result.setMsg("删除角色失败!");
        }
        return result;
    }

    /**
     * 角色 授权
     * @param info
     * @return
     */
    @Override
    @Transactional(rollbackFor = ErpException.class)
    public void grantAuth(RoleMenuInfo info, String operator) {
        try {
            //先删除角色对于的数据
            RoleMenuBeanExample example = new RoleMenuBeanExample();
            RoleMenuBeanExample.Criteria criteria = example.createCriteria();
            criteria.andRoleCodeEqualTo(info.getRoleCode());
            roleMenuMapper.deleteByExample(example);

            //授权
            List<RoleMenuBean> list = new ArrayList<>();
            RoleMenuBean bean = null;
            if(!CollectionUtils.isEmpty(info.getMenuCodes())){
                for(String menuCode : info.getMenuCodes()){
                    bean = new RoleMenuBean();
                    bean.setMenuCode(menuCode);
                    bean.setRoleCode(info.getRoleCode());
                    bean.setCreateUser(operator);
                    bean.setUpdateUser(operator);
                    list.add(bean);
                }
                roleMenuMapper.grantAuth(list);
            }
        } catch(Exception ex) {
            LOGGER.error("异常方法:{}异常信息:{}", RoleServiceImpl.class.getName()+".authorize", ex.getMessage());
            throw new ErpException(400, "角色授权失败!");
        }
    }
}
