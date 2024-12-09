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
        validateCompany(company, null);
        companyRepository.save(company);
    }

    public void updateCompany(Long id, Company updatedCompany) {
        validateCompany(updatedCompany, id);
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid company ID: " + id));
        company.setName(updatedCompany.getName());
        company.setTimeToFirsRespond(updatedCompany.getTimeToFirsRespond());
        company.setTimeToResolve(updatedCompany.getTimeToResolve());
        companyRepository.save(company);
    }


    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Firma o podanym ID nie istnieje: " + id));
        companyRepository.delete(company);
    }

    private void validateCompany(Company company, Long id) {
        if (company.getTimeToFirsRespond() < 1 || company.getTimeToFirsRespond() > 48) {
            throw new IllegalArgumentException("Czas na pierwszą reakcję musi być pomiędzy 1 a 48 godzin.");
        }
        if (company.getTimeToResolve() < 1 || company.getTimeToResolve() > 168) {
            throw new IllegalArgumentException("Czas na rozwiązanie musi być pomiędzy 1 a 168 godzin.");
        }
        if (companyRepository.findByName(company.getName()).isPresent()) {
            Long existingCompanyId = companyRepository.findByName(company.getName()).get().getId();
            if (id == null || !existingCompanyId.equals(id)) {
                throw new IllegalArgumentException("Firma o podanej nazwie już istnieje: " + company.getName());
            }
        }
    }


}
