from pydantic import BaseModel, Field
from typing import Optional
from uuid import UUID
from datetime import datetime


class RecommendationRequest(BaseModel):

    user_id: int

    age: int
    bmi: float

    goal_progress_percentage: float
    days_remaining: int

    workouts_last_30_days: int
    avg_duration_minutes: float
    avg_distance_km: float
    avg_calories_burned: float

    total_duration_minutes: float
    total_distance_km: float
    days_since_last_activity: int

    fitness_goal: str
    activity_level: str
    goal_type: str
    goal_activity_type: str
    previous_activity_type: str


class RecommendationResponse(BaseModel):

    id: UUID
    user_id: int

    activity_type: str
    intensity: str

    duration_minutes: int
    distance_km: Optional[float]

    explanation: Optional[str]
    status: str

    model_version: Optional[str]
    created_at: datetime

    model_config = {
        "from_attributes": True
    }


from typing import Literal


class RecommendationFeedbackRequest(BaseModel):
    user_id: int
    status: Literal["ACCEPTED", "COMPLETED", "REJECTED"]

