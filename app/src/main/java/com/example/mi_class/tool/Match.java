package com.example.mi_class.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Match {
    public static boolean match_mobile(String mobile)
    {
        String p = "^1(3([0-35-9]\\d|4[1-8])|4[14-9]\\d|5([01235689]\\d|7[1-79])|66\\d|7[2-35-8]\\d|8\\d{2}|9[13589]\\d)\\d{7}$";
        return Pattern.matches(p,mobile);
    }

    // 判断是否有特殊字符
    public static boolean char_mobile(String input){
        String regEx = "[\"_`~!@#$%^&*\\\\/￥¥]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(input);
 //       System.out.println("input:"+input);
//        System.out.println("input-flag:"+!m.find());
        boolean y = !m.find();
     //   System.out.println("input-flag:"+y);
        return y;
    }
}
