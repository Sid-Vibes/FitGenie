from contextlib import asynccontextmanager

from fastapi import FastAPI
from app.api.recommendation_routes import router as recommendation_router
from app.core.model_loder import load_models

@asynccontextmanager
async def lifespan(app: FastAPI):
    print("Loading FitGenie ML models...")

    app.state.models = load_models()

    print("All ML models are ready!")

    yield

    print("FitGenie ML service shutting down...")


app = FastAPI(
    title="FitGenie ML Service",
    description="Personalized fitness recommendation API",
    version="2.0.0",
    lifespan=lifespan
)
#register the recommendation router
app.include_router(recommendation_router)

@app.get("/")
def home():
    return {
        "message": "FitGenie ML Service is running!",
        "status": "healthy"
    }