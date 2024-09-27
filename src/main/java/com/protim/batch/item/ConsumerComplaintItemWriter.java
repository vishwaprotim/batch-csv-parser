package com.protim.batch.item;

import com.protim.batch.entity.ConsumerComplaint;
import com.protim.batch.repository.ConsumerComplaintRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ConsumerComplaintItemWriter implements ItemWriter<ConsumerComplaint> {

    @Autowired
    ConsumerComplaintRepository repository;

    @Override
    public void write(Chunk<? extends ConsumerComplaint> chunk) throws Exception {
        repository.saveAll(chunk);
    }
}
