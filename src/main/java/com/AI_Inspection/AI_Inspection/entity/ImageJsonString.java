package com.AI_Inspection.AI_Inspection.entity;


import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "image_json_string")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageJsonString {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Lob
    private String imagejson;

    private String imageName;
}
