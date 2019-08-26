package com.mooc.house.biz.mapper;

import com.mooc.house.common.model.User;
import com.mooc.house.common.page.PageParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AgencyMapper {
   public List<User> selectAgent(@Param("user")User user, @Param("pageParams")PageParams pageParams);

   public Long selectAgentCount(@Param("user")User user);
}
