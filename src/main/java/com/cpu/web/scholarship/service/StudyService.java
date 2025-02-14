package com.cpu.web.scholarship.service;

import com.cpu.web.member.entity.Member;
import com.cpu.web.member.repository.MemberRepository;
import com.cpu.web.scholarship.dto.request.StudyRequestDTO;
import com.cpu.web.scholarship.entity.MemberStudy;
import com.cpu.web.scholarship.entity.Study;
import com.cpu.web.scholarship.repository.MemberStudyRepository;
import com.cpu.web.scholarship.repository.StudyRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;
    private final MemberStudyRepository memberStudyRepository;
    private final EntityManager entityManager;

    @Transactional
    public Study createStudy(StudyRequestDTO studyRequestDTO) {

        // 로그인된 사용자 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("로그인된 사용자명: " + username);
        Optional<Member> member = memberRepository.findByUsername(username);

        if (member.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        // 리더 ID 가져오기
        Member leader = member.get();
        if (leader == null || leader.getMemberId() == null) {
            throw new IllegalArgumentException("리더 정보가 올바르지 않습니다. leader = " + leader);
        }

        Long leaderId = Optional.ofNullable(leader.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("리더 ID가 존재하지 않습니다. leader: " + leader));

        System.out.println("leader = " + leader);
        System.out.println("leader ID = " + leader.getMemberId());

        // 스터디 생성 및 저장
        Study study = studyRequestDTO.toStudyEntity(leader);
        Study savedStudy = studyRepository.save(study);

        // 강제 flush로 즉시 DB 반영
        studyRepository.flush();
        System.out.println("Study 저장 완료, leader ID = " + savedStudy.getLeaderId());

        if (savedStudy.getLeaderId() == null) {
            throw new IllegalArgumentException("Study의 leader_id가 null입니다.");
        }

        // ✅ leader를 `EntityManager`를 통해 관리 상태로 변경
        Member managedLeader = entityManager.merge(leader);
        System.out.println("Managed leader ID = " + managedLeader.getMemberId());

        if (leader == null) {
            throw new IllegalArgumentException("leader 객체가 null입니다.");
        }
        if (leader.getMemberId() == null) {
            throw new IllegalArgumentException("leader ID가 null입니다.");
        }
        if (leader.getClass() != Member.class) {
            throw new IllegalArgumentException("leader 객체 타입이 Member가 아닙니다.");
        }
        System.out.println("MemberStudy에 추가될 멤버아이디 = " + leader.getMemberId());


        // 매핑 테이블에 팀장 정보 추가
        MemberStudy memberStudy = new MemberStudy();
        memberStudy.setMember(managedLeader); // ✅ `managedLeader` 사용
        memberStudy.setStudy(savedStudy);
        memberStudy.setIsLeader(true);

        // 멤버 정보가 정상적으로 들어가는지 확인
        System.out.println("MemberStudy에 추가될 멤버아이디 = " + managedLeader.getMemberId());


        memberStudyRepository.save(memberStudy);

        return savedStudy;
    }

//
//
//    public Page<StudyRequestDTO> getAllStudies(int page, int size, String studyType) {
//        PageRequest pageRequest = PageRequest.of(page, size);
//        if (studyType != null && !studyType.isEmpty()) {
//            Study.StudyType type = Study.StudyType.valueOf(studyType.toLowerCase());
//            return studyRepository.findByStudyType(type, pageRequest).map(StudyRequestDTO::new);
//        }
//        return studyRepository.findAll(pageRequest).map(StudyRequestDTO::new);
//    }
//
//    public Optional<StudyRequestDTO> getStudyById(Long id) {
//        // ✅ 스터디 정보 가져오기
//        Optional<Study> study = studyRepository.findById(id);
//        if (study.isEmpty()) {
//            return Optional.empty();
//        }
//
//        // ✅ 해당 스터디에 참여 중인 멤버 정보 가져오기
//        List<MemberStudy> memberStudies = memberStudyRepository.findByStudy_StudyId(id);
//
//        // ✅ StudyDTO 변환
//        return Optional.of(new StudyRequestDTO(study.get(), memberStudies));
//    }
//
//
//    public StudyRequestDTO updateStudy(Long id, StudyRequestDTO studyRequestDTO) {
//        // 로그인된 사용자 정보 가져오기
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        Optional<Member> member = memberRepository.findByUsername(username);
//
//        if (member.isEmpty()) {
//            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
//        }
//
//        Long leaderId = member.get().getMemberId(); // ✅ 현재 로그인한 사용자 ID 가져오기
//
//        // 스터디 찾기
//        Study study = studyRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid study ID: " + id));
//
//        // ✅ 스터디 리더인지 확인
//        if (!study.getLeaderId().equals(leaderId)) {
//            throw new IllegalArgumentException("팀장이 아니므로 수정 권한이 없습니다: " + leaderId);
//        }
//
//        study.setStudyName(studyRequestDTO.getStudyName());
//        study.setStudyDescription(studyRequestDTO.getStudyDescription());
//        study.setMaxMembers(studyRequestDTO.getMaxMembers());
//        study.setTechStack(studyRequestDTO.getTechStack());
//        study.setLocation(studyRequestDTO.getLocation());
//        study.setEtc(studyRequestDTO.getEtc());
//
//        // studyType 변환 처리
//        String typeStr = studyRequestDTO.getStudyType().toLowerCase().trim();
//        switch (typeStr) {
//            case "study":
//                study.setStudyType(Study.StudyType.study);
//                break;
//            case "session":
//                study.setStudyType(Study.StudyType.session);
//                break;
//            case "project":
//                study.setStudyType(Study.StudyType.project);
//                break;
//            default:
//                throw new IllegalArgumentException("유효하지 않은 스터디 타입입니다: " + studyRequestDTO.getStudyType());
//        }
//
//        return new StudyRequestDTO(studyRepository.save(study));
//    }
//
//
//    public void deleteStudy(Long id) {
//        // 로그인된 사용자 정보 가져오기
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        Optional<Member> member = memberRepository.findByUsername(username);
//
//        if (member.isEmpty()) {
//            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
//        }
//
//        Long leaderId = member.get().getMemberId(); // ✅ 현재 로그인한 사용자 ID 가져오기
//
//        // 스터디 찾기
//        Study study = studyRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid study ID: " + id));
//
//        // ✅ 스터디 리더인지 확인
//        if (!study.getLeaderId().equals(leaderId)) {
//            throw new IllegalArgumentException("팀장이 아니므로 삭제 권한이 없습니다: " + leaderId);
//        }
//
//        studyRepository.deleteById(id);
//    }

}
