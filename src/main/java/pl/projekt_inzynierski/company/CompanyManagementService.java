package pl.projekt_inzynierski.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyManagementService {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyManagementService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public List<Company> findAllCompanies() {
        return companyRepository.findAll();
    }

    public void saveCompany(Company company) {
        if (companyRepository.findByName(company.getName()).isPresent()) {
            throw new IllegalArgumentException("Firma o podanej nazwie już istnieje: " + company.getName());
        }
        companyRepository.save(company);
    }

    public void updateCompany(Long id, Company updatedCompany) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid company ID: " + id));
        company.setName(updatedCompany.getName());
        company.setTimeToFirsRespond(updatedCompany.getTimeToFirsRespond());
        company.setTimeToResolve(updatedCompany.getTimeToResolve());
        companyRepository.save(company);
    }

    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }


}
