import joblib
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parents[2]

MODEL_DIR = BASE_DIR / "models"


def load_models():

    models = {}

    models["activity"] = joblib.load(
        MODEL_DIR / "activity_classifier_v2.joblib"
    )

    models["intensity"] = joblib.load(
        MODEL_DIR / "intensity_classifier_v2.joblib"
    )

    models["duration"] = joblib.load(
        MODEL_DIR / "duration_regressor_v2.joblib"
    )

    models["distance"] = joblib.load(
        MODEL_DIR / "distance_regressor_v2.joblib"
    )

    print("All ML models loaded successfully!")

    return models