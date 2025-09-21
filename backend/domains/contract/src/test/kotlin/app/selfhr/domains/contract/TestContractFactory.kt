package app.selfhr.domains.contract

import app.selfhr.domains.contract.entities.Contract
import app.selfhr.domains.contract.entities.ContractFactory
import app.selfhr.domains.contract.entities.ContractUpdateValidator
import app.selfhr.domains.contract.entities.ContractVersionBumpUpVerifier
import app.selfhr.domains.contract.vo.ContractNo
import app.selfhr.domains.contract.vo.ContractPeriod
import app.selfhr.domains.contract.vo.FixedRateCondition
import app.selfhr.domains.contract.vo.TimeBasedCondition
import app.selfhr.domains.proprietor.vo.UUIDv7ProprietorIDGenerator
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

object TestContractFactory {
    private val proprietorIdGenerator = UUIDv7ProprietorIDGenerator()
    private val factory = ContractFactory(
        idGenerator = TestContractIDGenerator(),
        updateValidator = ContractUpdateValidator,
        contractVersionBumpUpVerifier = ContractVersionBumpUpVerifier
    )

    fun createTimeBasedContract(
        hourlyRate: Double = 5000.0,
        startDate: LocalDate = YearMonth.now().atDay(1),
        endDate: LocalDate = YearMonth.now().plusMonths(1).atEndOfMonth()
    ): Contract {
        val command = ContractFactory.ContractCreateCommand(
            proprietorId = proprietorIdGenerator.generate(),
            number = ContractNo("TEST-${UUID.randomUUID()}"),
            period = ContractPeriod(startDate, endDate),
            billingConditions = listOf(TimeBasedCondition(hourlyRate)),
            note = null
        )
        return factory.create(command)
    }

    fun createFixedRateContract(
        fixedAmount: Double = 100000.0,
        startDate: LocalDate = YearMonth.now().atDay(1),
        endDate: LocalDate = YearMonth.now().plusMonths(1).atEndOfMonth()
    ): Contract {
        val command = ContractFactory.ContractCreateCommand(
            proprietorId = proprietorIdGenerator.generate(),
            number = ContractNo("TEST-${UUID.randomUUID()}"),
            period = ContractPeriod(startDate, endDate),
            billingConditions = listOf(FixedRateCondition(fixedAmount)),
            note = null
        )
        return factory.create(command)
    }

    fun createMixedContract(
        hourlyRate: Double = 5000.0,
        fixedAmount: Double = 100000.0,
        startDate: LocalDate = YearMonth.now().atDay(1),
        endDate: LocalDate = YearMonth.now().plusMonths(1).atEndOfMonth()
    ): Contract {
        val command = ContractFactory.ContractCreateCommand(
            proprietorId = proprietorIdGenerator.generate(),
            number = ContractNo("TEST-${UUID.randomUUID()}"),
            period = ContractPeriod(startDate, endDate),
            billingConditions = listOf(
                TimeBasedCondition(hourlyRate),
                FixedRateCondition(fixedAmount)
            ),
            note = null
        )
        return factory.create(command)
    }
}
