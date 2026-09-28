package com.ijse.learnhub.courseservice.service;

import com.ijse.learnhub.courseservice.dto.CourseDtos.CourseRequest;
import com.ijse.learnhub.courseservice.dto.CourseDtos.CourseResponse;
import com.ijse.learnhub.courseservice.entity.Course;
import com.ijse.learnhub.courseservice.exception.ConflictException;
import com.ijse.learnhub.courseservice.exception.ResourceNotFoundException;
import com.ijse.learnhub.courseservice.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class CourseService {

    private final CourseRepository repository;
    private final int maxDurationWeeks;

    /** maxDurationWeeks is a business rule served by the Config Server (config-repo/course-service.yml). */
    public CourseService(CourseRepository repository,
                         @Value("${learnhub.course.max-duration-weeks:104}") int maxDurationWeeks) {
        this.repository = repository;
        this.maxDurationWeeks = maxDurationWeeks;
    }

    public CourseResponse create(CourseRequest request) {
        String code = normalizeCode(request.courseCode());
        checkDuration(request.durationWeeks());
        if (repository.existsByCourseCode(code)) {
            throw new ConflictException("The course code " + code + " is already in use.");
        }
        Course course = new Course();
        applyRequest(course, request, code);
        return toResponse(repository.save(course));
    }

    public List<CourseResponse> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream().map(this::toResponse).toList();
    }

    public CourseResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    public CourseResponse update(Long id, CourseRequest request) {
        Course course = getOrThrow(id);
        String code = normalizeCode(request.courseCode());
        checkDuration(request.durationWeeks());
        if (repository.existsByCourseCodeAndIdNot(code, id)) {
            throw new ConflictException("The course code " + code + " is already in use.");
        }
        applyRequest(course, request, code);
        return toResponse(repository.save(course));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id " + id);
        }
        repository.deleteById(id);
    }

    private Course getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + id));
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private void checkDuration(Integer weeks) {
        if (weeks != null && weeks > maxDurationWeeks) {
            throw new IllegalArgumentException("A course can run for at most " + maxDurationWeeks + " weeks.");
        }
    }

    private void applyRequest(Course course, CourseRequest request, String code) {
        course.setCourseCode(code);
        course.setTitle(request.title().trim());
        course.setDescription(request.description() == null ? null : request.description().trim());
        course.setDurationWeeks(request.durationWeeks());
        course.setFee(request.fee());
    }

    private CourseResponse toResponse(Course c) {
        return new CourseResponse(
                c.getId(), c.getCourseCode(), c.getTitle(), c.getDescription(),
                c.getDurationWeeks(), c.getFee(), c.getCreatedAt()
        );
    }
}
