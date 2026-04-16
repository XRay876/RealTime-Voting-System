package com.example.polls_service.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.polls_service.dto.request.AddPollOptionRequest;
import com.example.polls_service.dto.request.UpdatePollOptionRequest;
import com.example.polls_service.dto.response.PollOptionResponse;
import com.example.polls_service.exception.BadRequestException;
import com.example.polls_service.exception.NotFoundException;
import com.example.polls_service.model.Poll;
import com.example.polls_service.model.PollOption;
import com.example.polls_service.model.PollStatus;
import com.example.polls_service.repository.PollOptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PollOptionService {

    private final PollOptionRepository pollOptionRepository;
    private final PollService pollService;

    // add option 
    public PollOptionResponse addOption(Long pollId, AddPollOptionRequest request) {

        Poll poll = pollService.getPollEntity(pollId);


        if (poll.getStatus() == PollStatus.CLOSED) {
            throw new BadRequestException("Cannot add option to a closed poll");
        }

        PollOption option = PollOption.builder()
                .poll(poll)
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();

        return map(pollOptionRepository.save(option));
    }

    // update option
    public PollOptionResponse updateOption(Long optionId, UpdatePollOptionRequest request) {

        PollOption option = pollOptionRepository.findById(optionId)
                .orElseThrow(() -> new NotFoundException("Option not found with id: " + optionId));

        if (option.getPoll().getStatus() == PollStatus.CLOSED) {
            throw new BadRequestException("Cannot update option in a closed poll");
        }

        option.setName(request.getName());
        option.setDescription(request.getDescription());

        
        return map(pollOptionRepository.save(option));
    }


    // delete option
    public void deleteOption(Long optionId) {
        PollOption option = pollOptionRepository.findById(optionId)
                .orElseThrow(() -> new NotFoundException("Option not found with id: " + optionId));

                
        if (option.getPoll().getStatus() == PollStatus.CLOSED) {
            throw new BadRequestException("Cannot delete option from a closed poll");
        }

        pollOptionRepository.delete(option);
    }

    
    private PollOptionResponse map(PollOption option) {

        return PollOptionResponse.builder()
                .id(option.getId())
                .pollId(option.getPoll().getId())
                .name(option.getName())
                .description(option.getDescription())
                .build();
    }
}