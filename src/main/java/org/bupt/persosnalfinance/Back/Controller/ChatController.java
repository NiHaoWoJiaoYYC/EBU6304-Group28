package org.bupt.persosnalfinance.Back.Controller;

import org.bupt.persosnalfinance.Back.Service.ChatService;
import org.bupt.persosnalfinance.dto.ChatRequest;
import org.bupt.persosnalfinance.dto.ChatResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class ChatController {

    private final ChatService svc;

    public ChatController(ChatService svc) { this.svc = svc; }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest req,
                             @RequestHeader(value="X-USER", defaultValue="demo") String uid){
        return svc.chat(req, uid);
    }

    @GetMapping("/history")
    public java.util.List<String> history(@RequestHeader(value="X-USER", defaultValue="demo") String uid){
        return svc.getDialog(uid);
    }
}
