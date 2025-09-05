package lk.ijse.learners.dto;

import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LessonDto {
    private Long lessonId;
    private InstructorDto instructor;
    private CourseDto course;
    private StudentDto student;
    private String name;
    private Timestamp start_time;
    private Timestamp end_time;
    private boolean status;
}
