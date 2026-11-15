package com.example.sms.service;

import com.example.sms.dto.CourseRequest;
import com.example.sms.dto.CourseResponse;
import com.example.sms.exception.BusinessRuleException;
import com.example.sms.exception.DuplicateResourceException;
import com.example.sms.exception.ResourceNotFoundException;
import com.example.sms.model.Course;
import com.example.sms.repository.CourseRepository;
import com.example.sms.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public CourseService(CourseRepository courseRepository, StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public CourseResponse create(CourseRequest request) {
        if (courseRepository.existsByCourseCode(request.courseCode())) {
            throw new DuplicateResourceException("Course with code " + request.courseCode() + " already exists");
        }
        Course course = new Course();
        apply(course, request);
        return toResponse(courseRepository.save(course));
    }

    public List<CourseResponse> getAll() {
        return courseRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CourseResponse getById(Long id) {
        return toResponse(findCourse(id));
    }

    @Transactional
    public CourseResponse update(Long id, CourseRequest request) {
        Course course = findCourse(id);
        if (courseRepository.existsByCourseCodeAndIdNot(request.courseCode(), id)) {
            throw new DuplicateResourceException("Course with code " + request.courseCode() + " already exists");
        }
        apply(course, request);
        return toResponse(courseRepository.save(course));
    }

    @Transactional
    public void delete(Long id) {
        Course course = findCourse(id);
        if (studentRepository.countByCourseId(id) > 0) {
            throw new BusinessRuleException("Cannot delete course with enrolled students");
        }
        courseRepository.delete(course);
    }

    private Course findCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
    }

    private void apply(Course course, CourseRequest request) {
        course.setCourseCode(request.courseCode().trim());
        course.setName(request.name().trim());
        course.setDescription(request.description());
        course.setDurationInMonths(request.durationInMonths());
    }

    private CourseResponse toResponse(Course c) {
        return new CourseResponse(c.getId(), c.getCourseCode(), c.getName(),
                c.getDescription(), c.getDurationInMonths());
    }
}
