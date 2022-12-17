package io.chudzik.recruitment.budfox

import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import static io.chudzik.recruitment.budfox.utils.BudFoxTestProfiles.TEST_INTEGRATION

@ActiveProfiles(TEST_INTEGRATION)
abstract class BaseITSpec extends Specification { }
