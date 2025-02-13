package com.ladysparks.ttaenggrang.global.docs;

import com.ladysparks.ttaenggrang.domain.teacher.dto.IncentiveDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kotlin.OptionalExpectation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Salary", description = "지급 관련 API")
public interface SalaryApiSpecification {

    @Operation(summary = "학생 주급 지급", description = """
            💡 교사가 '주급 지급' 버튼을 클릭하면, 각 학생의 직업에 따라 설정된 기본급(`baseSalary`)이 학생들의 계좌로 자동으로 지급됩니다.
            ### 기능 설명
            - **대상:** 교사의 반에 등록된 모든 학생
            - **급여 기준:** 학생이 가진 직업(`job`)의 기본급(`baseSalary`)
            - **결과:** 지급된 금액이 학생들의 계좌(`BankAccount`) 잔액에 반영됨
            
            ### 예외 사항
            - ⚠️ **주의:** 이미 지급된 주급에 대한 중복 지급은 방지됩니다.
            - 주급은 지급일 기준, **최소 일주일 후** 지급 가능
            
            ### 에러 유형
            - `401 Unauthorized` : "인증되지 않은 사용자입니다. 로그인 후 다시 시도하세요."
            - `404 Not Found` : "해당 이메일을 가진 교사를 찾을 수 없습니다."
            - `400 BAD_REQUEST` : "이미 이번 주 주급이 지급되었습니다."
            - `404 Not Found` : "해당 교사를 찾을 수 없습니다."
            - `404 Not Found` : "우리 반 학생이 없습니다."
            - `500 Internal Server Error` : 서버 내부 오류 발생
            - `200 OK` : "주급이 성공적으로 지급되었습니다."
            """)
    ResponseEntity<ApiResponse<String>> distributeBaseSalary();

    @Operation(summary = "학생 인센티브 지급", description = """
            💡 교사가 우리 반 학생들에게 인센티브를 지급하는 기능입니다.
            ### 기능 설명
            - 교사는 드롭다운 목록에서 학생을 선택하고, 인센티브 금액을 입력하여 지급할 수 있습니다.
            - 인센티브는 **일주일에 한 번**만 같은 학생에게 지급할 수 있습니다.
            - 지급된 인센티브는 해당 학생의 **은행 계좌에 자동으로 입금**되며, 지급 내역은 저장됩니다.
           
            ### 요청 값
            - `studentId` : 인센티브를 받을 학생의 ID
            - `incentive` : 지급할 인센티브 금액 (양수 값)

            ### 예외 사항
            - 학생의 계좌 정보가 없을 경우 인센티브 지급이 불가합니다.
            - 이미 인센티브를 지급한 학생에게는 일주일 이내에 다시 지급할 수 없습니다.
           
            ### 응답
            - 성공 시: **"인센티브가 성공적으로 지급되었습니다."** 메시지를 반환합니다.
            - 실패 시: 오류 메시지와 함께 상태 코드(`400`, `404`)를 반환합니다.
            
            ### 에러 유형
            - `404 NOT_FOUND` : "학생 정보를 찾을 수 없습니다."
            - `400 BAD_REQUEST` : "학생의 계좌 정보가 없습니다."
            - `400 BAD_REQUEST` : "최근 일주일 이내에 이미 인센티브가 지급되었습니다."
            - `500 Internal Server Error` : 서버 내부 오류 발생
            - `200 OK` : "인센티브가 성공적으로 지급되었습니다."
            """)
    ResponseEntity<ApiResponse<String>> giveIncentive(@RequestBody IncentiveDTO incentiveDTO);
}
