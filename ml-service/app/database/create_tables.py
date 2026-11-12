from app.database.connection import Base, engine
from app.db_models.recommendation import Recommendation

Base.metadata.create_all(bind=engine)

print("Database tables created successfully!")
