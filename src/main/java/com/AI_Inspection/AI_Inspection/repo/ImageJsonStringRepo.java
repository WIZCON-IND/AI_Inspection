package com.AI_Inspection.AI_Inspection.repo;

import com.AI_Inspection.AI_Inspection.entity.ImageJsonString;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageJsonStringRepo extends JpaRepository<ImageJsonString, Integer> {

}
