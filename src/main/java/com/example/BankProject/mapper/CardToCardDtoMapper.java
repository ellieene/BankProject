package com.example.BankProject.mapper;

import com.example.BankProject.model.dto.CardDTO;
import com.example.BankProject.model.entity.Card;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Configuration
public class CardToCardDtoMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void setupMapper() {
        modelMapper.createTypeMap(Card.class, CardDTO.class)
                .addMappings(m -> m.map(card -> card.getUser().getFio(), (cardDTO, value) -> cardDTO.setFio((String) value)))
                .addMappings(m -> m.using(dateConverter()).map(Card::getExpirationDate, CardDTO::setExpirationDate));
    }

    private Converter<LocalDate, String> dateConverter() {
        return context ->
            context.getSource().format(DateTimeFormatter.ofPattern("MM/yy"));
    }
}
