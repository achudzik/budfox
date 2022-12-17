package io.chudzik.recruitment.budfox

import io.chudzik.recruitment.budfox.config.tests.SingletonFixedClockProviderTConfig

import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

import static io.chudzik.recruitment.budfox.commons.tests.BudFoxTestProfiles.CLOCK_ADJUSTED

@ActiveProfiles(CLOCK_ADJUSTED)
@ContextConfiguration(classes = SingletonFixedClockProviderTConfig)
abstract class BaseClockFixedITSpec extends BaseITSpec { }
