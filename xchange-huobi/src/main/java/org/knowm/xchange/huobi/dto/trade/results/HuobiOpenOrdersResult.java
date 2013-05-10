package org.knowm.xchange.huobi.dto.trade.results;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.knowm.xchange.huobi.dto.HuobiResult;
import org.knowm.xchange.huobi.dto.trade.HuobiOpenOrder;

public class HuobiOpenOrdersResult extends HuobiResult<HuobiOpenOrder[]> {

    public HuobiOpenOrdersResult(@JsonProperty("status") String status,
                                 @JsonProperty("data") HuobiOpenOrder[] result,
                                 @JsonProperty("err-code") String errCode, @JsonProperty("err-msg") String errMsg) {
        super(status, errCode, errMsg, result);
    }

}
