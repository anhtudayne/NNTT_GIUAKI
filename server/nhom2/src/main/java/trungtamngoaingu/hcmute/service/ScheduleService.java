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
}
