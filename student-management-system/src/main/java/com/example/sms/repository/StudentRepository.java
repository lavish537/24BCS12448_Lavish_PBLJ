package com.example.sms.repository;

import com.example.sms.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    long countByCourseId(Long courseId);

    @EntityGraph(attributePaths = "course")
    @Query("SELECT s FROM Student s WHERE "
            + "(:keyword IS NULL OR LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
            + "AND (:courseId IS NULL OR s.course.id = :courseId)")
    Page<Student> search(@Param("keyword") String keyword,
                         @Param("courseId") Long courseId,
                         Pageable pageable);
}
