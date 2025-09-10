package lk.ijse.learners.bo.util;

import lk.ijse.learners.dao.DAOFactory;
import lk.ijse.learners.dao.custom.StudentDAO;
import lk.ijse.learners.dto.*;
import lk.ijse.learners.entity.*;

import java.util.ArrayList;
import java.util.List;

public class EntityDTOConverter {
    private final StudentDAO studentDAO = (StudentDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.STUDENT);


    public StudentDTO getStudentDTO(Student student) throws Exception {
        return new StudentDTO(
                student.getStudentId(),
                student.getFirstName(),
                student.getLastName(),
                student.getDob(),
                student.getEmail(),
                student.getContactNumber(),
                student.getAddress(),
                toPaymentDTOList(student.getPayments()),
                toLessonDTOList(student.getLessons()),
                toStudentCourseDetailsDTOList(student.getStudentCourseDetails())
        );
    }

    public List<StudentDTO> toStudentDTOList(List<Student> studentList) throws Exception {
        List<StudentDTO> studentDTOList = new ArrayList<>();
        studentList.forEach(student -> {
            try {
                studentDTOList.add(getStudentDTO(student));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return studentDTOList;
    }

    public Student getStudentEntity(StudentDTO studentDTO) throws Exception {
        return new Student(
                studentDTO.getStudentId(),
                studentDTO.getFirstName(),
                studentDTO.getLastName(),
                studentDTO.getDob(),
                studentDTO.getEmail(),
                studentDTO.getContactNumber(),
                studentDTO.getAddress(),
                toPaymentEntityList(studentDTO.getPayments()),
                toLessonEntityList(studentDTO.getLessons()),
                toStudentCourseDetailsEntityList(studentDTO.getStudentCourseDetails())
        );
    }

    public List<Student> toStudentEntityList(List<StudentDTO> studentList) throws Exception {
        List<Student> studentEntityList = new ArrayList<>();
        studentList.forEach(student -> {
            try {
                studentEntityList.add(getStudentEntity(student));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return studentEntityList;
    }


    public PaymentDTO getPaymentDTO(Payment payment) throws Exception {
        return new PaymentDTO(
            payment.getPaymentId(),
            payment.getStudent().getStudentId(),
            payment.getPaymentDate(),
            payment.getType(),
            payment.getAmount(),
            payment.getStatus()
        );
    }

    public List<PaymentDTO> toPaymentDTOList(List<Payment> paymentList) throws Exception {
        List<PaymentDTO> paymentDTOList = new ArrayList<>();
        paymentList.forEach(payment -> {
            try {
                paymentDTOList.add(getPaymentDTO(payment));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return paymentDTOList;
    }

    public Payment getPaymentEntity(PaymentDTO paymentDTO) throws Exception {
        return new Payment(
                paymentDTO.getPaymentId(),
                studentDAO.findById(paymentDTO.getStudentId()).orElse(null),
                paymentDTO.getPaymentDate(),
                paymentDTO.getType(),
                paymentDTO.getAmount(),
                paymentDTO.getStatus()
        );
    }

    public List<Payment> toPaymentEntityList(List<PaymentDTO> paymentDTOList) throws Exception {
        List<Payment> paymentEntityList = new ArrayList<>();
        paymentDTOList.forEach(paymentDTO -> {
            try {
                paymentEntityList.add(getPaymentEntity(paymentDTO));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return paymentEntityList;
    }


    public LessonDTO getLessonDTO(Lesson lesson) throws Exception {
        return new LessonDTO(
                lesson.getLessonId(),
                lesson.getInstructor().getInstructorId(),
                lesson.getCourse().getCourseId(),
                lesson.getStudent().getStudentId(),
                lesson.getName(),
                lesson.getStart_time(),
                lesson.getEnd_time(),
                lesson.getStatus()
        );
    }

    public List<LessonDTO> toLessonDTOList(List<Lesson> lessonList) throws Exception {
        List<LessonDTO> lessonDTOList = new ArrayList<>();
        lessonList.forEach(lesson -> {
            try {
                lessonDTOList.add(getLessonDTO(lesson));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return lessonDTOList;
    }

    public Lesson getLessonEntity(LessonDTO lessonDTO) throws Exception {
        Instructor instructor = new Instructor();
        instructor.setInstructorId(lessonDTO.getInstructorId());

        Course course = new Course();
        course.setCourseId(lessonDTO.getCourseId());

        Student student = new Student();
        student.setStudentId(lessonDTO.getStudentId());

        return new Lesson(
                lessonDTO.getLessonId(),
                instructor,
                course,
                student,
                lessonDTO.getName(),
                lessonDTO.getStart_time(),
                lessonDTO.getEnd_time(),
                lessonDTO.getStatus()
        );
    }

    public List<Lesson> toLessonEntityList(List<LessonDTO> lessonDTOList) throws Exception {
        List<Lesson> lessonEntityList = new ArrayList<>();
        lessonDTOList.forEach(lessonDTO -> {
            try {
                lessonEntityList.add(getLessonEntity(lessonDTO));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return lessonEntityList;
    }


    public StudentCourseDetailsDTO getStudentCourseDetailsDTO(StudentCourseDetails studentCourseDetails) throws Exception {
        return new StudentCourseDetailsDTO(
                studentCourseDetails.getStudentCourseDetailsId(),
                studentCourseDetails.getStudent().getStudentId(),
                studentCourseDetails.getCourse().getCourseId(),
                studentCourseDetails.getEnrollmentDate(),
                studentCourseDetails.getStatus(),
                studentCourseDetails.getGrade()
        );
    }

    public List<StudentCourseDetailsDTO> toStudentCourseDetailsDTOList(List<StudentCourseDetails> studentCourseDetailsList) throws Exception {
        List<StudentCourseDetailsDTO> studentCourseDetailsDTOList = new ArrayList<>();
        studentCourseDetailsList.forEach(studentCourseDetails -> {
            try {
                studentCourseDetailsDTOList.add(getStudentCourseDetailsDTO(studentCourseDetails));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return studentCourseDetailsDTOList;
    }

    public StudentCourseDetails getStudentCourseDetailsEntity(StudentCourseDetailsDTO studentCourseDetailsDTO) throws Exception {
        Student student = new Student();
        student.setStudentId(studentCourseDetailsDTO.getStudentId());

        Course course = new Course();
        course.setCourseId(studentCourseDetailsDTO.getCourseId());

        return new StudentCourseDetails(
                studentCourseDetailsDTO.getStudentCourseDetailsId(),
                student,
                course,
                studentCourseDetailsDTO.getEnrollmentDate(),
                studentCourseDetailsDTO.getStatus(),
                studentCourseDetailsDTO.getGrade()
        );
    }

    public List<StudentCourseDetails> toStudentCourseDetailsEntityList(List<StudentCourseDetailsDTO> studentCourseDetailsDTOList) throws Exception {
        List<StudentCourseDetails> studentCourseDetailsEntityList = new ArrayList<>();
        studentCourseDetailsDTOList.forEach(studentCourseDetailsDTO -> {
            try {
                studentCourseDetailsEntityList.add(getStudentCourseDetailsEntity(studentCourseDetailsDTO));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return studentCourseDetailsEntityList;
    }


    public InstructorDTO getInstructorDTO(Instructor instructor) throws Exception {
        return new InstructorDTO(
                instructor.getInstructorId(),
                instructor.getName(),
                instructor.getDob(),
                instructor.getEmail(),
                instructor.getContact(),
                instructor.getSpeciality(),
                instructor.getAvailability(),
                toCourseDTOList(instructor.getCourses()),
                toLessonDTOList(instructor.getLessons())
        );
    }

    public List<InstructorDTO> toInstructorDTOList(List<Instructor> instructorList) throws Exception {
        List<InstructorDTO> instructorDTOList = new ArrayList<>();
        instructorList.forEach(instructor -> {
            try {
                instructorDTOList.add(getInstructorDTO(instructor));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return instructorDTOList;
    }

    public Instructor getInstructorEntity(InstructorDTO instructorDTO) throws Exception {
        return new Instructor(
                instructorDTO.getInstructorId(),
                instructorDTO.getName(),
                instructorDTO.getDob(),
                instructorDTO.getEmail(),
                instructorDTO.getContact(),
                instructorDTO.getSpeciality(),
                instructorDTO.getAvailability(),
                toCourseEntityList(instructorDTO.getCourses()),
                toLessonEntityList(instructorDTO.getLessons())
        );
    }

    public List<Instructor> toInstructorEntityList(List<InstructorDTO> instructorDTOList) throws Exception {
        List<Instructor> instructorEntityList = new ArrayList<>();
        instructorDTOList.forEach(instructorDTO -> {
            try {
                instructorEntityList.add(getInstructorEntity(instructorDTO));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return instructorEntityList;
    }


    public CourseDTO getCourseDTO(Course course) throws Exception {
        return new CourseDTO(
                course.getCourseId(),
                course.getInstructor().getInstructorId(),
                course.getName(),
                course.getDescription(),
                course.getType(),
                toLessonDTOList(course.getLessons()),
                toStudentCourseDetailsDTOList(course.getStudentCourseDetails())
        );
    }

    public Course getCourseEntity(CourseDTO courseDTO) throws Exception {
        Instructor instructor = new Instructor();
        instructor.setInstructorId(courseDTO.getInstructorId());

        return new Course(
                courseDTO.getCourseId(),
                instructor,
                courseDTO.getName(),
                courseDTO.getDescription(),
                courseDTO.getType(),
                toLessonEntityList(courseDTO.getLessons()),
                toStudentCourseDetailsEntityList(courseDTO.getStudentCourseDetails())
        );
    }

    public List<CourseDTO> toCourseDTOList(List<Course> courseList) throws Exception {
        List<CourseDTO> courseDTOList = new ArrayList<>();
        courseList.forEach(course -> {
            try {
                courseDTOList.add(getCourseDTO(course));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return courseDTOList;
    }

    public List<Course> toCourseEntityList(List<CourseDTO> courseDTOList) throws Exception {
        List<Course> courseEntityList = new ArrayList<>();
        courseDTOList.forEach(courseDTO -> {
            try {
                courseEntityList.add(getCourseEntity(courseDTO));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return courseEntityList;
    }

}
