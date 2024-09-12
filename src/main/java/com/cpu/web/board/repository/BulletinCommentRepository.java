package com.cpu.web.board.repository;

import com.cpu.web.board.entity.BulletinComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BulletinCommentRepository extends JpaRepository<BulletinComment, Long> {
    List<BulletinComment> findByBulletin_BulletinId(Long bulletinId);
}