package com.company.efood.search.controller;

import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.search.dto.SearchResultDto;
import com.company.efood.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.company.efood.base.BaseConstants.PUBLIC_ENDPOINT;

@RestController
@RequestMapping(PUBLIC_ENDPOINT + "search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final BaseUtils baseUtils;

    @GetMapping
    public BaseResponse search(@RequestParam("query") String query,
                               @RequestParam(value = "zoneId", required = false) Long zoneId,
                               @RequestParam(value = "type", defaultValue = "ALL") String type) {
        try {
            SearchResultDto result = searchService.search(query, zoneId, type);
            return baseUtils.generateSuccessResponse(result, "Search successful", "Search successful");
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }
}
