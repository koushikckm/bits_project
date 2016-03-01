package com.koushik.bits.api.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.koushik.bits.common.response.Error;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.koushik.bits.common.constants.Constants;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class ValidationFilter extends OncePerRequestFilter
{
    private volatile static String schema = null;
    private final Logger logger = LoggerFactory.getLogger(ValidationFilter.class);
    private MessageSource source;

    public ValidationFilter(MessageSource source)
    {
        DateTimeZone.setDefault(DateTimeZone.UTC);
        this.source = source;
    }

    @Override
    public void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain chain) throws IOException, ServletException
    {
        Locale locale = httpServletRequest.getLocale();
        String requestData = (String) httpServletRequest.getAttribute("requestData");

        try
        {
            if (schema == null)
            {
                File schemaFile = new File(this.getClass().getResource(Constants.SCHEMA_V1).toURI());
                schema = new Scanner(schemaFile, "UTF-8").useDelimiter("\\Z").next();
                //logger.debug("{}: schema : {}", correlationId, schema);
            }
        }
        catch (URISyntaxException e)
        {
            //logger.error("{}: Schema not define", correlationId);
            throw new ServletException("Schema not defined");
        }

        List<Error> validationErrors = (List<Error>) httpServletRequest.getAttribute("validationErrors");

        if (validationErrors == null || validationErrors.size() == 0)
        {
            validationErrors = new ArrayList<>();
            if (logger.isDebugEnabled())
            {
                //logger.debug("{}: started validation with locale {}", correlationId, locale);
            }

            if (requestData == null)
            {
                //logger.error("{}: Json request data not present in validation filter", correlationId);
                throw new ServletException("Json request data not present in validation filter");
            }

            ProcessingReport report = null;
            try
            {
                JsonNode schemaNode = JsonLoader.fromString(schema);
                JsonNode data = JsonLoader.fromString(requestData);
                JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
                JsonSchema schema = factory.getJsonSchema(schemaNode);
                report = schema.validate(data);
            }
            catch (Exception e)
            {
                //logger.info("{}: Incorrect input Json : ParseException : {} ", correlationId, ExceptionUtils.getFullStackTrace(e));
                createError(validationErrors, locale, "Invalid Json");
            }

            if (report != null)
            {
                Iterator iter = report.iterator();
                while (iter.hasNext())
                {
                    ProcessingMessage message = (ProcessingMessage) iter.next();
                    createError(validationErrors, locale, message.getMessage());
                }
            }
            if (logger.isDebugEnabled())
            {
                logger.debug("after validation filter process size {}", validationErrors.size());
            }
            if (validationErrors != null && validationErrors.size() != 0)
            {
                httpServletRequest.setAttribute("validationErrors", validationErrors);
            }
        }

        chain.doFilter(httpServletRequest, httpServletResponse);
    }

    public void createError(List<Error> errors, Locale locale, String message)
    {
        Error error = new Error();
        error.setErrorMessage(source.getMessage("invalid.json.message", null, locale));
        error.setErrorMessageDetailed(message);
        error.setErrorCode(source.getMessage("invalid.json.code", null, locale));
        errors.add(error);
    }
}
