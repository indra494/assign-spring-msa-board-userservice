package com.indra.userservice.service;

import com.indra.userservice.domain.CompanyEntity;
import com.indra.userservice.domain.MemberDto;
import com.indra.userservice.domain.MemberEntity;
import com.indra.userservice.domain.MemberRedis;
import com.indra.userservice.exception.BasicException;
import com.indra.userservice.repository.CompanyRepository;
import com.indra.userservice.repository.MemberRedisRepository;
import com.indra.userservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Locale;

@RequiredArgsConstructor
@Service
public class MemberService implements UserDetailsService {

    private final MessageSource messageSource;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final CompanyRepository companyRepository;
    private final MemberRedisRepository memberRedisRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public Page<MemberEntity> getMemberList(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    public MemberEntity createMember(MemberDto member) {

        CompanyEntity companyEntity = companyRepository.findById(member.getCompanyId());
        if(companyEntity == null) {
            Locale locale = LocaleContextHolder.getLocale();
            throw new BasicException(messageSource.getMessage("company.error.modify_company",null,locale));
        }

        MemberEntity memberEntity = modelMapper.map(member, MemberEntity.class);
        memberEntity.setCompany(companyEntity);
        memberEntity.setPassword(passwordEncoder.encode(member.getPassword()));
        return memberRepository.save(memberEntity);
    }

    public MemberRedis createMemberRedis(MemberRedis memberRedis) {
        return memberRedisRepository.save(memberRedis);
    }

    public MemberEntity modifyMemberRole(MemberDto member) {
        MemberEntity memberEntity = memberRepository.findById(member.getId());

        if(memberEntity == null) {
            Locale locale = LocaleContextHolder.getLocale();
            throw new BasicException(messageSource.getMessage("member.error.modify_member_role",null,locale));
        }

        memberEntity.setRole(member.getRole());
        return memberEntity;
    }

    public MemberDto getUserDetailsById(long id) {
        MemberEntity memberEntity = memberRepository.findById(id);

        if(memberEntity == null) {
            throw new UsernameNotFoundException(Long.toString(id));
        }

        return new ModelMapper().map(memberEntity, MemberDto.class);
    }

    public MemberDto getUserDetailsByAccountId(String accountId) {
        MemberEntity memberEntity = memberRepository.findByAccountId(accountId);

        if(memberEntity == null) {
            throw new UsernameNotFoundException(accountId);
        }

        return new ModelMapper().map(memberEntity, MemberDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberEntity memberEntity = memberRepository.findByAccountId(username);

        if(memberEntity == null)
            throw new UsernameNotFoundException(username);

        return new User(memberEntity.getAccountId(), memberEntity.getPassword(), true, true, true, true, new ArrayList<>());
    }
}
