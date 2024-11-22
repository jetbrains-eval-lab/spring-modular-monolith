package myapp.module2;

import myapp.module1.NewsLetterSender;

public class NotificationJobScheduler {
    private final NewsLetterSender newsLetterSender = new NewsLetterSender();

    public void sendNewsLetter() {
        String content = "Email Content Here";
        newsLetterSender.sendNewsLetter(content);
    }
}
