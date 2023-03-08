package module13.superCoolCurrencyBot.dto;

import lombok.Data;
import module13.superCoolCurrencyBot.enums.Currency;

import java.math.BigDecimal;

@Data
public class CurrencyRateNbuResponseDto
{
   private BigDecimal rate;

   private Currency cc;
}
