package com.springbatchtest.batch;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomSkipPolicy implements SkipPolicy {
    @Override
    public boolean shouldSkip(@NonNull Throwable t, long skipCount) throws SkipLimitExceededException {
        if (t instanceof FileNotFoundException) return false;
        if (t instanceof IOException) return false;
        return true;
    }
}
