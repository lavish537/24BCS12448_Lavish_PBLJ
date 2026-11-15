package com.example.sms.service;

import com.example.sms.dto.PageResponse;
import com.example.sms.dto.StudentRequest;
import com.example.sms.dto.StudentResponse;
import com.example.sms.exception.BusinessRuleException;
import com.example.sms.exception.DuplicateResourceException;
import com.example.sms.exception.ResourceNotFoundException;
import com.example.sms.model.Course;
import com.example.sms.model.Student;
import com.example.sms.repository.CourseRepository;
import com.example.sms.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(readOnly = true)
public class StudentService {

    private static final Set<String> SORTABLE_FIELDS =
            Set.of("id", "firstName", "lastName", "email", "dateOfBirth", "enrollmentDate");

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public StudentService(StudentRepository studentRepository, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public StudentResponse create(StudentRequest request) {
        if (studentRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Student with email " + request.email() + " already exists");
        }
        Student student = new Student();
        apply(student, request);
        return toResponse(studentRepository.save(student));
    }

    public StudentResponse getById(Long id) {
        return toResponse(findStudent(id));
    }

    public PageResponse<StudentResponse> getAll(int page, int size, String sortBy, String direction,
                                                String keyword, Long courseId) {
        if (!SORTABLE_FIELDS.contains(sortBy)) {
            throw new BusinessRuleException("Cannot sort by '" + sortBy + "'. Allowed fields: " + SORTABLE_FIELDS);
        }
        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), sort);
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        Page<Student> result = studentRepository.search(kw, courseId, pageable);
        return PageResponse.from(result.map(this::toResponse));
    }

    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = findStudent(id);
        if (studentRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateResourceException("Student with email " + request.email() + " already exists");
        }
        apply(student, request);
        return toResponse(studentRepository.save(student));
    }

    @Transactional
    public void delete(Long id) {
        Student student = findStudent(id);
        studentRepository.delete(student);
    }

    // ---------- helpers ----------

    private Student findStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }

    private void apply(Student student, StudentRequest r) {
        student.setFirstName(r.firstName().trim());
        student.setLastName(r.lastName().trim());
        student.setEmail(r.email().trim());
        student.setPhone(r.phone());
        student.setDateOfBirth(r.dateOfBirth());
        student.setGender(r.gender());
        student.setAddress(r.address());
        student.setEnrollmentDate(r.enrollmentDate());

        if (r.courseId() != null) {
            Course course = courseRepository.findById(r.courseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course", r.courseId()));
            student.setCourse(course);
        } else {
            student.setCourse(null);
        }
    }

    private StudentResponse toResponse(Student s) {
        Course c = s.getCourse();
        return new StudentResponse(
                s.getId(), s.getFirstName(), s.getLastName(), s.getEmail(), s.getPhone(),
                s.getDateOfBirth(), s.getGender(), s.getAddress(), s.getEnrollmentDate(),
                c != null ? c.getId() : null,
                c != null ? c.getName() : null);
    }
}
