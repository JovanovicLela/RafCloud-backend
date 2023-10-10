package rs.raf.demo.services;

import org.springframework.stereotype.Service;
import rs.raf.demo.model.ErrorMessage;
import rs.raf.demo.repositories.ErrorMessageRepository;

import java.util.List;

@Service
public class ErrorMessageService {

    private final ErrorMessageRepository errorMessageRepository;

    public ErrorMessageService(ErrorMessageRepository errorMessageRepository) {
        this.errorMessageRepository = errorMessageRepository;
    }

    public List<ErrorMessage> findErrorMessages(Long id) {
        return this.errorMessageRepository.findErrorMessages(id);
    }
}
