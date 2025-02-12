package com.ladysparks.ttaenggrang.domain.teacher.service;

import com.ladysparks.ttaenggrang.domain.bank.service.BankAccountService;
import com.ladysparks.ttaenggrang.domain.item.service.ItemService;
import com.ladysparks.ttaenggrang.domain.teacher.dto.NationDTO;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.teacher.dto.TeacherDashboardDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

@Service
@RequiredArgsConstructor
public class TeacherDashboardService {

    private final NationService nationService;
    private final StudentService studentService;
    private final ItemService itemService;
    private final TeacherService teacherService;
    private final BankAccountService bankAccountService;

    public TeacherDashboardDTO getTeacherDashboardData() {
        Long teacherId = teacherService.getCurrentTeacherId();

        NationDTO nationDTO = nationService.findNationByTeacherId(teacherId);

        int nationalTreasuryIncome = nationDTO.getNationalTreasury();
        int averageStudentBalance = (int) bankAccountService.getAverageBalanceByTeacherId(teacherId);
        int activeItemCount = itemService.findActiveItemListByTeacher().size();
        int classSavingsGoal = nationDTO.getSavingsGoalAmount();

        return TeacherDashboardDTO.builder()
                .nationalTreasuryIncome(nationalTreasuryIncome)
                .averageStudentBalance(averageStudentBalance)
                .activeItemCount(activeItemCount)
                .classSavingsGoal(classSavingsGoal)
                .build();
    }

}
