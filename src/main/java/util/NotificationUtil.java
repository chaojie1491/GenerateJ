package util;

import javafx.application.Platform;
import javafx.geometry.Pos;
import org.controlsfx.control.Notifications;

public class NotificationUtil {
    public static void notification(String title, String content, String type) {
        Notifications notificationBuilder = Notifications.create()
                .title(title)
                .text(content)
                .position(Pos.BASELINE_RIGHT)
                .onAction(e -> System.out.println("Notification clicked on!"));
        Platform.runLater(() -> {
            switch (type) {
                case "error":
                    notificationBuilder.showError();
                    break;
                case "info":
                    notificationBuilder.showInformation();
                    break;
            }
        });
    }
}
