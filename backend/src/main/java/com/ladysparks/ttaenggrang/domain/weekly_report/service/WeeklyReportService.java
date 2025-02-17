package com.ladysparks.ttaenggrang.domain.weekly_report.service;

import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsSubscription;
import com.ladysparks.ttaenggrang.domain.bank.service.BankTransactionService;
import com.ladysparks.ttaenggrang.domain.bank.service.SavingsSubscriptionService;
import com.ladysparks.ttaenggrang.domain.item.service.ItemTransactionService;
import com.ladysparks.ttaenggrang.domain.weekly_report.dto.WeeklyReportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeeklyReportService {

    private final BankTransactionService bankService;
    private final ItemTransactionService itemService;
    private final SavingsSubscriptionService savingsService;

    public WeeklyReportDTO generateWeeklyReport() {
        return bankService.generateWeeklyReport();
    }

}
