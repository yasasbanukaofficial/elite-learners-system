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
    private String instructorId;
    private String name;
    private String description;
    private String type;
    private List<LessonDTO> lessons;
    private List<CourseDTO> courses;
}
