package me.contest.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;
import java.util.Map;

import static me.contest.utils.JsonUtils.valueParser;

/**
 * Created by shilong.zhang on 2017/12/18.
 */
@Slf4j
public class ContractUtils {
    public static List<Map<String, String>> generateContractList (String deskName, JSONArray jsonArray) {
        List<Map<String, String>> result = Lists.newArrayList();
        jsonArray.forEach(json -> {
            Map<String, String> contract = Maps.newHashMap();
            String uuid = StrUtils.uuid();
            Map<String, String> context = Maps.newHashMap();
            context.put("uuid", uuid);
            context.put("uuid_irs", uuid + "+IRS");
            ((JSONObject) json).entrySet()
                    .forEach(
                            stringObjectEntry -> {
                                try {
                                    contract.put(stringObjectEntry.getKey(), valueParser((String) stringObjectEntry.getValue(), context));
                                } catch (Exception ex) {
                                    log.error("Unparsable contract entry. key: {}, value: {}", stringObjectEntry.getKey(), stringObjectEntry.getValue());
                                }
                            }
                    );
            contract.put("deskName", deskName);
            result.add(contract);
        });
        return result;
    }

    public static JSONObject loadContractsJson(String fpath) {
        try {
            InputStream is = ContractUtils.class.getClassLoader().getResourceAsStream(fpath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null){
                sb.append(line);
            }
            return JSONObject.parseObject(sb.toString());
        } catch (Exception e) {
            log.error("cannot load contracts json file {}", fpath);
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        InputStream is = ContractUtils.class.getClassLoader().getResourceAsStream("contracts.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null){
            sb.append(line);
        }
        System.out.println(generateContractList("IRS", JSONObject.parseObject(sb.toString()).getJSONArray("contractIdList")));
    }
}
