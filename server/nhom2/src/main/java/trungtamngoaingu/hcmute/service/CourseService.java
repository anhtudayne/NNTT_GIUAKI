package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Course;
import trungtamngoaingu.hcmute.repository.CourseRepository;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.myGetAll();
    }

    // Lấy 1 khóa học theo ID bằng Stream
    public Optional<Course> getCourseById(Integer id) {
        return courseRepository.myGetAll().stream()
                .filter(c -> c.getCourseId().equals(id))
                .findFirst();
    }

    // Tạo mới khóa học (vẫn dùng save để lưu xuống DB)
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    // Cập nhật khóa học bằng cách kiểm tra tồn tại qua Stream
    public Course updateCourse(Integer id, Course course) {
        boolean exists = courseRepository.myGetAll().stream()
                .anyMatch(c -> c.getCourseId().equals(id));

        if (exists) {
            course.setCourseId(id);
            return courseRepository.save(course);
        }
        return null;
    }

    // Xóa khóa học sau khi lọc ID
    public void deleteCourse(Integer id) {
        courseRepository.myGetAll().stream()
                .filter(c -> c.getCourseId().equals(id))
                .findFirst()
                .ifPresent(c -> courseRepository.deleteById(c.getCourseId()));
    }

    // public Optional<Course> getCourseById(Integer id) {
    //     return courseRepository.findById(id);
    // }

    // public Course createCourse(Course course) {
    //     return courseRepository.save(course);
    // }

    // public Course updateCourse(Integer id, Course course) {
    //     if (courseRepository.existsById(id)) {
    //         course.setCourseId(id);
    //         return courseRepository.save(course);
    //     }
    //     return null;
    // }

    // public void deleteCourse(Integer id) {
    //     courseRepository.deleteById(id);
    // }
}
