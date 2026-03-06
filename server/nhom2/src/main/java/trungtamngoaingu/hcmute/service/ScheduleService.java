package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Schedule;
import trungtamngoaingu.hcmute.repository.ScheduleRepository;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.myGetAll();
    }

    public Optional<Schedule> getScheduleById(Integer id) {
        return scheduleRepository.findById(id);
    }

    public Schedule createSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public Schedule updateSchedule(Integer id, Schedule schedule) {
        if (scheduleRepository.existsById(id)) {
            schedule.setScheduleId(id);
            return scheduleRepository.save(schedule);
        }
        return null;
    }

    public void deleteSchedule(Integer id) {
        scheduleRepository.deleteById(id);
    }

    // ===================================================================================
    // TÍNH NĂNG KHÓ: KIỂM TRA PHÒNG TRỐNG VÀ GIÁO VIÊN RẢNH BẰNG STREAM API (LAMBDA)
    // ===================================================================================

    @Autowired
    private trungtamngoaingu.hcmute.repository.RoomRepository roomRepository;
    @Autowired
    private trungtamngoaingu.hcmute.repository.TeacherRepository teacherRepository;

    /**
     * TÌM PHÒNG TRỐNG (Available Rooms)
     * - Lấy tất cả các phòng hiện có.
     * - Lấy tất cả lịch học. Lọc (filter) ra danh sách lịch học bị trùng ngày và giờ.
     * - Dùng Stream lấy ID các phòng đang bị trùng lịch (occupiedRoomIds).
     * - Cuối cùng, dùng Filter lọc lại các phòng KHÔNG có ID nằm trong nhóm bị trùng.
     */
    public List<trungtamngoaingu.hcmute.entity.Room> getAvailableRooms(java.time.LocalDate targetDate, java.time.LocalTime start, java.time.LocalTime end) {
        List<trungtamngoaingu.hcmute.entity.Room> allRooms = roomRepository.findAll();
        List<Schedule> allSchedules = scheduleRepository.findAll();

        // 1. Dùng Stream lấy danh sách ID của các phòng CÓ LỊCH TRÙNG vắt ngang thời gian start và end
        List<Integer> occupiedRoomIds = allSchedules.stream()
                .filter(s -> s.getDate().equals(targetDate)) // Có cùng ngày
                // Logic trùng giờ: (sStart < end) VÀ (sEnd > start) -> Có sự giao nhau về thời gian
                .filter(s -> s.getStartTime().isBefore(end) && s.getEndTime().isAfter(start))
                .map(s -> s.getRoom().getRoomId()) // Trích xuất ra RoomID
                .toList();

        // 2. Trả về các phòng "Sạch" (Trạng thái Available VÀ Id không nằm trong danh sách đen)
        return allRooms.stream()
                .filter(room -> room.getStatus() == trungtamngoaingu.hcmute.entity.Room.Status.Available)
                .filter(room -> !occupiedRoomIds.contains(room.getRoomId()))
                .toList();
    }

    /**
     * TÌM GIÁO VIÊN RẢNH (Available Teachers)
     * - Tương tự như quy trình check phòng trống, ta quét bảng Schedule nhưng lấy ra TeacherID.
     * - Cấu trúc: Schedule -> Class -> Teacher.
     */
    public List<trungtamngoaingu.hcmute.entity.Teacher> getAvailableTeachers(java.time.LocalDate targetDate, java.time.LocalTime start, java.time.LocalTime end) {
        List<trungtamngoaingu.hcmute.entity.Teacher> allTeachers = teacherRepository.findAll();
        List<Schedule> allSchedules = scheduleRepository.findAll();

        // 1. Dùng Stream để quét các giáo viên dính lịch
        List<Integer> occupiedTeacherIds = allSchedules.stream()
                .filter(s -> s.getDate().equals(targetDate))
                .filter(s -> s.getStartTime().isBefore(end) && s.getEndTime().isAfter(start))
                .filter(s -> s.getClassEntity() != null && s.getClassEntity().getTeacher() != null)
                .map(s -> s.getClassEntity().getTeacher().getTeacherId()) // Trích xuất TeacherID
                .toList();

        // 2. Lọc các giáo viên đang Active và KHÔNG dính lịch
        return allTeachers.stream()
                .filter(teacher -> teacher.getStatus() == trungtamngoaingu.hcmute.entity.Teacher.Status.Active)
                .filter(teacher -> !occupiedTeacherIds.contains(teacher.getTeacherId()))
                .toList();
    }
}
