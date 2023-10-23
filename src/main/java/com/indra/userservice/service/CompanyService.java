package com.indra.userservice.service;

import com.indra.userservice.domain.CompanyDto;
import com.indra.userservice.domain.CompanyEntity;
import com.indra.userservice.exception.BasicException;
import com.indra.userservice.repository.CompanyRepository;
import com.indra.userservice.utils.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Locale;

@RequiredArgsConstructor
@Service
public class CompanyService {

    private final MessageSource messageSource;
    private final ModelMapper modelMapper;
    private final CompanyRepository companyRepository;

    public Page<CompanyEntity> getCompanyList(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }
    public CompanyEntity createCompany(CompanyDto company) {
        CompanyEntity companyEntity = modelMapper.map(company, CompanyEntity.class);
        return companyRepository.save(companyEntity);
    }

    public CompanyEntity modifyCompany(CompanyDto company) {
        CompanyEntity companyEntity = companyRepository.findById(company.getId());

        if(companyEntity == null) {
            Locale locale = LocaleContextHolder.getLocale();
            throw new BasicException(messageSource.getMessage("company.error.modify_company",null,locale));
        }

        companyEntity.setName(ObjectUtil.nvl(company.getName(),companyEntity.getName()).toString());
        companyEntity.setDescription(ObjectUtil.nvl(company.getDescription(),companyEntity.getDescription()).toString());
        return companyEntity;
    }



}
