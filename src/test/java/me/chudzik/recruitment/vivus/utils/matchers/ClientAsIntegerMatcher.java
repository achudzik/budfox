package me.chudzik.recruitment.vivus.utils.matchers;

import me.chudzik.recruitment.vivus.model.Client;

public class ClientAsIntegerMatcher extends LongAsIntegerMatcher {

    public ClientAsIntegerMatcher(Client wanted) {
        super(wanted.getId());
    }

}
