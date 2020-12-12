package smolka.smsapi.enums.smshub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smolka.smsapi.enums.CountryList;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum SmsHubCountryDictionary {
    SMSHUB_RUSSIA("ru", CountryList.RUSSIA),
    SMSHUB_KAZAKHSTAN("kz", CountryList.KAZAKHSTAN),
    SMSHUB_UKRAINE("ua", CountryList.UKRAINE),
    SMSHUB_POLAND("pl", CountryList.POLAND),
    SMSHUB_SERBIA("sb", CountryList.SERBIA);

    private final String smsHubCountryName;
    private final CountryList country;

    public static CountryList getInternalCountryBySmsHubCountryName(String smsHubCountryName) {
        return Arrays.stream(values())
                .filter(e -> e.smsHubCountryName.equals(smsHubCountryName))
                .findFirst()
                .map(SmsHubCountryDictionary::getCountry)
                .orElse(null);
    }

    public static SmsHubCountryDictionary getSmsHubCountryByInternalCountry(CountryList country) {
        return Arrays.stream(values())
                .filter(e -> e.country.equals(country))
                .findFirst()
                .orElse(null);
    }
}
