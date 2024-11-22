package com.AI_Inspection.AI_Inspection.repo;

import com.AI_Inspection.AI_Inspection.entity.DocumentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DocumentDetailsRepo extends JpaRepository<DocumentDetails, Integer> {

    DocumentDetails findTopByOrderByIdDesc();
}
