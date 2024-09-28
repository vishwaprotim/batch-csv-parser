package com.protim.batch.item;

import com.protim.batch.entity.ConsumerComplaint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class ConsumerComplaintProcessor implements ItemProcessor<ConsumerComplaint, ConsumerComplaint> {

    public static int errorComplaintId = 3184037;

    @Value("${spring.batch.retry.limit}")
    int retryLimit;

    @Value("${spring.batch.test.enabled}")
    boolean testEnabled;

    int attempt = 0;

    @Override
    public ConsumerComplaint process(ConsumerComplaint consumerComplaint) throws Exception {
        // Testing retry LOGIC
        if(testEnabled && consumerComplaint.getComplaintId() == errorComplaintId){
            LocalDateTime now = LocalDateTime.now();
            log.info("Attempt {}: [RETRY @ {}.{}.{}]", attempt+1, now.getMinute(), now.getSecond(), now.getNano()/1000000);
            // We are logging time to verify exponential back-off logic
            attempt++;
            if(attempt < retryLimit){
                throw new UnsupportedOperationException("Cannot process for complaintId " + errorComplaintId);
            }
        }


        return consumerComplaint;
    }
}
