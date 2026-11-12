import os
import secrets

from fastapi import Header, HTTPException, status


def verify_internal_api_key(
    x_internal_api_key: str = Header(...)
):
    expected_key = os.getenv("ML_SERVICE_API_KEY")

    if not expected_key:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="ML service API key is not configured"
        )

    if not secrets.compare_digest(
        x_internal_api_key,
        expected_key
    ):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid service API key"
        )

    return True