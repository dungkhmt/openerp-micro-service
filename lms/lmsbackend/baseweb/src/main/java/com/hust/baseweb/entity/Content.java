package com.hust.baseweb.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "content_id")
    private UUID contentId;
    private String contentTypeId;
    private String mimeType;
    private String characterSet;
    private String url;
    private Date createdAt;
    private Date lastUpdatedAt;

    public Content(String contentTypeId, String url, Date createdAt) {
        this.contentTypeId = contentTypeId;
        this.url = url;
        this.createdAt = createdAt;
    }

    public Content() {
    }

}
