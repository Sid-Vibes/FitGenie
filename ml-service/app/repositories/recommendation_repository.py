from uuid import UUID

from sqlalchemy import select
from sqlalchemy.orm import Session

from app.db_models.recommendation import (
    Recommendation,
    RecommendationStatus
)


class RecommendationRepository:

    def __init__(self, db: Session):
        self.db = db

    # Save a recommendation
    def save(self, recommendation: Recommendation):

        try:
            self.db.add(recommendation)
            self.db.commit()
            self.db.refresh(recommendation)

            return recommendation

        except Exception:
            self.db.rollback()
            raise

    # Find recommendation by UUID
    def find_by_id(self, recommendation_id: UUID):

        return self.db.get(
            Recommendation,
            recommendation_id
        )

    # Get all recommendations for a particular user
    def find_by_user_id(self, user_id: int):

        statement = (
            select(Recommendation)
            .where(Recommendation.user_id == user_id)
            .order_by(Recommendation.created_at.desc())
        )

        return self.db.scalars(statement).all()

    # Update recommendation status
    def update_status(
        self,
        recommendation_id: UUID,
        status: RecommendationStatus
    ):

        recommendation = self.find_by_id(recommendation_id)

        if recommendation is None:
            return None

        try:
            recommendation.status = status

            self.db.commit()
            self.db.refresh(recommendation)

            return recommendation

        except Exception:
            self.db.rollback()
            raise

    # Find recommendation by ID and user ID
    def find_by_id_and_user_id(self, recommendation_id, user_id):
        statement = (
          select(Recommendation)
         .where(
            Recommendation.id == recommendation_id,
            Recommendation.user_id == user_id
            )
        )
        return self.db.scalars(statement).first()    