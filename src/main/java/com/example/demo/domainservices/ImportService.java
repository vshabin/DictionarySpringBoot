package com.example.demo.domainservices;

import com.example.demo.domain.export.ExportCriteriaModel;
import com.example.demo.domain.export.ExportReturnModel;
import com.example.demo.domain.export.ExportType;
import com.example.demo.domain.fileImport.ImportReturnModel;
import com.example.demo.domain.fileImport.ImportType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
@Service
public class ImportService {
    private final String NO_SUCH_STRATEGY_ERROR_CODE="NO_SUCH_STRATEGY_ERROR_CODE";
    private final String NO_SUCH_STRATEGY_ERROR_MESSAGE="No such strategy";

    private final Map<ImportType, ImportInterface> strategies;

    public ImportService(Collection<ImportInterface> exportImpls) {
        this.strategies = exportImpls.stream()
                .collect(Collectors.toMap(ImportInterface::getType, Function.identity()));
    }

    public ImportReturnModel readFile(ImportType type, MultipartFile file) throws IOException {
        var strategy = strategies.get(type);
        if (strategy == null) {
            return new ImportReturnModel(NO_SUCH_STRATEGY_ERROR_CODE,NO_SUCH_STRATEGY_ERROR_MESSAGE);
        }
        return strategy.readFile(file);
    }
}
