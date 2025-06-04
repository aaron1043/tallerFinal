package edu.unac.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.unac.domain.Device;
import edu.unac.domain.DeviceStatus;
import edu.unac.domain.Loan;
import edu.unac.repository.DeviceRepository;
import edu.unac.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
class DeviceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private LoanRepository loanRepository;

    @BeforeEach
    void setup() {
        deviceRepository.deleteAll();
    }

    @Test
    void testGetAllDevices() throws Exception {
        deviceRepository.save(new Device(null, "Laptop", "Dell", "XPS 13", DeviceStatus.AVAILABLE, 123356L));
        deviceRepository.save(new Device(null, "Smartphone", "Samsung", "Galaxy S21", DeviceStatus.AVAILABLE, 123456L));

        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetDeviceById() throws Exception {
        Device device = deviceRepository.save(new Device(null, "Laptop", "Dell", "XPS 13", DeviceStatus.AVAILABLE, 123356L));
        mockMvc.perform(get("/api/devices/" + device.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Laptop")));
    }

    @Test
    void testRegisterDevice() throws Exception {
        Device device = new Device(null, "Laptop", "Dell", "XPS 13", DeviceStatus.AVAILABLE, 123356L);
        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(device)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Laptop")));
    }
    @Test
    void testInvalidRegisterDevice() throws Exception {
        Device device = new Device(null, null, "Dell", "XPS 13", DeviceStatus.AVAILABLE, 123356L);
        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(device)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testUpdateDeviceStatus() throws Exception {
        Device device = deviceRepository.save(new Device(null, "Laptop", "Dell", "XPS 13", DeviceStatus.AVAILABLE, 123356L));
        mockMvc.perform(put("/api/devices/" + device.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", "AVAILABLE")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Laptop")));
    }

    @Test
    void testUpdateDeviceStatusNotFound() throws Exception {
        long nonExistentId = 999L;

        mockMvc.perform(put("/api/devices/" + nonExistentId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", "AVAILABLE"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteDevice() throws Exception {
        Device device = deviceRepository.save(new Device(null, "Laptop", "Dell", "XPS 13", DeviceStatus.AVAILABLE, 123356L));
        mockMvc.perform(delete("/api/devices/" + device.getId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/devices/" + device.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteDeviceWithLoanHistoryReturnsConflict() throws Exception {
        Device device = new Device(1L, null, "Dell", "XPS 13", DeviceStatus.AVAILABLE, System.currentTimeMillis());
        device = deviceRepository.save(device);
        Loan loan = new Loan(1L,device.getId(),"juan.perez@example.com",20230,20234,false);
        loanRepository.save(loan);
        mockMvc.perform(delete("/api/devices/" + device.getId()))
                .andExpect(status().isConflict());
    }




}