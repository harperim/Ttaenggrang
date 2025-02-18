import joblib
import os

# 모델 파일 로드
model_dir = "../model"
scaler = joblib.load(f"{model_dir}/scaler.pkl")
model = joblib.load(f"{model_dir}/model.pkl")
label_encoder = joblib.load(f"{model_dir}/label_encoder.pkl")

def predict_cluster(input_data):
    # 데이터 변환
    scaled_data = scaler.transform([input_data])
    predicted_cluster_num = model.predict(scaled_data)[0]
    predicted_cluster = label_encoder.inverse_transform([predicted_cluster_num])[0]
    return predicted_cluster