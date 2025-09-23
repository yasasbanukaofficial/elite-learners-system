package lk.ijse.learners.bo.custom.impl;

import lk.ijse.learners.bo.custom.PaymentBO;
import lk.ijse.learners.bo.util.EntityDTOConverter;
import lk.ijse.learners.dao.DAOFactory;
import lk.ijse.learners.dao.custom.PaymentDAO;
import lk.ijse.learners.dto.PaymentDTO;
import lk.ijse.learners.dto.StudentDTO;

import java.util.List;
import java.util.Optional;

public class PaymentBOImpl implements PaymentBO {
    private final PaymentDAO paymentDAO = (PaymentDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.PAYMENT);
    private final EntityDTOConverter entityDTOConverter = new EntityDTOConverter();

    @Override
    public List<PaymentDTO> getAll() throws Exception {
        return entityDTOConverter.toPaymentDTOList(paymentDAO.getAll());
    }

    @Override
    public String getLastId() throws Exception {
        return paymentDAO.getLastId();
    }

    @Override
    public boolean save(PaymentDTO paymentDTO) throws Exception {
        return paymentDAO.save(entityDTOConverter.getPaymentEntity(paymentDTO));
    }

    @Override
    public boolean update(PaymentDTO paymentDTO) throws Exception {
        return paymentDAO.update(entityDTOConverter.getPaymentEntity(paymentDTO));
    }

    @Override
    public boolean delete(String id) throws Exception {
        return paymentDAO.delete(id);
    }

    @Override
    public List<String> getAllIds() throws Exception {
        return paymentDAO.getAllIds();
    }

    @Override
    public Optional<PaymentDTO> findById(String id) throws Exception {
        return paymentDAO.findById(id).map(payment -> {
            try {
                return entityDTOConverter.getPaymentDTO(payment);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to convert Payment to DTO, findById",e);
            }
        });
    }

    @Override
    public String loadNextId() throws Exception {
        String lastId = getLastId();
        String prefix = "PAY-%03d";
        if (lastId != null) {
            String lastIdNumString = lastId.substring(4);
            int lastIdNum = Integer.parseInt(lastIdNumString);
            return String.format(prefix, lastIdNum + 1);
        }
        return String.format(prefix, 1);
    }

    @Override
    public boolean isIdExisting(String id) {
        return paymentDAO.isIdExisting(id);
    }

    @Override
    public StudentDTO getStudentsByPaymentId(String payId) throws Exception {
        return entityDTOConverter.getStudentDTO(paymentDAO.getStudentsByPaymentId(payId));
    }
}

