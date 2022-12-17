package io.chudzik.recruitment.budfox.loans

import io.chudzik.recruitment.budfox.BaseClockFixedITSpec
import io.chudzik.recruitment.budfox.BudfoxApplication
import io.chudzik.recruitment.budfox.config.BusinessConfiguration

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.SpringBootDependencyInjectionTestExecutionListener
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.web.servlet.MockMvc

import groovy.json.JsonSlurper

import static org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint.LOG_DEBUG
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

import static io.chudzik.recruitment.budfox.commons.tests.PreExistingEntities.VALID_CLIENT
import static io.chudzik.recruitment.budfox.commons.tests.PreExistingEntities.validId

@DatabaseSetup("loanExtensionData.xml")
@TestExecutionListeners([
        TransactionDbUnitTestExecutionListener,
        SpringBootDependencyInjectionTestExecutionListener,
])
@AutoConfigureMockMvc(print = LOG_DEBUG)
@SpringBootTest(classes = BudfoxApplication, webEnvironment = MOCK)
class LoanExtensionITSpec extends BaseClockFixedITSpec {

    @Autowired BusinessConfiguration businessConf
    @Autowired MockMvc mockMvc


    //FIXME-ach: remove dbunit and validate db content programmatically
    //@ExpectedDatabase(value = "loanExtensionData-expected.xml", assertionMode = NON_STRICT)
    def "should allow extending new loans"() {
        given:
            BigDecimal interestAfterFirstExtension = businessConf.basicInterest() * businessConf.interestMultiplier()
            BigDecimal interestAfterSecondExtension = interestAfterFirstExtension * businessConf.interestMultiplier()

        when:
            MockHttpServletResponse extendLoanResponse = mockMvc.perform(
                            put("/loans/{id}?extend=true", validId())
                    )
                    //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                    .andReturn().response
        then:
            extendLoanResponse.status == OK.value()
            extendLoanResponse.contentType == APPLICATION_JSON_VALUE
            verifyAll (new JsonSlurper().parseText(extendLoanResponse.contentAsString)) {
                it.id == validId()
                it.client == VALID_CLIENT.id
                // ... current conditions
                it.conditions.amount == 'PLN 3.00'
                it.conditions.interest == interestAfterSecondExtension
                // FIXME-ach: compare actual dates
                //it.conditions.maturityDate == MONTH_AND_A_TWO_WEEKS_LATER
                // ... previous conditions
                it.previousConditions instanceof List
                // ...... conditions after first extension
                it.previousConditions[0].amount == 'PLN 3.00'
                // ......... interest == interestAfterFirstExtension * businessConf.interestMultiplier()
                it.previousConditions[0].interest == BigDecimal.valueOf(15)
                //it.previousConditions[0].maturityDate == MONTH_AND_A_WEEK_LATER
                // ....... basic conditions
                it.previousConditions[1].amount == 'PLN 3.00'
                // ......... interest == businessConf.basicInterest() * businessConf.interestMultiplier()
                it.previousConditions[1].interest == BigDecimal.valueOf(10)
                //it.previousConditions[1].maturityDate == MONTH_LATER
            }
    }

}
