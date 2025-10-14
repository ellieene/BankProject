package com.example.BankProject.mapper;

import com.example.BankProject.model.dto.CardBlockResponseDTO;
import com.example.BankProject.model.entity.Card;
import com.example.BankProject.model.entity.CardBlockRequest;
import com.example.BankProject.model.enums.Status;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class BlockedCardRequestToBlockedCardResponseDTOMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void setupMapper(){
        modelMapper.createTypeMap(CardBlockRequest.class, CardBlockResponseDTO.class)
                .addMappings(m -> {
                    m.using(getFioConverter()).map(CardBlockRequest::getCard, CardBlockResponseDTO::setFio);
                    m.using(getStatusConverter()).map(CardBlockRequest::getCard, CardBlockResponseDTO::setStatus);
                    m.using(getNumberConverter()).map(CardBlockRequest::getCard, CardBlockResponseDTO::setNumber);
                    m.map(CardBlockRequest::getCard, CardBlockResponseDTO::setExpirationDate);
                });
    }

    private Converter<Card, String> getFioConverter() {
        return context -> {
            Card card = context.getSource();
            return card == null || card.getUser() == null ? null : card.getUser().getFio();
        };
    }

    private Converter<Card, Status> getStatusConverter() {
        return context -> {
            Card card = context.getSource();
            return card == null ? null : card.getStatus();
        };
    }

    private Converter<Card, String> getNumberConverter() {
        return context -> {
            Card card = context.getSource();
            return card == null || card.getUser() == null ? null : card.getNumber();
        };
    }

    private Converter<Card, String> getExpirationDateConverter() {
        return context -> {
            Card card = context.getSource();
            return card == null || card.getUser() == null ? null : card.getNumber();
        };
    }
}
