from typing import List

from fastapi import APIRouter, Depends, Request
from sqlalchemy.orm import Session
from uuid import UUID
from fastapi import HTTPException

from app.schemas.recommendation_schema import (
    RecommendationFeedbackRequest
)
from app.db_models.recommendation import RecommendationStatus

from app.database.connection import get_db
from app.schemas.recommendation_schema import (
    RecommendationRequest,
    RecommendationResponse
)
from app.services.recommendation_service import (
    RecommendationService
)
from app.repositories.recommendation_repository import (
    RecommendationRepository
)
from app.api.dependencies import verify_internal_api_key


router = APIRouter(
    prefix="/api/ml/recommendations",
    tags=["Recommendations"]
)


@router.post(
    "",
    response_model=RecommendationResponse
)
def generate_recommendation(
    request_data: RecommendationRequest,
    request: Request,
    db: Session = Depends(get_db),
    authenticated: bool = Depends(verify_internal_api_key)
):

    models = request.app.state.models

    service = RecommendationService(
        db=db,
        models=models
    )

    user_features = request_data.model_dump(
        exclude={"user_id"}
    )

    recommendation = service.generate_recommendation(
        user_id=request_data.user_id,
        user_features=user_features
    )

    return recommendation

@router.get(
    "/user/{user_id}",
    response_model=List[RecommendationResponse]
)
def get_user_recommendations(
    user_id: int,
    db: Session = Depends(get_db),
    authenticated: bool = Depends(verify_internal_api_key)
):
    if user_id <= 0:
        from fastapi import HTTPException

        raise HTTPException(
            status_code=400,
            detail="Invalid user ID"
        )

    repository = RecommendationRepository(db)

    recommendations = repository.find_by_user_id(user_id)

    return recommendations

@router.patch("/{recommendation_id}/feedback",
              response_model=RecommendationResponse)
def update_recommendation_feedback(
    recommendation_id: UUID,
    feedback: RecommendationFeedbackRequest,
    db: Session = Depends(get_db),
    authenticated: bool = Depends(verify_internal_api_key)
):

    repository = RecommendationRepository(db)

    recommendation = repository.find_by_id_and_user_id(
        recommendation_id,
        feedback.user_id
    )

    if recommendation is None:
        raise HTTPException(
            status_code=404,
            detail="Recommendation not found"
        )

    try:
        recommendation.status = RecommendationStatus(feedback.status)

        db.commit()
        db.refresh(recommendation)

        return recommendation

    except Exception:
        db.rollback()
        raise