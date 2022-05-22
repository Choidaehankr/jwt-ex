package com.springsecurity.ex.backend.domain.repository;

import com.springsecurity.ex.backend.domain.Member;
import com.springsecurity.ex.backend.login.oauth2.SocialType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}
