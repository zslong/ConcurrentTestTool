package me.contest.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.contest.exception.DcsConTestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 * Created by shilong.zhang on 2017/12/18.
 */
@Slf4j
public class HttpUtils {
    public static JSONObject getJSONFromResponse(HttpResponse response) {
        JSONObject result = new JSONObject();
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String entityStr = EntityUtils.toString(entity, "UTF-8");
                result = JSONObject.parseObject(entityStr);
            }
        } catch (IOException e) {
            log.error("GetJSONFromResponse error. Details: {}", e.getMessage());
        }
        return result;
    }

    public static void handleHttpRequestException(String requestName, Exception exception) {
        String error = format("%s request failed. Error details: %s", requestName, exception.getMessage());
        log.error(error);
        throw new DcsConTestException(error, HttpStatus.SC_BAD_REQUEST);
    }

    public static JSONObject processDcsResponse(String requestName, HttpResponse response) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            String error = format("%s request failed -- status code: %s, response entity: %s",
                    requestName, statusCode, EntityUtils.toString(response.getEntity()));
            log.error(error);
            throw new DcsConTestException(error, statusCode);
        }
        return getJSONFromResponse(response);
    }

    public static List<Map<String, Object>> jsonArrayToList(String json) {
        List<Map<String, Object>> result = Lists.newArrayList();

        JSONArray jsonArray = JSONArray.parseArray(json);
        for (int i = 0; i < jsonArray.size(); i++) {
            result.add(jsonObjectToMap(jsonArray.getJSONObject(i)));
        }

        return result;
    }

    public static Map<String, Object> jsonObjectToMap(JSONObject jsonObject) {
        Map<String, Object> result = Maps.newHashMap();
        jsonObject.entrySet().stream().forEach(entry -> {
            result.put(entry.getKey(), entry.getValue());
        });
        return result;
    }
}
