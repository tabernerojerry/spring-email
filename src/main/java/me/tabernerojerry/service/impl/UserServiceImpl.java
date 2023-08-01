package me.tabernerojerry.service.impl;

import lombok.RequiredArgsConstructor;
import me.tabernerojerry.domain.Confirmation;
import me.tabernerojerry.domain.User;
import me.tabernerojerry.repository.IConfirmationRepository;
import me.tabernerojerry.repository.IUserRepository;
import me.tabernerojerry.service.IEmailService;
import me.tabernerojerry.service.IUserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;

    private final IConfirmationRepository confirmationRepository;

    private final IEmailService emailService;

    @Override
    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exist.");
        }

        user.setEnabled(false);
        User savedUser = userRepository.save(user);

        Confirmation confirmation = new Confirmation(savedUser);
        confirmationRepository.save(confirmation);

        // Simple Mail Message
        /*emailService.sendSimpleMailMessage(
                savedUser.getName(),
                savedUser.getEmail(),
                confirmation.getToken()
        );*/

        // Mail With Attachments
        /*emailService.sendMimeMessageWithAttachments(
                savedUser.getName(),
                savedUser.getEmail(),
                confirmation.getToken()
        );*/

        // Mail With Embedded Files
        /*emailService.sendMimeMessageWithEmbeddedFiles(
                savedUser.getName(),
                savedUser.getEmail(),
                confirmation.getToken()
        );*/

        // Mail With HTML
        /*emailService.sendHtmlEmail(
                savedUser.getName(),
                savedUser.getEmail(),
                confirmation.getToken()
        );*/

        // Mail With HTML and Embedded Files
        emailService.sendHtmlEmailWithEmbeddedFiles(
                savedUser.getName(),
                savedUser.getEmail(),
                confirmation.getToken()
        );

        return savedUser;
    }

    @Override
    public Boolean verifyToken(String token) {
        Confirmation confirmation = confirmationRepository.findByToken(token);

        User user = userRepository.findByEmailIgnoreCase(confirmation.getUser().getEmail());
        user.setEnabled(true);
        userRepository.save(user);

        return Boolean.TRUE;
    }
}
