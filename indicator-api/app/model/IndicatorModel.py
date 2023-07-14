from pydantic import BaseModel


class EmvFilterParams(BaseModel):
    start_mark_value: int
    end_mark_value: int
    start_value: float = -0.1
    end_value: float = 0.2

class MarketValueFilterParams(BaseModel):
    start_mark_value: int
    end_mark_value: int