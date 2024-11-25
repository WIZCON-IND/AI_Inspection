package com.AI_Inspection.AI_Inspection.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "image_weightage")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageWeightage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String category;

    private int weightage;

    private String description;

    private String imageName;
}
