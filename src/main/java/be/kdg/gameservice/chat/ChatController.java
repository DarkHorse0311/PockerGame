package be.kdg.gameservice.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public final class ChatController {
    private final SimpMessagingTemplate template;

    /**
     * If a player sends a chat message to a room, it is received here.
     * The message is then send to all players in the same room.
     */
    @MessageMapping("/chatroom/send/{roomId}")
    public void onReceiveMessage(MessageDTO message, @DestinationVariable("roomId") String roomId) {
        this.template.convertAndSend("/chatroom/receive/" + roomId, message);
    }
}