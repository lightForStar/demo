package com.tuorong.dao;

import com.tuorong.model.Admin;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminMapper {
    int deleteByPrimaryKey(Integer hrid);

    int insert(Admin record);

    int insertSelective(Admin record);

    Admin selectByPrimaryKey(Integer hrid);

    int updateByPrimaryKeySelective(Admin record);

    int updateByPrimaryKey(Admin record);

    List<Admin> selectAll();

    List<Admin> listAdmin(@Param("mobileOrName") String mobileOrName);


//    String selAdminLoginname(String loginname);

    Admin selectAdminByName(String loginname);

    String selAdminPwd(String loginname);
}