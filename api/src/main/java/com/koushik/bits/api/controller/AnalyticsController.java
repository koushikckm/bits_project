package com.koushik.bits.api.controller;

import com.google.gson.Gson;
import com.koushik.bits.api.exception.ApplicationException;
import com.koushik.bits.api.service.AnalyticsService;
import com.koushik.bits.common.constants.Constants;
import com.koushik.bits.common.model.Input;
import com.koushik.bits.common.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;

@RestController
public class AnalyticsController
{
    private final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);
    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(final AnalyticsService analyticsService)
    {
        this.analyticsService = analyticsService;
    }

    @RequestMapping(value = Constants.COMPUTE_ANALYTICS_PATH, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object  computeAnalytics(HttpServletRequest request)
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("json request in request {}");
        }
        String requestData = (String) request.getAttribute("requestData");
        System.out.println("****In controller reqdata "+requestData);
        Gson gson = new Gson();
        Input input = gson.fromJson(requestData, Input.class);
        System.out.println("***In controller**"+input.getData().getFirstName()+" "+input.getData().getSsn());
        Response response = analyticsService.computeAnalytics(input);

        if(logger.isDebugEnabled())
        {
            logger.debug("computeAnalytics - response = {}", response);
        }
        return response;
    }
}
