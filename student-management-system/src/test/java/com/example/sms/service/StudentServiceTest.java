package com.example.sms.service;

import com.example.sms.dto.StudentRequest;
import com.example.sms.dto.StudentResponse;
import com.example.sms.exception.DuplicateResourceException;
import com.example.sms.exception.ResourceNotFoundException;
import com.example.sms.model.Course;
import com.example.sms.model.Gender;
import com.example.sms.model.Student;
import com.example.sms.repository.CourseRepository;
import com.example.sms.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private StudentService studentService;

    private StudentRequest request;

    @BeforeEach
    void setUp() {
        request = new StudentRequest("Aarav", "Sharma", "aarav@example.com", "9876543210",
                LocalDate.of(2004, 5, 17), Gender.MALE, "Sri Ganganagar",
                LocalDate.of(2024, 7, 1), 1L);
    }

    @Test
    void create_withValidData_savesAndReturnsStudent() {
        Course course = new Course();
        course.setId(1L);
        course.setName("BCA");

        when(studentRepository.existsByEmail("aarav@example.com")).thenReturn(false);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> {
            Student s = inv.getArgument(0);
            s.setId(10L);
            return s;
        });

        StudentResponse response = studentService.create(request);

        assertEquals(10L, response.id());
        assertEquals("Aarav", response.firstName());
        assertEquals(1L, response.courseId());
        assertEquals("BCA", response.courseName());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void create_withExistingEmail_throwsDuplicateException() {
        when(studentRepository.existsByEmail("aarav@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> studentService.create(request));
        verify(studentRepository, never()).save(any());
    }

    @Test
    void create_withUnknownCourse_throwsNotFound() {
        when(studentRepository.existsByEmail("aarav@example.com")).thenReturn(false);
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentService.create(request));
        verify(studentRepository, never()).save(any());
    }

    @Test
    void getById_withUnknownId_throwsNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentService.getById(99L));
    }

    @Test
    void delete_withExistingId_deletesStudent() {
        Student student = new Student();
        student.setId(5L);
        when(studentRepository.findById(5L)).thenReturn(Optional.of(student));

        studentService.delete(5L);

        verify(studentRepository, times(1)).delete(student);
    }
}
