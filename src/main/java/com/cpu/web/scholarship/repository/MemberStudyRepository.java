package com.cpu.web.scholarship.repository;

import com.cpu.web.scholarship.entity.MemberStudy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberStudyRepository extends JpaRepository<MemberStudy, Long> {
    // 특정 멤버가 특정 스터디에 참여하고 있는지 확인
    boolean existsByStudy_StudyIdAndMember_MemberId(Long studyId, Long memberId);

    // 특정 멤버가 리더인지 확인
    boolean existsByStudy_StudyIdAndMember_MemberIdAndIsLeader(Long studyId, Long memberId, boolean isLeader);

    // 특정 멤버의 신청 정보 조회
    Optional<MemberStudy> findByStudy_StudyIdAndMember_MemberId(Long studyId, Long memberId);
}
