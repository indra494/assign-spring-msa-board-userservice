package com.indra.userservice.domain;

import com.indra.userservice.controller.MemberController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class MemberEntityModel extends EntityModel<MemberEntity> {
    public MemberEntityModel(MemberEntity memberEntity, Iterable<Link>... links){
        super(memberEntity);
        add(linkTo(MemberController.class).slash(memberEntity.getId()).withSelfRel());
    }
}