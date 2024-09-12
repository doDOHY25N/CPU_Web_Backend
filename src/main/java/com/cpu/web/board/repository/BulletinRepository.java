package com.cpu.web.board.repository;

import com.cpu.web.board.entity.Bulletin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BulletinRepository extends JpaRepository<Bulletin, Long> {
    // 페이징 처리된 게시글 목록 조회
    Page<Bulletin> findAll(Pageable pageable);
}