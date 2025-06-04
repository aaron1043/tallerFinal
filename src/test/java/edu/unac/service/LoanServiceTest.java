package edu.unac.service;

import edu.unac.domain.Device;
import edu.unac.domain.DeviceStatus;
import edu.unac.domain.Loan;
import edu.unac.repository.DeviceRepository;
import edu.unac.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LoanServiceTest {
    @Test
    void testRegisterLoan() {

        LoanRepository loanRepository = mock(LoanRepository.class);
        DeviceRepository deviceRepository = mock(DeviceRepository.class);
        LoanService loanService = new LoanService(loanRepository, deviceRepository);

        Device device = new Device();
        device.setId(1L);
        device.setName("Test Device");
        device.setStatus(DeviceStatus.AVAILABLE);

        Loan loan = new Loan();
        loan.setDeviceId(1L);
        loan.setStartDate(123L);
        loan.setEndDate(456L);
        loan.setReturned(false);
        loan.setBorrowedBy("John Doe");

        when(deviceRepository.findById(1L))
                .thenReturn(Optional.of(device));

        when(loanRepository
                .save(loan))
                .thenReturn(loan);

        when(deviceRepository
                .save(device))
                .thenReturn(device);

        Loan registeredLoan = loanService.registerLoan(loan);

        assertEquals(loan, registeredLoan);
        verify(deviceRepository).findById(1L);
        verify(loanRepository).save(loan);
        verify(deviceRepository).save(device);
    }

    @Test
    void testDeviceNotFound() {
        LoanRepository loanRepository = mock(LoanRepository.class);
        DeviceRepository deviceRepository = mock(DeviceRepository.class);
        LoanService loanService = new LoanService(loanRepository, deviceRepository);

        when(deviceRepository.findById(1L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loanService.registerLoan(new Loan());
        });

        assertEquals("Device not found", exception.getMessage());
    }

    @Test
    void testDeviceNotAvailable() {
        LoanRepository loanRepository = mock(LoanRepository.class);
        DeviceRepository deviceRepository = mock(DeviceRepository.class);
        LoanService loanService = new LoanService(loanRepository, deviceRepository);

        Device device = new Device();
        device.setId(1L);
        device.setName("Test Device");
        device.setStatus(DeviceStatus.LOANED);

        Loan loan = new Loan();
        loan.setDeviceId(1L);
        loan.setStartDate(123L);
        loan.setEndDate(456L);
        loan.setReturned(false);
        loan.setBorrowedBy("John Doe");

        when(deviceRepository.findById(1L))
                .thenReturn(Optional.of(device));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            loanService.registerLoan(loan);
        });

        assertEquals("Device is not available for loan", exception.getMessage());
    }

    @Test
    void testGetAllLoans() {
        LoanRepository loanRepository = mock(LoanRepository.class);
        DeviceRepository deviceRepository = mock(DeviceRepository.class);
        LoanService loanService = new LoanService(loanRepository, deviceRepository);

        Loan loan1 = new Loan();
        loan1.setId(1L);
        loan1.setDeviceId(1L);
        loan1.setStartDate(123L);
        loan1.setEndDate(456L);
        loan1.setReturned(false);
        loan1.setBorrowedBy("John Doe");

        Loan loan2 = new Loan();
        loan2.setId(2L);
        loan2.setDeviceId(2L);
        loan2.setStartDate(789L);
        loan2.setEndDate(101112L);
        loan2.setReturned(true);
        loan2.setBorrowedBy("Jane Doe");

        when(loanRepository.findAll())
                .thenReturn(List.of(loan1, loan2));

        List<Loan> foundLoans = loanService.getAllLoans();

        assertNotNull(foundLoans);
        assertEquals(2, foundLoans.size());
        assertEquals(loan1, foundLoans.get(0));
        assertEquals(loan2, foundLoans.get(1));
    }

    @Test
    void testGetLoanById() {
        LoanRepository loanRepository = mock(LoanRepository.class);
        DeviceRepository deviceRepository = mock(DeviceRepository.class);
        LoanService loanService = new LoanService(loanRepository, deviceRepository);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setDeviceId(1L);
        loan.setStartDate(123L);
        loan.setEndDate(456L);
        loan.setReturned(false);
        loan.setBorrowedBy("John Doe");

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        Optional<Loan> foundLoan = loanService.getLoanById(1L);

        assertTrue(foundLoan.isPresent());
        assertEquals(loan, foundLoan.get());
    }

    @Test
    void testMarkAsReturned() {
        LoanRepository loanRepository = mock(LoanRepository.class);
        DeviceRepository deviceRepository = mock(DeviceRepository.class);
        LoanService loanService = new LoanService(loanRepository, deviceRepository);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setDeviceId(1L);
        loan.setStartDate(123L);
        loan.setEndDate(456L);
        loan.setReturned(false);
        loan.setBorrowedBy("John Doe");

        Device device = new Device();
        device.setId(1L);
        device.setName("Test Device");
        device.setStatus(DeviceStatus.AVAILABLE);

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        when(deviceRepository.findById(1L))
                .thenReturn(Optional.of(device));

        when(loanRepository.save(loan))
                .thenReturn(loan);

        Loan returnedLoan = loanService.markAsReturned(1L);

        assertNotNull(returnedLoan);
        assertEquals(1L, returnedLoan.getId());
        assertEquals(1L, returnedLoan.getDeviceId());
        assertEquals(123L, returnedLoan.getStartDate());
        assertTrue(returnedLoan.isReturned());

    }

    @Test
    void testMarkAsReturnedLoanNotFound() {
        LoanRepository loanRepository = mock(LoanRepository.class);
        DeviceRepository deviceRepository = mock(DeviceRepository.class);
        LoanService loanService = new LoanService(loanRepository, deviceRepository);

        when(loanRepository.findById(1L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loanService.markAsReturned(1L);
        });

        assertEquals("Loan not found", exception.getMessage());
    }

    @Test
    void testMarkAsReturnedAlreadyReturned() {
        LoanRepository loanRepository = mock(LoanRepository.class);
        DeviceRepository deviceRepository = mock(DeviceRepository.class);
        LoanService loanService = new LoanService(loanRepository, deviceRepository);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setDeviceId(1L);
        loan.setStartDate(123L);
        loan.setEndDate(456L);
        loan.setReturned(true);
        loan.setBorrowedBy("John Doe");

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            loanService.markAsReturned(1L);
        });

        assertEquals("Loan is already marked as returned", exception.getMessage());
    }

    @Test
    void testMarkAsReturnedDeviceNotFound() {
        LoanRepository loanRepository = mock(LoanRepository.class);
        DeviceRepository deviceRepository = mock(DeviceRepository.class);
        LoanService loanService = new LoanService(loanRepository, deviceRepository);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setDeviceId(1L);
        loan.setStartDate(123L);
        loan.setEndDate(456L);
        loan.setReturned(false);
        loan.setBorrowedBy("John Doe");

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        when(deviceRepository.findById(1L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loanService.markAsReturned(1L);
        });

        assertEquals("Device not found", exception.getMessage());
    }

    @Test
    void testGetLoansByDeviceId () {
        LoanRepository loanRepository = mock(LoanRepository.class);
        DeviceRepository deviceRepository = mock(DeviceRepository.class);
        LoanService loanService = new LoanService(loanRepository, deviceRepository);

        List<Loan> loans = new ArrayList<>();

        Loan loan1 = new Loan();
        loan1.setId(1L);
        loan1.setDeviceId(1L);
        loan1.setStartDate(123L);
        loan1.setEndDate(456L);
        loan1.setReturned(false);
        loan1.setBorrowedBy("John Doe");
        loans.add(loan1);

        Loan loan2 = new Loan();
        loan2.setId(2L);
        loan2.setDeviceId(1L);
        loan2.setStartDate(789L);
        loan2.setEndDate(101112L);
        loan2.setReturned(true);
        loan2.setBorrowedBy("Jane Doe");
        loans.add(loan2);

        when(loanRepository.findByDeviceId(1L))
                .thenReturn(loans);

        List<Loan> foundLoans = loanService.getLoansByDeviceId(1L);

        assertNotNull(foundLoans);
        assertEquals(2, foundLoans.size());
        assertEquals(loan1, foundLoans.get(0));
        assertEquals(loan2, foundLoans.get(1));
    }
}