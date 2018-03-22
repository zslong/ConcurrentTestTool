package me.contest.utils;

import com.google.common.collect.Maps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shilong.zhang on 2017/12/18.
 */
public class JsonUtils {
    private static final Pattern variablePattern = Pattern.compile("\\$\\{(.*?)\\}");

    private static final Pattern parametersPattern = Pattern.compile("\\((.*?)\\)");

    public static String valueParser(String value, Map<String, String> context) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        StringBuffer vb = new StringBuffer(value.length());
        final Matcher matcher = variablePattern.matcher(value);
        while (matcher.find()) {
            matcher.appendReplacement(vb, eval(matcher.group(1), context));
        }
        matcher.appendTail(vb);
        return vb.toString();
    }

    public static String eval(String func, Map<String, String> context) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String result = "";
        if (func.startsWith("__Random")) {
            Matcher matcher = parametersPattern.matcher(func);
            int[] intParamsArray = new int[2];
            if (matcher.find()) {
                String[] paramsArray = matcher.group(1).split(",");
                for (int i = 0; i < 2; i++) {
                    intParamsArray[i] = Integer.parseInt(paramsArray[i].trim());
                }
            }
            Method method = ThreadLocalRandom.current().getClass().getDeclaredMethod("nextInt", int.class, int.class);
            result = String.valueOf(method.invoke(ThreadLocalRandom.current(), intParamsArray[0], intParamsArray[1]));
        } else if (func.startsWith("__time")) {
            Matcher matcher = parametersPattern.matcher(func);
            String timePattern = "";
            if (matcher.find()) {
                timePattern = matcher.group(1).split(",")[0].trim();
            }
            result = new SimpleDateFormat(timePattern).format(new Date());
        } else {
            result = context.get(func);
        }
        return result;
    }

    public static void main(String... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String value = "${__Random(1,9)}.${__Random(1,99)}";
        Map<String, String> context = Maps.newHashMap();
        context.put("uuid", "12345");
        System.out.println(valueParser(value, context));
    }
}
