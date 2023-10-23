package com.indra.userservice.domain;

import com.indra.userservice.controller.CompanyController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class CompanyEntityModel extends EntityModel<CompanyEntity> {
    public CompanyEntityModel(CompanyEntity companyEntity, Iterable<Link>... links){
        super(companyEntity);
        add(linkTo(CompanyController.class).slash(companyEntity.getId()).withSelfRel());
    }
}