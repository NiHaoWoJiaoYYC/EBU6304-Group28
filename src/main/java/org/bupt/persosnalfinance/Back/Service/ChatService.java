package org.bupt.persosnalfinance.Back.Service;

import java.util.List;
import org.bupt.persosnalfinance.dto.ChatRequest;
import org.bupt.persosnalfinance.dto.ChatResponse;

public interface ChatService {
    ChatResponse chat(ChatRequest req, String userId);
    List<String> getDialog(String userId);   // Pull history Dialog
}
