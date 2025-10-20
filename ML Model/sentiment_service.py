from fastapi import FastAPI
from pydantic import BaseModel
from vaderSentiment.vaderSentiment import SentimentIntensityAnalyzer
from typing import Dict

app = FastAPI()
analyzer = SentimentIntensityAnalyzer()

class Req(BaseModel):
    text: str

@app.post("/analyze")
async def analyze(req: Req) -> Dict[str, float]:
    text = req.text or ""
    if text.strip() == "":
        return {"sentiment": 0.0}
    scores = analyzer.polarity_scores(text)
    # compound is between -1 and 1
    return {"sentiment": scores["compound"]}
