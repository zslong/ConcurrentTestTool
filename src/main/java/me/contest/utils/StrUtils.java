package me.contest.utils;

import java.util.UUID;

/**
 * Created by shilong.zhang on 2017/12/18.
 */
public class StrUtils {
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
