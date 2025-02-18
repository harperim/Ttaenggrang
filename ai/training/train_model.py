import os
import joblib
import numpy as np
import pandas as pd
from sklearn.preprocessing import MinMaxScaler, LabelEncoder
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from imblearn.over_sampling import SMOTE

# 환경 변수 설정 (경고 방지)
os.environ["LOKY_MAX_CPU_COUNT"] = "4"
os.environ["OMP_NUM_THREADS"] = "1"

# 데이터 생성 (가상의 학생 금융 데이터)
np.random.seed(42)
students = [f"학생{i+1}" for i in range(100)]
data = {
    "총 수입": np.random.randint(5000, 50000, len(students)),
    "총 지출": np.random.randint(2000, 30000, len(students)),
    "총 투자 비용": np.random.randint(0, 20000, len(students)),
    "총 투자 수익": np.random.randint(-5000, 10000, len(students)),
    "세금 납부액": np.random.randint(0, 5000, len(students)),
    "벌금 납부액": np.random.randint(0, 3000, len(students)),
    "인센티브": np.random.randint(0, 10000, len(students))
}
df = pd.DataFrame(data)

# Feature Engineering
df['저축률'] = (df['총 수입'] - df['총 지출'] - df['총 투자 비용']) / df['총 수입']
df['소비 비율'] = df['총 지출'] / df['총 수입']
df['투자 비율'] = df['총 투자 비용'] / df['총 수입']
df['투자 수익률'] = df['총 투자 수익'] / (df['총 투자 비용'] + 1)
df['순자산 변화'] = df['총 수입'] - df['총 지출']
df['수입 대비 투자 비율'] = df['총 투자 비용'] / df['총 수입']
df['세금 비율'] = df['세금 납부액'] / df['총 수입']
df['벌금 비율'] = df['벌금 납부액'] / df['총 수입']
df['투자 손익 비율'] = df['총 투자 수익'] / (df['총 투자 비용'] + 1)
df['소비 대비 저축 비율'] = (df['총 수입'] - df['총 지출']) / (df['총 지출'] + 1)

# 데이터 정규화
features = ['저축률', '소비 비율', '투자 비율', '투자 수익률', '순자산 변화', 
            '수입 대비 투자 비율', '세금 비율', '벌금 비율', '투자 손익 비율', '소비 대비 저축 비율']
scaler = MinMaxScaler()
X_scaled = scaler.fit_transform(df[features])

# 라벨 인코딩 및 학습 데이터 준비
from sklearn.mixture import GaussianMixture
gmm = GaussianMixture(n_components=3, random_state=42)
df["클러스터"] = gmm.fit_predict(X_scaled)
cluster_labels = {0: "저축형", 1: "투자형", 2: "소비형"}
df['클러스터 유형'] = df['클러스터'].map(cluster_labels)
label_encoder = LabelEncoder()
df["클러스터 유형"] = label_encoder.fit_transform(df["클러스터 유형"])

# 데이터 오버샘플링 (SMOTE 적용)
smote = SMOTE(sampling_strategy='auto', random_state=42)
X_resampled, y_resampled = smote.fit_resample(X_scaled, df["클러스터 유형"])

# 학습/테스트 데이터 분리
X_train, X_test, y_train, y_test = train_test_split(X_resampled, y_resampled, test_size=0.2, random_state=42)

# 모델 학습 (RandomForest 사용)
model = RandomForestClassifier(n_estimators=150, max_depth=7, random_state=42)
model.fit(X_train, y_train)

# 모델 저장 경로 설정
model_dir = "../model"  # `/backend/fastapi/model/` 폴더에 저장
os.makedirs(model_dir, exist_ok=True)

joblib.dump(scaler, f"{model_dir}/scaler.pkl")
joblib.dump(model, f"{model_dir}/model.pkl")
joblib.dump(label_encoder, f"{model_dir}/label_encoder.pkl")

print("✅ 모델 파일 저장 완료: scaler.pkl, model.pkl, label_encoder.pkl")
