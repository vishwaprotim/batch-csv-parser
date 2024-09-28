package com.protim.batch.config;

import com.protim.batch.item.ConsumerComplaintFieldSetMapper;
import com.protim.batch.entity.ConsumerComplaint;
import com.protim.batch.listener.CsvParserStepListener;
import com.protim.batch.listener.CsvParserFaultToleranceListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class CSVParserBatchConfiguration {

    @Value("${spring.batch.input}")
    Resource csvInputFile;

    @Value("${spring.batch.chunk.size}")
    int chunkSize;

    @Value("${spring.batch.skip.limit}")
    int skipLimit;

    @Value("${spring.batch.retry.limit}")
    int retryLimit;


    @Bean
    public Job csvParserJob(@Autowired JobRepository jobRepository,
                            @Qualifier("step1") Step step1){
        return new JobBuilder("CSV Parser", jobRepository)
                .start(step1)
                .build();
    }


    @Bean
    public Step step1(@Qualifier("consumerComplaintFlatFileItemReader") ItemReader<ConsumerComplaint> reader,
                      @Autowired ItemProcessor<ConsumerComplaint, ConsumerComplaint> processor,
                      @Autowired ItemWriter<ConsumerComplaint> writer,
                      @Autowired CsvParserFaultToleranceListener faultToleranceListener,
                      @Autowired CsvParserStepListener stepListener,
                      @Autowired JobRepository jobRepository,
                      @Autowired PlatformTransactionManager txManager) {
        var name = "INSERT CSV RECORDS To DB Step";
        return new StepBuilder(name, jobRepository)
                .<ConsumerComplaint, ConsumerComplaint>chunk(chunkSize, txManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skip(FlatFileParseException.class)
                .skipLimit(skipLimit)
                .retry(UnsupportedOperationException.class)
                .retryLimit(retryLimit)
                .backOffPolicy(new ExponentialBackOffPolicy())
                .listener(faultToleranceListener)
                .listener(stepListener)
                .build();
    }


    @Bean
    public FlatFileItemReader<ConsumerComplaint> consumerComplaintFlatFileItemReader(@Autowired ConsumerComplaintFieldSetMapper mapper){
        var lineMapper = new DefaultLineMapper<ConsumerComplaint>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer()); // Default delimiter is comma.
        lineMapper.setFieldSetMapper(mapper);

        var itemReader = new FlatFileItemReader<ConsumerComplaint>();
        itemReader.setLineMapper(lineMapper);
        itemReader.setResource(csvInputFile);
        itemReader.setLinesToSkip(1); // The first line is a header
//        itemReader.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());
        return itemReader;
    }

}
