package io.chudzik.recruitment.budfox.utils.matchers;

import io.chudzik.recruitment.budfox.model.Client;

public class ClientAsIntegerMatcher extends LongAsIntegerMatcher {

    public ClientAsIntegerMatcher(Client wanted) {
        super(wanted.getId());
    }

}
