package com.two.serviceusers.connect;

import com.two.http_api.authentication.RequestContext;
import com.two.http_api.model.Tokens;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

@RestController
@AllArgsConstructor
public class ConnectController {

    private static final Logger logger = LoggerFactory.getLogger(ConnectController.class);
    private final ConnectService connectService;

    @PostMapping("/connect/{connectCode}")
    public Tokens connect(HttpServletRequest request, @PathVariable("connectCode") String partnerConnectCode) {
        RequestContext ctx = RequestContext.from(request);
        if (!ctx.isConnectToken()) {
            logger.warn("Connected user is attempting another connect.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already connected.");
        }

        logger.info("Connecting UID {} with partner connect code {}.", ctx.getUid(), partnerConnectCode);
        Tokens tokens = this.connectService.connectUsers(ctx.getUid(), partnerConnectCode);

        logger.info("Returning newly generated tokens: {}.", tokens);
        return tokens;
    }

}
