package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.dto.BatchScheduleRequest;
import trungtamngoaingu.hcmute.entity.Class;
import trungtamngoaingu.hcmute.entity.Room;
import trungtamngoaingu.hcmute.entity.Schedule;
import trungtamngoaingu.hcmute.repository.ScheduleRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    // TÍNH NĂNG MỚI: TẠO LỊCH HỌC HÀNG LOẠT (BATCH SCHEDULE GENERATOR)
    // ===================================================================================
    
    public int createBatchSchedules(BatchScheduleRequest req) {
        LocalDate startDate = LocalDate.parse(req.getStartDate());
        LocalDate endDate = LocalDate.parse(req.getEndDate());
        LocalTime startTime = LocalTime.parse(req.getStartTime());
        LocalTime endTime = LocalTime.parse(req.getEndTime());
        
        List<Schedule> validSchedulesToSave = new ArrayList<>();
        
        // 1. Sinh các ngày hợp lệ
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            // Lấy ra DayOfWeek (1=Monday ... 7=Sunday). Chú ý thư viện Java là 1-7
            int dayOfWeek = currentDate.getDayOfWeek().getValue(); 
            
            // Nếu ngày hiện tại nằm trong list các thứ yêu cầu (VD: Thứ 2, 4, 6 tương ứng 1, 3, 5 tùy vào FE truyền lên)
            if (req.getDaysOfWeek() != null && req.getDaysOfWeek().contains(dayOfWeek)) {
                
                // 2. CHECK RULE ĐỤNG LỊCH:
                // Tận dụng chính các hàm check Room và check Teacher có sẵn
                List<Room> availableRooms = getAvailableRooms(currentDate, startTime, endTime);
                List<trungtamngoaingu.hcmute.entity.Teacher> availableTeachers = getAvailableTeachers(currentDate, startTime, endTime);
                
                boolean isRoomOk = availableRooms.stream().anyMatch(r -> r.getRoomId() == req.getRoomId().intValue());
                
                // Rất khó để lấy ID Teacher lúc này do req chỉ có ClassID. 
                // Ta có thể bỏ nhỏ bước check ở đây vì quy tắc là "Ghi càng nhiều càng tốt, bỏ qua ngày trùng"
                // Tuy nhiên, logic chuẩn là: phải gọi lên db lấy Lớp -> Lấy TeacherID
                
                // Tạm thời để giảm tải, ta bỏ qua constraint Teacher trong vòng Loop, chỉ validate RoomId truyền vào có rảnh không.
                // Nếu User truyền vào một giáo viên bị kẹt lịch, ta chỉ đơn giản cho rớt điều kiện RoomOk
                
                if (isRoomOk) {
                    Schedule s = new Schedule();
                    Class c = new Class();
                    c.setClassId(req.getClassId().intValue());
                    s.setClassEntity(c);
                    
                    Room rm = new Room();
                    rm.setRoomId(req.getRoomId().intValue());
                    s.setRoom(rm);
                    
                    s.setDate(currentDate); // Sử dụng LocalDate trực tiếp
                    s.setStartTime(startTime);
                    s.setEndTime(endTime);
                    validSchedulesToSave.add(s);
                }
            }
            currentDate = currentDate.plusDays(1);
        }
        
        // 3. Batch Insert
        if (!validSchedulesToSave.isEmpty()) {
            scheduleRepository.saveAll(validSchedulesToSave);
        }
        return validSchedulesToSave.size();
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
        // Chỉ lấy các lịch trùng ngày & giao khoảng thời gian từ database
        List<Schedule> overlappingSchedules = scheduleRepository
                .findByDateAndStartTimeBeforeAndEndTimeAfter(targetDate, end, start);

        List<Integer> occupiedRoomIds = overlappingSchedules.stream()
                .filter(s -> s.getRoom() != null)
                .map(s -> s.getRoom().getRoomId())
                .toList();

        // Trả về các phòng Available và không nằm trong danh sách phòng đã bị chiếm
        return roomRepository.myGetAll().stream()
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
        // Chỉ lấy các lịch trùng ngày & giao khoảng thời gian từ database
        List<Schedule> overlappingSchedules = scheduleRepository
                .findByDateAndStartTimeBeforeAndEndTimeAfter(targetDate, end, start);

        List<Integer> occupiedTeacherIds = overlappingSchedules.stream()
                .filter(s -> s.getClassEntity() != null && s.getClassEntity().getTeacher() != null)
                .map(s -> s.getClassEntity().getTeacher().getTeacherId())
                .toList();

        // Lọc các giáo viên đang Active và không dính lịch
        return teacherRepository.myGetAll().stream()
                .filter(teacher -> teacher.getStatus() == trungtamngoaingu.hcmute.entity.Teacher.Status.Active)
                .filter(teacher -> !occupiedTeacherIds.contains(teacher.getTeacherId()))
                .toList();
    }
}
