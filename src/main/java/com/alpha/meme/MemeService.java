package com.alpha.meme;

import com.alpha.common.exceptions.InvalidParametersException;
import com.alpha.common.exceptions.UnreadableResponseException;
import com.alpha.currencyExchange.CurrencyRateService;
import com.alpha.visualMedia.VisualMediaObject;
import com.alpha.visualMedia.VisualMediaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class MemeService
{
    CurrencyRateService currencyRateService;
    VisualMediaService mediaService;
    ObjectMapper objectMapper;

    @Autowired
    public MemeService(CurrencyRateService currencyRateService, VisualMediaService mediaService,
                       ObjectMapper objectMapper)
    {
        this.currencyRateService = currencyRateService;
        this.mediaService = mediaService;
        this.objectMapper = objectMapper;
    }

    @Getter
    @AllArgsConstructor
    static class RecentRateMemeResponse
    {
        Set<VisualMediaObject> visualMediaObjects;
        CurrencyRateService.QuoteRateChange recentRateDynamics;
    }

    public RecentRateMemeResponse getRecentRateMemes(String quoteCurrencyId, String baseCurrencyId) throws InvalidParametersException,
            JsonProcessingException, UnreadableResponseException
    {
        var recentRateDynamics = currencyRateService.getRecentRateDynamics(quoteCurrencyId,
                baseCurrencyId);

        Set<VisualMediaObject> visualMediaObjectSet;

        switch (recentRateDynamics)
        {
            case GAIN ->
            {
                visualMediaObjectSet = mediaService.getMediaObjectsByDescription("rich");
            }

            case STABLE ->
            {
                visualMediaObjectSet = mediaService.getMediaObjectsByDescription("patience");
            }

            case FALL ->
            {
                visualMediaObjectSet = mediaService.getMediaObjectsByDescription("broke");
            }

            default ->
            {
                throw new IllegalStateException("Variable recentRateDynamics, enum (class RateChange) stores state " +
                        "that could not be handled");
            }
        }

        return new RecentRateMemeResponse(visualMediaObjectSet, recentRateDynamics);
    }
}
