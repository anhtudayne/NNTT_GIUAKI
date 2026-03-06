package client_ttnn.hcmute.model;

public class PlacementTest {
    private Long testId;
    private Student student;
    private String testDate;
    private Double score;
    private String recommendedLevel;

    public PlacementTest() {
    }

    public PlacementTest(Student student, String testDate, Double score, String recommendedLevel) {
        this.student = student;
        this.testDate = testDate;
        this.score = score;
        this.recommendedLevel = recommendedLevel;
    }

    public Long getPlacementTestId() {
        return testId;
    }

    public void setPlacementTestId(Long placementTestId) {
        this.testId = placementTestId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getTestDate() {
        return testDate;
    }

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getRecommendedLevel() {
        return recommendedLevel;
    }

    public void setRecommendedLevel(String recommendedLevel) {
        this.recommendedLevel = recommendedLevel;
    }
}
