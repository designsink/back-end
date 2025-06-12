package com.designsink.dsink.repository.info;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.designsink.dsink.entity.info.MainPageInfo;

@Repository
public interface MainPageInfoRepository extends JpaRepository<MainPageInfo, Integer> {
}
