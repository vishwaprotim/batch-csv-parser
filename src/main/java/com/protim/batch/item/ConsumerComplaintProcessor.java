package com.protim.batch.item;

import com.protim.batch.entity.ConsumerComplaint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsumerComplaintProcessor implements ItemProcessor<ConsumerComplaint, ConsumerComplaint> {


    @Override
    public ConsumerComplaint process(ConsumerComplaint consumerComplaint) throws Exception {
        return consumerComplaint;
    }
}
