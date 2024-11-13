package com.ing.loan.application.service;

import com.ing.loan.application.entity.*;
import com.ing.loan.application.exception.CreditLimitException;
import com.ing.loan.application.exception.IllegalInterestRateException;
import com.ing.loan.application.exception.InstallmentNumberException;
import com.ing.loan.application.model.CreateLoanRequestModel;
import com.ing.loan.application.model.CreateLoanResponseModel;
import com.ing.loan.application.model.LoanResponseModel;
import com.ing.loan.application.repository.CustomerRepository;
import com.ing.loan.application.repository.LoanRepository;
import com.ing.loan.application.repository.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Profile("test")
public class LoanServiceTest {

    @Value("${ing.loanApplication.minInterestRate}")
    private float minInterestRate;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoanService loanService;

    @Mock
    private CustomerService customerService;

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private LoanInstallmentService loanInstallmentService;


    private Customer customer;
    private User user;


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(loanService, "installments", "6,9,12,24");
        ReflectionTestUtils.setField(loanService, "minInterestRate", 0.1f);
        ReflectionTestUtils.setField(loanService, "maxInterestRate", 0.5f);

        customer = Customer.builder()
                .id(1L)
                .name("Deandre")
                .surname("Lopez")
                .creditLimit(BigDecimal.valueOf(100L))
                .usedCreditLimit(BigDecimal.ZERO)
                .build();
        user = User.builder()
                .id(1L)
                .username("Deandre.Lopez")
                .authority(new Role(1L, "ROLE_CUSTOMER"))
                .password("12345")
                .customerId(1L)
                .build();
    }

    @Test
    void interestRateTest() {
        when(customerService.findById(1L)).
                thenReturn(customer);

        Executable executable = () -> loanService.create(
                new CreateLoanRequestModel(
                        1L,
                        BigDecimal.valueOf(50L),
                        BigDecimal.valueOf(0.6),
                        6
                )
        );

        assertThrows(IllegalInterestRateException.class, executable);
    }

    @Test
    void creditLimitTest() {
        when(customerService.findById(1L)).
                thenReturn(customer);

        Executable executable = () -> loanService.create(
                new CreateLoanRequestModel(
                        1L,
                        BigDecimal.valueOf(150L),
                        BigDecimal.valueOf(0.4),
                        6
                )
        );

        assertThrows(CreditLimitException.class, executable);
    }

    @Test
    void numberOfInstallmentsTest1() {
        when(customerService.findById(1L)).
                thenReturn(customer);

        Executable executable = () -> loanService.create(
                new CreateLoanRequestModel(
                        1L,
                        BigDecimal.valueOf(50L),
                        BigDecimal.valueOf(0.4),
                        4
                )
        );

        assertThrows(InstallmentNumberException.class, executable);
    }

    @Test
    void numberOfInstallmentsTest2() {
        when(customerService.findById(1L)).
                thenReturn(customer);

        Executable executable = () -> loanService.create(
                new CreateLoanRequestModel(
                        1L,
                        BigDecimal.valueOf(50L),
                        BigDecimal.valueOf(0.4),
                        13
                )
        );

        Exception exception = assertThrows(InstallmentNumberException.class, executable);
        String expectedMessage = "Installments must be: 6,9,12,24";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void loanRepaymentTest() throws IllegalInterestRateException, InstallmentNumberException, CreditLimitException {
        Loan loan = Loan.builder()
                .id(1L)
                .customerId(1L)
                .loanAmount(BigDecimal.valueOf(50L))
                .numberOfInstallment(12)
                .createDate(Timestamp.valueOf(LocalDateTime.now()))
                .isPaid(false)
                .build();

        when(customerService.findById(1L)).thenReturn(customer);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        Mockito.doNothing().when(loanInstallmentService).saveAll(anyList());

        CreateLoanResponseModel createResponse = loanService.create(
                new CreateLoanRequestModel(
                        1L,
                        BigDecimal.valueOf(50L),
                        BigDecimal.valueOf(0.4),
                        12
                )
        );

        assertEquals(createResponse.getLoanRepayment(), BigDecimal.valueOf(50.0 * (1.0 + 0.4)));
    }

    @Test
    void listInstallmentsOfLoan(){}

    @Test
    void listAllLoans(){}

    @Test
    void paymentTest(){}
}
