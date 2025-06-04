package edu.unac.controller;


import edu.unac.domain.Device;
import edu.unac.domain.Loan;
import edu.unac.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.unac.domain.DeviceStatus;
import edu.unac.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
class LoanControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        loanRepository.deleteAll();
        deviceRepository.deleteAll();
    }


    @Test
    void getAllLoans() throws Exception {

        Loan loan = new Loan();
        loan.setDeviceId(1L);
        loan.setStartDate(123L);
        loan.setEndDate(456L);
        loan.setReturned(false);
        loan.setBorrowedBy("John Doe");
        loanRepository.save(loan);

        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

    }

    @Test
    void getLoanById() throws Exception {

        Loan loan = new Loan();
        loan.setDeviceId(1L);
        loan.setStartDate(123L);
        loan.setEndDate(456L);
        loan.setReturned(false);
        loan.setBorrowedBy("John Doe");
        loanRepository.save(loan);

        mockMvc.perform(get("/api/loans/{id}", loan.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(loan.getId().intValue())));
    }

    @Test
    void getLoanByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/loans/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void registerLoan() throws Exception {
        Device device = new Device();
        device.setName("Test Device");
        device.setStatus(DeviceStatus.AVAILABLE);
        Device saved = deviceRepository.save(device);

        Loan loan = new Loan();
        loan.setDeviceId(saved.getId());
        loan.setStartDate(123L);
        loan.setEndDate(456L);
        loan.setReturned(false);
        loan.setBorrowedBy("John Doe");

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loan)))
                .andExpect(status().isCreated());
    }

    @Test
    void registerLoanDeviceNotFound() throws Exception {
        Loan loan = new Loan();
        loan.setDeviceId(1L);
        loan.setStartDate(123L);
        loan.setEndDate(456L);
        loan.setReturned(false);
        loan.setBorrowedBy("John Doe");

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loan)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void markAsReturned() throws Exception {

        Device device = new Device();
        device.setName("Test Device");
        device.setStatus(DeviceStatus.AVAILABLE);
        Device deviceSaved = deviceRepository.save(device);

        Loan loan = new Loan();
        loan.setDeviceId(deviceSaved.getId());
        loan.setStartDate(123L);
        loan.setEndDate(456L);
        loan.setReturned(false);
        loan.setBorrowedBy("John Doe");

        loanRepository.save(loan);

        mockMvc.perform(put("/api/loans/{id}/return", loan.getId()))
                .andExpect(status().isOk());


    }

    @Test
    void markAsReturnedLoanNotFound() throws Exception {
        mockMvc.perform(put("/api/loans/{id}/return", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void markAsReturnedLoanAlreadyReturned() throws Exception {

        Loan loan = new Loan();
        loan.setDeviceId(1L);
        loan.setStartDate(123L);
        loan.setEndDate(456L);
        loan.setReturned(true);
        loan.setBorrowedBy("John Doe");
        loanRepository.save(loan);

        mockMvc.perform(put("/api/loans/{id}/return", loan.getId()))
                .andExpect(status().isConflict());
    }

    @Test
    void getLoansByDeviceId() throws Exception {
        Device device = new Device();
        device.setId(1L);
        device.setName("Test Device");
        device.setStatus(DeviceStatus.AVAILABLE);
        deviceRepository.save(device);

        Loan loan = new Loan();
        loan.setDeviceId(1L);
        loan.setStartDate(123L);
        loan.setEndDate(456L);
        loan.setReturned(false);
        loan.setBorrowedBy("John Doe");
        loanRepository.save(loan);

        mockMvc.perform(get("/api/loans/device/{deviceId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}