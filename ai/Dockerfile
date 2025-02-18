# Python 3.9 이미지 사용
FROM python:3.9

# 작업 디렉토리 설정
WORKDIR /app

# pip 최신 버전으로 업데이트
RUN pip install --upgrade pip

# 의존성 설치
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 모델 파일 추가 (FastAPI 컨테이너에 모델 포함)
COPY model/scaler.pkl model/model.pkl model/label_encoder.pkl /app/model/

# 애플리케이션 코드 복사
# 컨테이너 내부 /app 경로에 FastAPI 코드가 복사
COPY ./app /app

# FastAPI 실행
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
