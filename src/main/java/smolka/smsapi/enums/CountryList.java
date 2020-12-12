package smolka.smsapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CountryList {
    RUSSIA("ru"),
    KAZAKHSTAN("kz"),
    UKRAINE("ua"),
    POLAND("pl"),
    SERBIA("sb");

    private final String countryName;
}
