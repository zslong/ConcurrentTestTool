package me.contest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by shilong.zhang on 2017/12/18.
 */
public class Test {
    public static void main(String[] args) {
        List<String> stringList = Lists.newArrayList();
        stringList.add("hello");
        System.out.println(JSONArray.toJSONString(stringList));
    }
}
