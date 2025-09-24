package lk.ijse.learners.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lk.ijse.learners.bo.BOFactory;
import lk.ijse.learners.bo.exception.DuplicateException;
import lk.ijse.learners.bo.exception.InUseException;
import lk.ijse.learners.controller.util.ViewPath;
import lk.ijse.learners.bo.custom.UserBO;
import lk.ijse.learners.bo.context.RefreshContext;
import lk.ijse.learners.controller.util.AlertUtil;
import lk.ijse.learners.controller.util.ViewPath;
import lk.ijse.learners.controller.util.WindowManagerUtil;
import lk.ijse.learners.dto.UserDTO;

import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;

public class UserMgmtPageController implements Initializable {

    private final UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.USER);
    public StackPane btnOpenUserForm;
    private UserDTO userDTO;

    public AnchorPane ancUser;
    public TextField txtUserName;
    public TextField txtRole;
    public TextField txtEmail;
    public TextField txtAge;
    public TextField txtContact;
    public Label btnShowPassword;
    public PasswordField userPassword;
    public Button btnDeleteUser;
    public Button btnEdit;
    public ListView <UserDTO> listUsers;

    private void setupLists() {
        try {
            listUsers.getItems().setAll(userBO.getAll());
            listUsers.setSelectionModel(null);
            listUsers.setCellFactory(lv -> new ListCell<UserDTO>() {
                @Override
                protected void updateItem(UserDTO user, boolean empty) {
                    super.updateItem(user, empty);

                    if (empty || user == null) {
                        setGraphic(null);
                    } else {
                        VBox card = new VBox(8);
                        card.setStyle(
                                "-fx-background-color: white; " +
                                        "-fx-border-color: #ddd; " +
                                        "-fx-border-radius: 10; " +
                                        "-fx-background-radius: 10; " +
                                        "-fx-padding: 12; " +
                                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);"
                        );

                        Label lblName = new Label(user.getName());
                        lblName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                        Label lblEmail = new Label("📧 " + user.getEmail());
                        Label lblContact = new Label("📞 " + user.getContactNumber());
                        Label lblRole = new Label("👤 Role: " + user.getRole());
                        Label lblDob = new Label("🎂 Age: " + user.getAge());

                        card.getChildren().addAll(lblName, lblEmail, lblContact, lblRole, lblDob);

                        card.setOnMouseClicked(event -> {
                            userDTO = user;
                            txtUserName.setText(user.getName());
                            txtEmail.setText(user.getEmail());
                            txtContact.setText(user.getContactNumber());
                            txtRole.setText(user.getRole());
                            userPassword.setText(user.getPassword());
                            txtAge.setText(user.getAge());
                        });

                        setGraphic(card);
                    }
                }
            });
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to load users");
            e.printStackTrace();
        }

    }



    private boolean validateUserDetails(String fullName, String email, String contact, String role, String password) {
        if (fullName.isEmpty() || email.isEmpty() || contact.isEmpty() || role.isEmpty() || password.isEmpty() || txtAge.getText().trim().isEmpty()) {
            AlertUtil.setErrorAlert("Please fill all fields");
            return false;
        }
        try {
            int age = Integer.parseInt(txtAge.getText().trim());
            if (age < 0 || age > 120) {
                AlertUtil.setErrorAlert("Please enter a valid age between 0-120");
                return false;
            }
        } catch (NumberFormatException e) {
            AlertUtil.setErrorAlert("Please enter a valid numeric age");
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            AlertUtil.setErrorAlert("Invalid email format");
            return false;
        }
        if (!contact.matches("^\\d{10}$")) {
            AlertUtil.setErrorAlert("Invalid contact number");
            return false;
        }
        return true;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupLists();
        RefreshContext.getInstance().getRefreshFlag(RefreshContext.TableName.USERS).addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> {
                    try {
                        listUsers.getItems().setAll(userBO.getAll());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.USERS, false);
                });
            }
        });
    }

    public void deleteUser(ActionEvent event) {
        try {
            if (AlertUtil.setConfirmationAlert("Before continuing", "Are you sure you want to delete user ?")) {
                userBO.delete(userDTO.getUserId());
            }
            RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.USERS, true);
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to delete user");
            throw new RuntimeException(e);
        }
    }

    public void editUser(ActionEvent event) {
        String fullName = txtUserName.getText().trim();
        String email = txtEmail.getText().trim();
        String contact = txtContact.getText().trim();
        String role = txtRole.getText().trim();
        String password = userPassword.getText().trim();
        String age = txtAge.getText().trim();

        boolean unchanged = fullName.equals(userDTO.getName()) &&
                email.equals(userDTO.getEmail()) &&
                contact.equals(userDTO.getContactNumber()) &&
                role.equals(userDTO.getRole()) &&
                password.equals(userDTO.getPassword()) &&
                age.equals(userDTO.getAge());

        if (unchanged) {
            AlertUtil.setErrorAlert("No changes made");
            return;
        }

        try {
            if (validateUserDetails(fullName, email, contact, role, password)) {
                UserDTO updatedUserDTO = new UserDTO(
                        userDTO.getUserId(),
                        fullName,
                        age,
                        email,
                        password,
                        contact,
                        role
                );

                if (AlertUtil.setConfirmationAlert("Before continuing", "Are you sure you want to update user details ?")) {
                    if (userBO.update(updatedUserDTO)) {
                        RefreshContext.getInstance().setRefreshFlag(RefreshContext.TableName.USERS, true);
                    } else {
                        AlertUtil.setErrorAlert("Failed to update user");
                    }
                }
            }
        } catch (InUseException e) {
            AlertUtil.setErrorAlert("This email address is already registered");
        } catch (DuplicateException e) {
            AlertUtil.setErrorAlert("User ID already exists");
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Failed to update user");
            e.printStackTrace();
        }
    }

    public void openUserForm(MouseEvent mouseEvent) {
        WindowManagerUtil.openForm(ViewPath.ADD_USER_FORM.getPath(), false);
    }
}
