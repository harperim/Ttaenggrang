package com.ladysparks.ttaenggrang.data.remote

import com.ladysparks.ttaenggrang.base.ApplicationClass

class RetrofitUtil {
    companion object{
//        val commentService = ApplicationClass.retrofit.create(CommentService::class.java)
//        val orderService = ApplicationClass.retrofit.create(OrderService::class.java)
//        val productService = ApplicationClass.retrofit.create(ProductService::class.java)
//        val userService = ApplicationClass.retrofit.create(UserService::class.java)
        val storeService = ApplicationClass.retrofit.create(StoreService::class.java)
//        val likeService = ApplicationClass.retrofit.create(LikeService::class.java)
//        val diaryService = ApplicationClass.retrofit.create(DiaryService::class.java)
        val authService = ApplicationClass.retrofit.create(AuthService::class.java)
        val alarmService = ApplicationClass.retrofit.create(AlarmService::class.java)
        val teacherService = ApplicationClass.retrofit.create(TeacherService::class.java)
        val taxService = ApplicationClass.retrofit.create(TaxService::class.java)
        val salariesService = ApplicationClass.retrofit.create(SalariesService::class.java)
        val stockService = ApplicationClass.retrofit.create(StockService::class.java)
        val notificationService = ApplicationClass.retrofit.create(NotificationService::class.java)
        val voteService = ApplicationClass.retrofit.create(VoteService::class.java)

        val studentService = ApplicationClass.retrofit.create(StudentService::class.java)
    }
}
