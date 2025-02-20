from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import numpy as np
import joblib
import os

app = FastAPI()

# 🔹 CORS 설정 추가
app.add_middleware(
    CORSMiddleware,
    allow_origins=["https://i12d107.p.ssafy.io"],  # ✅ 특정 도메인만 허용
    allow_credentials=True,
    allow_methods=["GET", "POST", "OPTIONS"],  # ✅ 필요한 메서드만 허용
    allow_headers=["Content-Type", "Authorization"],  # ✅ 필요한 헤더만 허용
)

# 🔹 모델 파일 로드 경로 (절대 경로 수정)
MODEL_DIR = os.path.join(os.path.dirname(__file__), "../model/")

# 🔹 모델 로드 (예외 처리 추가)
try:
    print(f"📌 모델 로드 경로: {MODEL_DIR}")

    # 🔹 지도 학습 모델 사용
    rf_model = joblib.load(os.path.join(MODEL_DIR, "rf_model.pkl"))  # ✅ 지도 학습 모델 사용
    cluster_mapping = joblib.load(os.path.join(MODEL_DIR, "cluster_mapping.pkl"))
    scaler = joblib.load(os.path.join(MODEL_DIR, "scaler.pkl"))
    label_encoder = joblib.load(os.path.join(MODEL_DIR, "label_encoder.pkl"))  # ✅ 라벨 인코더 추가

    print("✅ 모델 로드 성공!")
except Exception as e:
    print(f"🚨 모델 파일을 로드하는 중 오류 발생: {e}")
    raise RuntimeError(f"🚨 모델 파일을 로드하는 중 오류 발생: {e}")

# 🔹 입력 데이터 구조 정의
class StudentData(BaseModel):
    student_id: int
    total_income: int
    total_expense: int
    total_savings: int
    total_investment: int

# 🔹 학생 금융 데이터 예측 API (지도 학습 적용)
@app.post("/predict-cluster")
def predict_cluster(data: StudentData):
    try:
        print(f"📌 요청 데이터 (학생 ID {data.student_id}): {data.dict()}")

        if data.total_income == 0:
            raise HTTPException(status_code=400, detail="총 수입(total_income)은 0보다 커야 합니다.")

        # 🔹 비율 계산
        savings_ratio = data.total_savings / data.total_income
        spending_ratio = data.total_expense / data.total_income
        investment_ratio = data.total_investment / data.total_income

        print(f"✅ 계산된 비율: 저축률={savings_ratio:.2f}, 소비 비율={spending_ratio:.2f}, 투자 비율={investment_ratio:.2f}")

        new_data = np.array([[savings_ratio, spending_ratio, investment_ratio]])
        new_data_scaled = scaler.transform(new_data)

        # 🔹 지도 학습 모델(Random Forest)로 예측
        predicted_cluster_num = rf_model.predict(new_data_scaled)[0]

        # 🔹 라벨 인코더를 사용해 클러스터 이름 변환
        predicted_cluster_label = cluster_mapping.get(predicted_cluster_num, "알 수 없음")

        print(f"✅ 예측 결과: {predicted_cluster_label}")

        return {
            "student_id": int(data.student_id),
            "savings_ratio": round(float(savings_ratio), 2),
            "spending_ratio": round(float(spending_ratio), 2),
            "investment_ratio": round(float(investment_ratio), 2),
            "predicted_cluster": predicted_cluster_label
        }
    except Exception as e:
        print(f"🚨 예측 중 오류 발생: {e}")
        raise HTTPException(status_code=500, detail=f"🚨 예측 중 오류 발생: {str(e)}")

# 🔹 기본 엔드포인트 (서버 상태 확인)
@app.get("/")
def read_root():
    return {"message": "FastAPI 금융 예측 서버가 정상적으로 실행 중입니다."}
