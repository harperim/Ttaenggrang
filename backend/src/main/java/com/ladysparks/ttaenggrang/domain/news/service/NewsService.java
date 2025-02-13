package com.ladysparks.ttaenggrang.domain.news.service;


import com.ladysparks.ttaenggrang.domain.news.dto.NewsDTO;
import com.ladysparks.ttaenggrang.domain.news.entity.News;
import com.ladysparks.ttaenggrang.domain.news.entity.NewsType;
import com.ladysparks.ttaenggrang.domain.news.repository.NewsRepository;
import com.ladysparks.ttaenggrang.domain.stock.entity.Stock;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

//@Service
//public class NewsService {
//
//    @Autowired
//    private OpenAiService openAiService;
//
//    @Autowired
//    private StockRepository stockRepository;
//
//    @Autowired
//    private NewsRepository newsRepository;
//
//    // 뉴스 생성 및 DB 저장 메소드
//    public NewsDTO generateAndPublishNews() {
//        // 1. 주식 리스트 가져오기
//        List<Stock> stocks = stockRepository.findAll();
//
//        // 주식 리스트가 비어 있는지 체크
//        if (stocks.isEmpty()) {
//            System.out.println("주식 데이터가 없습니다.");
//            return null;
//        }
//
//
//        // 2. 랜덤으로 주식 선택
//        Random random = new Random();
//        Stock selectedStock = stocks.get(random.nextInt(stocks.size()));
//        String companyName = selectedStock.getName();
//
//        // 3. 호재 뉴스 생성
//        String positivePrompt = "Create a positive news article about the company " + companyName + ", such as a successful product launch, positive earnings report, or new business venture.";
//        NewsDTO positiveNewsDTO = openAiService.generateNews(positivePrompt);
//        positiveNewsDTO.setNewsType(NewsType.호재);  // 뉴스 타입을 NewsType으로 설정
//
//        // 4. 악재 뉴스 생성
//        String negativePrompt = "Create a negative news article about the company " + companyName + ", such as a financial loss, legal trouble, or management scandal.";
//        NewsDTO negativeNewsDTO = openAiService.generateNews(negativePrompt);
//        negativeNewsDTO.setNewsType(NewsType.악재);  // 뉴스 타입을 NewsType으로 설정
//
//        // 5. 뉴스 객체 저장 (뉴스의 제목, 내용, 주식 정보 저장)
//        saveNewsToDatabase(positiveNewsDTO.getTitle(), positiveNewsDTO.getNewsType(), positiveNewsDTO.getContent(), selectedStock);
//        saveNewsToDatabase(negativeNewsDTO.getTitle(), negativeNewsDTO.getNewsType(), negativeNewsDTO.getContent(), selectedStock);
//
//        // 6. 생성된 뉴스 출력
//        System.out.println("Positive News: " + positiveNewsDTO.getContent());
//        System.out.println("Negative News: " + negativeNewsDTO.getContent());
//
//        // 6. 생성된 뉴스 반환
//        return positiveNewsDTO;  // 여기에서 호재 뉴스 반환 (혹은 악재 뉴스도 함께 반환할 수 있음)
//    }
//
//    // 뉴스 저장 메소드
//    public void saveNewsToDatabase(String title, NewsType newsType, String content, Stock stock) {
//        News news = new News();
//        news.setTitle(title);
//        news.setNewsType(newsType);
//        news.setContent(content);
//        news.setStock(stock); // 주식과 뉴스 연결
//        // 뉴스 저장
//        newsRepository.save(news);
//    }
//}