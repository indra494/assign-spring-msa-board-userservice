package com.indra.userservice.controller;

import com.indra.userservice.domain.MemberDto;
import com.indra.userservice.domain.MemberEntity;
import com.indra.userservice.domain.MemberEntityModel;
import com.indra.userservice.service.MemberService;
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
@RequestMapping(value = "members", produces = MediaTypes.HAL_JSON_VALUE)
public class MemberController extends BaseApiController {

    private final MemberService memberService;

    @GetMapping()
    public ResponseEntity getMemberList(@PageableDefault(size = 10, sort = "regDate", direction = Sort.Direction.DESC) Pageable pageable,
                                        Locale locale) {

        Page<MemberEntity> members = memberService.getMemberList(pageable);

        PagedResourcesAssembler<MemberEntity> assembler = new PagedResourcesAssembler<MemberEntity>(null,null);
        RepresentationModelAssembler<MemberEntity, MemberEntityModel> memberAssembler;
        memberAssembler = e -> new MemberEntityModel(e);
        var memberEnitiyModels = assembler.toModel(members, memberAssembler);

        return makeDataMessageForSuccess (
                memberEnitiyModels,
                locale
        );

    }

    @PostMapping()
    public ResponseEntity createMember(@RequestBody @Valid MemberDto member,
                                       BindingResult bindingResult,
                                       Locale locale) {

        ValidationObject validationObject = new ValidationObject();
        if(doFormValidation(bindingResult,validationObject)) {
            return makeDataMessageForValidation(validationObject, locale);
        }

        WebMvcLinkBuilder selfLink = linkTo(methodOn(MemberController.class).createMember(member,null, locale)).slash(member.getAccountId());
        URI createUri = selfLink.toUri();

        return makeDataMessageForCreated (
                createUri,
                memberService.createMember(member),
                locale
        );
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity modifyMemberRole(@RequestBody MemberDto member,
                                           @PathVariable long id,
                                           Locale locale) {

        member.setId(id);
        MemberEntity retMember = memberService.modifyMemberRole(member);

        WebMvcLinkBuilder selfLink = linkTo(methodOn(MemberController.class).modifyMemberRole(member,id,locale)).slash(member.getAccountId());
        EntityModel entityModel = EntityModel.of(retMember);
        entityModel.add(selfLink.withRel("modify-member"));
        entityModel.add(linkTo(methodOn(this.getClass()).createMember(member, null, locale)).withRel("create-member"));
        entityModel.add(selfLink.withSelfRel());

        return makeDataMessageForSuccess (
                entityModel,
                locale
        );

    }


}
