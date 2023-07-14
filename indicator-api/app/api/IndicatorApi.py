import json
import pandas as pd
from pandas import DataFrame
from fastapi import APIRouter
from pandarallel import pandarallel

from app.config.db import engine
from app.utils.MyTT import *
from app.model.IndicatorModel import *
from app.config.log import logger as log

router = APIRouter(prefix="/tech/analysis")

pandarallel.initialize(progress_bar=True)


def get_days_data(days) -> DataFrame:
    df_date = pd.read_sql(f"select * from trade_day order by date desc limit {days}", engine.connect())
    end_date = df_date.iloc[0]["date"]
    start_date = df_date.iloc[-1]["date"]

    df = pd.read_sql(f"select * from cn_stock_data where date >= '{start_date}' and date <='{end_date}' order by date asc", engine.connect())
    return df


def filter_market_value(start_mark_value: int, end_mark_value: int, symbol_tuple: tuple) -> list:
    df_symbol = pd.read_sql(f"select * from cn_stock_info where symbol in {symbol_tuple}", engine.connect())
    df_symbol = df_symbol[(df_symbol["market_value"] > start_mark_value * 10000 * 10000) & (df_symbol["market_value"] < end_mark_value * 10000 * 10000)]
    return list(df_symbol["symbol"].values)


def dropNa_symbol(data: DataFrame):
    data = data.reset_index(name="filter")
    symbol_tuple = tuple(data.dropna(subset=["filter"])["symbol"].values)
    return symbol_tuple


@router.get("/emv")
async def emv(symbol: str, limit: int):
    df = pd.read_sql(f"select * from cn_stock_data where symbol = '{symbol}' order by date desc limit {limit}", engine.connect())
    df.sort_values(by="date", ascending=True, inplace=True)
    df["EMV"], df["MAEMV"] = EMV(df.high, df.low, df.trade_vol)
    return json.loads(df.to_json(orient="records", force_ascii=False))


@router.post("/emv/filter")
def filter_emv(params: EmvFilterParams):
    def filter_symbol(group):
        # 计算每个组的均值
        group["EMV"], group["MAEMV"] = EMV(group.high, group.low, group.trade_vol)
        emv = group.iloc[-1]["EMV"]
        symbol = group.iloc[-1]["symbol"]
        log.info(f"{symbol} emv is {emv}")
        if params.start_value <= emv <= params.end_value:
            return symbol

    df = get_days_data(60)
    temp = df.groupby(by="symbol", sort="date").parallel_apply(filter_symbol)
    symbol_tuple = dropNa_symbol(temp)
    emv_list = filter_market_value(params.start_mark_value, params.end_mark_value, symbol_tuple)
    five_list = five_cross_ten(MarketValueFilterParams(start_mark_value=params.start_mark_value, end_mark_value=params.end_mark_value))
    # five_list = five_cross_ten(params)
    return set(emv_list) & set(five_list)


@router.post("/five/corss")
def five_cross_ten(params: MarketValueFilterParams):
    def filter_symbol(group):
        # 计算每个组的均值
        group["EMV5"] = EMA(group.close, 5)
        group["EMV10"] = EMA(group.close, 10)
        log.info(group.tail(3))
        if (group["EMV5"].iloc[-3:] > group["EMV10"].iloc[-3:]).all():
            return group.iloc[-1]["symbol"]

    df = get_days_data(120)
    temp = df.groupby(by="symbol", sort="date").parallel_apply(filter_symbol)
    return filter_market_value(params.start_mark_value, params.end_mark_value, dropNa_symbol(temp))
