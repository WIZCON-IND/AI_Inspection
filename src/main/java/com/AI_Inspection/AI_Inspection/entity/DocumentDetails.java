package com.AI_Inspection.AI_Inspection.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "document_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String documentName;

    private String Address;

}
