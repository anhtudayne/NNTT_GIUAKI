package client_ttnn.hcmute.util;

import client_ttnn.hcmute.model.Classes;
import client_ttnn.hcmute.model.Course;
import client_ttnn.hcmute.model.Room;
import client_ttnn.hcmute.model.Teacher;
import client_ttnn.hcmute.service.ClassesApiService;
import client_ttnn.hcmute.service.CourseApiService;
import client_ttnn.hcmute.service.RoomApiService;
import client_ttnn.hcmute.service.TeacherApiService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Lớp tiện ích giúp quản lý bộ nhớ đệm (Cache) cho các dữ liệu ít thay đổi ở Frontend.
 * Điều này tránh việc gọi API nhiều lần mỗi khi mở lại các Panel.
 */
public class CacheManager {
    private static CacheManager instance;

    private List<Course> cachedCourses;
    private List<Room> cachedRooms;
    private List<Teacher> cachedTeachers;
    private List<Classes> cachedClasses;

    private final CourseApiService courseApiService = new CourseApiService();
    private final RoomApiService roomApiService = new RoomApiService();
    private final TeacherApiService teacherApiService = new TeacherApiService();
    private final ClassesApiService classesApiService = new ClassesApiService();

    private CacheManager() {
        // Private constructor for Singleton
    }

    public static synchronized CacheManager getInstance() {
        if (instance == null) {
            instance = new CacheManager();
        }
        return instance;
    }

    // --- COURSES ---
    public List<Course> getCourses() {
        if (cachedCourses == null) {
            try {
                cachedCourses = courseApiService.getAllCourses();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cachedCourses;
    }

    public void invalidateCourses() {
        cachedCourses = null;
    }

    // --- ROOMS ---
    public List<Room> getRooms() {
        if (cachedRooms == null) {
            try {
                cachedRooms = roomApiService.getAllRooms();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cachedRooms;
    }

    public void invalidateRooms() {
        cachedRooms = null;
    }

    // --- TEACHERS ---
    public List<Teacher> getTeachers() {
        if (cachedTeachers == null) {
            try {
                cachedTeachers = teacherApiService.getAllTeachers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cachedTeachers;
    }

    public void invalidateTeachers() {
        cachedTeachers = null;
    }

    // --- CLASSES ---
    public List<Classes> getClasses() {
        if (cachedClasses == null) {
            try {
                cachedClasses = classesApiService.getAllClasses();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cachedClasses;
    }

    public void invalidateClasses() {
        cachedClasses = null;
    }

    // Phương thức tải nền (Background Pre-fetch) toàn bộ Cache
    public void prefetchDataAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                getCourses();
                getRooms();
                getTeachers();
                getClasses();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
