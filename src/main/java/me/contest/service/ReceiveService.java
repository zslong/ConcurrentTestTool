package me.contest.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import me.contest.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Created by shilong.zhang on 2017/12/18.
 */
@Service
@Slf4j
public class ReceiveService {
    @Resource
    private SimpMessagingTemplate template;

    public String receiveDcsDealPush(String method, String result) {
        JSONArray contracts = JSONArray.parseArray(result);
        List<String> contractIds = contracts.stream().map(cont -> ((JSONObject) cont).getString("contractId")).collect(Collectors.toList());
        log.info("receive DCS deal push. method: {}, contractsIds: {}", method, Arrays.asList(contractIds));
        template.convertAndSend("/topic/greetings", new Result("info", format("receive DCS deal push. method: %s, contractIds: {}", method, Arrays.asList(contractIds))));
        if ("update".equals(method)) {
            String contractIdOrderNoMap = format("contractId->orderNo\n%s",
                    contracts.stream()
                            .map(cont-> format("%s->%s\n", ((JSONObject) cont).get("contractId"), ((JSONObject) cont).get("orderNo")))
                            .collect(Collectors.toList()));
            log.info("contractId->orderNo\n{}", contractIdOrderNoMap);
            template.convertAndSend("/topic/greetings", new Result("info", contractIdOrderNoMap));
        }
        return "true";
    }
}
