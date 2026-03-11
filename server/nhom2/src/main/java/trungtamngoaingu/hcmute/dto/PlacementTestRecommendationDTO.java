package trungtamngoaingu.hcmute.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import trungtamngoaingu.hcmute.entity.Class;
import trungtamngoaingu.hcmute.entity.Course;
import trungtamngoaingu.hcmute.entity.PlacementTest;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlacementTestRecommendationDTO {
    private PlacementTest placementTest;
    private String recommendedLevel;
    private List<Course> suggestedCourses;
    private List<Class> suggestedClasses;
}

