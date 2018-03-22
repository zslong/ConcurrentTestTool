package me.contest.entity;

import lombok.Data;

/**
 * Created by shilong.zhang on 2017/12/19.
 */
@Data
public class Result {
    private String method;
    private String content;

    public Result(String method, String content) {
        this.method = method;
        this.content = content;
    }
}
