package com.team5.backend.domain.dibs.service;

import com.team5.backend.domain.dibs.dto.DibsCreateReqDto;
import com.team5.backend.domain.dibs.dto.DibsResDto;
import com.team5.backend.domain.dibs.entity.Dibs;
import com.team5.backend.domain.dibs.repository.DibsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DibsService {

    private final DibsRepository dibsRepository;

    public DibsResDto createDibs(DibsCreateReqDto request) {
        Dibs dibs = Dibs.builder()
                .memberId(request.getMemberId())
                .productId(request.getProductId())
                .status(true)
                .build();

        Dibs saved = dibsRepository.save(dibs);
        return toResponse(saved);
    }

    public List<DibsResDto> getAllDibs() {
        return dibsRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void deleteDibs(Long dibsId) {
        dibsRepository.deleteById(dibsId);
    }

    private DibsResDto toResponse(Dibs dibs) {
        return DibsResDto.builder()
                .dibsId(dibs.getDibsId())
                .memberId(dibs.getMemberId())
                .productId(dibs.getProductId())
                .status(dibs.getStatus())
                .build();
    }
}
