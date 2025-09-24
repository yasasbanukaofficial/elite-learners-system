package lk.ijse.learners.bo.custom.impl;

import lk.ijse.learners.bo.custom.UserBO;
import lk.ijse.learners.bo.exception.DuplicateException;
import lk.ijse.learners.bo.exception.InUseException;
import lk.ijse.learners.bo.exception.NotFoundException;
import lk.ijse.learners.bo.util.EntityDTOConverter;
import lk.ijse.learners.dao.DAOFactory;
import lk.ijse.learners.dao.custom.UserDAO;
import lk.ijse.learners.dto.UserDTO;
import lk.ijse.learners.entity.User;

import java.util.List;
import java.util.Optional;

public class UserBOImpl implements UserBO {
    private final UserDAO userDAO = (UserDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.USER);
    private final EntityDTOConverter entityDTOConverter = new EntityDTOConverter();

    @Override
    public List<UserDTO> getAll() throws Exception {
        return entityDTOConverter.toUserDTOList(userDAO.getAll());
    }

    @Override
    public String getLastId() throws Exception {
        return userDAO.getLastId();
    }

    @Override
    public boolean save(UserDTO userDto) throws Exception {
        Optional<User> userById = userDAO.findById(userDto.getUserId());
        if (userById.isPresent()) {
            throw new DuplicateException("User already exists");
        }
        if (userDAO.existsByField("email", userDto.getEmail())) {
            throw new InUseException("Email already exists");
        }
        return userDAO.save(entityDTOConverter.getUserEntity(userDto));
    }

    @Override
    public boolean update(UserDTO userDto) throws Exception {
        Optional<User> userById = userDAO.findById(userDto.getUserId());
        if (userById.isEmpty()) {
            throw new NotFoundException("User doesn't exists");
        }
        User existingUser = userById.get();
        if (!existingUser.getEmail().equals(userDto.getEmail())) {
            if (userDAO.existsByField("email", userDto.getEmail())) {
                throw new InUseException("Email already exists to another user");
            }
        }
        return userDAO.update(entityDTOConverter.getUserEntity(userDto));
    }

    @Override
    public boolean delete(String id) throws Exception {
        return userDAO.delete(id);
    }

    @Override
    public List<String> getAllIds() throws Exception {
        return userDAO.getAllIds();
    }

    @Override
    public Optional<UserDTO> findById(String id) throws Exception {
        return userDAO.findById(id).map(usr -> {
            try {
                return entityDTOConverter.getUserDTO(usr);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to convert User to DTO, findById", e);
            }
        });
    }

    @Override
    public String loadNextId() throws Exception {
        String lastId = getLastId();
        String prefix = "USR-%03d";
        if (lastId != null) {
            String lastIdNumString = lastId.substring(4);
            int lastIdNum = Integer.parseInt(lastIdNumString);
            return String.format(prefix, lastIdNum + 1);
        }
        return String.format(prefix, 1);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userDAO.existsByUsername(username);
    }

    @Override
    public Optional<User> findByName(String username) {
        return userDAO.findByName(username);
    }
}