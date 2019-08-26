package com.mooc.house.biz.service;


import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mooc.house.common.model.House;
import com.mooc.house.common.page.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecommandService {

    private static final String HOT_HOUSE_KEY = "hot_host";

    @Autowired
    private HouseService houseService;

    public void increase(Long id){
            Jedis jedis = new Jedis("127.0.0.1");
            jedis.zincrby(HOT_HOUSE_KEY,1.0D,id+"");
            jedis.zremrangeByRank(HOT_HOUSE_KEY,10,-1);
            jedis.close();
    }

    public List<Long> getHot(){
        Jedis jedis = new Jedis("127.0.0.1");
        Set<String> idSet = jedis.zrevrange(HOT_HOUSE_KEY,0,-1);
        jedis.close();
        List<Long> ids = idSet.stream().map(Long::parseLong).collect(Collectors.toList());
        return ids;
    }
    public List<House> getHotHouse(Integer size){
        House query = new House();
        List<Long> list = getHot();
        list = list.subList(0,Math.min(list.size(),size));
        if(list.isEmpty()){
            return Lists.newArrayList();
        }
        query.setIds(list);
        List<House> houses =  houseService.queryAndSetImg(query,new PageParams(size,1));
        final List<Long> orderList = list;
        Ordering<House> houseSort = Ordering.natural().onResultOf(hs -> {
            return orderList.indexOf(hs.getId());
                });
        return houseSort.sortedCopy(houses);
        }

    public List<House> getLatest() {
        House query = new House();
        query.setSort("create_time");
        List<House> houses = houseService.queryAndSetImg(query,PageParams.build(8,1));
        return houses;
    }
}
