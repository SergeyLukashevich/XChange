package org.knowm.xchange.huobi;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.huobi.dto.account.HuobiBalanceRecord;
import org.knowm.xchange.huobi.dto.account.HuobiBalanceSum;
import org.knowm.xchange.huobi.dto.marketdata.HuobiAsset;
import org.knowm.xchange.huobi.dto.marketdata.HuobiAssetPair;
import org.knowm.xchange.huobi.dto.marketdata.HuobiTicker;
import org.knowm.xchange.huobi.dto.trade.HuobiOpenOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HuobiAdapters {

    public static Ticker adaptTicker(HuobiTicker huobiTicker, CurrencyPair currencyPair) {
        Ticker.Builder builder = new Ticker.Builder();
        builder.open(huobiTicker.getOpen());
        builder.ask(huobiTicker.getAsk().getPrice());
        builder.bid(huobiTicker.getBid().getPrice());
        builder.last(huobiTicker.getClose());
        builder.high(huobiTicker.getHigh());
        builder.low(huobiTicker.getLow());
        builder.volume(huobiTicker.getVol());
        builder.timestamp(huobiTicker.getTs());
        builder.currencyPair(currencyPair);
        return builder.build();
    }

    public static ExchangeMetaData adaptToExchangeMetaData(HuobiAssetPair[] assetPairs, HuobiAsset[] assets) {
        HuobiUtils.setHuobiAssets(assets);
        HuobiUtils.setHuobiAssetPairs(assetPairs);

        Map<CurrencyPair, CurrencyPairMetaData> pairs = new HashMap<>();
        for (HuobiAssetPair pair : assetPairs) {
            pairs.put(adaptCurrencyPair(pair.getKey()), adaptPair(pair));
        }

        Map<Currency, CurrencyMetaData> currencies = new HashMap<>();
        for (HuobiAsset asset : assets) {
            Currency currency = adaptCurrency(asset.getAsset());
            currencies.put(currency, new CurrencyMetaData(0, null));
        }

        return new ExchangeMetaData(pairs, currencies, null, null, false);
    }

    public static CurrencyPair adaptCurrencyPair(String currencyPair) {
        return HuobiUtils.translateHuobiCurrencyPair(currencyPair);
    }

    private static CurrencyPairMetaData adaptPair(HuobiAssetPair pair) {
        return new CurrencyPairMetaData(null, null, null,
                new Integer(pair.getPricePrecision()));
    }

    public static Currency adaptCurrency(String currency) {
        return HuobiUtils.translateHuobiCurrencyCode(currency);
    }

    public static Wallet adaptWallet(Map<String, HuobiBalanceSum> huobiWallet) {
        List<Balance> balances = new ArrayList<>(huobiWallet.size());
        for (Map.Entry<String, HuobiBalanceSum> record : huobiWallet.entrySet()) {
            Currency currency = adaptCurrency(record.getKey());
            Balance balance = new Balance(currency, record.getValue().getTotal(), record.getValue().getAvailable(),
                    record.getValue().getFrozen());
            balances.add(balance);
        }
        return new Wallet(balances);
    }

    public static Map<String, HuobiBalanceSum> adaptBalance(HuobiBalanceRecord[] huobiBalance) {
        Map<String, HuobiBalanceSum> map = new HashMap<>();
        for (HuobiBalanceRecord record : huobiBalance) {
            HuobiBalanceSum sum = map.get(record.getCurrency());
            if (sum == null) {
                sum = new HuobiBalanceSum();
                map.put(record.getCurrency(), sum);
            }
            if (record.getType().equals("trade")) {
                sum.setAvailable(record.getBalance());
            } else if (record.getType().equals("frozen")) {
                sum.setFrozen(record.getBalance());
            }
        }
        return map;
    }

    public static OpenOrders adaptOpenOrders(HuobiOpenOrder[] openOrders) {
        List<LimitOrder> limitOrders = new ArrayList<>();
        for(HuobiOpenOrder openOrder : openOrders) {
            if (openOrder.isLimit()) {
                limitOrders.add((LimitOrder) adaptOrder(openOrder));
            }
        }
        return new OpenOrders(limitOrders);
    }

    public static Order adaptOrder(HuobiOpenOrder openOrder) {
        Order order = null;
        OrderType orderType = adaptOrderType(openOrder.getType());
        CurrencyPair currencyPair = adaptCurrencyPair(openOrder.getSymbol());
        if (openOrder.isMarket()) {
            // TODO check if all fields are right
            order = new MarketOrder(orderType, openOrder.getAmount(), currencyPair);
        }
        if (openOrder.isLimit()) {
            // TODO LimitOrder.originalAmount == openOrder.getAmount() ?
            // TODO LimitOrder.timestamp == openOrder.getCreatedAt() ?
            // TODO Check if all fields are right
            order = new LimitOrder(orderType, openOrder.getAmount(), currencyPair, String.valueOf(openOrder.getId()),
                    openOrder.getCreatedAt(), openOrder.getPrice());
        }
        return order;
    }

    public static OrderType adaptOrderType(String orderType) {
        return orderType.substring(1, 3).equals("buy") ? OrderType.BID : OrderType.ASK;
    }

}