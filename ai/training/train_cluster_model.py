import pickle
import numpy as np
import pandas as pd
import joblib
from sklearn.cluster import KMeans, DBSCAN
from sklearn.mixture import GaussianMixture
from sklearn.preprocessing import MinMaxScaler, StandardScaler, RobustScaler, LabelEncoder
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from imblearn.over_sampling import SMOTE
from sklearn.metrics import silhouette_score, accuracy_score, classification_report, precision_score, recall_score, f1_score
import os
os.environ["LOKY_MAX_CPU_COUNT"] = "4"  # 원하는 코어 개수로 설정


# 📌 데이터 생성 (10만 개 샘플)
np.random.seed(42)
num_students = 100000

# 데이터 생성
total_income = np.random.randint(1000000, 5000000, num_students)  # 총소득 (100만 ~ 500만)

# **비율 기반이 아니라 개별적으로 랜덤값을 생성하는 기존 방식 유지**
savings = np.random.randint(10000, 3000000, num_students)  # 저축액 (1만 ~ 300만)
investment = np.random.randint(10000, 3000000, num_students)  # 투자액 (1만 ~ 300만)
spending = np.random.randint(10000, 3000000, num_students)  # 소비액 (1만 ~ 300만)
salary = np.random.randint(800000, 3000000, num_students)  # 급여 (80만 ~ 300만)
incentive = np.random.randint(0, 500000, num_students)  # 인센티브 (0 ~ 50만)

# **총합이 total_income을 초과하지 않도록 조정**
total_expense = savings + investment + spending
excess_mask = total_expense > total_income

# 초과하는 경우, 비율을 재조정하여 total_income을 넘지 않게 만듦
savings[excess_mask] = (savings[excess_mask] / total_expense[excess_mask]) * total_income[excess_mask]
investment[excess_mask] = (investment[excess_mask] / total_expense[excess_mask]) * total_income[excess_mask]
spending[excess_mask] = (spending[excess_mask] / total_expense[excess_mask]) * total_income[excess_mask]

# 데이터프레임 생성
data = pd.DataFrame({
    'total_income': total_income,
    'salary': salary,
    'incentive': incentive,
    'savings': savings.astype(int),
    'investment': investment.astype(int),
    'spending': spending.astype(int)
})

# 📌 6️⃣ 주요 비율 계산 (스케일링 없이 사용)
data['savings_ratio'] = data['savings'] / data['total_income']
data['investment_ratio'] = data['investment'] / data['total_income']
data['spending_ratio'] = data['spending'] / data['total_income']

# 정규화 적용
features = ['savings_ratio', 'investment_ratio', 'spending_ratio']
scaler = MinMaxScaler()
# scaler = StandardScaler()
# scaler = RobustScaler()
X_scaled = scaler.fit_transform(data[features])

# 📌 3️⃣ 클러스터링 모델 학습
kmeans = KMeans(n_clusters=3, random_state=42)
data['kmeans_cluster'] = kmeans.fit_predict(X_scaled)

dbscan = DBSCAN(eps=0.1, min_samples=5)
data['dbscan_cluster'] = dbscan.fit_predict(X_scaled)

gmm = GaussianMixture(n_components=4, random_state=42, covariance_type='diag')
data['gmm_cluster'] = gmm.fit_predict(X_scaled)

# 📌 4️⃣ 모델 성능 평가 (Silhouette Score 기준)
kmeans_silhouette = silhouette_score(X_scaled, data['kmeans_cluster'])
dbscan_silhouette = silhouette_score(X_scaled, data['dbscan_cluster']) if len(set(data['dbscan_cluster'])) > 1 else -1
gmm_silhouette = silhouette_score(X_scaled, data['gmm_cluster'])
print(kmeans_silhouette, dbscan_silhouette, gmm_silhouette)

# 📌 최적의 모델 선택 (best_model)
best_model, best_labels = max([
    (kmeans, data['kmeans_cluster'], kmeans_silhouette, "K-Means"),
    (dbscan, data['dbscan_cluster'], dbscan_silhouette, "DBSCAN"),
    (gmm, data['gmm_cluster'], gmm_silhouette, "GMM")
], key=lambda x: x[2])[:2]  # 실루엣 점수가 가장 높은 모델 선택

print(f"🎯 최적의 모델은: {best_model.__class__.__name__}")

data['cluster'] = best_labels  # 최적 모델의 클러스터 컬럼을 'cluster'로 저장

# 📌 5️⃣ 클러스터 매핑 (소비형, 저축형, 투자형)
cluster_means = data.groupby(best_labels)[features].mean()
cluster_mapping = {
    cluster_means['spending_ratio'].idxmax(): '소비형',
    cluster_means['savings_ratio'].idxmax(): '저축형',
    cluster_means['investment_ratio'].idxmax(): '투자형'
}
print("📌 클러스터 매핑:", cluster_mapping)

# 📌 6️⃣ 모델 및 클러스터 매핑 저장 (pkl 파일)
model_data = {
    "model": best_model,
    "cluster_mapping": cluster_mapping
}

# 📌 클러스터 정보 출력
print("✅ 최적 모델:", best_model.__class__.__name__)
print("📌 실루엣 점수:", max(kmeans_silhouette, dbscan_silhouette, gmm_silhouette))
print("📌 최종 클러스터 개수:", len(set(best_labels)))
print("📌 클러스터 분포:")
print(data['cluster'].value_counts())
print("📌 클러스터별 평균값:")
print(cluster_means)

# ----------------------------------------------

# 📌 지도 학습을 위한 데이터 준비
X = data[['savings_ratio', 'spending_ratio', 'investment_ratio']]
y = best_labels

# 📌 라벨 인코딩
label_encoder = LabelEncoder()
y_encoded = label_encoder.fit_transform(y)

# 📌 데이터 오버샘플링 (SMOTE)
smote = SMOTE(sampling_strategy='auto', random_state=42)
X_resampled, y_resampled = smote.fit_resample(X, y_encoded)

# 📌 지도 학습 - 랜덤 포레스트 분류기 학습
X_train, X_test, y_train, y_test = train_test_split(X_resampled, y_resampled, test_size=0.2, random_state=42)
rf_model = RandomForestClassifier(n_estimators=150, max_depth=7, random_state=42)
rf_model.fit(X_train, y_train)

# 📌 지도 학습 성능 평가
y_pred = rf_model.predict(X_test)
accuracy = accuracy_score(y_test, y_pred)
precision = precision_score(y_test, y_pred, average='weighted')
recall = recall_score(y_test, y_pred, average='weighted')
f1 = f1_score(y_test, y_pred, average='weighted')
print("✅ 지도 학습 정확도:", accuracy)
print("📌 정밀도(Precision):", precision)
print("📌 재현율(Recall):", recall)
print("📌 F1 점수:", f1)
print("📌 분류 리포트:")
print(classification_report(y_test, y_pred))


# 📌 모델 저장 경로 설정
model_dir = "./model"
os.makedirs(model_dir, exist_ok=True)

# 📌 모델 저장
joblib.dump(best_model, f"{model_dir}/cluster_model.pkl")  # 비지도 학습 모델
joblib.dump(scaler, f"{model_dir}/scaler.pkl")  # 정규화 도구
joblib.dump(cluster_mapping, f"{model_dir}/cluster_mapping.pkl")  # 클러스터 매핑
joblib.dump(rf_model, f"{model_dir}/rf_model.pkl")  # 지도 학습 모델
joblib.dump(label_encoder, f"{model_dir}/label_encoder.pkl")  # 라벨 인코더

print("✅ 모델 저장 완료: cluster_model.pkl, scaler.pkl, cluster_mapping.pkl, rf_model.pkl, label_encoder.pkl")
