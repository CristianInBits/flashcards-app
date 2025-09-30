package com.csindila.flashcards.review.repo;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.csindila.flashcards.review.domain.ReviewLog;

public interface ReviewLogRepository extends JpaRepository<ReviewLog, UUID> {

}
