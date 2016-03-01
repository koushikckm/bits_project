package com.koushik.bits.api.service;

import com.koushik.bits.common.model.Input;
import com.koushik.bits.common.response.Response;

public interface AnalyticsService
{
    Response computeAnalytics(final Input input);
}
