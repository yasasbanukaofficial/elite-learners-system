package lk.ijse.learners.dto;

import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LessonDTO {
    private Long lessonId;
    private InstructorDTO instructor;
    private CourseDTO course;
    private StudentDTO student;
    private String name;
    private Timestamp start_time;
    private Timestamp end_time;
    private boolean status;
}
