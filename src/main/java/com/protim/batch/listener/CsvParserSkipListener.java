package com.protim.batch.listener;

import com.protim.batch.entity.ConsumerComplaint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CsvParserSkipListener implements SkipListener<ConsumerComplaint, ConsumerComplaint> {

    private int skipCount = 0;

    @Override
    public void onSkipInRead(Throwable t) {
        skipCount++;
        log.error("{} Exception skipped! Protim prints *************", skipCount, t);
    }
}
