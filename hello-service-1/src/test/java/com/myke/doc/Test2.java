package com.myke.doc;

import org.junit.Test;

import java.time.LocalDate;

/**
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/10/29 16:20
 * ================================
 */
public class Test2 {
    @Test
    public void dayTest() {
        LocalDate begin = LocalDate.of(2017, 7, 27);
        LocalDate end = LocalDate.of(2017, 8, 4);
        System.out.println("day: " + (end.toEpochDay() - begin.toEpochDay()));
    }
}
