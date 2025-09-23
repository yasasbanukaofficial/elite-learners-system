package lk.ijse.learners.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.custom.CourseBO;
import lk.ijse.learners.bo.custom.InstructorBO;
import lk.ijse.learners.bo.custom.LessonBO;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.WindowManagerUtil;
import lk.ijse.learners.dto.CourseDTO;
import lk.ijse.learners.dto.InstructorDTO;
import lk.ijse.learners.dto.LessonDTO;

import java.sql.Timestamp;
import java.util.*;

import java.net.URL;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.SpinnerValueFactory;

import java.util.ArrayList;

public class AddLessonFormController implements Initializable {
   
    public AnchorPane ancAddCourseForm;
    public Label lblLessonId;
    public TextField txtCName;
    public DatePicker dobPicker;
    public DatePicker dpStartTime;
    public DatePicker dpEndTime;
    public ListView <String> chosenInstructors;
    public ListView <String> choosenCourses;
    public VBox instructorSection;
    public ImageView btnCloseLesForm;
    public ListView <String> availableInstructors;
    public ListView <String> availableCourses;
    public Button btnCancel;
    public Button btnAddLesson;

    private final LessonBO lessonBO = (LessonBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.LESSON);
    private final CourseBO courseBO = (CourseBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.COURSE);
    private final InstructorBO instructorBO = (InstructorBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.INSTRUCTOR);
    public Spinner spinnerStartHr;
    public Spinner spinnerStartMin;
    public Spinner spinnerEndHr;
    public Spinner spinnerEndMin;
    private List<String> selectedInstructors = new ArrayList<>();
    private List<String> selectedCourses = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblLessonId.setText(loadNextId());

        SpinnerValueFactory<Integer> startHrFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 9);
        spinnerStartHr.setValueFactory(startHrFactory);

        SpinnerValueFactory<Integer> startMinFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 5);
        spinnerStartMin.setValueFactory(startMinFactory);

        SpinnerValueFactory<Integer> endHrFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 11);
        spinnerEndHr.setValueFactory(endHrFactory);

        SpinnerValueFactory<Integer> endMinFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 5);
        spinnerEndMin.setValueFactory(endMinFactory);

        try {
            List<String> instructors = instructorBO.getAllAvailableInstructors();
            if (instructors.isEmpty()) {
                Platform.runLater(() -> AlertUtil.setErrorAlert("Please add some instructors first!!"));
                btnAddLesson.setDisable(true);
                return;
            }
            availableInstructors.getItems().addAll(instructors);

            List<String> courses = courseBO.getAll().stream().map(CourseDTO::getName).toList();
            if (courses.isEmpty()) {
                Platform.runLater(() -> AlertUtil.setErrorAlert("Please add some courses first!!"));
                btnAddLesson.setDisable(true);
                return;
            }
            availableCourses.getItems().addAll(courses);

            setupListeners();
        } catch (Exception e) {
            btnAddLesson.setDisable(true);
            AlertUtil.setErrorAlert("Failed to load instructors or courses. Please try again.");
        }
    }


    private void setupListeners() {
        availableInstructors.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String selected = availableInstructors.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    selectedInstructors.clear();
                    chosenInstructors.getItems().clear();
                    selectedInstructors.add(selected);
                    chosenInstructors.getItems().add(selected);
                }
            }
        });

        availableCourses.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String selected = availableCourses.getSelectionModel().getSelectedItem();
                if (selected != null && !selectedCourses.contains(selected)) {
                    selectedCourses.add(selected);
                    choosenCourses.getItems().add(selected);
                }
            }
        });
    }

    public void closeLessonFormOnAction(MouseEvent mouseEvent) {
        WindowManagerUtil.closeForm(ancAddCourseForm);
    }

    public void closeLsnFormOnAction(MouseEvent mouseEvent) {
        WindowManagerUtil.closeForm(ancAddCourseForm);
    }

    private boolean validateLessonDetails(String name, Timestamp startTime, Timestamp endTime) {
        if (name == null || name.isEmpty()) {
            AlertUtil.setErrorAlert("Lesson name cannot be empty");
            return false;
        }
        if (startTime == null || endTime == null) {
            AlertUtil.setErrorAlert("Lesson time must be set");
            return false;
        }
        if (startTime.after(endTime)) {
            AlertUtil.setErrorAlert("Start time cannot be after end time");
            return false;
        }
        return true;
    }

    public void addLsn(ActionEvent actionEvent) {
        String lessonId = lblLessonId.getText();
        String name = txtCName.getText();

        Timestamp startTime = Timestamp.valueOf(dpStartTime.getValue().atTime(
                (Integer) spinnerStartHr.getValue(),
                (Integer) spinnerStartMin.getValue()
        ));

        Timestamp endTime = Timestamp.valueOf(dpEndTime.getValue().atTime(
                (Integer) spinnerEndHr.getValue(),
                (Integer) spinnerEndMin.getValue()
        ));

        if (selectedCourses.isEmpty()) {
            AlertUtil.setErrorAlert("Please select a course first!");
            return;
        }

        if (selectedInstructors.isEmpty()) {
            AlertUtil.setErrorAlert("Please select an instructor first!");
            return;
        }

        Optional<InstructorDTO> instructorDTO = null;
        try {
            instructorDTO = instructorBO.findByName(selectedInstructors.get(0));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (instructorDTO.isEmpty()) {
            AlertUtil.setErrorAlert("Instructor does not exist");
            return;
        }

        String instructorId = instructorDTO.get().getInstructorId();

        Optional<CourseDTO> courseDTO = courseBO.findByName(selectedCourses.get(0));
        if (courseDTO.isEmpty()) {
            AlertUtil.setErrorAlert("Course does not exist");
            return;
        }

        String courseId = courseDTO.get().getCourseId();

        String studentId = "STD-002";

        if (validateLessonDetails(name, startTime, endTime)) {
            try {
                LessonDTO lesson = new LessonDTO(
                        lessonId,
                        instructorId,
                        courseId, 
                        studentId,
                        name,
                        startTime,
                        endTime,
                        "scheduled"
                );

                if (lessonBO.save(lesson)) {
                    AlertUtil.setInfoAlert("Successfully added lesson!");
                    WindowManagerUtil.closeForm(ancAddCourseForm);
                }
            } catch (Exception e) {
                AlertUtil.setErrorAlert("Failed to add lesson");
                e.printStackTrace();
            }
        }
    }



    private String loadNextId() {
        try {
            return lessonBO.loadNextId();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error when loading next id", e);
        }
    }
}
