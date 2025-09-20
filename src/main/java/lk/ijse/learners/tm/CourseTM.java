package lk.ijse.learners.tm;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CourseTM {
    private String courseId;
    private String name;
    private String duration;
    private String fee;
}
