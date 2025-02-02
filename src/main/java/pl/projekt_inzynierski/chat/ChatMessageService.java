package pl.projekt_inzynierski.chat;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }
    @Transactional
    public void deleteAllMessages(List<ChatMessage> messages) {
        chatMessageRepository.deleteAll(messages);
    }

    public List<ChatMessage> findAllByReportId(Long id) {
        return chatMessageRepository.findAllByReportId(id);
    }
}
