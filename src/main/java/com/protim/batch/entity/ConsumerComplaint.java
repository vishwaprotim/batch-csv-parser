package com.protim.batch.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "CONSUMER_COMPLAINT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerComplaint {

    private LocalDate dateReceived;
    private String product;
    private String subProduct;
    private String issue;
    private String subIssue;
    @Column(columnDefinition="TEXT") // TODO : Which one to choose and why, also step listener, job listeners, read skip listener, write transaction listener, CSV record restart listener
    @Lob
    private String consumerComplaintNarrative; // This is a large text. Default Varchar(255) will not suffice. Hence, we use LOB
    private String companyPublicResponse;
    private String company;
    private String state;
    private String zipCode;
    private String tags;
    private String consumerConsentProvided;
    private String submittedVia;
    private LocalDate dateSentToCompany;
    private String companyResponseToConsumer;
    private boolean timelyResponse;
    private String consumerDisputed;
    @Id
    private long complaintId;

}
