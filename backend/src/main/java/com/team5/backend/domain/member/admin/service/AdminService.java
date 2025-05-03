package com.team5.backend.domain.member.admin.service;

import com.team5.backend.domain.member.admin.dto.ProductRequestResDto;
import com.team5.backend.domain.member.admin.entity.ProductRequest;
import com.team5.backend.domain.member.admin.entity.ProductRequestStatus;
import com.team5.backend.domain.member.admin.repository.ProductRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ProductRequestRepository productRequestRepository;

    public Page<ProductRequestResDto> getProductRequests(Pageable pageable, ProductRequestStatus status) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<ProductRequest> pageResult = (status != null)
                ? productRequestRepository.findAllByStatus(status, sortedPageable)
                : productRequestRepository.findAll(sortedPageable);

        return pageResult.map(ProductRequestResDto::fromEntity);
    }

}
