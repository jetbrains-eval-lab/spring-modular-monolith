package myapp.module1;

import java.util.List;

public class NewsLetterSender {
    private final EmailSender emailSender = new EmailSender();

    public void sendNewsLetter(String content) {
        List<String> emails = fetchEmails();
        emails.forEach(email -> sendEmail(email, content));
    }

    private List<String> fetchEmails() {
        return List.of();
    }

    private void sendEmail(String email, String content) {
        emailSender.sendEmail(email, content);
    }
}
