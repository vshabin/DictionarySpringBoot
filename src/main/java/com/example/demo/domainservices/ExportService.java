package com.example.demo.domainservices;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.export.ExportCriteriaModel;
import com.example.demo.domain.export.ExportReturnModel;
import com.example.demo.domain.export.ExportType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExportService {
    private final String NO_SUCH_STRATEGY_ERROR_CODE="NO_SUCH_STRATEGY_ERROR_CODE";
    private final String NO_SUCH_STRATEGY_ERROR_MESSAGE="No such strategy";

    private final Map<ExportType, ExportInterface> strategies;

    public ExportService(Collection<ExportInterface> exportImpls) {
        this.strategies = exportImpls.stream()
                .collect(Collectors.toMap(ExportInterface::getType, Function.identity()));
    }

    public ExportReturnModel getFile(ExportCriteriaModel criteriaModel) throws IOException {
        var strategy = strategies.get(criteriaModel.getExportType());
        if (strategy == null) {
            return new ExportReturnModel(NO_SUCH_STRATEGY_ERROR_CODE,NO_SUCH_STRATEGY_ERROR_MESSAGE);
        }
        return strategies.get(criteriaModel.getExportType()).getFile(criteriaModel);
    }
}
