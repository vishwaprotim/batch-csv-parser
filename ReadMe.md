# CSV Parser Batch Job

## Performance Improvements
- The basic batch uses JPA Repository **save()** method to insert records into the Database.
  - In this case, with default propagation as REQUIRED, each time an insert occurs, a new transaction is created.
  - Performance: 5000 records processed in **3.217** seconds
- Using **saveAll()** provides us better performance.
  - Both **save()** and **saveAll()** are annotated with **@Transactional**. Although internally saveAll() iterates the collection and calls save() for each record, the existing transaction is reused, instead of creating a fresh one each time.
  - Performance: 5000 records processed in **2.784** seconds
  - Read more [here](https://www.baeldung.com/spring-data-save-saveall).
- Increasing the chunk size
  - Increasing chunk size means larger data size during batch inserts. This can improve performance in many cases. However, there is a threshold limit as this puts load on memory as well.
  - In our case, chunk size 500 works well.
  - Performance: 5000 records processed in **2.577** seconds
- Enabling **JPA Batch Inserts**
  - Batch inserts are by default disabled by JPA. Since Spring Batch works through chunk based processing, there is not much trade off when it comes to enable batch JPA inserts, which work in a similar fashion. You can read further [here](https://www.baeldung.com/spring-data-jpa-batch-inserts).

## Fault Tolerance
Fault Tolerance is important for any Batch Job. For certain exceptions, we may not want out batch to end abruptly. Designing a batch with fault tolerance needs a good understanding on the type of work this batch will perform, as well as the possible failure cases. 

### Skip Policy and Limit
Say your Spring Batch is reading data from a CSV file. For a few records, you get a parsing exception. In such cases, it is better to skip those records, and keep a track of the skipped records and get them checked later.

Also, for certain scenarios, you may want to create a Skip Policy that helps your batch decide how to deal with certain exceptions.

Note than when the skip limit exceeds, it is good to deduce that the input flat file is faulty, and hence, the batch should not proceed. Thus the behavior of Spring Batch. Your batch terminates with status FAILED for SkipLimitExceededException.

### Retry Policy and Limit
Let us consider, your batch is making a REST API call while processing the records. For a few instances, the REST call fails. In that case, you may want to retry that record instead of skipping it altogether. Hence, in such cases it is a good habit to implement a retry policy or a limit, if you want to keep things simple.

### Backoff Policy
In case of retry, Spring Batch will back off for some time, allowing the system to heal. Exponential back off policy is a good practice.



## Tips:
### @EnableBatchProcessing is now discouraged.
Previously, @EnableBatchProcessing could be used to enable Spring Boot’s auto-configuration of Spring Batch. It is no longer required and should be removed from applications that want to use Boot’s auto-configuration. A bean that is annotated with @EnableBatchProcessing or that extends Batch’s **DefaultBatchConfiguration** can now be defined to tell the auto-configuration to back off, allowing the application to take complete control of how Batch is configured. Read more at [here](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide#enablebatchprocessing-is-now-discouraged).


### Specify Chunk generics when using a step builder
Here is an example:
```java

// Note the <ConsumerComplaint, ConsumerComplaint> preceding chunk()
return new StepBuilder(name, jobRepository)
                .<ConsumerComplaint, ConsumerComplaint>chunk(10, txManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
```

### Auto-Create Batch Metadata Tables
By default, Spring will create Batch Metadata Tables only when using embedded databases(H2). If you are not using an embedded database and want the metadata tables created automatically, ensure below property is set:
```yaml
spring:
  batch:
    jdbc.initialize-schema: always #default value is never
```

### Reading CSV records with line breaks
Say you need to read the below CSV record:
```json
email, name
abc@z.com, "NEW NAME
 ABC"
```
In this case, ensure that you set the **DefaultRecordSeparatorPolicy**. Out of the box the FlatFileItemReader uses a **SimpleRecordSeparatorPolicy**, which treats a new line as end of record. Hence use the below code snippet:

```java
itemReader.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());
```

### Re-Fire Completed Step
Since batch details are persisted, Spring by default does not allow a step/job with same parameters to rerun. To counter this, either modify the parameters,  or use set **allowStartIfComplete** to **true** for a step.

### Logging
As per Spring Docs,

"When possible, we recommend that you use the -spring variants for your logging configuration (for example, logback-spring.xml rather than logback.xml). If you use standard configuration locations, Spring cannot completely control log initialization."
