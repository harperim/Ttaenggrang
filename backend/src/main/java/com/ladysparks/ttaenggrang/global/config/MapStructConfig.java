package com.ladysparks.ttaenggrang.global.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
        componentModel = "spring", // Spring Bean으로 관리
        unmappedTargetPolicy = ReportingPolicy.IGNORE // 매핑되지 않은 필드는 무시
)

public interface MapStructConfig {}
