package com.wzy.yuka;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        float sum = 0;
        float a = 1f;
        float b = 1f;
        float na = 0;
        float nb = 0;
        //a/b
        int N = 10;
        for (int i = 0; i < N; i++) {
            na = a + b;
            nb = a;
            sum += na / nb;
            a = na;
            b = nb;
        }
        System.out.println(String.format("%.2f", sum));
    }
}