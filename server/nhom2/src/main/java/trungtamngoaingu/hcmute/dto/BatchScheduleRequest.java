package trungtamngoaingu.hcmute.dto;

import lombok.Data;
import java.util.List;

@Data
public class BatchScheduleRequest {
    private Long classId;
    private Long roomId;
    private String startDate; // YYYY-MM-DD
    private String endDate;   // YYYY-MM-DD
    private List<Integer> daysOfWeek; // List of Days (1=Monday, 7=Sunday)
    private String startTime; // HH:mm:ss
    private String endTime;   // HH:mm:ss
}
