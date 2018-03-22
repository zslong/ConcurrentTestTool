package me.contest.controller;

import me.contest.entity.Command;
import me.contest.service.EmitService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by shilong.zhang on 2017/12/18.
 */
@RestController
@RequestMapping("/test/contracts")
public class EmitController {

    @Resource
    private EmitService emitService;

    @RequestMapping(method = RequestMethod.POST)
    public String emit(Command command) throws InterruptedException {
        return emitService.emit(command);
    }
}
