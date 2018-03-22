package me.contest.utils;

import me.contest.entity.Result;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shilong.zhang on 2017/12/19.
 */
public class WebSocketUtils {
    private static ExecutorService service = Executors.newSingleThreadExecutor();

    public static void notify(SimpMessagingTemplate template, String topic, Result result) {
        service.submit(() -> {
            template.convertAndSend(topic, result);
        });
    }
}
