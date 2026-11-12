import uuid
import enum

from sqlalchemy import (
    Column,
    String,
    BigInteger,
    Integer,
    Float,
    Text,
    DateTime,
    Enum
)
from sqlalchemy.dialects.postgresql import UUID, JSONB
from sqlalchemy.sql import func

from app.database.connection import Base


class RecommendationStatus(str, enum.Enum):
    PENDING = "PENDING"
    ACCEPTED = "ACCEPTED"
    COMPLETED = "COMPLETED"
    REJECTED = "REJECTED"


class Recommendation(Base):

    __tablename__ = "recommendations"

    # Primary key
    id = Column(
        UUID(as_uuid=True),
        primary_key=True,
        default=uuid.uuid4
    )

    # Spring Boot MySQL user ID (external reference)
    user_id = Column(BigInteger, nullable=False, index=True)

    # ML prediction details
    activity_type = Column(String(50), nullable=False)
    intensity = Column(String(20), nullable=False)

    duration_minutes = Column(Integer, nullable=False)
    distance_km = Column(Float, nullable=True)

    explanation = Column(Text, nullable=True)

    # Recommendation feedback
    status = Column(
        Enum(RecommendationStatus),
        nullable=False,
        default=RecommendationStatus.PENDING
    )

    # ML model information
    model_version = Column(String(50), nullable=True)

    # Store prediction input features
    input_snapshot = Column(JSONB, nullable=True)

    # Timestamps
    created_at = Column(
        DateTime(timezone=True),
        server_default=func.now()
    )

    updated_at = Column(
        DateTime(timezone=True),
        onupdate=func.now()
    )