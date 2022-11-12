package com.xlk.takeout;

import org.junit.jupiter.api.Test;

public class UploadFile {

    @Test
    public void test1(){
        String s = "werwrw.jpg";
        String s1 = s.substring(s.lastIndexOf("."));
        System.out.println(s1);
    }
}
