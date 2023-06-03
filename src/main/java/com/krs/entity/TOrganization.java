package com.krs.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "tOrganizations")
public class TOrganization {
    private String organizationName;
    private String accessToken;
}