package com.koushik.bits.api.service;

import com.koushik.bits.api.persistance.AnalyticalStats;
import com.koushik.bits.api.repository.AnalyticalRepository;
import com.koushik.bits.common.model.Input;
import com.koushik.bits.common.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticaServiceImpl implements AnalyticsService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticaServiceImpl.class);

    /*@Autowired
    private final AnalyticalRepository analyticalRepository;

    @Autowired
    public AnalyticaServiceImpl(final AnalyticalRepository analyticalRepository)
    {
        this.analyticalRepository = analyticalRepository;
    }*/

    public Response computeAnalytics(final Input input)
    {
        System.out.println("*****In service*****");
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("In compute-Analytics service", input);
        }

        //Saving to stats table
       /* AnalyticalStats analyticalStats = new AnalyticalStats();
        analyticalStats.setA(input.getData().getA());
        analyticalStats.setB(input.getData().getB());
        analyticalStats = analyticalRepository.save(analyticalStats);
        if(analyticalStats == null)
        {
            //failed to insert record
        }*/
        Response response = new Response();
        response.setPhoneNo(input.getData().getPhoneNo());
        response.setSsn(input.getData().getSsn());
        System.out.println("*****In service response*****"+response);

        return response;
    }
}
