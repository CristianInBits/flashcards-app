package com.CristianInBits.flashcards.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CristianInBits.flashcards.models.Flashcard;
import com.CristianInBits.flashcards.models.LearningStatus;

public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {

    List<Flashcard> findByTopic(String topic);

    List<Flashcard> findByStatus(LearningStatus status);

    List<Flashcard> findByTopicAndStatus(String topic, LearningStatus status);

    List<Flashcard> findByCollection(String collection);

    List<Flashcard> findByCollectionAndTopic(String collection, String topic);

    List<Flashcard> findByCollectionAndNextReviewDateLessThanEqual(String collection, LocalDate date);

}
