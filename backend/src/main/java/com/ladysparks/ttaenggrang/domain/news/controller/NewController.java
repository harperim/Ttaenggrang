package com.ladysparks.ttaenggrang.domain.news.controller;

//import com.ladysparks.ttaenggrang.domain.news.service.NewsService;
//import com.ladysparks.ttaenggrang.domain.news.service.OpenAiService;


//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/news")
//public class NewController implements NewsApiSpecification {
//    @Autowired
//    private OpenAiService openAiService;
//    @Autowired
//    private NewsService newsService;
//
//    @PostMapping("/create")
//    public ResponseEntity<ApiResponse<NewsDTO>> generateNews() {
//        NewsDTO newsDTO = newsService.generateAndPublishNews();
//
//        ApiResponse<NewsDTO> response = new ApiResponse<>(200, "뉴스가 성공적으로 생성되었습니다.", newsDTO);
//
//        return ResponseEntity.ok(response);
//    }
//}

//    @GetMapping("/generate-news")
//    public ResponseEntity<ApiResponse<NewsDTO>> generateNews() {
//        // 뉴스 생성
//        NewsDTO newsDTO = newsService.generateAndPublishNews();  // 생성된 뉴스
//
//        // ApiResponse 객체 생성
//        ApiResponse<NewsDTO> response = new ApiResponse<>(true, "뉴스가 성공적으로 생성되었습니다.", newsDTO);
//
//        // ResponseEntity로 ApiResponse 반환
//        return ResponseEntity.ok(response);
//    }


