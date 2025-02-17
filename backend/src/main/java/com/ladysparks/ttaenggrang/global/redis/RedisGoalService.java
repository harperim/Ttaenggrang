package com.ladysparks.ttaenggrang.global.redis;

import com.ladysparks.ttaenggrang.domain.student.dto.SavingsAchievementDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisGoalService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String GOAL_ACHIEVEMENT_KEY = "goal_achievement";

    /**
     * 목표 달성률 저장/업데이트 (학생 ID, 목표 달성률(%))
     */
    public int saveOrUpdateGoalAchievement(Long teacherId, SavingsAchievementDTO savingsAchievementDTO) {
        Long studentId = savingsAchievementDTO.getStudentId();
        double achievementRate = savingsAchievementDTO.getSavingsAchievementRate();

        String key = GOAL_ACHIEVEMENT_KEY + ":" + teacherId; // 반 ID 포함한 key 생성

        redisTemplate.opsForZSet().add(key, studentId, achievementRate);

        Long rank = redisTemplate.opsForZSet().reverseRank(key, studentId);
        return (rank != null) ? rank.intValue() + 1 : -1;
    }

//    public void saveOrUpdateGoalAchievement(SavingsAchievementDTO savingsAchievementDTO) {
//        Long studentId = savingsAchievementDTO.getStudentId();
//        double achievementRate = savingsAchievementDTO.getSavingsAchievementRate();
//
//        String key = GOAL_ACHIEVEMENT_KEY + studentId;
//        redisTemplate.opsForValue().set(key, achievementRate, Duration.ofDays(1)); // 1일 동안 캐싱
//        log.info("✅ Redis에 목표 달성률 저장: {} -> {}", studentId, achievementRate);
//    }

    /**
     * 특정 학생의 목표 달성률 및 순위 조회 (반 기준)
     */
    public SavingsAchievementDTO getGoalAchievement(Long teacherId, Long studentId) {
        String key = GOAL_ACHIEVEMENT_KEY + ":" + teacherId; // 반 ID 포함한 key

        Double savingsAchievementRate = redisTemplate.opsForZSet().score(key, studentId);
        Long rank = redisTemplate.opsForZSet().reverseRank(key, studentId);

        return SavingsAchievementDTO.builder()
                .studentId(studentId)
                .savingsAchievementRate(savingsAchievementRate)
                .rank((rank != null) ? rank.intValue() + 1 : -1)
                .build();
    }

//    public Double getGoalAchievement(Long studentId) {
//        String key = GOAL_ACHIEVEMENT_KEY + studentId;
//        Object value = redisTemplate.opsForValue().get(key);
//        return value != null ? (Double) value : null;
//    }

    /**
     * 목표 달성률 기준 TOP N 학생 조회
     */
    public List<SavingsAchievementDTO> getTopStudents(Long teacherId, int topN) {
        String key = GOAL_ACHIEVEMENT_KEY + ":" + teacherId; // 반 ID 포함한 key

        Set<ZSetOperations.TypedTuple<Object>> topStudents =
                redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, topN - 1);

        if (topStudents == null) {
            return Collections.emptyList();
        }

        List<SavingsAchievementDTO> studentList = new ArrayList<>();
        int rank = 1;

        for (ZSetOperations.TypedTuple<Object> tuple : topStudents) {
            Object studentIdObject = tuple.getValue();
            Long studentId = (studentIdObject instanceof Integer)
                    ? ((Integer) studentIdObject).longValue()
                    : (Long) studentIdObject;

            studentList.add(
                    SavingsAchievementDTO.builder()
                            .studentId(studentId)
                            .savingsAchievementRate(tuple.getScore())
                            .rank(rank++)  // 순위 증가
                            .build()
            );
        }

        return studentList;
    }

}