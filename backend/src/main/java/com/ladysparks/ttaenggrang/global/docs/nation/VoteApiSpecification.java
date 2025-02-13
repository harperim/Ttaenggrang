package com.ladysparks.ttaenggrang.global.docs.nation;

import com.ladysparks.ttaenggrang.domain.vote.dto.VoteCreateDTO;
import com.ladysparks.ttaenggrang.domain.vote.dto.VoteItemResponseDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "[교사/학생] 투표", description = "투표 관리 기능 API")
public interface VoteApiSpecification {

    @Operation(summary = "(교사) 투표 생성 [새 투표 등록]", description = """
            💡 교사가 새 투표를 생성합니다.
            
            - **학생 모드 (STUDENT)**: 우리 반 학생 목록을 자동으로 불러와 투표 항목으로 추가합니다.
            - **선택형 모드 (CUSTOM)**: 교사가 직접 투표 항목을 추가할 수 있습니다.
            
            ✅ 진행 중인 투표가 있을 경우 새 투표를 생성할 수 없습니다.
            """)
    ResponseEntity<ApiResponse<VoteCreateDTO>> createVote(@Valid @RequestBody VoteCreateDTO voteCreateDTO);

    @Operation(summary = "(교사) 현재 진행 중인 투표 [조회]", description = """
            💡 교사가 진행 중인 투표를 조회합니다.
            현재 진행 중인 투표가 있는지 확인하고, 진행 중인 투표가 있을 경우 해당 정보를 반환합니다.
            
            - IN_PROGRESS : 현재 진행 중 / COMPLETED : 투표 완료
            - **진행 중인 투표가 있는 경우**: 투표 제목, 기간, 상태 등의 정보를 확인할 수 있습니다.
            - **진행 중인 투표가 없는 경우**: '진행 중인 투표가 없습니다' 메시지를 반환합니다.
            """)
    ResponseEntity<ApiResponse<VoteCreateDTO>> getCurrentVote();

    @Operation(summary = "(교사) 진행 중인 투표 종료 [투표 마감]", description = """
            💡 교사가 진행 중인 투표를 종료합니다.
            
            - IN_PROGRESS : 현재 진행 중 / COMPLETED : 투표 완료
            - 진행 중인 투표의 상태가 **종료(COMPLETED)**로 변경됩니다.
            - 투표가 종료된 후 새로운 투표를 생성할 수 있습니다.
            """)
    ResponseEntity<ApiResponse<String>> stopCurrentVote();

    @Operation(summary = "(학생) 우리 반 학생 목록 보여주기", description = """
            💡 현재 투표가 진행 중이라면, 아이들이 투표할 수 있도록 우리 반 학생 목록을 보여줍니다.
            """)
    ResponseEntity<ApiResponse<List<VoteItemResponseDTO>>> getVoteItems();

    @Operation(summary = "(학생) 투표하기", description = """
            💡 현재 투표가 진행 중이라면, 우리 반 친구들 중 한 명을 투표할 수 있습니다.
            """)
    ResponseEntity<ApiResponse<String>> castStudentVote(@RequestParam Long voteItemId);

}
