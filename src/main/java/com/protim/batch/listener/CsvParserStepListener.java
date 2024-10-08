package com.protim.batch.listener;

import com.protim.batch.entity.ConsumerComplaint;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.AfterWrite;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Getter
@Component
public class CsvParserStepListener {


    @AfterChunk
    public void afterChunk(ChunkContext chunkContext){
        log.info("[CHUNK] processing complete.");
    }


    @AfterWrite
    public void afterWrite(Chunk<? extends ConsumerComplaint> chunk ){
        log.info("[WRITE] Inserted {} records to database", chunk.size());
    }

    @AfterStep
    public void afterStep(StepExecution stepExecution){
        Duration duration = Duration.between(Objects.requireNonNull(stepExecution.getStartTime()), LocalDateTime.now());
        log.info("[STEP] {} finished with status {}. Total time taken: {}h {}m {}s {}ms.",
                stepExecution.getStepName(),
                stepExecution.getStatus(),
                duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart(), duration.toMillisPart());
        // We are using LocalDateTime.now() instead of stepExecution.getEndTime(), because it returns null
        // This is a known issue: https://github.com/spring-projects/spring-batch/issues/3846
        log.info("[STEP] Records skipped: {}", stepExecution.getSkipCount());
        log.info("[STEP] Records read: {}", stepExecution.getReadCount());
        log.info("[STEP] Commits: {}", stepExecution.getCommitCount());
        log.info("[STEP] Write count: {}", stepExecution.getWriteCount());
    }

}
