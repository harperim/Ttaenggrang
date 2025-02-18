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
    allow_origins=["*"],  # 모든 도메인에서 요청 허용
    allow_credentials=True,
    allow_methods=["*"],  # 모든 HTTP 메서드 허용 (POST, GET 등)
    allow_headers=["*"],  # 모든 헤더 허용
)

# 🔹 모델 파일 로드 경로 (절대 경로 수정)
MODEL_DIR = os.path.join(os.path.dirname(__file__), "model/")

# 🔹 모델 로드 (예외 처리 추가)
try:
    print(f"📌 모델 로드 경로: {MODEL_DIR}")
    scaler = joblib.load(os.path.join(MODEL_DIR, "scaler.pkl"))
    model = joblib.load(os.path.join(MODEL_DIR, "model.pkl"))
    label_encoder = joblib.load(os.path.join(MODEL_DIR, "label_encoder.pkl"))
    print("✅ 모델 로드 성공!")
except Exception as e:
    print(f"🚨 모델 파일을 로드하는 중 오류 발생: {e}")
    raise RuntimeError(f"🚨 모델 파일을 로드하는 중 오류 발생: {e}")

# 🔹 입력 데이터 구조 정의
class StudentData(BaseModel):
    total_income: int
    total_expense: int
    total_investment: int
    investment_return: int
    tax_paid: int
    fine_paid: int
    incentive: int

# 🔹 학생 금융 데이터 예측 API
@app.post("/predict-cluster")
def predict_cluster(data: StudentData):
    try:
        print(f"📌 요청 데이터: {data.dict()}")

        # 데이터 전처리
        new_data = np.array([
            (data.total_income - data.total_expense - data.total_investment) / data.total_income,  # 저축률
            data.total_expense / data.total_income,  # 소비 비율
            data.total_investment / data.total_income,  # 투자 비율
            data.investment_return / (data.total_investment + 1),  # 투자 수익률
            data.total_income - data.total_expense,  # 순자산 변화
            data.total_investment / data.total_income,  # 수입 대비 투자 비율
            data.tax_paid / data.total_income,  # 세금 비율
            data.fine_paid / data.total_income,  # 벌금 비율
            data.investment_return / (data.total_investment + 1),  # 투자 손익 비율
            (data.total_income - data.total_expense) / (data.total_expense + 1)  # 소비 대비 저축 비율
        ]).reshape(1, -1)

        print(f"✅ 전처리된 데이터: {new_data}")

        # 데이터 변환 및 예측
        new_data_scaled = scaler.transform(new_data)
        predicted_cluster_num = model.predict(new_data_scaled)[0]
        predicted_cluster = label_encoder.inverse_transform([predicted_cluster_num])[0]

        print(f"✅ 예측 결과: {predicted_cluster}")
        return {"predicted_cluster": predicted_cluster}

    except Exception as e:
        print(f"🚨 예측 중 오류 발생: {e}")
        raise HTTPException(status_code=500, detail=f"🚨 예측 중 오류 발생: {str(e)}")

# 🔹 기본 엔드포인트 (서버 상태 확인)
@app.get("/")
def read_root():
    return {"message": "FastAPI 금융 예측 서버가 정상적으로 실행 중입니다."}
