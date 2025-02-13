package com.ladysparks.ttaenggrang.domain.student.service;

import com.ladysparks.ttaenggrang.domain.bank.dto.BankAccountDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankAccount;
import com.ladysparks.ttaenggrang.domain.bank.mapper.BankAccountMapper;
import com.ladysparks.ttaenggrang.domain.bank.service.BankAccountService;
import com.ladysparks.ttaenggrang.domain.student.dto.*;
import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.student.repository.StudentRepository;
import com.ladysparks.ttaenggrang.domain.teacher.dto.*;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Job;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import com.ladysparks.ttaenggrang.domain.teacher.repository.JobRespository;
import com.ladysparks.ttaenggrang.domain.teacher.repository.TeacherRepository;
import com.ladysparks.ttaenggrang.domain.teacher.service.NationService;
import com.ladysparks.ttaenggrang.domain.weekly_report.service.InvestmentService;
import com.ladysparks.ttaenggrang.global.config.JwtTokenProvider;
import com.ladysparks.ttaenggrang.global.redis.RedisGoalService;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import com.ladysparks.ttaenggrang.global.utill.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final NationService nationService;
    private final InvestmentService investmentService;
    private final RedisGoalService redisGoalService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityUtil securityUtil;
    private final JobRespository jobRespository;
    private final BankAccountService bankAccountService;
    private final BankAccountMapper bankAccountMapper;

    public Long getCurrentStudentId() {
        String username = securityUtil.getCurrentUser();
        return studentRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 학생을 찾을 수 없습니다."))
                .getId();
    }

    public Optional<Long> getOptionalCurrentStudentId() {
        String username = securityUtil.getCurrentUser();
        return studentRepository.findByUsername(username).map(Student::getId);
    }

    // ✅ 프로필 이미지 URL 업데이트 메서드
    @Transactional
    public void updateProfileImage(Long studentId, String profileImageUrl) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));

        student.setProfileImageUrl(profileImageUrl);  // ✅ 이미지 URL 저장
        studentRepository.save(student);
    }

    // ✅ 계좌 번호 생성 메서드
    private String generateAccountNumber() {
        return "110-" + (int) (Math.random() * 1_000_000_000);
    }

    // 🔥 파일 파싱 메서드 (CSV 및 XLSX 파일 처리)
    private List<String> parseNamesFromFile(MultipartFile file) {
        List<String> names = new ArrayList<>();
        String fileName = file.getOriginalFilename();

        try {
            if (fileName != null && fileName.endsWith(".csv")) {
                // CSV 파일 처리
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                    String line;
                    String[] headers = reader.readLine().split(",");  // 첫 줄(헤더) 읽기

                    int nameColumnIndex = -1;
                    for (int i = 0; i < headers.length; i++) {
                        if ("이름".equals(headers[i].trim())) {
                            nameColumnIndex = i;
                            break;
                        }
                    }

                    if (nameColumnIndex == -1) {
                        throw new IllegalArgumentException("CSV 파일에 '이름' 컬럼이 없습니다.");
                    }

                    // 데이터 행 읽기
                    while ((line = reader.readLine()) != null) {
                        String[] values = line.split(",");
                        if (values.length > nameColumnIndex) {
                            names.add(values[nameColumnIndex].trim());
                        }
                    }
                }
            } else if (fileName != null && (fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
                // XLSX 파일 처리
                Workbook workbook = new XSSFWorkbook(file.getInputStream());
                Sheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rowIterator = sheet.iterator();

                if (!rowIterator.hasNext()) {
                    throw new IllegalArgumentException("엑셀 파일이 비어 있습니다.");
                }

                // 헤더 행 읽기
                Row headerRow = rowIterator.next();
                int nameColumnIndex = -1;

                for (Cell cell : headerRow) {
                    if (cell.getCellType() == CellType.STRING) {
                        if ("이름".equals(cell.getStringCellValue().trim())) {
                            nameColumnIndex = cell.getColumnIndex();
                            break;
                        }
                    }
                }

                if (nameColumnIndex == -1) {
                    throw new IllegalArgumentException("엑셀 파일에 '이름' 컬럼이 없습니다.");
                }

                // 데이터 행 읽기
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    Cell nameCell = row.getCell(nameColumnIndex);

                    if (nameCell != null && nameCell.getCellType() == CellType.STRING) {
                        names.add(nameCell.getStringCellValue().trim());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 파싱 중 오류 발생", e);
        }

        return names;
    }


    // 여러 학생 계정 생성
    @Transactional
    public List<StudentResponseDTO> createStudentAccounts(Long teacherId, MultipleStudentCreateDTO multipleStudentCreateDTO) {
        // 1️⃣ 교사 확인
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("교사를 찾을 수 없습니다."));

        List<StudentResponseDTO> createdStudents = new ArrayList<>();
        List<String> namesFromFile = new ArrayList<>();

        // 🔥 파일이 업로드된 경우 이름 추출
        if (multipleStudentCreateDTO.getFile() != null && !multipleStudentCreateDTO.getFile().isEmpty()) {
            namesFromFile = parseNamesFromFile(multipleStudentCreateDTO.getFile());  // 파일에서 이름 리스트 가져오기
        }

        // 2️⃣ 학생 계정 자동 생성 (이름 포함)
        for (int i = 1; i <= multipleStudentCreateDTO.getStudentCount(); i++) {
            String username = multipleStudentCreateDTO.getBaseId() + i;
            String password = multipleStudentCreateDTO.getBaseId() + i;

            // 3️⃣ 중복 확인
            if (studentRepository.findByUsername(username).isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 학생 계정: " + username);
            }

            // 4️⃣ BankAccount 생성 및 저장 (중요!!)
            BankAccountDTO bankAccountDTO = BankAccountDTO.builder()
                    .accountNumber(generateAccountNumber()) // 계좌 번호 생성
                    .balance(0) // 초기 잔액 0
                    .build();

            // DTO를 Entity로 변환 후 **저장**
            BankAccount bankAccount = bankAccountMapper.toUpdatedEntity(bankAccountService.addBankAccount(bankAccountDTO)); // DB에 먼저 저장

            // 🔥 파일에서 이름이 있는 경우, 해당 이름 사용
            String studentName = (i <= namesFromFile.size()) ? namesFromFile.get(i - 1) : null;

            // 4. 기본 직업 "시민"으로 설정
            Job defaultJob = jobRespository.findByJobName("시민")
                    .orElseGet(() -> {
                        Job newJob = Job.builder()
                                .jobName("시민")
                                .jobDescription("기본 직업입니다.")
                                .baseSalary(1000)
                                .maxPeople(30)
                                .build();
                        return jobRespository.save(newJob);
                    });

            // 5️⃣ 학생 계정 생성 (은행 계좌 연결)
            Student student = Student.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .teacher(teacher)
                    .bankAccount(bankAccount) // ✅ **저장된 계좌 연결**
                    .name(studentName)  // 이름 저장
                    .job(defaultJob)
                    .build();

            studentRepository.save(student); // ✅ **저장된 bankAccount를 참조하는 상태에서 저장**

            // 6. 직업 정보 jobinfoDTO로 변환
            JobInfoDTO jobInfoDTO = JobInfoDTO.builder()
                    .jobName(defaultJob.getJobName())
                    .baseSalary(defaultJob.getBaseSalary())
                    .build();

            // 6️⃣ 생성된 계정 리스트에 추가
            createdStudents.add(new StudentResponseDTO(
                    student.getId(),
                    student.getUsername(),
                    student.getName(),  // 🔥 이름 필드 포함
                    student.getProfileImageUrl(),
                    student.getTeacher(),
                    student.getBankAccount(),
                    jobInfoDTO,
                    null  // 초기 생성 시 토큰은 null로 설정

            ));
        }

        return createdStudents;
    }

    // 단일 학생 계정 생성
    @Transactional
    public ApiResponse<StudentResponseDTO> createStudent(Long teacherId, SingleStudentCreateDTO studentCreateDTO) {

        // 1. 교사 확인
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("교사를 찾을 수 없습니다."));

        // 2. 중복 계정 확인
        String username = studentCreateDTO.getUsername();
        if (studentRepository.findByUsername(username).isPresent()) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "이미 존재하는 학생 계정입니다.: " + username,  null);
        }

        // 3. 이름 필수 입력 검사
        if (studentCreateDTO.getName() == null || studentCreateDTO.getName().isEmpty()) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "학생 이름은 필수 입력 항목입니다.", null);
        }

        // 4. 직업 조회 (ID로 조회)
        Job selectedJob;
        if (studentCreateDTO.getJobId() != null) {
            // 사용자가 선택한 직업이 있는 경우 해당 직업 조회
            selectedJob = jobRespository.findById(studentCreateDTO.getJobId())
                    .orElseThrow(() -> new IllegalArgumentException("선택한 직업이 존재하지 않습니다."));
        } else {
            // 직업 ID가 없으면 기본 직업 "시민"으로 설정
            selectedJob = jobRespository.findByJobName("시민")
                    .orElseGet(() -> {
                        Job newJob = Job.builder()
                                .jobName("시민")
                                .jobDescription("기본 직업입니다.")
                                .baseSalary(1000)
                                .maxPeople(30)
                                .build();
                        return jobRespository.save(newJob);
                    });
        }

        // 5. 은행 계좌 생성 및 저장
        BankAccountDTO bankAccountDTO = BankAccountDTO.builder()
                .accountNumber(generateAccountNumber())
                .balance(0)
                .build();

        BankAccount bankAccount = bankAccountMapper.toUpdatedEntity(bankAccountService.addBankAccount(bankAccountDTO));

        // 6. 학생 계정 생성 (이름과 선택 직업 연결)
        Student student = Student.builder()
                .username(username)
                .password(passwordEncoder.encode(studentCreateDTO.getPassword()))
                .teacher(teacher)
                .name(studentCreateDTO.getName())
                .bankAccount(bankAccount)
                .job(selectedJob)
                .build();


        // 7. DB 저장
        studentRepository.save(student);

        // 8. 직업 정보 DTO 생성
        JobInfoDTO jobInfoDTO = JobInfoDTO.builder()
                .jobName(selectedJob.getJobName())
                .baseSalary(selectedJob.getBaseSalary())
                .build();

        // 9. 생성된 학생 정보 반환
        StudentResponseDTO responseDTO = new StudentResponseDTO(
                student.getId(),
                student.getUsername(),
                student.getName(),
                student.getProfileImageUrl(),
                teacher,
                student.getBankAccount(),
                jobInfoDTO,
                null
        );
        return ApiResponse.success("학생 계정이 성공적으로 생성되었습니다.", responseDTO);
    }

    // 학생 로그인
    public StudentLoginResponseDTO loginStudent(StudentLoginRequestDTO studentLoginRequestDTO) {

        // 1. 학생 ID 확인
        Student student = studentRepository.findByUsername(studentLoginRequestDTO.getUsername())
                .orElseThrow(() -> new IllegalIdentifierException("아이디를 찾을 수 없습니다."));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(studentLoginRequestDTO.getPassword(), student.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. JMT 토큰 생성
        String token = jwtTokenProvider.createToken(student.getUsername());

        // 응답을 위한 DTO 생성
        return new StudentLoginResponseDTO(
                student.getId(),
                student.getUsername(),
                student.getName(),
                student.getProfileImageUrl(),
                student.getTeacher(),  // teacher 정보 추가
                student.getBankAccount(),  // bankAccount 정보 추가
                token
        );
    }

    // 직업 [해당 직업을 가진 전체 학생 목록 조회]
    public ApiResponse<List<StudentResponseDTO>> getStudentsByJobIdAndTeacher(Long teacherId, Long jobId) {
        List<Student> students = studentRepository.findByTeacherIdAndJobId(teacherId, jobId);

        if (students.isEmpty()) {
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "해당 직업을 가진 학생이 없습니다.", null);
        }

        List<StudentResponseDTO> responseDTOs = students.stream()
                .map(student -> new StudentResponseDTO(
                        student.getId(),
                        student.getUsername(),
                        student.getName(),
                        student.getProfileImageUrl(),
                        student.getTeacher(),
                        student.getBankAccount(),
                        null,
                        jwtTokenProvider.createToken(student.getUsername()) // ✅ JWT 토큰 포함
                ))
                .collect(Collectors.toList());

        return ApiResponse.success("직업을 가진 학생 목록 조회 성공", responseDTOs);
    }

    // ✅ 교사 ID로 우리반 학생 전체 조회
    public ApiResponse<List<StudentResponseDTO>> getMyClassStudents(Long teacherId) {
        List<Student> students = studentRepository.findAllByTeacherId(teacherId);

        if (students.isEmpty()) {
            return ApiResponse.error(404, "우리반 학생이 없습니다.", null);
        }

        List<StudentResponseDTO> responseDTOs = students.stream()
                .map(student -> {
                    // ✅ JWT 토큰 생성 (학생의 username 기반)
                    String token = jwtTokenProvider.createToken(student.getUsername());

                    // Job 정보 변환 (null 체크)
                    JobInfoDTO jobInfo = null;
                    if (student.getJob() != null) {
                        jobInfo = JobInfoDTO.builder()
                                .jobName(student.getJob().getJobName())
                                .baseSalary(student.getJob().getBaseSalary())
                                .build();
                    }

                    // 학생 정보 DTO로 변환
                    return new StudentResponseDTO(
                            student.getId(),
                            student.getUsername(),
                            student.getName(),
                            student.getProfileImageUrl(),
                            student.getTeacher(),
                            student.getBankAccount(),
                            jobInfo,
                            token
                    );
                })
                .collect(Collectors.toList());

        return ApiResponse.success("우리반 학생 목록 조회 성공", responseDTOs);
    }

    // ✅ 교사 ID와 학생 ID로 우리반 학생 상세 조회
    public ApiResponse<StudentResponseDTO> getStudentById(Long teacherId, Long studentId) {

        // 1️⃣ 학생 조회 (해당 교사의 반에 속한 학생인지 확인)
        Optional<Student> optionalStudent = studentRepository.findByIdAndTeacherId(studentId, teacherId);

        if (optionalStudent.isEmpty()) {
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "해당 학생을 찾을 수 없습니다.", null);
        }

        Student student = optionalStudent.get();

        // 2️⃣ JWT 토큰 생성 (학생의 username 기반)
        String token = jwtTokenProvider.createToken(student.getUsername()); // ✅ JWT 생성

        JobInfoDTO jobInfo = null;
        if (student.getJob() != null) {
            jobInfo = JobInfoDTO.builder()
                    .jobName(student.getJob().getJobName())
                    .baseSalary(student.getJob().getBaseSalary())
                    .build();
        }

        // 3️⃣ 학생 정보를 DTO로 변환하여 반환 (Base64 인코딩 포함)
        StudentResponseDTO responseDTO = new StudentResponseDTO(
                student.getId(),
                student.getUsername(),
                student.getName(),
                student.getProfileImageUrl(),
                student.getTeacher(),
                student.getBankAccount(),
                jobInfo,
                token
        );

        return ApiResponse.success("학생 정보 조회 성공", responseDTO);
    }

    // ✅ 교사 이메일로 ID 조회
    public Long getTeacherIdByEmail(String email) {
        return teacherRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 교사를 찾을 수 없습니다."))
                .getId();
    }

    public ApiResponse<List<StudentResponseDTO>> getAllStudents() {
        List<Student> students = studentRepository.findAll();

        if (students.isEmpty()) {
            return ApiResponse.error(404, "등록된 학생이 없습니다.", null);
        }

        List<StudentResponseDTO> responseDTOs = students.stream()
                .map(student -> {
                    JobInfoDTO jobInfo = null;
                    if (student.getJob() != null) {
                        jobInfo = JobInfoDTO.builder()
                                .jobName(student.getJob().getJobName())
                                .baseSalary(student.getJob().getBaseSalary())
                                .build();
                    }

                    // StudentResponseDTO 반환
                    return new StudentResponseDTO(
                            student.getId(),
                            student.getUsername(),
                            student.getName(),
                            student.getProfileImageUrl(),
                            student.getTeacher(),
                            student.getBankAccount(),
                            jobInfo,
                            jwtTokenProvider.createToken(student.getUsername())
                    );
                })
                .collect(Collectors.toList());

        return ApiResponse.success("학생 목록 조회 성공", responseDTOs);
    }

    public Long getNationIdById(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 학생이 존재하지 않습니다."));

        return nationService.findNationByTeacherId(student.getTeacher().getId())
                .orElseThrow(() -> new NotFoundException("등록된 국가가 없습니다."))
                .getId();
    }

    public Long findBankAccountIdById(Long studentId) {
        return studentRepository.findBankAccountIdById(studentId);
    }

    // ✅ 교사 ID로 우리반 학생 전체 조회
    public List<StudentResponseDTO> findAllByTeacherId(Long teacherId) {
        List<Student> students = studentRepository.findAllByTeacherId(teacherId);

        if (students.isEmpty()) {
            throw new IllegalArgumentException("우리반 학생이 없습니다.");
        }

        List<StudentResponseDTO> responseDTOs = students.stream()
                .map(student -> {
                    // ✅ JWT 토큰 생성 (학생의 username 기반)
                    String token = jwtTokenProvider.createToken(student.getUsername());

                    JobInfoDTO jobInfo = null;
                    if (student.getJob() != null) {
                        jobInfo = JobInfoDTO.builder()
                                .jobName(student.getJob().getJobName())
                                .baseSalary(student.getJob().getBaseSalary())
                                .build();
                    }

                    // 학생 정보 DTO로 변환
                    return new StudentResponseDTO(
                            student.getId(),
                            student.getUsername(),
                            student.getName(),
                            student.getProfileImageUrl(),
                            student.getTeacher(),
                            student.getBankAccount(),
                            jobInfo,
                            token
                    );
                })
                .collect(Collectors.toList());

        return responseDTOs;
    }

    public Long findTeacherIdByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 학생이 존재하지 않습니다."));
        return student.getTeacher().getId();
    }

    public void getAllSavingsAchievementRates(Long studentId, Long teacherId) {
        List<Student> students = studentRepository.findAllByTeacherId(teacherId);
        for (Student student : students) {
            SavingsAchievementDTO cacheSavingsAchievementDTO = redisGoalService.getGoalAchievement(teacherId, student.getId());
            if (cacheSavingsAchievementDTO.getSavingsAchievementRate() == null) {
                calculateSavingsAchievementRate();
            }
        }
    }

    /**
     * 특정 학생의 저축 목표 달성률을 조회
     */
    public SavingsAchievementDTO getSavingsAchievementRateByStudentId(Long studentId) {
        // 학생 정보 조회
        Long teacherId = findTeacherIdByStudentId(studentId);

        // Redis에서 목표 달성률 조회
        SavingsAchievementDTO cacheSavingsAchievementDTO = redisGoalService.getGoalAchievement(teacherId, studentId);
        if (cacheSavingsAchievementDTO.getSavingsAchievementRate() != null) {
            return cacheSavingsAchievementDTO;
        } else { // Redis에 없으면 DB에서 계산 후 Redis에 저장
            return calculateSavingsAchievementRate();
        }
    }

    /**
     * 특정 학생의 저축 목표 달성률을 계산
     */
    public SavingsAchievementDTO calculateSavingsAchievementRate() {
        // 학생 정보 조회
        Long studentId = getCurrentStudentId();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("해당 ID의 학생이 존재하지 않습니다."));

        // 학생의 국가 정보 조회
        Long teacherId = findTeacherIdByStudentId(studentId);
        NationDTO nationDTO = nationService.findNationByTeacherId(teacherId)
                .orElseThrow(() -> new NotFoundException("등록된 국가가 없습니다."));

        SavingsAchievementDTO savingsAchievementDTO = SavingsAchievementDTO.builder()
                .studentId(studentId)
                .build();

        int savingsGoalAmount = nationDTO.getSavingsGoalAmount(); // 국가에서 설정한 목표 저축 금액
        double achievementRate = 0.0; // 목표 저축 금액이 0이면 달성률도 0

        if (savingsGoalAmount != 0) {
            int bankBalance = student.getBankAccount().getBalance(); // 학생의 은행 잔고
            int investmentValue = investmentService.getCurrentInvestmentAmount(studentId); // 현재 투자 평가액

            // 내 총 자산 계산
            int totalAssets = bankBalance + investmentValue;

            // 목표 달성률 계산
            achievementRate = ((double) totalAssets / savingsGoalAmount) * 100;
        }

        savingsAchievementDTO.setSavingsAchievementRate(achievementRate);

        // Redis에 저장
        int rank = redisGoalService.saveOrUpdateGoalAchievement(student.getTeacher().getId(), savingsAchievementDTO);
        savingsAchievementDTO.setRank(rank);

        return savingsAchievementDTO;
    }

    public String findNameById(Long teacherId) {
        return studentRepository.findById(teacherId)
                .map(Student::getName)
                .orElseThrow(() -> new NotFoundException("등록된 학생이 없습니다."));
    }

    public Student findById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생이 존재하지 않습니다."));
    }

    public Long findJobIdByStudentId(Long studentId) {
        return studentRepository.findJobIdById(studentId);
    }

    public List<StudentManagementDTO> getStudentManagementListByTeacherId(Long teacherId) {
        return studentRepository.getStudentManagementListByTeacherId(teacherId);
    }

}
