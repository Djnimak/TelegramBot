package module13.superCoolCurrencyBot.service;

import module13.superCoolCurrencyBot.dto.CurrencyRateDto;
import module13.superCoolCurrencyBot.dto.SettingsDto;

import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RateOperationsService {
    private List<CurrencyRetrievalService> retrievalServices = List.of(
//            new CurrencyRetrievalMonoService(),
            new CurrencyRetrievalPrivatService(),
            new CurrencyRetrievalNbuService()
    );

    public Optional<CurrencyRateDto> getRatesBasedOnSettings(SettingsDto userSettings)
    {
        Optional<CurrencyRateDto> rates = getActualRates().stream()
           .filter(rate -> rate.getCurrency() == userSettings.getCurrency())
           .filter(rate -> rate.getBankName() == userSettings.getBank())
           .findFirst();
        rates.ifPresent(rate -> {
               rate.setBuyRate(rate.getBuyRate().setScale(userSettings.getDecimalCount(), RoundingMode.CEILING));
               rate.setSellRate(rate.getSellRate().setScale(userSettings.getDecimalCount(), RoundingMode.CEILING));
           });
        return rates;
    }

    public List<CurrencyRateDto> getActualRates(){
        return retrievalServices.stream()
                .map(CurrencyRetrievalService::getCurrencyRates)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
