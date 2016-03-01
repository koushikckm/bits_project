package com.koushik.bits.api.filter;

import com.google.gson.Gson;
import com.koushik.bits.common.constants.Constants;
import com.koushik.bits.common.response.Error;

import com.google.gson.JsonSyntaxException;
import com.koushik.bits.common.model.Input;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.apache.commons.io.IOUtils;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class StatsFilter extends OncePerRequestFilter
{
    private final Logger logger = LoggerFactory.getLogger(StatsFilter.class);
    private ApplicationContext applicationContext;
    private MessageSource source;

    public StatsFilter(ApplicationContext applicationContext, MessageSource source)
    {
        DateTimeZone.setDefault(DateTimeZone.UTC);
        this.applicationContext = applicationContext;
        this.source = source;
    }

    @Override
    public void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain chain) throws IOException, ServletException
    {
        /*if (logger.isDebugEnabled())
        {
            logger.debug("{} is correlationId", correlationId);
        }*/
        System.out.println("*****In stats filter");
        Gson gson = new Gson();
        Locale locale = httpServletRequest.getLocale();
        HttpResetStreamRequest wrappedRequest = new HttpResetStreamRequest(httpServletRequest);
        BufferedReader reader = wrappedRequest.getReader();
        StringBuilder requestData = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
        {
            requestData.append(line.trim());
        }
        /*if (logger.isDebugEnabled())
        {
            logger.debug("{}: requestData {} ", correlationId, requestData);
        }*/

        wrappedRequest.resetInputStream();
        reader = wrappedRequest.getReader();
        Input input = null;

        List<Error> errors = new ArrayList();
        try
        {
            input = gson.fromJson(reader, Input.class);
        } catch (JsonSyntaxException syntaxException)
        {
           /* if (logger.isDebugEnabled())
            {
                logger.debug("{}: Incorrect input Json : JsonSyntaxException : {} ", correlationId, syntaxException.getStackTrace());
            }*/
            createError(errors, locale, "malformed.json");
        } catch (RuntimeException runtimeException)
        {
            /*if (logger.isDebugEnabled())
            {
                logger.debug("{}: Incorrect input Json : RuntimeException : {} ", correlationId, runtimeException.getStackTrace());
            }*/
            createError(errors, locale, "malformed.json");
        }

        if (errors.size() != 0)
        {
            httpServletRequest.setAttribute("validationErrors", errors);
        }

        wrappedRequest.resetInputStream();
        wrappedRequest.setAttribute("requestData", requestData.toString());
        chain.doFilter(wrappedRequest, httpServletResponse);

    }

    private String getUUID()
    {
        return UUID.randomUUID().toString();
    }

    private static class HttpResetStreamRequest extends HttpServletRequestWrapper
    {
        private byte[] rawData;
        private HttpServletRequest request;
        private ResetInputStream servletStream;

        public HttpResetStreamRequest(HttpServletRequest request)
        {
            super(request);
            this.request = request;
            this.servletStream = new ResetInputStream();
        }

        public void resetInputStream()
        {
            servletStream.stream = new ByteArrayInputStream(rawData);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException
        {
            if (rawData == null)
            {
                rawData = IOUtils.toByteArray(this.request.getReader());
                servletStream.stream = new ByteArrayInputStream(rawData);
            }
            return servletStream;
        }

        @Override
        public BufferedReader getReader() throws IOException
        {
            if (rawData == null)
            {
                rawData = IOUtils.toByteArray(this.request.getReader());
                servletStream.stream = new ByteArrayInputStream(rawData);
            }
            return new BufferedReader(new InputStreamReader(servletStream, StandardCharsets.UTF_8));
        }

        private static class ResetInputStream extends ServletInputStream
        {
            private ByteArrayInputStream stream;

            @Override
            public int read() throws IOException
            {
                return stream.read();
            }

            public boolean isFinished()
            {
                return stream.available() == 0;
            }

            public boolean isReady()
            {
                return true;
            }

            public void setReadListener(ReadListener listener)
            {
                throw new RuntimeException("Not implemented");
            }
        }
    }

    public void createError(List<Error> errors, Locale locale, String message)
    {
        Error error = new Error();

        error.setErrorMessage(source.getMessage(message + Constants.VALIDATION_MESSAGE, null, locale));
        error.setErrorMessageDetailed(source.getMessage(message + Constants.VALIDATION_DESCRIPTION, null, locale));
        error.setErrorCode(source.getMessage(message + Constants.VALIDATION_CODE, null, locale));
        errors.add(error);
    }
}

