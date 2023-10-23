package com.indra.userservice.controller;

import com.indra.userservice.domain.CompanyDto;
import com.indra.userservice.domain.CompanyEntity;
import com.indra.userservice.domain.CompanyEntityModel;
import com.indra.userservice.service.CompanyService;
import com.indra.userservice.support.BaseApiController;
import com.indra.userservice.support.ValidationObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Locale;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "companys", produces = MediaTypes.HAL_JSON_VALUE)
public class CompanyController extends BaseApiController {

    private final CompanyService companyService;

    @GetMapping()
    public ResponseEntity getCompanyList(@PageableDefault(size = 10, sort = "regDate", direction = Sort.Direction.DESC) Pageable pageable,
                                         Locale locale) {

        Page<CompanyEntity> companys = companyService.getCompanyList(pageable);

        PagedResourcesAssembler<CompanyEntity> assembler = new PagedResourcesAssembler<CompanyEntity>(null,null);
        RepresentationModelAssembler<CompanyEntity, CompanyEntityModel> companyAssembler;
        companyAssembler = e -> new CompanyEntityModel(e);
        var companyEnitiyModels = assembler.toModel(companys, companyAssembler);

        return makeDataMessageForSuccess (
                companyEnitiyModels,
                locale
        );

    }

    @PostMapping()
    public ResponseEntity createCompany(@RequestBody @Valid CompanyDto company,
                                        BindingResult bindingResult,
                                        Locale locale) {

        ValidationObject validationObject = new ValidationObject();
        if(doFormValidation(bindingResult,validationObject)) {
            return makeDataMessageForValidation(validationObject, locale);
        }

        WebMvcLinkBuilder selfLink = linkTo(methodOn(CompanyController.class).createCompany(company,null, locale)).slash(company.getId());
        URI createUri = selfLink.toUri();

        return makeDataMessageForCreated (
                createUri,
                companyService.createCompany(company),
                locale
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity modifyCompany(@RequestBody CompanyDto company,
                                        @PathVariable long id,
                                        Locale locale) {

        company.setId(id);
        CompanyEntity retCompany = companyService.modifyCompany(company);

        WebMvcLinkBuilder selfLink = linkTo(methodOn(CompanyController.class).modifyCompany(company,id,locale)).slash(company.getId());
        EntityModel entityModel = EntityModel.of(retCompany);
        entityModel.add(selfLink.withRel("modify-company"));
        entityModel.add(linkTo(methodOn(this.getClass()).createCompany(company, null, locale)).withRel("create-company"));
        entityModel.add(selfLink.withSelfRel());

        return makeDataMessageForSuccess (
                entityModel,
                locale
        );

    }


}
