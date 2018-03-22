package me.contest.controller;

import me.contest.service.ReceiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by shilong.zhang on 2017/12/18.
 */
@RestController
@Slf4j
public class ReceiveController {
    @Resource
    private ReceiveService receiveService;

    @RequestMapping(path = "/IRS_PART/m1/xml/deal/generic/dealAPI/pushContractToIrs", method = RequestMethod.POST)
    public String receiveDcsDealPush(String method, String result) {
        log.info("method: {}, result: {}", method, result);
        return receiveService.receiveDcsDealPush(method, result);
    }
}
