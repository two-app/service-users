package com.two.serviceusers.users;

import com.two.http_api.api.PublicApiContracts;
import com.two.http_api.authentication.RequestContext;
import com.two.http_api.model.Partner;
import com.two.http_api.model.User;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Validated
@AllArgsConstructor
public class PartnerController implements PublicApiContracts.GetPartner {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(PartnerController.class);

    @GetMapping("/partner")
    @Override
    public Partner getPartner(HttpServletRequest req) {
        var ctx = RequestContext.from(req);
        var pid = ctx.getPid();
        logger.info("User {} retrieving partner with pid {}.", ctx.getUid(), pid);

        if (pid == null) {
            // TODO replace with a filter in Zuul for all routes requiring connection
            logger.warn("User not connected with partner.");
            throw new UserNotExistsException();
        }

        return userService.getUser(pid, Partner.class);
    }
}

