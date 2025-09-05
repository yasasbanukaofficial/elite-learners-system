package lk.ijse.learners.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CourseDto {
    private String courseId;
    private InstructorDto instructor;
    private String name;
    private String description;
    private String type;
    private List<LessonDto> lessons;
    private List<CourseDto> courses;
}
