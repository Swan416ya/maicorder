// 存机厅
package com.maicorder.repository;

import com.maicorder.entity.Arcade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArcadeRepository extends JpaRepository<Arcade, Long> {
}