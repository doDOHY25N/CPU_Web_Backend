package com.cpu.web.repository.board;

import com.cpu.web.entity.board.Bulletin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BulletinRepository extends JpaRepository<Bulletin, Long> {
}