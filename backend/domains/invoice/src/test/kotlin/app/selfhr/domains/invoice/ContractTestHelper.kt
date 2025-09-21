package app.selfhr.domains.invoice

import app.selfhr.domains.contract.entities.Contract
import app.selfhr.domains.contract.vo.ContractPeriod
import app.selfhr.domains.contract.vo.FixedRateCondition
import app.selfhr.domains.contract.vo.TimeBasedCondition
import app.selfhr.domains.proprietor.vo.UUIDv7ProprietorIDGenerator
import java.time.LocalDate
import java.time.YearMonth

object ContractTestHelper {
    private val proprietorIdGenerator = UUIDv7ProprietorIDGenerator()

    fun createTimeBasedContract(
        hourlyRate: Double = 5000.0,
        startDate: LocalDate = YearMonth.now().atDay(1),
        endDate: LocalDate = YearMonth.now().plusMonths(1).atEndOfMonth()
    ): Contract {
        return TestContractFactory.create(
            proprietorId = proprietorIdGenerator.generate(),
            period = ContractPeriod(startDate, endDate),
            billingConditions = listOf(TimeBasedCondition(hourlyRate))
        )
    }

    fun createFixedRateContract(
        fixedAmount: Double = 100000.0,
        startDate: LocalDate = YearMonth.now().atDay(1),
        endDate: LocalDate = YearMonth.now().plusMonths(1).atEndOfMonth()
    ): Contract {
        return TestContractFactory.create(
            proprietorId = proprietorIdGenerator.generate(),
            period = ContractPeriod(startDate, endDate),
            billingConditions = listOf(FixedRateCondition(fixedAmount))
        )
    }

    fun createMixedContract(
        hourlyRate: Double = 5000.0,
        fixedAmount: Double = 100000.0,
        startDate: LocalDate = YearMonth.now().atDay(1),
        endDate: LocalDate = YearMonth.now().plusMonths(1).atEndOfMonth()
    ): Contract {
        return TestContractFactory.create(
            proprietorId = proprietorIdGenerator.generate(),
            period = ContractPeriod(startDate, endDate),
            billingConditions = listOf(
                TimeBasedCondition(hourlyRate),
                FixedRateCondition(fixedAmount)
            )
        )
    }
}
