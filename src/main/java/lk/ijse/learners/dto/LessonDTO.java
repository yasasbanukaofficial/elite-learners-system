package lk.ijse.learners.dto;

import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LessonDTO {
    private String lessonId;
    private String instructorId;
    private String courseId;
    private String student;
    private String name;
    private Timestamp start_time;
    private Timestamp end_time;
    private String status;
}
