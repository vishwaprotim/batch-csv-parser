package com.protim.batch.listener;

import com.protim.batch.entity.ConsumerComplaint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CsvParserFaultToleranceListener implements RetryListener {

    private int skipCount = 0;

    @OnSkipInRead
    public void onSkipInRead(Throwable t) {
        skipCount++;
        log.error("[SKIP] Exception {}:{} skipped. Message: {}",
                skipCount,
                t.getClass().getSimpleName(),
                t.getMessage());
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable t) {
        log.info("[RETRY] [ON ERROR] Exception {}:{}", t.getClass().getSimpleName(), t.getMessage());
    }

}
