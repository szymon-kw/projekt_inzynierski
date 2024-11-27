package pl.projekt_inzynierski.mailing;

import org.springframework.stereotype.Component;
import pl.projekt_inzynierski.Dto.ChatQueueDto;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Component
public class ChatNotificationQueue {
    private final List<ChatQueueDto> ChatQueue = new LinkedList<>();

    public void addChatQueueNotification(ChatQueueDto chatQueueDto) {
        ChatQueueDto existingDto = ChatQueue.stream()
                .filter(dto -> dto.getReportId() == chatQueueDto.getReportId())
                .findFirst()
                .orElse(null);

        if (existingDto != null) existingDto = chatQueueDto;
        else {
            ChatQueue.add(chatQueueDto);
        }

    }

    public boolean isEmpty() {
        return ChatQueue.isEmpty();
    }

    public List<ChatQueueDto> getChatQueue() {
        return ChatQueue;
    }
}
