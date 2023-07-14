package com.sky.stock.helper;

import com.google.common.util.concurrent.RateLimiter;
import me.tongfei.progressbar.ProgressBar;

import java.util.function.Consumer;
import java.util.stream.IntStream;

public class RateLimiterHelper {

    public static void run(String taskName, double rate, int size, Consumer<Integer> task) {
        RateLimiter limiter = RateLimiter.create(rate);
        ProgressBar.wrap(IntStream.range(0, size).parallel(), taskName).forEach(i -> {
            limiter.acquire();
            task.accept(i);
        });
    }
}
