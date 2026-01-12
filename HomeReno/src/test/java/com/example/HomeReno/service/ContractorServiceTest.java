package com.example.HomeReno.service;

import com.example.HomeReno.entity.Contractor;
import com.example.HomeReno.repository.ContractorRepository;
import com.example.HomeReno.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ContractorServiceTest {
    @Mock
    private ContractorRepository contractorRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ContractorService contractorService;

    @Test
    void createContractorBlankNameThrows() {
        Contractor contractor = new Contractor(" ", 100.0, Contractor.Expertise.SENIOR);

        assertThrows(IllegalArgumentException.class, () -> contractorService.createContractor(contractor));
    }

    @Test
    void createContractorNegativePriceThrows() {
        Contractor contractor = new Contractor("Jane Builder", -1.0, Contractor.Expertise.SENIOR);

        assertThrows(IllegalArgumentException.class, () -> contractorService.createContractor(contractor));
    }
}
