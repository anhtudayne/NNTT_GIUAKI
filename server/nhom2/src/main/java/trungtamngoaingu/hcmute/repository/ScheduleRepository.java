package trungtamngoaingu.hcmute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import trungtamngoaingu.hcmute.entity.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
  @Query("SELECT s FROM Schedule s")
  List<Schedule> myGetAll();

  // Các truy vấn phục vụ việc kiểm tra trùng lịch theo ngày và khoảng thời gian
  List<Schedule> findByDate(LocalDate date);

  // Lấy các lịch trùng khoảng thời gian trên một ngày (overlap time window)
  List<Schedule> findByDateAndStartTimeBeforeAndEndTimeAfter(LocalDate date, LocalTime end, LocalTime start);
}
