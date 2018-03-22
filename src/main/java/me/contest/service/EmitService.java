package me.contest.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import me.contest.client.DcsClient;
import me.contest.entity.Command;
import me.contest.entity.Result;
import me.contest.utils.ContractUtils;
import me.contest.utils.WebSocketUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Created by shilong.zhang on 2017/12/18.
 */
@Service
@Slf4j
public class EmitService {
    @Resource
    private DcsClient dcsClient;

    @Value("${test.contracts.path}")
    private String contractsJsonPath;

    @Value("${test.operator}")
    private String operator;

    @Value("${test.submit.concurrent}")
    private int submitConcurrent;

    @Resource
    private SimpMessagingTemplate template;

    public String emit(Command command) throws InterruptedException {
        int round = command.getRound() == 0 ? 1 : command.getRound();
        int concurrent = command.getConcurrent() == 0 ? submitConcurrent : command.getConcurrent();
        StringBuilder resultBuilder = new StringBuilder();
        log.info("start to emit commands.");
        resultBuilder.append("start to emit commands.");
        WebSocketUtils.notify(template, "/topic/greetings", new Result("info", "start to emit commands."));
        JSONObject jsonObject = ContractUtils.loadContractsJson(contractsJsonPath);
        if (jsonObject == null) {
            log.error("cannot load contracts json file {}. Will not emit command.", contractsJsonPath);
            WebSocketUtils.notify(template, "/topic/greetings", new Result("error", "cannot load contracts json file."));
            return "";
        }
        for (int r = 0; r < round; r++) {
            List<Map<String, String>> contracts = ContractUtils.generateContractList("IRS", jsonObject.getJSONArray("contractIdList"));
            List<String> contractIds = contracts.stream().map(cont -> cont.get("contractId")).collect(Collectors.toList());
            WebSocketUtils.notify(template, "/topic/greetings", new Result("info", "emitted addContractList command. contract IDs: " + Arrays.asList(contractIds)));
            dcsClient.addContractList("IRS",
                    JSON.toJSONString(contracts), operator);
            log.info("emitted addContractList command. contract IDs: {}", contractIds);
            Thread.sleep(100);
            // confirm contracts
            contractIds.forEach(contractId -> {
                WebSocketUtils.notify(template, "/topic/greetings",
                        new Result("info", format("emitted updateContractConfirmStatus command. contract ID: %s, confirmIndex: %s, confirmStatus: %s", contractId, "0", "1")));
                dcsClient.updateContractConfirmStatus("IRS", contractId, "0", "1", operator);
                WebSocketUtils.notify(template, "/topic/greetings",
                        new Result("info", format("emitted updateContractConfirmStatus command. contract ID: %s, confirmIndex: %s, confirmStatus: %s", contractId, "1", "1")));
                dcsClient.updateContractConfirmStatus("IRS", contractId, "1", "1", operator);
            });
            Thread.sleep(100);
            // submit contracts
            CyclicBarrier cyclicBarrier = new CyclicBarrier(concurrent);
            ExecutorService service = Executors.newFixedThreadPool(concurrent);
            for (int i = 0; i < concurrent; i++) {
                service.submit(() -> {
                    try {
                        cyclicBarrier.await();
                        WebSocketUtils.notify(template, "/topic/greetings",
                                new Result("info", format("emitted submitContractList command. contract IDs: %s", Arrays.asList(contractIds))));
                        dcsClient.submitContractList("IRS", String.join(",", contractIds), operator);
                    } catch (Exception e) {
                        log.error("submit contract failed. contract ids: {}", contractIds);
                    }
                });
            }
            service.awaitTermination(5, TimeUnit.MINUTES);
            service.shutdown();
        }
        resultBuilder.append("finish emit commands");
        log.info("finised emitting commands.");
        WebSocketUtils.notify(template, "/topic/greetings",
                new Result("info", "finished emitting commands."));
        return resultBuilder.toString();
    }
}
