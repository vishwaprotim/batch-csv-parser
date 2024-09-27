package com.protim.batch.item;

import com.protim.batch.entity.ConsumerComplaint;
import lombok.NoArgsConstructor;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@Component
public class ConsumerComplaintFieldSetMapper implements FieldSetMapper<ConsumerComplaint> {

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Override
    public ConsumerComplaint mapFieldSet(FieldSet fieldSet) {
        ConsumerComplaint consumerComplaint = new ConsumerComplaint();

        consumerComplaint.setDateReceived((LocalDate.parse(fieldSet.readString(0), formatter)));
        consumerComplaint.setProduct(fieldSet.readString(1));
        consumerComplaint.setSubProduct(fieldSet.readString(2));
        consumerComplaint.setIssue(fieldSet.readString(3));
        consumerComplaint.setSubIssue(fieldSet.readString(4));
        consumerComplaint.setConsumerComplaintNarrative(fieldSet.readString(5));
        consumerComplaint.setCompanyPublicResponse(fieldSet.readString(6));
        consumerComplaint.setCompany(fieldSet.readString(7));
        consumerComplaint.setState(fieldSet.readString(8));
        consumerComplaint.setZipCode(fieldSet.readString(9));
        consumerComplaint.setTags(fieldSet.readString(10));
        consumerComplaint.setConsumerConsentProvided(fieldSet.readString(11));
        consumerComplaint.setSubmittedVia(fieldSet.readString(12));
        consumerComplaint.setDateSentToCompany(LocalDate.parse(fieldSet.readString(13), formatter));
        consumerComplaint.setCompanyResponseToConsumer(fieldSet.readString(14));
        consumerComplaint.setTimelyResponse(fieldSet.readBoolean(15));
        consumerComplaint.setConsumerDisputed(fieldSet.readString(16));
        consumerComplaint.setComplaintId(fieldSet.readLong(17));

        return consumerComplaint;
    }
}
