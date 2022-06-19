package com.assadev.batch.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StringDecomposer {


    public static List<String> forwardDisassembly(String str){
        List<String> result = new ArrayList<>();
        if(StringUtils.isEmpty(str)){
            return result;
        }
        StringBuilder tmp = new StringBuilder();
        char[] termBuffer = str.toCharArray();
        for( char term : termBuffer){
            tmp.append(term);
            result.add(tmp.toString());
        }
        return result;
    }

    public static List<String> rearDisassembly(String str){
        List<String> result = new ArrayList<>();
        if(StringUtils.isEmpty(str)){
            return result;
        }
        char[] termBuffer = str.toCharArray();
        StringBuilder tmp = new StringBuilder();
        for(int i=termBuffer.length - 1; i>=0; i--){
            tmp.append(termBuffer[i]);
            result.add(tmp.reverse().toString());
        }
        return result;
    }
}
