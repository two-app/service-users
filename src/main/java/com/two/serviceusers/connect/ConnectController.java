package com.two.serviceusers.connect;

import com.two.http_api.api.PublicApiContracts;
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
public class ConnectController implements PublicApiContracts.PostConnect {

    private static final Logger logger = LoggerFactory.getLogger(ConnectController.class);
    private final ConnectService connectService;

    @PostMapping(postConnectPath)
    public Tokens connect(HttpServletRequest request, @PathVariable(connectCodePathVariable) String partnerConnectCode) {
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
