from fastapi import FastAPI
from app.api import IndicatorApi

app = FastAPI()
app.include_router(IndicatorApi.router)


@app.get("/")
def read_root():
    return "stock support system"
