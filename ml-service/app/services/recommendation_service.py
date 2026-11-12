import pandas as pd

from app.db_models.recommendation import (
    Recommendation,
    RecommendationStatus
)
from app.repositories.recommendation_repository import (
    RecommendationRepository
)
from app.core.model_loder import load_models


class RecommendationService:

    DISTANCE_ACTIVITIES = {
        "RUNNING",
        "WALKING",
        "CYCLING",
        "SWIMMING"
    }

    def __init__(self, db, models):
       self.models = models
       self.repository = RecommendationRepository(db)

    def predict(self, model_name, features):

        model = self.models[model_name]

        # Use the exact feature order expected by the model.
        expected_features = list(model.feature_names_in_)

        df = pd.DataFrame([features])

        df = df[expected_features]

        prediction = model.predict(df)[0]

        return prediction

    def generate_recommendation(self, user_id, user_features):

        # 1. Predict activity type
        activity = self.predict(
            "activity",
            user_features
        )

        activity = str(activity)

        # 2. Predict intensity
        intensity_features = {
            **user_features,
            "next_activity_type": activity
        }

        intensity = self.predict(
            "intensity",
            intensity_features
        )

        intensity = str(intensity)

        # 3. Predict duration
        duration_features = {
            **intensity_features,
            "next_intensity": intensity
        }

        duration = self.predict(
            "duration",
            duration_features
        )

        duration = max(1, round(float(duration)))

        # 4. Predict distance when applicable
        distance = None

        if activity in self.DISTANCE_ACTIVITIES:

            distance_features = {
                **duration_features,
                "next_duration_minutes": duration
            }

            distance = self.predict(
                "distance",
                distance_features
            )

            distance = max(0.0, round(float(distance), 2))

        # 5. Generate explanation
        explanation = (
            f"Based on your fitness profile and activity history, "
            f"we recommend {activity.lower()} at {intensity.lower()} "
            f"intensity for {duration} minutes."
        )

        if distance is not None:
            explanation += f" Target distance: {distance} km."

        # 6. Create recommendation record
        recommendation = Recommendation(
            user_id=user_id,
            activity_type=activity,
            intensity=intensity,
            duration_minutes=duration,
            distance_km=distance,
            explanation=explanation,
            status=RecommendationStatus.PENDING,
            model_version="v2",
            input_snapshot=user_features
        )

        # 7. Save in PostgreSQL
        saved_recommendation = self.repository.save(
            recommendation
        )

        return saved_recommendation