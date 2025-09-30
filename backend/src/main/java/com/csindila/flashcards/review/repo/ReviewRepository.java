package com.csindila.flashcards.review.repo;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.csindila.flashcards.review.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findByDueAtLessThanEqualOrderByDueAtAsc(OffsetDateTime now, Pageable pageable);

    Optional<Review> findById(UUID cardId); // alias semántico
}