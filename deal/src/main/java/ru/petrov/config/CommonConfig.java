package ru.petrov.config;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.petrov.dto.ScoringDataDto;
import ru.petrov.models.LoanOffer;

@Configuration
public class CommonConfig {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        PropertyMap<LoanOffer, ScoringDataDto> loanOfferScoringDataDtoPropertyMap = new PropertyMap<LoanOffer, ScoringDataDto>() {
            @Override
            protected void configure() {
                map().setAmount(source.getRequestedAmount());
            }
        };
        modelMapper.addMappings(loanOfferScoringDataDtoPropertyMap);
        return modelMapper;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
