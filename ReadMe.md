# CSV Parser Batch Job

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

### Performance Improvements
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