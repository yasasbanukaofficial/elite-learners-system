package lk.ijse.learners.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CourseDTO {
    private String courseId;
    private String name;
    private String description;
    private String duration;
    private String fees;
    private List<InstructorDTO> instructors;
    private List<LessonDTO> lessons;
    private List<StudentDTO> students;

    public CourseDTO(String courseId, String name, String description, String duration, String fees) {
        this.courseId = courseId;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.fees = fees;
    }
}
