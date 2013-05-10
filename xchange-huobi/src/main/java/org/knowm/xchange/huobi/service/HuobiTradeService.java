package org.knowm.xchange.huobi.service;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.*;
import org.knowm.xchange.huobi.HuobiAdapters;
import org.knowm.xchange.huobi.dto.trade.HuobiOpenOrder;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.CancelOrderByIdParams;
import org.knowm.xchange.service.trade.params.CancelOrderParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParams;

import java.io.IOException;
import java.util.Collection;

public class HuobiTradeService extends HuobiTradeServiceRaw implements TradeService{

    public HuobiTradeService(Exchange exchange) {
        super(exchange);
    }

    @Override
    public UserTrades getTradeHistory(TradeHistoryParams tradeHistoryParams) throws IOException {
        return null;
    }

    @Override
    public Collection<Order> getOrder(String... strings) throws IOException {
        return null;
    }

    @Override
    public OpenOrdersParams createOpenOrdersParams() {
        return null;
    }

    @Override
    public TradeHistoryParams createTradeHistoryParams() {
        return null;
    }

    @Override
    public boolean cancelOrder(String orderId) throws IOException {
        // TODO if (orderId == result) then check for equity
        return cancelHuobiOrder(orderId).length() > 0;
    }

    @Override
    public boolean cancelOrder(CancelOrderParams cancelOrderParams) throws IOException {
        if (cancelOrderParams instanceof CancelOrderByIdParams) {
            return cancelOrder(((CancelOrderByIdParams)cancelOrderParams).getOrderId());
        }
        return false;
    }

    @Override
    public String placeMarketOrder(MarketOrder marketOrder) throws IOException {
        return placeHuobiMarketOrder(marketOrder);
    }

    @Override
    public OpenOrders getOpenOrders() throws IOException {
        return getOpenOrders(createOpenOrdersParams());
    }

    @Override
    public OpenOrders getOpenOrders(OpenOrdersParams openOrdersParams) throws IOException {
        HuobiOpenOrder[] openOrders = getHuobiOpenOrders();
        return HuobiAdapters.adaptOpenOrders(openOrders);
    }

    @Override
    public String placeLimitOrder(LimitOrder limitOrder) throws IOException {
        return placeHuobiLimitOrder(limitOrder);
    }

    @Override
    public String placeStopOrder(StopOrder stopOrder) throws IOException {
        return null;
    }

}
